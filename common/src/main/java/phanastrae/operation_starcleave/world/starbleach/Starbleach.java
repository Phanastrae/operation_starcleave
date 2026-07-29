package phanastrae.operation_starcleave.world.starbleach;

import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;
import phanastrae.operation_starcleave.block.ShortHolyMossBlock;
import phanastrae.operation_starcleave.block.tag.OperationStarcleaveBlockTags;
import phanastrae.operation_starcleave.data.worldgen.features.OperationStarcleaveConfiguredFeatures;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;
import phanastrae.operation_starcleave.entity.StarbleachChargeEntity;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;
import phanastrae.operation_starcleave.sound.OperationStarcleaveSoundEvents;
import phanastrae.operation_starcleave.world.OperationStarcleaveGameRules;
import phanastrae.operation_starcleave.world.firmament.Firmament;
import phanastrae.operation_starcleave.world.firmament.FirmamentRegion;
import phanastrae.operation_starcleave.world.firmament.FirmamentSubRegion;
import phanastrae.operation_starcleave.world.intermediate.IntermediateWorldGenLevel;
import phanastrae.operation_starcleave.world.intermediate.SimpleStorage;

import java.util.Optional;

public class Starbleach {

    public static void starbleachChunk(ServerLevel level, LevelChunk chunk, int randomTickSpeed) {
        Firmament firmament = Firmament.fromLevel(level);
        if (firmament == null) {
            return;
        }

        level.getProfiler().popPush("starcleave_starbleach");

        ChunkPos chunkPos = chunk.getPos();
        int minX = chunkPos.getMinBlockX();
        int minZ = chunkPos.getMinBlockZ();
        FirmamentSubRegion subRegion = firmament.getSubRegion(minX, minZ);
        if (subRegion == null) return;

        StarbleachTarget starbleachTarget = StarbleachTarget.getFractureStarbleachingTarget(level);
        RandomSource random = level.random;

        for (int k = 0; k < randomTickSpeed; ++k) {
            if (random.nextInt(90) == 0) {
                int x = minX + random.nextInt(16);
                int z = minZ + random.nextInt(16);

                int damage = subRegion.getDamage(x & FirmamentRegion.SUBREGION_MASK, z & FirmamentRegion.SUBREGION_MASK);
                if (damage >= 5) {
                    int topY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
                    BlockPos targetPos = new BlockPos(x, topY - 1, z);
                    starbleachWithSeeping(level, targetPos, starbleachTarget, 20);
                }
            }
        }
    }

    public static void starbleachWithSeeping(ServerLevel level, BlockPos blockPos, StarbleachTarget starbleachTarget, int starbleachCharge) {
        BlockState blockState = level.getBlockState(blockPos);

        if (starbleachTarget.shouldFillCauldrons()) {
            if (starbleachPos(level, blockPos, blockState, StarbleachTarget.ONLY_FILLING, 15, 10)) {
                return;
            }
        }

        if (isStarbleached(blockState)) {
            // move down through starbleached blocks to (try to) find a non-starbleached block and target that instead
            Optional<Pair<BlockPos, BlockState>> pairOptional = findNonStarbleachedBlock(level, blockPos);
            if (pairOptional.isEmpty()) {
                return;
            } else {
                Pair<BlockPos, BlockState> pair = pairOptional.get();
                blockPos = pair.left();
                blockState = pair.right();
            }
        }

        if (starbleachTarget.shouldConvertBlocks() && level.getRandom().nextInt(10) == 0) {
            spawnCharge(level, blockPos, starbleachCharge);
        }
    }

    @Nullable
    public static StarbleachChargeEntity spawnCharge(ServerLevel level, BlockPos blockPos, int starbleachCharge) {
        StarbleachChargeEntity charge = OperationStarcleaveEntityTypes.STARBLEACH_CHARGE.create(level);
        if (charge != null) {
            charge.setPos(blockPos.getBottomCenter());
            charge.setCharge(starbleachCharge);
            level.addFreshEntity(charge);
        }

        return charge;
    }

    public static boolean starbleachPos(ServerLevel level, BlockPos blockPos, BlockState blockState, StarbleachTarget starbleachTarget, int glimmerCount, int swirlCount) {
        RandomSource random = level.random;

        BlockState newState = StarbleachConversions.getStarbleachResult(level, blockPos, blockState, random, starbleachTarget);
        if (newState != null) {
            convertBlock(level, blockPos, blockState, newState, glimmerCount, swirlCount);
            starbleachAttachedBlocks(level, random, blockPos, newState);
            newState.updateNeighbourShapes(level, blockPos, 3);

            // randomly bonemeal short holy moss
            BlockPos upPos = blockPos.above();
            BlockState upState = level.getBlockState(upPos);
            if (upState.getBlock() instanceof ShortHolyMossBlock block && random.nextInt(14) == 0) {
                block.performBonemeal(level, random, upPos, upState);
            }

            placeFeatures(level, random, blockPos, newState);
            return true;
        } else {
            return false;
        }
    }

    public static void starbleachAttachedBlocks(ServerLevel level, RandomSource random, BlockPos blockPos, BlockState newState) {
        for (Direction direction : Direction.values()) {
            starbleachAttachedBlocks(level, random, blockPos, newState, direction);
        }
    }

    public static void starbleachAttachedBlocks(ServerLevel level, RandomSource random, BlockPos blockPos, BlockState newState, Direction direction) {
        BlockPos adjPos = blockPos.offset(direction.getNormal());
        BlockState adjState = level.getBlockState(adjPos);

        BlockState newAdjState = StarbleachConversions.getStarbleachAttachedBlockResult(level, adjPos, adjState, random, newState, direction);
        if (newAdjState != null) {
            if (newAdjState.is(OperationStarcleaveBlocks.TALL_HOLY_MOSS) || newAdjState.is(BlockTags.TALL_FLOWERS) || newAdjState.is(OperationStarcleaveBlocks.STARTOUCHED_DOOR)) {
                level.setBlock(adjPos, newAdjState, 2 | 16);
                starbleachAttachedBlocks(level, random, adjPos, newAdjState, direction);
                newState.updateNeighbourShapes(level, blockPos, 3);
            } else {
                level.setBlockAndUpdate(adjPos, newAdjState);
            }
        }
    }

    public static void placeFeatures(ServerLevel level, RandomSource random, BlockPos blockPos, BlockState newState) {
        BlockPos upPos = blockPos.above();
        BlockState upState = level.getBlockState(upPos);

        if (newState.is(OperationStarcleaveBlocks.HOLY_MOSS) && random.nextInt(3) == 0 && upState.isAir()) {
            tryPlaceFeature(level, random, OperationStarcleaveConfiguredFeatures.HOLY_MOSS_VEGETATION, upPos);
            return;
        } else if (newState.is(OperationStarcleaveBlocks.STELLAR_MULCH) && random.nextInt(3) == 0 && upState.isAir()) {
            tryPlaceFeature(level, random, OperationStarcleaveConfiguredFeatures.STELLAR_MULCH_VEGETATION, upPos);
            return;
        }

        if ((newState.is(OperationStarcleaveBlocks.HOLY_MOSS) || newState.is(OperationStarcleaveBlocks.STELLAR_MULCH) || newState.is(OperationStarcleaveBlocks.STELLAR_SEDIMENT) || newState.is(OperationStarcleaveBlocks.STARDUST_BLOCK))
                && (random.nextInt(128) == 0)
        ) {
            tryPlaceAsterubbleBoulder(level, random, upPos);
        } else if (newState.is(OperationStarcleaveBlocks.STELLARUBBLE_MIX) && random.nextInt(80) == 0) {
            tryPlaceAsterubbleBoulder(level, random, upPos);
        } else if (newState.is(OperationStarcleaveBlocks.ASTERUBBLE) && random.nextInt(20) == 0) {
            tryPlaceAsterubbleBoulder(level, random, upPos);
        }
    }

    public static void tryPlaceAsterubbleBoulder(ServerLevel level, RandomSource random, BlockPos pos) {
        if (level.getBlockState(pos).canBeReplaced()) {
            SimpleStorage storage = new SimpleStorage();
            IntermediateWorldGenLevel intermediateLevel = new IntermediateWorldGenLevel(storage, level);

            tryPlaceFeature(
                    intermediateLevel,
                    level.registryAccess(),
                    level.getChunkSource().getGenerator(),
                    random,
                    OperationStarcleaveConfiguredFeatures.ASTERUBBLE_BOULDER,
                    pos
            );

            AABB bb = storage.getBlockBoundingBox();
            if (bb != null) {
                // don't place if building-blocking entities are inside the bounding box
                if (!level.getEntitiesOfClass(Entity.class, bb, e -> e.blocksBuilding).isEmpty()) {
                    return;
                }
                // don't place if players are close to the bounding box
                AABB inflated = bb.inflate(2);
                if (!level.getEntitiesOfClass(Player.class, inflated).isEmpty()) {
                    return;
                }
            }

            // for asterubble boulders, just place blocks directly
            // for other features, splitting this into a place step and an update step may be required
            // also for other features/structures, placing block entities and entities would be required
            storage.forEachBlock((p, state) -> level.setBlock(p, state, 3));

            storage.forEachBlock((p, state) -> {
                spawnParticles(level, p, 1, 2, random.nextInt(3) == 0 ? random.nextIntBetweenInclusive(1, 3) : 0);

                if (random.nextInt(20) == 0) {
                    level.playSeededSound(null, p.getX(), p.getY(), p.getZ(), OperationStarcleaveSoundEvents.STARBLEACH, SoundSource.BLOCKS, 0.1F, 1.6F + 0.4F * level.random.nextFloat(), level.random.nextLong());
                }
            });
        }
    }

    public static void tryPlaceFeature(ServerLevel level, RandomSource random, ResourceKey<ConfiguredFeature<?, ?>> featureKey, BlockPos pos) {
        tryPlaceFeature(level, level.registryAccess(), level.getChunkSource().getGenerator(), random, featureKey, pos);
    }

    public static void tryPlaceFeature(WorldGenLevel level, RegistryAccess registryAccess, ChunkGenerator chunkGenerator, RandomSource random, ResourceKey<ConfiguredFeature<?, ?>> featureKey, BlockPos pos) {
        registryAccess
                .registry(Registries.CONFIGURED_FEATURE)
                .flatMap(feature -> feature.getHolder(featureKey))
                .ifPresent(feature -> feature.value().place(level, chunkGenerator, random, pos));
    }

    public static boolean isStarbleached(BlockState blockState) {
        return blockState.is(OperationStarcleaveBlockTags.STARBLEACHED);
    }

    public static Optional<Pair<BlockPos, BlockState>> findNonStarbleachedBlock(ServerLevel level, BlockPos blockPos) {
        BlockPos.MutableBlockPos mutable = blockPos.mutable();
        for (int i = 0; i < 12; i++) {
            // move randomly sideways or downwards
            boolean moveHorizontal = level.random.nextInt(6) == 0;
            if (moveHorizontal) {
                int x = 0;
                int z = 0;
                switch (level.random.nextInt(4)) {
                    case 0 -> x = 1;
                    case 1 -> x = -1;
                    case 2 -> z = 1;
                    case 3 -> z = -1;
                }
                mutable.move(x, 0, z);
            } else {
                mutable.move(0, -1, 0);
            }

            BlockState blockState = level.getBlockState(mutable);

            // move downwards until block is not air
            for (int j = 0; j < 8; j++) {
                if (blockState.isAir()) {
                    mutable.move(0, -1, 0);
                    blockState = level.getBlockState(mutable);
                } else {
                    break;
                }
            }

            if (!isStarbleached(blockState)) {
                return Optional.of(Pair.of(mutable.immutable(), blockState));
            }
        }
        return Optional.empty();
    }

    public static void convertBlock(ServerLevel level, BlockPos blockPos, BlockState oldState, BlockState newState, int glimmerCount, int swirlCount) {
        level.setBlock(blockPos, newState, 2 | 16);
        if (newState.isAir()) {
            level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, blockPos, Block.getId(oldState));
            level.gameEvent(GameEvent.BLOCK_DESTROY, blockPos, GameEvent.Context.of(null, oldState));
        }
        level.playSeededSound(null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), OperationStarcleaveSoundEvents.STARBLEACH, SoundSource.BLOCKS, 0.1F, 1.6F + 0.4F * level.random.nextFloat(), level.random.nextLong());
        spawnParticles(level, blockPos, glimmerCount, swirlCount, 0);
    }

    public static void spawnParticles(ServerLevel level, BlockPos blockPos, int glimmerCount, int swirlCount, int largeSwirlCount) {
        for (Direction direction : Direction.values()) {
            Vec3i v = direction.getNormal();
            if (level.getBlockState(blockPos.offset(v)).canBeReplaced()) {
                double x = blockPos.getX() + 0.5 + 0.5 * v.getX();
                double y = blockPos.getY() + 0.5 + 0.5 * v.getY();
                double z = blockPos.getZ() + 0.5 + 0.5 * v.getZ();

                if (glimmerCount > 0) {
                    spawnParticles(level, OperationStarcleaveParticleTypes.FIRMAMENT_GLIMMER, x, y, z, v, glimmerCount, 0.05);
                }
                if (swirlCount > 0) {
                    spawnParticles(level, OperationStarcleaveParticleTypes.STARBLEACH_SWIRL, x, y, z, v, swirlCount, 0.035);
                }
                if (largeSwirlCount > 0) {
                    spawnParticles(level, OperationStarcleaveParticleTypes.LARGE_STARBLEACH_SWIRL, x, y, z, v, largeSwirlCount, 0.02);
                }
            }
        }
    }

    private static void spawnParticles(ServerLevel level, SimpleParticleType type, double x, double y, double z, Vec3i normal, int count, double speed) {
        level.sendParticles(type, x, y, z,
                count,
                normal.getX() == 0 ? 0.5 : 0,
                normal.getY() == 0 ? 0.5 : 0,
                normal.getZ() == 0 ? 0.5 : 0,
                speed
        );
    }

    public enum StarbleachTarget {
        ALL(true, true), // convert all blocks. default behaviour beneath fractures
        ONLY_FILLING(false, true), // only fill cauldrons. behaviour beneath fractures if fracture starbleaching is disabled
        NO_FILLING(true, false); // convert all blocks except cauldrons. behaviour of splash starbleach bottles

        private final boolean convertBlocks;
        private final boolean fillCauldrons;

        StarbleachTarget(boolean convertBlocks, boolean fillCauldrons) {
            this.convertBlocks = convertBlocks;
            this.fillCauldrons = fillCauldrons;
        }

        public boolean shouldConvertBlocks() {
            return this.convertBlocks;
        }

        public boolean shouldFillCauldrons() {
            return this.fillCauldrons;
        }

        public static StarbleachTarget getFractureStarbleachingTarget(ServerLevel level) {
            boolean bl = level.getGameRules().getBoolean(OperationStarcleaveGameRules.DO_FRACTURE_STARBLEACHING);
            return bl ? StarbleachTarget.ALL : StarbleachTarget.ONLY_FILLING;
        }
    }
}

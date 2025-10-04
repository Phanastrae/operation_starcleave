package phanastrae.operation_starcleave.world.starbleach;

import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;
import phanastrae.operation_starcleave.block.tag.OperationStarcleaveBlockTags;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;
import phanastrae.operation_starcleave.sound.OperationStarcleaveSoundEvents;
import phanastrae.operation_starcleave.world.OperationStarcleaveGameRules;
import phanastrae.operation_starcleave.world.firmament.Firmament;
import phanastrae.operation_starcleave.world.firmament.FirmamentRegion;
import phanastrae.operation_starcleave.world.firmament.FirmamentSubRegion;

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
                    starbleach(level, targetPos, starbleachTarget, 150);
                }
            }
        }
    }

    public static void starbleach(ServerLevel level, BlockPos blockPos, StarbleachTarget starbleachTarget, int particleCount) {
        BlockState blockState = level.getBlockState(blockPos);
        if (isStarbleached(blockState)) {
            // try to decorate
            if (starbleachTarget.shouldConvertBlocks()) {
                if (tryDecorateAtPosition(level, blockPos)) {
                    return;
                }
            }

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

        // try to starbleach target block
        BlockState newState = StarbleachConversions.getStarbleachResult(level, blockPos, blockState, level.random, starbleachTarget);
        if (newState != null) {
            convertBlock(level, blockPos, newState, particleCount);
        }
    }

    public static boolean isStarbleached(BlockState blockState) {
        return blockState.is(OperationStarcleaveBlockTags.STARBLEACHED);
    }

    public static boolean tryDecorateAtPosition(ServerLevel level, BlockPos blockPos) {
        if (level.random.nextInt(5) == 0) {
            BlockPos upPos = blockPos.above();
            decorate(level, upPos, 5, OperationStarcleaveBlocks.HOLY_MOSS, OperationStarcleaveBlocks.SHORT_HOLY_MOSS);
            decorate(level, upPos, 10, OperationStarcleaveBlocks.STELLAR_MULCH, OperationStarcleaveBlocks.MULCHBORNE_TUFT);
            return true;
        } else {
            return false;
        }
    }

    public static void decorate(ServerLevel level, BlockPos blockPos, int threshold, Block baseBlock, Block decoBlock) {
        int nearby = 0;
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                for (int k = -2; k <= 2; k++) {
                    if (i * i + j * j + k * k > 6) continue;

                    if (level.getBlockState(blockPos.offset(i, j, k)).is(decoBlock)) {
                        nearby++;
                    }
                }
            }
        }
        if (nearby > threshold) {
            return;
        }

        if (level.getBlockState(blockPos).isAir() && level.getBlockState(blockPos.below()).is(baseBlock)) {
            level.setBlockAndUpdate(blockPos, decoBlock.defaultBlockState());
        }
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

    public static void convertBlock(ServerLevel level, BlockPos blockPos, BlockState newState, int particleCount) {
        if (newState.isAir()) {
            level.destroyBlock(blockPos, false);
        } else {
            level.setBlockAndUpdate(blockPos, newState);
        }
        level.playSeededSound(null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), OperationStarcleaveSoundEvents.STARBLEACH, SoundSource.BLOCKS, 0.1F, 1.6F + 0.4F * level.random.nextFloat(), level.random.nextLong());
        spawnParticles(level, blockPos, particleCount);
    }

    public static void spawnParticles(ServerLevel level, BlockPos blockPos, int particleCount) {
        for (Direction direction : Direction.values()) {
            Vec3i v = direction.getNormal();
            if (level.getBlockState(blockPos.offset(v)).canBeReplaced()) {
                double x = blockPos.getX() + 0.5 + 0.5 * v.getX();
                double y = blockPos.getY() + 0.5 + 0.5 * v.getY();
                double z = blockPos.getZ() + 0.5 + 0.5 * v.getZ();
                level.sendParticles(OperationStarcleaveParticleTypes.FIRMAMENT_GLIMMER, x, y, z, particleCount,
                        v.getX() == 0 ? 0.5 : 0,
                        v.getY() == 0 ? 0.5 : 0,
                        v.getZ() == 0 ? 0.5 : 0,
                        0.05);
            }
        }
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

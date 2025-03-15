package phanastrae.operation_starcleave.world.starbleach;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;
import phanastrae.operation_starcleave.block.StarbleachCauldronBlock;
import phanastrae.operation_starcleave.block.StellarFarmlandBlock;
import phanastrae.operation_starcleave.block.tag.OperationStarcleaveBlockTags;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;
import phanastrae.operation_starcleave.world.OperationStarcleaveGameRules;
import phanastrae.operation_starcleave.world.firmament.Firmament;
import phanastrae.operation_starcleave.world.firmament.FirmamentRegion;
import phanastrae.operation_starcleave.world.firmament.FirmamentSubRegion;
import phanastrae.operation_starcleave.world.firmament.SubRegionPos;

public class Starbleach {

    public enum StarbleachTarget {
        ALL, // convert all blocks. default behaviour beneath fractures
        ONLY_FILLING, // only fill cauldrons. behaviour beneath fractures if fracture starbleaching is disabled
        NO_FILLING // convert all blocks except cauldrons. behaviour of splash starbleach bottles
    }

    public static StarbleachTarget getFractureStarbleachTarget(ServerLevel world) {
        boolean bl = world.getGameRules().getBoolean(OperationStarcleaveGameRules.DO_FRACTURE_STARBLEACHING);
        return bl ? StarbleachTarget.ALL : StarbleachTarget.ONLY_FILLING;
    }

    public static void starbleachChunk(ServerLevel world, LevelChunk chunk, int randomTickSpeed) {
        Firmament firmament = Firmament.fromLevel(world);
        if(firmament == null) {
            return;
        }

        world.getProfiler().popPush("starcleave_starbleach");

        ChunkPos chunkPos = chunk.getPos();
        int i = chunkPos.getMinBlockX();
        int j = chunkPos.getMinBlockZ();
        SubRegionPos subRegionPos = SubRegionPos.fromWorldCoords(i, j);
        FirmamentSubRegion subRegion = firmament.getSubRegionFromId(subRegionPos.id);
        if(subRegion == null) return;

        StarbleachTarget starbleachTarget = getFractureStarbleachTarget(world);

        for(int k = 0; k < randomTickSpeed; ++k) {
            if (world.random.nextInt(300) == 0) {
                BlockPos blockPos = world.getBlockRandomPos(i, 0, j, 0xF);

                int damage = subRegion.getDamage(blockPos.getX() & FirmamentRegion.SUBREGION_MASK, blockPos.getZ() & FirmamentRegion.SUBREGION_MASK);
                if(damage >= 5) {
                    int topY = world.getHeight(Heightmap.Types.MOTION_BLOCKING, blockPos.getX(), blockPos.getZ());
                    BlockPos targetPos = new BlockPos(blockPos.getX(), topY - 1, blockPos.getZ());
                    starbleach(world, targetPos, starbleachTarget, 150);
                }
            }
        }
    }

    public static void starbleach(ServerLevel world, BlockPos blockPos, StarbleachTarget starbleachTarget, int particleCount) {
        BlockState blockState = world.getBlockState(blockPos);
        if(isStarbleached(blockState)) {
            if(starbleachTarget == StarbleachTarget.ALL || starbleachTarget == StarbleachTarget.NO_FILLING) {
                if (world.random.nextInt(5) == 0) {
                    decorate(world, blockPos.above(), 5, OperationStarcleaveBlocks.HOLY_MOSS, OperationStarcleaveBlocks.SHORT_HOLY_MOSS);
                    decorate(world, blockPos.above(), 10, OperationStarcleaveBlocks.STELLAR_MULCH, OperationStarcleaveBlocks.MULCHBORNE_TUFT);
                    return;
                }
            }

            boolean starbleached = true;
            for (int k = 0; k < 12 && starbleached; k++) {
                if (world.random.nextInt(6) == 0) {
                    int x = 0;
                    int z = 0;
                    switch (world.random.nextInt(4)) {
                        case 0 -> x = 1;
                        case 1 -> x = -1;
                        case 2 -> z = 1;
                        case 3 -> z = -1;
                    }
                    blockPos = blockPos.offset(x, 0, z);
                } else {
                    blockPos = blockPos.offset(0, -1, 0);
                }
                blockState = world.getBlockState(blockPos);
                for(int k2 = 0; k2 < 8; k2++) {
                    if(blockState.isAir()) {
                        blockPos = blockPos.offset(0, -1, 0);
                        blockState = world.getBlockState(blockPos);
                    }
                }
                if(!isStarbleached(blockState)) starbleached = false;
            }
            if(starbleached) {
                return;
            }
        }

        BlockState newState = getStarbleachResult(world, blockPos, blockState, world.random, starbleachTarget);

        if(newState != null) {
            if(newState.isAir()) {
                world.destroyBlock(blockPos, false);
            } else {
                world.setBlockAndUpdate(blockPos, newState);
            }
            world.playSeededSound(null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 0.1F, 1.6F + 0.4F * world.random.nextFloat(), world.random.nextLong());
            for(Direction direction : Direction.values()) {
                Vec3i v = direction.getNormal();
                if(world.getBlockState(blockPos.offset(v)).canBeReplaced()) {
                    double x = blockPos.getX() + 0.5 + 0.5 * v.getX();
                    double y = blockPos.getY() + 0.5 + 0.5 * v.getY();
                    double z = blockPos.getZ() + 0.5 + 0.5 * v.getZ();
                    world.sendParticles(OperationStarcleaveParticleTypes.FIRMAMENT_GLIMMER, x, y, z, particleCount,
                            v.getX() == 0 ? 0.5 : 0,
                            v.getY() == 0 ? 0.5 : 0,
                            v.getZ() == 0 ? 0.5 : 0,
                            0.05);
                }
            }
        }
    }

    public static void decorate(ServerLevel world, BlockPos blockPos, int threshold, Block baseBlock, Block decoBlock) {
        int nearby = 0;
        for(int i = -2; i <= 2; i++) {
            for(int j = -2; j <= 2; j++) {
                for(int k = -2; k <= 2; k++) {
                    if(i*i + j*j + k*k > 6) continue;

                    if(world.getBlockState(blockPos.offset(i, j, k)).is(decoBlock)) {
                        nearby++;
                    }
                }
            }
        }
        if(nearby > threshold) {
            return;
        }

        if(world.getBlockState(blockPos).isAir() && world.getBlockState(blockPos.below()).is(baseBlock)) {
            world.setBlockAndUpdate(blockPos, decoBlock.defaultBlockState());
        }
    }

    public static boolean isStarbleached(BlockState blockState) {
        return blockState.is(OperationStarcleaveBlockTags.STARBLEACHED);
    }

    @Nullable
    public static BlockState getStarbleachResult(Level world, BlockPos blockPos, BlockState blockState, RandomSource random, StarbleachTarget starbleachTarget) {
        BlockState newBlockstate = null;
        if(starbleachTarget == StarbleachTarget.ALL || starbleachTarget == StarbleachTarget.ONLY_FILLING) {
            newBlockstate = getStarbleachCauldronResult(blockState);
            if(newBlockstate != null) {
                return newBlockstate;
            }
        }
        if(starbleachTarget == StarbleachTarget.ALL || starbleachTarget == StarbleachTarget.NO_FILLING) {
            newBlockstate = getStarbleachBlockResult(world, blockPos, blockState, random);
            if(newBlockstate != null) {
                return newBlockstate;
            }
        }

        return newBlockstate;
    }


    @Nullable
    public static BlockState getStarbleachCauldronResult(BlockState blockState) {
        if (blockState.is(Blocks.CAULDRON)) {
            return OperationStarcleaveBlocks.STARBLEACH_CAULDRON.defaultBlockState();
        }
        if (blockState.is(OperationStarcleaveBlocks.STARBLEACH_CAULDRON)) {
            if (blockState.getValue(StarbleachCauldronBlock.LEVEL_7) != StarbleachCauldronBlock.MAX_STARBLEACH_LEVEL) {
                return blockState.cycle(StarbleachCauldronBlock.LEVEL_7);
            }
        }

        return null;
    }

    @Nullable
    public static BlockState getStarbleachBlockResult(Level world, BlockPos blockPos, BlockState blockState, RandomSource random) {
        // TODO implement proper datapack based system for this instead of hardcoding it all
        if(blockState.is(OperationStarcleaveBlockTags.STARBLEACH_IMMUNE)) {
            return null;
        }

        if(blockState.is(Blocks.PODZOL)
                || blockState.is(Blocks.MYCELIUM)) {
            return OperationStarcleaveBlocks.STELLAR_MULCH.defaultBlockState();
        }
        if(blockState.is(Blocks.GRASS_BLOCK)) {
            int steepness = 0;
            for(Direction direction : Direction.values()) {
                if(direction.getAxis() != Direction.Axis.Y) {
                    BlockState state = world.getBlockState(blockPos.offset(direction.getStepX(), 1, direction.getStepZ()));
                    if(!state.canBeReplaced()) {
                        steepness += 1;
                    }
                }
            }
            if(random.nextInt(2 + steepness) >= 2) {
                return OperationStarcleaveBlocks.STELLAR_MULCH.defaultBlockState();
            }

            int nearbyMulch = 0;
            for(int x = -1; x <= 1; x++) {
                for(int z = -1; z <= 1; z++) {
                    BlockState state = world.getBlockState(blockPos.offset(x, 0, z));
                    if(state.is(OperationStarcleaveBlocks.STELLAR_MULCH)) {
                        nearbyMulch += 1;
                    }
                }
            }
            if(random.nextInt(1 + (9 - nearbyMulch) * (9 - nearbyMulch)) <= 2) {
                return OperationStarcleaveBlocks.STELLAR_MULCH.defaultBlockState();
            } else {
                return OperationStarcleaveBlocks.HOLY_MOSS.defaultBlockState();
            }
        }
        if(blockState.is(Blocks.DIRT)
                || blockState.is(Blocks.COARSE_DIRT)
                || blockState.is(Blocks.ROOTED_DIRT)
                || blockState.is(BlockTags.BASE_STONE_OVERWORLD)
                || blockState.is(Blocks.END_STONE)) {
            if(world.getBlockState(blockPos.above()).isAir()) {
                int nearbyMulch = 0;
                for(int x = -1; x <= 1; x++) {
                    for(int z = -1; z <= 1; z++) {
                        BlockState state = world.getBlockState(blockPos.offset(x, 0, z));
                        if(state.is(OperationStarcleaveBlocks.STELLAR_MULCH)) {
                            nearbyMulch += 1;
                        }
                    }
                }
                if(random.nextInt(1 + (9 - nearbyMulch) * (9 - nearbyMulch)) <= 30) {
                    return OperationStarcleaveBlocks.STELLAR_MULCH.defaultBlockState();
                }
            }

            return OperationStarcleaveBlocks.STELLAR_SEDIMENT.defaultBlockState();
        }
        if(blockState.is(Blocks.NETHERRACK)
                || blockState.is(Blocks.SOUL_SAND)
                || blockState.is(Blocks.SOUL_SOIL)
                || blockState.is(Blocks.CRIMSON_NYLIUM)
                || blockState.is(Blocks.WARPED_NYLIUM)) {
            return Blocks.AIR.defaultBlockState();
        }
        if(blockState.is(BlockTags.SAND)
                || blockState.is(Blocks.GRAVEL)) {
            return OperationStarcleaveBlocks.STARDUST_BLOCK.defaultBlockState();
        }
        if(blockState.is(BlockTags.LEAVES)
                || blockState.is(BlockTags.WART_BLOCKS)
                || blockState.is(Blocks.CHORUS_PLANT)
                || blockState.is(Blocks.CHORUS_FLOWER)) {
            if(random.nextInt(3) == 0) {
                return OperationStarcleaveBlocks.STARBLEACHED_LEAVES.defaultBlockState();
            } else {
                return Blocks.AIR.defaultBlockState();
            }
        }
        if(blockState.is(BlockTags.LOGS)) {
            if(blockState.getProperties().contains(RotatedPillarBlock.AXIS)) {
                return OperationStarcleaveBlocks.STARBLEACHED_LOG.defaultBlockState().setValue(RotatedPillarBlock.AXIS, blockState.getValue(RotatedPillarBlock.AXIS));
            } else {
                return OperationStarcleaveBlocks.STARBLEACHED_LOG.defaultBlockState();
            }
        }
        if(blockState.is(Blocks.FARMLAND)) {
            Firmament firmament = Firmament.fromLevel(world);
            if(firmament != null && StellarFarmlandBlock.isStarlit(world, blockPos, firmament)) {
                return OperationStarcleaveBlocks.STELLAR_FARMLAND.defaultBlockState().setValue(FarmBlock.MOISTURE, 7);
            } else {
                return OperationStarcleaveBlocks.STELLAR_FARMLAND.defaultBlockState();
            }
        }

        return null;
    }
}

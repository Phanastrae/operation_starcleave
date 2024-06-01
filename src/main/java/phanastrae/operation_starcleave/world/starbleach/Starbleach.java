package phanastrae.operation_starcleave.world.starbleach;

import net.minecraft.block.*;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
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

    public static StarbleachTarget getFractureStarbleachTarget(ServerWorld world) {
        boolean bl = world.getGameRules().getBoolean(OperationStarcleaveGameRules.DO_FRACTURE_STARBLEACHING);
        return bl ? StarbleachTarget.ALL : StarbleachTarget.ONLY_FILLING;
    }

    public static void starbleachChunk(ServerWorld world, WorldChunk chunk, int randomTickSpeed) {
        Firmament firmament = Firmament.fromWorld(world);
        if(firmament == null) {
            return;
        }

        world.getProfiler().swap("starcleave_starbleach");

        ChunkPos chunkPos = chunk.getPos();
        int i = chunkPos.getStartX();
        int j = chunkPos.getStartZ();
        SubRegionPos subRegionPos = SubRegionPos.fromWorldCoords(i, j);
        FirmamentSubRegion subRegion = firmament.getSubRegionFromId(subRegionPos.id);
        if(subRegion == null) return;

        StarbleachTarget starbleachTarget = getFractureStarbleachTarget(world);

        for(int k = 0; k < randomTickSpeed; ++k) {
            if (world.random.nextInt(300) == 0) {
                BlockPos blockPos = world.getRandomPosInChunk(i, 0, j, 0xF);

                int damage = subRegion.getDamage(blockPos.getX() & FirmamentRegion.SUBREGION_MASK, blockPos.getZ() & FirmamentRegion.SUBREGION_MASK);
                if(damage >= 5) {
                    int topY = world.getTopY(Heightmap.Type.MOTION_BLOCKING, blockPos.getX(), blockPos.getZ());
                    BlockPos targetPos = new BlockPos(blockPos.getX(), topY - 1, blockPos.getZ());
                    starbleach(world, targetPos, starbleachTarget, 150);
                }
            }
        }
    }

    public static void starbleach(ServerWorld world, BlockPos blockPos, StarbleachTarget starbleachTarget, int particleCount) {
        BlockState blockState = world.getBlockState(blockPos);
        if(isStarbleached(blockState)) {
            if(starbleachTarget == StarbleachTarget.ALL || starbleachTarget == StarbleachTarget.NO_FILLING) {
                if (world.random.nextInt(5) == 0) {
                    decorate(world, blockPos.up(), 5, OperationStarcleaveBlocks.HOLY_MOSS, OperationStarcleaveBlocks.SHORT_HOLY_MOSS);
                    decorate(world, blockPos.up(), 10, OperationStarcleaveBlocks.STELLAR_MULCH, OperationStarcleaveBlocks.MULCHBORNE_TUFT);
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
                    blockPos = blockPos.add(x, 0, z);
                } else {
                    blockPos = blockPos.add(0, -1, 0);
                }
                blockState = world.getBlockState(blockPos);
                for(int k2 = 0; k2 < 8; k2++) {
                    if(blockState.isAir()) {
                        blockPos = blockPos.add(0, -1, 0);
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
                world.breakBlock(blockPos, false);
            } else {
                world.setBlockState(blockPos, newState);
            }
            world.playSound(null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 0.1F, 1.6F + 0.4F * world.random.nextFloat(), world.random.nextLong());
            for(Direction direction : Direction.values()) {
                Vec3i v = direction.getVector();
                if(world.getBlockState(blockPos.add(v)).isReplaceable()) {
                    double x = blockPos.getX() + 0.5 + 0.5 * v.getX();
                    double y = blockPos.getY() + 0.5 + 0.5 * v.getY();
                    double z = blockPos.getZ() + 0.5 + 0.5 * v.getZ();
                    world.spawnParticles(OperationStarcleaveParticleTypes.FIRMAMENT_GLIMMER, x, y, z, particleCount,
                            v.getX() == 0 ? 0.5 : 0,
                            v.getY() == 0 ? 0.5 : 0,
                            v.getZ() == 0 ? 0.5 : 0,
                            0.05);
                }
            }
        }
    }

    public static void decorate(ServerWorld world, BlockPos blockPos, int threshold, Block baseBlock, Block decoBlock) {
        int nearby = 0;
        for(int i = -2; i <= 2; i++) {
            for(int j = -2; j <= 2; j++) {
                for(int k = -2; k <= 2; k++) {
                    if(i*i + j*j + k*k > 6) continue;

                    if(world.getBlockState(blockPos.add(i, j, k)).isOf(decoBlock)) {
                        nearby++;
                    }
                }
            }
        }
        if(nearby > threshold) {
            return;
        }

        if(world.getBlockState(blockPos).isAir() && world.getBlockState(blockPos.down()).isOf(baseBlock)) {
            world.setBlockState(blockPos, decoBlock.getDefaultState());
        }
    }

    public static boolean isStarbleached(BlockState blockState) {
        return blockState.isIn(OperationStarcleaveBlockTags.STARBLEACHED);
    }

    @Nullable
    public static BlockState getStarbleachResult(World world, BlockPos blockPos, BlockState blockState, Random random, StarbleachTarget starbleachTarget) {
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
        if (blockState.isOf(Blocks.CAULDRON)) {
            return OperationStarcleaveBlocks.STARBLEACH_CAULDRON.getDefaultState();
        }
        if (blockState.isOf(OperationStarcleaveBlocks.STARBLEACH_CAULDRON)) {
            if (blockState.get(StarbleachCauldronBlock.LEVEL_7) != StarbleachCauldronBlock.MAX_STARBLEACH_LEVEL) {
                return blockState.cycle(StarbleachCauldronBlock.LEVEL_7);
            }
        }

        return null;
    }

    @Nullable
    public static BlockState getStarbleachBlockResult(World world, BlockPos blockPos, BlockState blockState, Random random) {
        // TODO implement proper datapack based system for this instead of hardcoding it all
        if(blockState.isOf(Blocks.PODZOL)
                || blockState.isOf(Blocks.MYCELIUM)) {
            return OperationStarcleaveBlocks.STELLAR_MULCH.getDefaultState();
        }
        if(blockState.isOf(Blocks.GRASS_BLOCK)) {
            int steepness = 0;
            for(Direction direction : Direction.values()) {
                if(direction.getAxis() != Direction.Axis.Y) {
                    BlockState state = world.getBlockState(blockPos.add(direction.getOffsetX(), 1, direction.getOffsetZ()));
                    if(!state.isReplaceable()) {
                        steepness += 1;
                    }
                }
            }
            if(random.nextInt(2 + steepness) >= 2) {
                return OperationStarcleaveBlocks.STELLAR_MULCH.getDefaultState();
            }

            int nearbyMulch = 0;
            for(int x = -1; x <= 1; x++) {
                for(int z = -1; z <= 1; z++) {
                    BlockState state = world.getBlockState(blockPos.add(x, 0, z));
                    if(state.isOf(OperationStarcleaveBlocks.STELLAR_MULCH)) {
                        nearbyMulch += 1;
                    }
                }
            }
            if(random.nextInt(1 + (9 - nearbyMulch) * (9 - nearbyMulch)) <= 2) {
                return OperationStarcleaveBlocks.STELLAR_MULCH.getDefaultState();
            } else {
                return OperationStarcleaveBlocks.HOLY_MOSS.getDefaultState();
            }
        }
        if(blockState.isOf(Blocks.DIRT)
                || blockState.isOf(Blocks.COARSE_DIRT)
                || blockState.isOf(Blocks.ROOTED_DIRT)
                || blockState.isIn(BlockTags.BASE_STONE_OVERWORLD)
                || blockState.isOf(Blocks.END_STONE)) {
            if(world.getBlockState(blockPos.up()).isAir()) {
                int nearbyMulch = 0;
                for(int x = -1; x <= 1; x++) {
                    for(int z = -1; z <= 1; z++) {
                        BlockState state = world.getBlockState(blockPos.add(x, 0, z));
                        if(state.isOf(OperationStarcleaveBlocks.STELLAR_MULCH)) {
                            nearbyMulch += 1;
                        }
                    }
                }
                if(random.nextInt(1 + (9 - nearbyMulch) * (9 - nearbyMulch)) <= 30) {
                    return OperationStarcleaveBlocks.STELLAR_MULCH.getDefaultState();
                }
            }

            return OperationStarcleaveBlocks.STELLAR_SEDIMENT.getDefaultState();
        }
        if(blockState.isOf(Blocks.NETHERRACK)
                || blockState.isOf(Blocks.SOUL_SAND)
                || blockState.isOf(Blocks.SOUL_SOIL)
                || blockState.isOf(Blocks.CRIMSON_NYLIUM)
                || blockState.isOf(Blocks.WARPED_NYLIUM)) {
            return Blocks.AIR.getDefaultState();
        }
        if(blockState.isIn(BlockTags.SAND)
                || blockState.isOf(Blocks.GRAVEL)) {
            return OperationStarcleaveBlocks.STARDUST_BLOCK.getDefaultState();
        }
        if(blockState.isIn(BlockTags.LEAVES)
                || blockState.isIn(BlockTags.WART_BLOCKS)
                || blockState.isOf(Blocks.CHORUS_PLANT)
                || blockState.isOf(Blocks.CHORUS_FLOWER)) {
            if(random.nextInt(3) == 0) {
                return OperationStarcleaveBlocks.STARBLEACHED_LEAVES.getDefaultState();
            } else {
                return Blocks.AIR.getDefaultState();
            }
        }
        if(blockState.isIn(BlockTags.LOGS)) {
            if(blockState.getProperties().contains(PillarBlock.AXIS)) {
                return OperationStarcleaveBlocks.STARBLEACHED_LOG.getDefaultState().with(PillarBlock.AXIS, blockState.get(PillarBlock.AXIS));
            } else {
                return OperationStarcleaveBlocks.STARBLEACHED_LOG.getDefaultState();
            }
        }
        if(blockState.isOf(Blocks.FARMLAND)) {
            Firmament firmament = Firmament.fromWorld(world);
            if(firmament != null && StellarFarmlandBlock.isStarlit(world, blockPos, firmament)) {
                return OperationStarcleaveBlocks.STELLAR_FARMLAND.getDefaultState().with(FarmlandBlock.MOISTURE, 7);
            } else {
                return OperationStarcleaveBlocks.STELLAR_FARMLAND.getDefaultState();
            }
        }

        return null;
    }
}

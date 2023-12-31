package phanastrae.operation_starcleave.world.starbleach;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.PillarBlock;
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
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;
import phanastrae.operation_starcleave.world.firmament.Firmament;
import phanastrae.operation_starcleave.world.firmament.FirmamentRegion;
import phanastrae.operation_starcleave.world.firmament.FirmamentSubRegion;
import phanastrae.operation_starcleave.world.firmament.SubRegionPos;

public class Starbleach {

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

        for(int k = 0; k < randomTickSpeed; ++k) {
            if (world.random.nextInt(300) == 0) {
                BlockPos blockPos = world.getRandomPosInChunk(i, 0, j, 0xF);

                int damage = subRegion.getDamage(blockPos.getX() & FirmamentRegion.SUBREGION_MASK, blockPos.getZ() & FirmamentRegion.SUBREGION_MASK);
                if(damage >= 5) {
                    int topY = world.getTopY(Heightmap.Type.MOTION_BLOCKING, blockPos.getX(), blockPos.getZ());
                    BlockPos targetPos = new BlockPos(blockPos.getX(), topY - 1, blockPos.getZ());
                    starbleach(world, targetPos, true, 150);
                }
            }
        }
    }

    public static void starbleach(ServerWorld world, BlockPos blockPos, boolean canFillCauldrons, int particleCount) {
        BlockState blockState = world.getBlockState(blockPos);
        if(isStarbleached(blockState)) {
            if(world.random.nextInt(5) == 0) {
                decorate(world, blockPos.add(0, 1, 0));
                return;
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

        BlockState newState = getStarbleachResult(blockState, world.random, canFillCauldrons);

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

    public static void decorate(ServerWorld world, BlockPos blockPos) {
        int nearby = 0;
        for(int i = -2; i <= 2; i++) {
            for(int j = -2; j <= 2; j++) {
                for(int k = -2; k <= 2; k++) {
                    if(i*i + j*j + k*k > 6) continue;

                    if(world.getBlockState(blockPos.add(i, j, k)).isOf(OperationStarcleaveBlocks.SHORT_HOLY_MOSS)) {
                        nearby++;
                    }
                }
            }
        }
        if(nearby > 5) {
            return;
        }

        if(world.getBlockState(blockPos).isReplaceable() && world.getBlockState(blockPos.down()).isOf(OperationStarcleaveBlocks.HOLY_MOSS)) {
            world.setBlockState(blockPos, OperationStarcleaveBlocks.SHORT_HOLY_MOSS.getDefaultState());
        }
    }

    public static boolean isStarbleached(BlockState blockState) {
        if(blockState.isOf(OperationStarcleaveBlocks.STELLAR_SEDIMENT)) return true;
        if(blockState.isOf(OperationStarcleaveBlocks.HOLY_MOSS)) return true;
        if(blockState.isOf(OperationStarcleaveBlocks.STARBLEACHED_LOG)) return true;
        if(blockState.isOf(OperationStarcleaveBlocks.STARBLEACHED_WOOD)) return true;
        if(blockState.isOf(OperationStarcleaveBlocks.STARBLEACHED_LEAVES)) return true;
        return false;
    }

    @Nullable
    public static BlockState getStarbleachResult(BlockState blockState, Random random, boolean canFillCauldrons) {
        if(canFillCauldrons) {
            if (blockState.isOf(Blocks.CAULDRON)) {
                return OperationStarcleaveBlocks.STARBLEACH_CAULDRON.getDefaultState();
            }
            if (blockState.isOf(OperationStarcleaveBlocks.STARBLEACH_CAULDRON)) {
                if (blockState.getProperties().contains(LeveledCauldronBlock.LEVEL)) {
                    if (blockState.get(LeveledCauldronBlock.LEVEL) != 3) {
                        return blockState.cycle(LeveledCauldronBlock.LEVEL);
                    }
                }
            }
        }
        if(blockState.isOf(Blocks.GRASS_BLOCK)) {
            return OperationStarcleaveBlocks.HOLY_MOSS.getDefaultState();
        }
        if(blockState.isOf(Blocks.DIRT)) {
            return OperationStarcleaveBlocks.STELLAR_SEDIMENT.getDefaultState();
        }
        if(blockState.isOf(Blocks.END_STONE)) {
            return OperationStarcleaveBlocks.STELLAR_SEDIMENT.getDefaultState();
        }
        if(blockState.isIn(BlockTags.BASE_STONE_OVERWORLD)) {
            return OperationStarcleaveBlocks.STELLAR_SEDIMENT.getDefaultState();
        }
        if(blockState.isIn(BlockTags.BASE_STONE_NETHER)) {
            return Blocks.AIR.getDefaultState();
        }
        if(blockState.isIn(BlockTags.SAND)) {
            return OperationStarcleaveBlocks.STARDUST_BLOCK.getDefaultState();
        }
        if(blockState.isOf(Blocks.GRAVEL)) {
            return OperationStarcleaveBlocks.STARDUST_BLOCK.getDefaultState();
        }
        if(blockState.isIn(BlockTags.LEAVES) || blockState.isOf(Blocks.CHORUS_PLANT) || blockState.isOf(Blocks.CHORUS_FLOWER)) {
            if(random.nextInt(3) == 0) {
                return OperationStarcleaveBlocks.STARBLEACHED_LEAVES.getDefaultState();
            } else {
                return Blocks.AIR.getDefaultState();
            }
        }
        if(blockState.isIn(BlockTags.LOGS)) {
            if(blockState.getProperties().contains(PillarBlock.AXIS)) {
                return OperationStarcleaveBlocks.STARBLEACHED_LOG.getDefaultState().with(PillarBlock.AXIS, blockState.get(PillarBlock.AXIS));
            }
            return OperationStarcleaveBlocks.STARBLEACHED_LOG.getDefaultState();
        }
        return null;
    }
}

package phanastrae.operation_starcleave.world.starbleach;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;
import phanastrae.operation_starcleave.block.StarbleachCauldronBlock;
import phanastrae.operation_starcleave.block.StellarFarmlandBlock;
import phanastrae.operation_starcleave.block.tag.OperationStarcleaveBlockTags;
import phanastrae.operation_starcleave.world.firmament.Firmament;

public class StarbleachConversions {

    @Nullable
    public static BlockState getStarbleachResult(Level level, BlockPos blockPos, BlockState blockState, RandomSource random, Starbleach.StarbleachTarget starbleachTarget) {
        BlockState newBlockstate = null;

        if (starbleachTarget.shouldFillCauldrons()) {
            newBlockstate = getStarbleachCauldronResult(blockState);
            if (newBlockstate != null) {
                return newBlockstate;
            }
        }

        if (starbleachTarget.shouldConvertBlocks()) {
            newBlockstate = getStarbleachBlockResult(level, blockPos, blockState, random);
            if (newBlockstate != null) {
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
    public static BlockState getStarbleachBlockResult(Level level, BlockPos blockPos, BlockState blockState, RandomSource random) {
        // TODO implement proper datapack based system for this instead of hardcoding it all
        if (blockState.is(OperationStarcleaveBlockTags.STARBLEACH_IMMUNE)) {
            return null;
        }

        if (blockState.is(Blocks.PODZOL)
                || blockState.is(Blocks.MYCELIUM)) {
            return OperationStarcleaveBlocks.STELLAR_MULCH.defaultBlockState();
        }
        if (blockState.is(Blocks.GRASS_BLOCK)) {
            int steepness = 0;
            for (Direction direction : Direction.values()) {
                if (direction.getAxis() != Direction.Axis.Y) {
                    BlockState state = level.getBlockState(blockPos.offset(direction.getStepX(), 1, direction.getStepZ()));
                    if (!state.canBeReplaced()) {
                        steepness += 1;
                    }
                }
            }
            if (random.nextInt(2 + steepness) >= 2) {
                return OperationStarcleaveBlocks.STELLAR_MULCH.defaultBlockState();
            }

            int nearbyMulch = 0;
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    BlockState state = level.getBlockState(blockPos.offset(x, 0, z));
                    if (state.is(OperationStarcleaveBlocks.STELLAR_MULCH)) {
                        nearbyMulch += 1;
                    }
                }
            }
            if (random.nextInt(1 + (9 - nearbyMulch) * (9 - nearbyMulch)) <= 2) {
                return OperationStarcleaveBlocks.STELLAR_MULCH.defaultBlockState();
            } else {
                return OperationStarcleaveBlocks.HOLY_MOSS.defaultBlockState();
            }
        }
        if (blockState.is(Blocks.DIRT)
                || blockState.is(Blocks.COARSE_DIRT)
                || blockState.is(Blocks.ROOTED_DIRT)
                || blockState.is(BlockTags.BASE_STONE_OVERWORLD)
                || blockState.is(Blocks.END_STONE)) {
            if (level.getBlockState(blockPos.above()).isAir()) {
                int nearbyMulch = 0;
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        BlockState state = level.getBlockState(blockPos.offset(x, 0, z));
                        if (state.is(OperationStarcleaveBlocks.STELLAR_MULCH)) {
                            nearbyMulch += 1;
                        }
                    }
                }
                if (random.nextInt(1 + (9 - nearbyMulch) * (9 - nearbyMulch)) <= 30) {
                    return OperationStarcleaveBlocks.STELLAR_MULCH.defaultBlockState();
                }
            }

            return OperationStarcleaveBlocks.STELLAR_SEDIMENT.defaultBlockState();
        }
        if (blockState.is(Blocks.NETHERRACK)
                || blockState.is(Blocks.SOUL_SAND)
                || blockState.is(Blocks.SOUL_SOIL)
                || blockState.is(Blocks.CRIMSON_NYLIUM)
                || blockState.is(Blocks.WARPED_NYLIUM)) {
            return Blocks.AIR.defaultBlockState();
        }
        if (blockState.is(BlockTags.SAND)
                || blockState.is(Blocks.GRAVEL)) {
            return OperationStarcleaveBlocks.STARDUST_BLOCK.defaultBlockState();
        }
        if (blockState.is(BlockTags.LEAVES)
                || blockState.is(BlockTags.WART_BLOCKS)
                || blockState.is(Blocks.CHORUS_PLANT)
                || blockState.is(Blocks.CHORUS_FLOWER)) {
            if (random.nextInt(3) == 0) {
                return OperationStarcleaveBlocks.STARBLEACHED_LEAVES.defaultBlockState();
            } else {
                return Blocks.AIR.defaultBlockState();
            }
        }
        if (blockState.is(BlockTags.LOGS)) {
            BlockState state = OperationStarcleaveBlocks.STARBLEACHED_LOG.defaultBlockState();
            if (blockState.getProperties().contains(RotatedPillarBlock.AXIS)) {
                return state.setValue(RotatedPillarBlock.AXIS, blockState.getValue(RotatedPillarBlock.AXIS));
            } else {
                return state;
            }
        }
        if (blockState.is(Blocks.FARMLAND)) {
            Firmament firmament = Firmament.fromLevel(level);
            if (firmament != null && StellarFarmlandBlock.isStarlit(level, blockPos, firmament)) {
                return OperationStarcleaveBlocks.STELLAR_FARMLAND.defaultBlockState().setValue(FarmBlock.MOISTURE, 7);
            } else {
                return OperationStarcleaveBlocks.STELLAR_FARMLAND.defaultBlockState();
            }
        }

        return null;
    }
}

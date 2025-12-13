package phanastrae.operation_starcleave.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.AmethystBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.world.firmament.Firmament;

public class BuddingCelestialOpalBlock extends AmethystBlock {
    public static final MapCodec<BuddingCelestialOpalBlock> CODEC = simpleCodec(BuddingCelestialOpalBlock::new);
    public static final int UNLIT_GROWTH_CHANCE = 8;
    public static final int STARLIT_GROWTH_CHANCE = 2;
    public static final int MAX_NATURAL_CRYSTAL_LENGTH = 3;
    private static final Direction[] DIRECTIONS = Direction.values();

    @Override
    public MapCodec<BuddingCelestialOpalBlock> codec() {
        return CODEC;
    }

    public BuddingCelestialOpalBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        Firmament firmament = Firmament.fromLevel(level);
        int growthChance;
        if (firmament != null && StellarFarmlandBlock.isStarlit(level, pos, firmament)) {
            growthChance = STARLIT_GROWTH_CHANCE;
        } else {
            growthChance = UNLIT_GROWTH_CHANCE;
        }

        if (random.nextInt(growthChance) == 0) {
            Direction direction = DIRECTIONS[random.nextInt(DIRECTIONS.length)];
            BlockPos adjPos = pos.relative(direction);
            BlockState adjState = level.getBlockState(adjPos);

            for (int i = 0; i < MAX_NATURAL_CRYSTAL_LENGTH - 1; i++) {
                if ((adjState.is(OperationStarcleaveBlocks.CELESTIAL_OPAL_CLUSTER) || adjState.is(OperationStarcleaveBlocks.CELESTIAL_OPAL_SPIRE)) && adjState.getValue(CelestialOpalBudBlock.FACING) == direction) {
                    adjPos = adjPos.relative(direction);
                    adjState = level.getBlockState(adjPos);
                }
            }

            Block block = getGrownBlock(adjState, direction);
            if (block != null) {
                BlockState newState = block.defaultBlockState()
                        .setValue(CelestialOpalBudBlock.FACING, direction)
                        .setValue(CelestialOpalBudBlock.WATERLOGGED, Boolean.valueOf(adjState.getFluidState().getType() == Fluids.WATER));
                level.setBlockAndUpdate(adjPos, newState);
            }
        }
    }

    @Nullable
    public static Block getGrownBlock(BlockState adjState, Direction direction) {
        if (canClusterGrowAtState(adjState)) {
            return OperationStarcleaveBlocks.SMALL_CELESTIAL_OPAL_BUD;
        } else if (adjState.hasProperty(CelestialOpalBudBlock.FACING) && adjState.getValue(CelestialOpalBudBlock.FACING) == direction) {
            if (adjState.is(OperationStarcleaveBlocks.SMALL_CELESTIAL_OPAL_BUD)) {
                return OperationStarcleaveBlocks.MEDIUM_CELESTIAL_OPAL_BUD;
            } else if (adjState.is(OperationStarcleaveBlocks.MEDIUM_CELESTIAL_OPAL_BUD)) {
                return OperationStarcleaveBlocks.LARGE_CELESTIAL_OPAL_BUD;
            } else if (adjState.is(OperationStarcleaveBlocks.LARGE_CELESTIAL_OPAL_BUD)) {
                return OperationStarcleaveBlocks.CELESTIAL_OPAL_CLUSTER;
            }
        }

        return null;
    }

    public static boolean canClusterGrowAtState(BlockState state) {
        return state.isAir() || state.is(Blocks.WATER) && state.getFluidState().getAmount() == 8;
    }
}

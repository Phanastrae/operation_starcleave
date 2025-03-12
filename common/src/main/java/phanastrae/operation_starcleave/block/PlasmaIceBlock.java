package phanastrae.operation_starcleave.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluids;

import java.util.OptionalInt;

public class PlasmaIceBlock extends Block {
    public static final MapCodec<PlasmaIceBlock> CODEC = simpleCodec(PlasmaIceBlock::new);
    public static final int MAX_DISTANCE = 4;
    public static final IntegerProperty DISTANCE = IntegerProperty.create("distance", 1, MAX_DISTANCE);
    public static final BooleanProperty PERSISTENT = BlockStateProperties.PERSISTENT;

    @Override
    protected MapCodec<? extends PlasmaIceBlock> codec() {
        return CODEC;
    }

    public PlasmaIceBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition
                        .any()
                        .setValue(DISTANCE, MAX_DISTANCE)
                        .setValue(PERSISTENT, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DISTANCE, PERSISTENT);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockstate = this.defaultBlockState()
                .setValue(PERSISTENT, true);
        return updateDistance(blockstate, context.getLevel(), context.getClickedPos());
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        int i = getDistanceAt(facingState) + 1;
        if (i != 1 || state.getValue(DISTANCE) != i) {
            level.scheduleTick(currentPos, this, 1);
        }

        return state;
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return state.getValue(DISTANCE) == MAX_DISTANCE && !state.getValue(PERSISTENT);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        tryDestroy(state, level, pos, random);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockState newState = updateDistance(state, level, pos);
        level.setBlock(pos, newState, 3);
        tryDestroy(newState, level, pos, random);
    }

    public void tryDestroy(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (this.decaying(state)) {
            level.removeBlock(pos, false);
            for(Direction direction : Direction.values()) {
                BlockPos adjPos = pos.relative(direction);
                BlockState adjState = level.getBlockState(adjPos);
                if(adjState.is(OperationStarcleaveBlocks.PLASMA_ICE)) {
                    level.scheduleTick(adjPos, adjState.getBlock(), random.nextInt(2) + 1);
                }
            }
        }
    }

    private static BlockState updateDistance(BlockState state, LevelAccessor level, BlockPos pos) {
        int i = MAX_DISTANCE;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

        for (Direction direction : Direction.values()) {
            mutableBlockPos.setWithOffset(pos, direction);
            i = Math.min(i, getDistanceAt(level.getBlockState(mutableBlockPos)) + 1);
            if (i == 1) {
                break;
            }
        }

        return state.setValue(DISTANCE, i);
    }

    private static int getDistanceAt(BlockState neighbor) {
        return getOptionalDistanceAt(neighbor).orElse(MAX_DISTANCE);
    }

    public static OptionalInt getOptionalDistanceAt(BlockState state) {
        if (!state.getFluidState().isEmpty()) {
            return OptionalInt.of(0);
        } else {
            return state.hasProperty(DISTANCE) ? OptionalInt.of(state.getValue(DISTANCE)) : OptionalInt.empty();
        }
    }

    protected boolean decaying(BlockState state) {
        return !state.getValue(PERSISTENT) && state.getValue(DISTANCE) == MAX_DISTANCE;
    }
}

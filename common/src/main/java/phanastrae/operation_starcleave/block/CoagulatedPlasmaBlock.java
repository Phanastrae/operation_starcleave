package phanastrae.operation_starcleave.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import java.util.OptionalInt;

public class CoagulatedPlasmaBlock extends Block {
    public static final MapCodec<CoagulatedPlasmaBlock> CODEC = simpleCodec(CoagulatedPlasmaBlock::new);
    public static final int MAX_DISTANCE = 4;
    public static final IntegerProperty DISTANCE = IntegerProperty.create("distance", 1, MAX_DISTANCE);
    public static final BooleanProperty PERSISTENT = BlockStateProperties.PERSISTENT;
    public static final Direction[] DIRECTIONS_EXCEPT_UP = new Direction[]{
            Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.DOWN
    };

    @Override
    protected MapCodec<? extends CoagulatedPlasmaBlock> codec() {
        return CODEC;
    }

    public CoagulatedPlasmaBlock(Properties properties) {
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
        return state.getValue(DISTANCE) == 1 && !state.getValue(PERSISTENT);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.hasProperty(DISTANCE) && state.getValue(DISTANCE) <= 3) {
            if(!nextToGap(pos, level)) {
                level.setBlockAndUpdate(pos, OperationStarcleaveBlocks.PETRICHORIC_PLASMA.defaultBlockState());
            }
        }
    }

    public static boolean nextToGap(BlockPos pos, Level level) {
        for(Direction direction : DIRECTIONS_EXCEPT_UP) {
            BlockPos adjPos = pos.relative(direction);
            BlockState adjState = level.getBlockState(adjPos);

            if(!adjState.is(OperationStarcleaveBlocks.PETRICHORIC_PLASMA) && !adjState.isFaceSturdy(level, adjPos, direction)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        level.setBlock(pos, updateDistance(state, level, pos), 3);
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
        if (state.is(OperationStarcleaveBlocks.PETRICHORIC_PLASMA)) {
            return OptionalInt.of(0);
        } else {
            return state.hasProperty(DISTANCE) ? OptionalInt.of(state.getValue(DISTANCE)) : OptionalInt.empty();
        }
    }
}

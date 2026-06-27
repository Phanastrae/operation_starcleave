package phanastrae.operation_starcleave.block;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class StarbleachedLeafLitterBlock extends BushBlock {
    // mostly the same as PinkPetalsBlock, but with a different amount propert ("segment_amount") to match leaf litter on newer versions
    // also does not support bonemealing, like leaf litter and unlike pink petals
    public static final IntegerProperty SEGMENT_AMOUNT = IntegerProperty.create("segment_amount", 1, 4);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final MapCodec<StarbleachedLeafLitterBlock> CODEC = simpleCodec(StarbleachedLeafLitterBlock::new);
    // function takes blockstate like newer versions, rather than 1.21.1 PinkPetalsBlock taking direction and integer
    private final ImmutableMap<BlockState, VoxelShape> shapeMap;

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    public StarbleachedLeafLitterBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(SEGMENT_AMOUNT, 1));

        this.shapeMap = createShapeMap();
    }

    private ImmutableMap<BlockState, VoxelShape> createShapeMap() {
        // mix of the 1.21.1 and 26.1 vanilla functions for this
        VoxelShape[] segmentShapes = new VoxelShape[]{
                Block.box(8.0, 0.0, 8.0, 16.0, 1.0, 16.0),
                Block.box(0.0, 0.0, 8.0, 8.0, 1.0, 16.0),
                Block.box(0.0, 0.0, 0.0, 8.0, 1.0, 8.0),
                Block.box(8.0, 0.0, 0.0, 16.0, 1.0, 8.0)
        };

        return this.getShapeForEachState(state -> {
            int segmentCount = state.getValue(SEGMENT_AMOUNT);

            Direction direction = state.getValue(FACING);
            VoxelShape voxelShape = Shapes.empty();
            for (int i = 0; i < segmentCount; i++) {
                voxelShape = Shapes.or(voxelShape, segmentShapes[direction.get2DDataValue()]);
                direction = direction.getCounterClockWise();
            }
            return voxelShape.singleEncompassing();
        });
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, SEGMENT_AMOUNT);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return this.shapeMap.get(state);
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
        if (!useContext.isSecondaryUseActive() && useContext.getItemInHand().is(this.asItem()) && state.getValue(SEGMENT_AMOUNT) < 4) {
            return true;
        } else {
            return super.canBeReplaced(state, useContext);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        if (state.is(this)) {
            return state.setValue(SEGMENT_AMOUNT, Math.min(4, state.getValue(SEGMENT_AMOUNT) + 1));
        } else {
            return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
        }
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        // behaviour differs from pink petals here
        BlockPos belowPos = pos.below();
        return level.getBlockState(belowPos).isFaceSturdy(level, belowPos, Direction.UP);
    }
}

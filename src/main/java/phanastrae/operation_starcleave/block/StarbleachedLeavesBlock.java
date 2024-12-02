package phanastrae.operation_starcleave.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class StarbleachedLeavesBlock extends FallingBlock implements SimpleWaterloggedBlock {
    public static final MapCodec<StarbleachedLeavesBlock> CODEC = simpleCodec(StarbleachedLeavesBlock::new);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public StarbleachedLeavesBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(false)));
    }

    @Override
    protected MapCodec<? extends StarbleachedLeavesBlock> codec() {
        return CODEC;
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
    }

    @Override
    protected int getDelayAfterPlace() {
        return 4;
    }

    @Override
    public BlockState updateShape(
            BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos
    ) {
        if (state.getValue(WATERLOGGED)) {
            world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }

        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        int connectedNeighbors = 0;
        for(Direction direction : Direction.values()) {
            BlockState blockState = world.getBlockState(pos.offset(direction.getNormal()));
            if(blockState.is(OperationStarcleaveBlocks.STARBLEACHED_LEAVES)) {
                connectedNeighbors += 1;
            } else if(blockState.is(OperationStarcleaveBlocks.STARBLEACHED_LOG) || blockState.is(OperationStarcleaveBlocks.STARBLEACHED_WOOD)) {
                connectedNeighbors += 2;
            }
        }
        if(connectedNeighbors < 2) {
            super.tick(state, world, pos, random);
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        FluidState fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());
        return super.getStateForPlacement(ctx).setValue(WATERLOGGED, Boolean.valueOf(fluidState.is(Fluids.WATER)));
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(true) : super.getFluidState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter world, BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter world, BlockPos pos) {
        return 1;
    }

    @Override
    public int getDustColor(BlockState state, BlockGetter world, BlockPos pos) {
        return 0xEF9FCFFF;
    }
}

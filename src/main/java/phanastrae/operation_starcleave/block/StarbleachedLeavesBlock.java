package phanastrae.operation_starcleave.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class StarbleachedLeavesBlock extends FallingBlock implements Waterloggable {
    public static final MapCodec<StarbleachedLeavesBlock> CODEC = createCodec(StarbleachedLeavesBlock::new);
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public StarbleachedLeavesBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(WATERLOGGED, Boolean.valueOf(false)));
    }

    @Override
    protected MapCodec<? extends StarbleachedLeavesBlock> getCodec() {
        return CODEC;
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
    }

    @Override
    protected int getFallDelay() {
        return 4;
    }

    @Override
    public BlockState getStateForNeighborUpdate(
            BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
    ) {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int connectedNeighbors = 0;
        for(Direction direction : Direction.values()) {
            BlockState blockState = world.getBlockState(pos.add(direction.getVector()));
            if(blockState.isOf(OperationStarcleaveBlocks.STARBLEACHED_LEAVES)) {
                connectedNeighbors += 1;
            } else if(blockState.isOf(OperationStarcleaveBlocks.STARBLEACHED_LOG) || blockState.isOf(OperationStarcleaveBlocks.STARBLEACHED_WOOD)) {
                connectedNeighbors += 2;
            }
        }
        if(connectedNeighbors < 2) {
            super.scheduledTick(state, world, pos, random);
        }
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        return super.getPlacementState(ctx).with(WATERLOGGED, Boolean.valueOf(fluidState.isOf(Fluids.WATER)));
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(true) : super.getFluidState(state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @Override
    public VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Override
    public int getOpacity(BlockState state, BlockView world, BlockPos pos) {
        return 1;
    }

    @Override
    public int getColor(BlockState state, BlockView world, BlockPos pos) {
        return 0xEF9FCFFF;
    }
}

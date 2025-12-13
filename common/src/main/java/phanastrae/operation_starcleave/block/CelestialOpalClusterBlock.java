package phanastrae.operation_starcleave.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;


public class CelestialOpalClusterBlock extends CelestialOpalBudBlock {

    public CelestialOpalClusterBlock(float height, float aabbOffset, Properties properties) {
        super(height, aabbOffset, properties);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction facingDirection = context.getClickedFace();
        BlockState headState = level.getBlockState(context.getClickedPos().relative(context.getClickedFace()));
        return getBaseBlockstate(headState, facingDirection)
                .setValue(WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER)
                .setValue(FACING, facingDirection);
    }

    protected BlockState getBaseBlockstate(BlockState headState, Direction facingDirection) {
        if (headState.getBlock() instanceof CelestialOpalBudBlock && headState.getValue(FACING) == facingDirection) {
            return this.getSpireBlock().defaultBlockState();
        } else {
            return this.getClusterBlock().defaultBlockState();
        }
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        Direction thisFacing = state.getValue(FACING);

        if (facing == thisFacing) {
            boolean facingBlockIsOpal = (facingState.getBlock() instanceof CelestialOpalBudBlock);
            if (state.is(this.getClusterBlock()) && facingBlockIsOpal) {
                return this.updateBlockAfterConversion(state, this.getSpireBlock());
            } else if (state.is(this.getSpireBlock()) && !facingBlockIsOpal) {
                return this.updateBlockAfterConversion(state, this.getClusterBlock());
            }
        }
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    protected BlockState updateBlockAfterConversion(BlockState oldState, Block newBlock) {
        return newBlock.defaultBlockState()
                .setValue(WATERLOGGED, oldState.getValue(WATERLOGGED))
                .setValue(FACING, oldState.getValue(FACING));
    }

    @Override
    public Item asItem() {
        return OperationStarcleaveItems.CELESTIAL_OPAL_CLUSTER;
    }
}

package phanastrae.operation_starcleave.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class CelestialOpalBudBlock extends AmethystClusterBlock {

    public CelestialOpalBudBlock(float height, float aabbOffset, Properties properties) {
        super(height, aabbOffset, properties);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction direction = state.getValue(FACING);
        BlockPos adjPos = pos.relative(direction.getOpposite());
        BlockState adjState = level.getBlockState(adjPos);

        if ((adjState.is(this.getSpireBlock()) || adjState.is(this.getClusterBlock())) && adjState.getValue(FACING) == state.getValue(FACING)) {
            return true;
        } else {
            return super.canSurvive(state, level, pos);
        }
    }

    public Block getClusterBlock() {
        return OperationStarcleaveBlocks.CELESTIAL_OPAL_CLUSTER;
    }

    public Block getSpireBlock() {
        return OperationStarcleaveBlocks.CELESTIAL_OPAL_SPIRE;
    }
}

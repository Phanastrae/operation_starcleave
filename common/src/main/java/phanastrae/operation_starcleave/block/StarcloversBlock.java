package phanastrae.operation_starcleave.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.PinkPetalsBlock;
import net.minecraft.world.level.block.state.BlockState;
import phanastrae.operation_starcleave.block.tag.OperationStarcleaveBlockTags;

public class StarcloversBlock extends PinkPetalsBlock {

    public StarcloversBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(OperationStarcleaveBlockTags.STARBLEACHED_SAPLING_PLANTABLE_ON);
    }
}

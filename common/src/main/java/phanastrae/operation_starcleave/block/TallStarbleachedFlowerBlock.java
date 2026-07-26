package phanastrae.operation_starcleave.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.TallFlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import phanastrae.operation_starcleave.block.tag.OperationStarcleaveBlockTags;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;

public class TallStarbleachedFlowerBlock extends TallFlowerBlock {

    public TallStarbleachedFlowerBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(OperationStarcleaveBlockTags.STARBLEACHED_SAPLING_PLANTABLE_ON);
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        if (this == OperationStarcleaveBlocks.STARCLOVER_BUSH && random.nextInt(4) != 0) {
            popResource(level, pos, new ItemStack(OperationStarcleaveItems.STARCLOVERS));
        } else {
            popResource(level, pos, new ItemStack(this));
        }
    }
}

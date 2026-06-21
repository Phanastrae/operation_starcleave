package phanastrae.operation_starcleave.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import phanastrae.operation_starcleave.block.tag.OperationStarcleaveBlockTags;

public class StarbleachedSaplingBlock extends BushBlock {
    public static final MapCodec<StarbleachedSaplingBlock> CODEC = simpleCodec(StarbleachedSaplingBlock::new);
    protected static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 12.0, 14.0);

    @Override
    public MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    protected StarbleachedSaplingBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(OperationStarcleaveBlockTags.STARBLEACHED_SAPLING_PLANTABLE_ON);
    }
}

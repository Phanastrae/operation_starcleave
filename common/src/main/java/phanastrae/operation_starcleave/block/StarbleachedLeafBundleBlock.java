package phanastrae.operation_starcleave.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;

public class StarbleachedLeafBundleBlock extends FallingBlock {
    public static final MapCodec<StarbleachedLeafBundleBlock> CODEC = simpleCodec(StarbleachedLeafBundleBlock::new);

    @Override
    protected MapCodec<? extends StarbleachedLeafBundleBlock> codec() {
        return CODEC;
    }

    public StarbleachedLeafBundleBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected int getDelayAfterPlace() {
        return 4;
    }

    @Override
    public int getDustColor(BlockState state, BlockGetter level, BlockPos pos) {
        return 0xEF9FCFFF;
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 0.8F;
    }
}

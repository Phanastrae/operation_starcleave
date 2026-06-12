package phanastrae.operation_starcleave.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;

public class StellarTileSlabBlock extends SlabBlock {
    public static final MapCodec<StellarTileSlabBlock> CODEC = simpleCodec(StellarTileSlabBlock::new);

    @Override
    public MapCodec<? extends StellarTileSlabBlock> codec() {
        return CODEC;
    }

    public StellarTileSlabBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return Math.max(super.getShadeBrightness(state, level, pos), 0.8F);
    }
}

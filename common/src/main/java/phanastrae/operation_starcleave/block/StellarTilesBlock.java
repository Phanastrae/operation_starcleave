package phanastrae.operation_starcleave.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class StellarTilesBlock extends Block {
    public static final MapCodec<StellarTilesBlock> CODEC = simpleCodec(StellarTilesBlock::new);

    @Override
    public MapCodec<StellarTilesBlock> codec() {
        return CODEC;
    }

    public StellarTilesBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 0.8F;
    }
}

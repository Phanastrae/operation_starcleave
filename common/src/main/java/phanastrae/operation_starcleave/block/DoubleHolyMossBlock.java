package phanastrae.operation_starcleave.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;

public class DoubleHolyMossBlock extends DoublePlantBlock {
    public static final MapCodec<DoubleHolyMossBlock> CODEC = simpleCodec(DoubleHolyMossBlock::new);

    @Override
    public MapCodec<DoubleHolyMossBlock> codec() {
        return CODEC;
    }

    public DoubleHolyMossBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState floor, BlockGetter world, BlockPos pos) {
        return floor.is(OperationStarcleaveBlocks.HOLY_MOSS);
    }
}

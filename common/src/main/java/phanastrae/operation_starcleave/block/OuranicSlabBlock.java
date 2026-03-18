package phanastrae.operation_starcleave.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;

public class OuranicSlabBlock extends SlabBlock {
    public static final MapCodec<OuranicSlabBlock> CODEC = simpleCodec(OuranicSlabBlock::new);

    @Override
    public MapCodec<OuranicSlabBlock> codec() {
        return CODEC;
    }

    public OuranicSlabBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(7) == 0) {
            NucleosyntheseedBlock.spawnLightningParticles(pos, random, level, state, true);
        }
    }
}

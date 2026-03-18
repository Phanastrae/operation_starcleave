package phanastrae.operation_starcleave.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

public class OuranicPillarBlock extends RotatedPillarBlock {
    public static final MapCodec<OuranicPillarBlock> CODEC = simpleCodec(OuranicPillarBlock::new);

    @Override
    public MapCodec<OuranicPillarBlock> codec() {
        return CODEC;
    }

    public OuranicPillarBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if(random.nextInt(7) == 0) {
            NucleosyntheseedBlock.spawnLightningParticles(pos, random, level, state, true);
        }
    }
}

package phanastrae.operation_starcleave.block;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;

public class OuranicStairBlock extends StairBlock {

    public OuranicStairBlock(BlockState baseState, Properties properties) {
        super(baseState, properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(7) == 0) {
            NucleosyntheseedBlock.spawnLightningParticles(pos, random, level, state);
        }
    }
}

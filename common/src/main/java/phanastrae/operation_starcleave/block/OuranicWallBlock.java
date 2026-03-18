package phanastrae.operation_starcleave.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;

public class OuranicWallBlock extends WallBlock {
    public static final MapCodec<WallBlock> CODEC = simpleCodec(OuranicWallBlock::new);

    @Override
    public MapCodec<WallBlock> codec() {
        return CODEC;
    }

    public OuranicWallBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(7) == 0) {
            NucleosyntheseedBlock.spawnLightningParticles(pos, random, level, state);
        }
    }
}

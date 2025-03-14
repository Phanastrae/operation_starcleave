package phanastrae.operation_starcleave.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;

public class CoagulatedPlasmaBlock extends Block {

    public CoagulatedPlasmaBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if(random.nextInt(16) == 0) {
            for (Direction direction : Direction.values()) {
                BlockPos adjPos = pos.relative(direction);
                BlockState adjState = level.getBlockState(adjPos);
                if (!adjState.isFaceSturdy(level, pos, direction.getOpposite())) {
                    for (int i = 0; i < 7 + random.nextInt(14); i++) {
                        level.addParticle(
                                OperationStarcleaveParticleTypes.PLASMA_DUST,
                                pos.getX() + 0.5,
                                pos.getY() + 0.5,
                                pos.getZ() + 0.5,
                                random.nextGaussian() * 0.1,
                                random.nextGaussian() * 0.1,
                                random.nextGaussian() * 0.1
                        );
                    }
                }
            }
        }
    }
}

package phanastrae.operation_starcleave.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;

public class ImbuedStarbleachedTilesBlock extends Block {

    public ImbuedStarbleachedTilesBlock(Properties settings) {
        super(settings);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        for (Direction direction : Direction.values()) {
            Vec3i v = direction.getNormal();
            if (level.getBlockState(pos.offset(v)).canBeReplaced()) {
                int vx = v.getX();
                int vy = v.getY();
                int vz = v.getZ();

                for (int k = 0; k < 2; k++) {
                    SimpleParticleType type;
                    if (random.nextInt(21) == 0) {
                        type = OperationStarcleaveParticleTypes.STARBLEACH_SWIRL;
                    } else {
                        type = OperationStarcleaveParticleTypes.FIRMAMENT_GLIMMER;
                    }

                    double x = pos.getX() + (vx == 0 ? 0.15 + 0.7 * random.nextFloat() : 0.5 + 0.5 * vx);
                    double y = pos.getY() + (vy == 0 ? 0.15 + 0.7 * random.nextFloat() : 0.5 + 0.5 * vy);
                    double z = pos.getZ() + (vz == 0 ? 0.15 + 0.7 * random.nextFloat() : 0.5 + 0.5 * vz);

                    level.addParticle(type, x, y, z,
                            (vx + random.nextFloat() - 0.5) * 0.05,
                            (vy + random.nextFloat() - 0.5) * 0.05,
                            (vz + random.nextFloat() - 0.5) * 0.05
                    );
                }
            }
        }
    }
}

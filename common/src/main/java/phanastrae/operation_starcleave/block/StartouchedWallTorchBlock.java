package phanastrae.operation_starcleave.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;

public class StartouchedWallTorchBlock extends WallTorchBlock {

    public StartouchedWallTorchBlock(SimpleParticleType flameParticle, Properties properties) {
        super(flameParticle, properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        Direction facingDirection = state.getValue(FACING);
        Direction antiFacingDirection = facingDirection.getOpposite();
        double x = (double) pos.getX() + 0.5 + 0.27 * (double) antiFacingDirection.getStepX();
        double y = (double) pos.getY() + 0.7 + 0.22;
        double z = (double) pos.getZ() + 0.5 + 0.27 * (double) antiFacingDirection.getStepZ();

        level.addParticle(OperationStarcleaveParticleTypes.GLIMMER_SMOKE, x, y, z, 0.0, 0.0, 0.0);
        for (int i = 0; i < 2; i++) {
            level.addParticle(OperationStarcleaveParticleTypes.FIRMAMENT_GLIMMER, x, y, z, random.nextGaussian() * 0.02F, random.nextGaussian() * 0.02F, random.nextGaussian() * 0.02F);
        }
        level.addParticle(this.flameParticle, x, y, z, 0.0, 0.0, 0.0);
    }
}

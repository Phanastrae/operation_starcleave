package phanastrae.operation_starcleave.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
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
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        if(random.nextInt(5) != 0) return;

        for(Direction direction : Direction.values()) {
            Vec3i v = direction.getNormal();
            if (world.getBlockState(pos.offset(v)).canBeReplaced()) {
                double x = pos.getX() + 0.5 + 0.5 * v.getX();
                double y = pos.getY() + 0.5 + 0.5 * v.getY();
                double z = pos.getZ() + 0.5 + 0.5 * v.getZ();
                for(int k = 0; k < 25; k++) {
                    world.addParticle(OperationStarcleaveParticleTypes.FIRMAMENT_GLIMMER,
                            x + (v.getX() == 0 ? random.nextFloat() - 0.5 : 0),
                            y + (v.getY() == 0 ? random.nextFloat() - 0.5 : 0),
                            z + (v.getZ() == 0 ? random.nextFloat() - 0.5 : 0),
                            v.getX() * 0.05 + random.nextFloat() * 0.05 - 0.025,
                            v.getY() * 0.05 + random.nextFloat() * 0.05 - 0.025,
                            v.getZ() * 0.05 + random.nextFloat() * 0.05 - 0.025);
                }
            }
        }
    }
}

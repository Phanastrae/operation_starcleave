package phanastrae.operation_starcleave.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;

public class ImbuedStarbleachedTilesBlock extends Block {

    public ImbuedStarbleachedTilesBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if(random.nextInt(5) != 0) return;

        for(Direction direction : Direction.values()) {
            Vec3i v = direction.getVector();
            if (world.getBlockState(pos.add(v)).isReplaceable()) {
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

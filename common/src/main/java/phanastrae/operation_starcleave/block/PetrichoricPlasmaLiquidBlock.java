package phanastrae.operation_starcleave.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import phanastrae.operation_starcleave.entity.OperationStarcleaveDamageTypes;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;

import static phanastrae.operation_starcleave.block.AbstractPetrichoricBlock.absorbWater;

public class PetrichoricPlasmaLiquidBlock extends CustomLiquidBlock {

    protected PetrichoricPlasmaLiquidBlock(FlowingFluid fluid, Properties properties) {
        super(fluid, properties);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean notify) {
        level.scheduleTick(pos, this, getDelay(level.getRandom(), level, pos, oldState));
        super.onPlace(state, level, pos, oldState, notify);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        // damage to entities inside block
        if(entity.hurt(OperationStarcleaveDamageTypes.source(level, OperationStarcleaveDamageTypes.IN_PHLOGISTIC_FIRE), 12.0F)) { // TODO add custom damage type
            if (!(entity instanceof Player player && player.getAbilities().invulnerable && player.getAbilities().flying)) {
                RandomSource random = level.getRandom();
                entity.push(random.nextFloat() * 0.8 - 0.4, random.nextFloat() * 0.3 + 0.6, random.nextFloat() * 0.8 - 0.4);
            }
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        // particles
        if(level.getBlockState(pos.above()).isAir()) {
            if (random.nextInt(8) == 0) {
                double x = pos.getX() + random.nextDouble();
                double y = pos.getY() + random.nextDouble() * 0.2 + 0.8;
                double z = pos.getZ() + random.nextDouble();
                level.addParticle(OperationStarcleaveParticleTypes.LARGE_GLIMMER_SMOKE, x, y, z, 0.0, 0.0, 0.0);
            } else {
                double x = pos.getX() + random.nextDouble();
                double y = pos.getY() + random.nextDouble() * 0.2 + 0.8;
                double z = pos.getZ() + random.nextDouble();
                level.addParticle(OperationStarcleaveParticleTypes.NUCLEAR_SMOKE, x, y, z, 0.0, 0.0, 0.0);
            }

            for (int j = 0; j < 1 + random.nextInt(3); j++) {
                double x = pos.getX() + random.nextDouble();
                double y = pos.getY() + random.nextDouble() * 0.2 + 0.8;
                double z = pos.getZ() + random.nextDouble();
                level.addParticle(OperationStarcleaveParticleTypes.PLASMA_DUST, x, y, z, random.nextGaussian() * 0.25, random.nextGaussian() * 0.25, random.nextGaussian() * 0.25);
            }
        }
    }

    @Override
    public BlockState updateShape(
            BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos
    ) {
        level.scheduleTick(pos, this, getDelay(level.getRandom(), level, pos, state));
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        absorbWater(level, pos, random);
    }

    public static int getDelay(RandomSource random, LevelAccessor level, BlockPos pos, BlockState state) {
        return 2 + random.nextInt(3);
    }
}

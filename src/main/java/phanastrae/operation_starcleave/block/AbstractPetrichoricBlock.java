package phanastrae.operation_starcleave.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import phanastrae.operation_starcleave.entity.OperationStarcleaveDamageTypes;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;

public abstract class AbstractPetrichoricBlock extends Block {
    public AbstractPetrichoricBlock(Settings settings) {
        super(settings);
    }

    @Override
    public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        return stateFrom.isOf(this);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        entity.slowMovement(state, new Vec3d(0.85, 0.5, 0.85));
        if(entity.damage(OperationStarcleaveDamageTypes.of(world, OperationStarcleaveDamageTypes.IN_PHLOGISTIC_FIRE), 12.0F)) { // TODO add custom damage type
            if (!(entity instanceof PlayerEntity player && player.getAbilities().invulnerable && player.getAbilities().flying)) {
                Random random = world.getRandom();
                entity.addVelocity(random.nextFloat() * 0.8 - 0.4, random.nextFloat() * 0.3 + 0.6, random.nextFloat() * 0.8 - 0.4);
            }
        }
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if(world.getBlockState(pos.up()).isAir()) {
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + random.nextDouble() * 0.2 + 0.8;
            double z = pos.getZ() + random.nextDouble();
            world.addParticle(OperationStarcleaveParticleTypes.LARGE_GLIMMER_SMOKE, x, y, z, 0.0, 0.0, 0.0);
        }
    }

    public static boolean canDestroy(BlockState state) {
        if(state.isAir() || state.isOf(OperationStarcleaveBlocks.PETRICHORIC_PLASMA) || state.isOf(OperationStarcleaveBlocks.PETRICHORIC_VAPOR)) {
            return false;
        }

        if(state.isReplaceable()) {
            return true;
        }

        if(state.isIn(BlockTags.WITHER_IMMUNE) || state.isIn(BlockTags.DRAGON_IMMUNE)) {
            return false;
        }

        return !(state.getBlock().getBlastResistance() > 6);
    }

    public static boolean absorbWater(World world, BlockPos pos) {
        int posY = pos.getY();
        return BlockPos.iterateRecursively(pos, 4, 65, (currentPos, queuer) -> {
            for(Direction direction : DIRECTIONS) {
                queuer.accept(currentPos.offset(direction));
            }
        }, currentPos -> {
            int yDif = currentPos.getY() - posY;
            BlockState newState = yDif <= 0 ? OperationStarcleaveBlocks.STELLAR_SEDIMENT.getDefaultState() : OperationStarcleaveBlocks.PETRICHORIC_VAPOR.getDefaultState();

            if (currentPos.equals(pos)) {
                return true;
            } else {
                BlockState blockState = world.getBlockState(currentPos);
                FluidState fluidState = world.getFluidState(currentPos);

                if (fluidState.isEmpty()) {
                    return false;
                } else {
                    Block block = blockState.getBlock();
                    if (block instanceof FluidBlock) {
                        world.setBlockState(currentPos, newState, Block.NOTIFY_ALL);
                        return true;
                    } else if (block instanceof FluidDrainable fluidDrainable && !fluidDrainable.tryDrainFluid(null, world, currentPos, blockState).isEmpty()) {
                        return true;
                    } else if (blockState.isOf(Blocks.KELP) || blockState.isOf(Blocks.KELP_PLANT) || blockState.isOf(Blocks.SEAGRASS) || blockState.isOf(Blocks.TALL_SEAGRASS)) {
                        BlockEntity blockEntity = blockState.hasBlockEntity() ? world.getBlockEntity(currentPos) : null;
                        dropStacks(blockState, world, currentPos, blockEntity);
                        world.setBlockState(currentPos, newState, Block.NOTIFY_ALL);
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }) > 1;
    }
}

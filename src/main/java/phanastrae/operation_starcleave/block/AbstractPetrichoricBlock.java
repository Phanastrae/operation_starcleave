package phanastrae.operation_starcleave.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import phanastrae.operation_starcleave.entity.OperationStarcleaveDamageTypes;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;

public abstract class AbstractPetrichoricBlock extends Block {
    public AbstractPetrichoricBlock(Properties settings) {
        super(settings);
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState stateFrom, Direction direction) {
        return stateFrom.is(this);
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        entity.makeStuckInBlock(state, new Vec3(0.85, 0.5, 0.85));
        if(entity.hurt(OperationStarcleaveDamageTypes.of(world, OperationStarcleaveDamageTypes.IN_PHLOGISTIC_FIRE), 12.0F)) { // TODO add custom damage type
            if (!(entity instanceof Player player && player.getAbilities().invulnerable && player.getAbilities().flying)) {
                RandomSource random = world.getRandom();
                entity.push(random.nextFloat() * 0.8 - 0.4, random.nextFloat() * 0.3 + 0.6, random.nextFloat() * 0.8 - 0.4);
            }
        }
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        if(world.getBlockState(pos.above()).isAir()) {
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + random.nextDouble() * 0.2 + 0.8;
            double z = pos.getZ() + random.nextDouble();
            world.addParticle(OperationStarcleaveParticleTypes.LARGE_GLIMMER_SMOKE, x, y, z, 0.0, 0.0, 0.0);
        }
    }

    public static boolean canDestroy(BlockState state) {
        if(state.isAir() || state.is(OperationStarcleaveBlocks.PETRICHORIC_PLASMA) || state.is(OperationStarcleaveBlocks.PETRICHORIC_VAPOR)) {
            return false;
        }

        if(state.canBeReplaced()) {
            return true;
        }

        if(state.is(BlockTags.WITHER_IMMUNE) || state.is(BlockTags.DRAGON_IMMUNE)) {
            return false;
        }

        return !(state.getBlock().getExplosionResistance() > 6);
    }

    public static boolean absorbWater(Level world, BlockPos pos) {
        int posY = pos.getY();
        return BlockPos.breadthFirstTraversal(pos, 4, 65, (currentPos, queuer) -> {
            for(Direction direction : UPDATE_SHAPE_ORDER) {
                queuer.accept(currentPos.relative(direction));
            }
        }, currentPos -> {
            int yDif = currentPos.getY() - posY;
            BlockState newState = yDif <= 0 ? OperationStarcleaveBlocks.STELLAR_SEDIMENT.defaultBlockState() : OperationStarcleaveBlocks.PETRICHORIC_VAPOR.defaultBlockState();

            if (currentPos.equals(pos)) {
                return true;
            } else {
                BlockState blockState = world.getBlockState(currentPos);
                FluidState fluidState = world.getFluidState(currentPos);

                if (fluidState.isEmpty()) {
                    return false;
                } else {
                    Block block = blockState.getBlock();
                    if (block instanceof LiquidBlock) {
                        world.setBlock(currentPos, newState, Block.UPDATE_ALL);
                        return true;
                    } else if (block instanceof BucketPickup fluidDrainable && !fluidDrainable.pickupBlock(null, world, currentPos, blockState).isEmpty()) {
                        return true;
                    } else if (blockState.is(Blocks.KELP) || blockState.is(Blocks.KELP_PLANT) || blockState.is(Blocks.SEAGRASS) || blockState.is(Blocks.TALL_SEAGRASS)) {
                        BlockEntity blockEntity = blockState.hasBlockEntity() ? world.getBlockEntity(currentPos) : null;
                        dropResources(blockState, world, currentPos, blockEntity);
                        world.setBlock(currentPos, newState, Block.UPDATE_ALL);
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }) > 1;
    }
}

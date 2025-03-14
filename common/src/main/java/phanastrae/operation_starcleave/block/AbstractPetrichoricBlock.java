package phanastrae.operation_starcleave.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import phanastrae.operation_starcleave.registry.OperationStarcleaveFluidTags;

public abstract class AbstractPetrichoricBlock extends Block {
    public AbstractPetrichoricBlock(Properties settings) {
        super(settings);
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState stateFrom, Direction direction) {
        return stateFrom.is(this);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        // slow entities inside block
        entity.makeStuckInBlock(state, new Vec3(0.85, 0.5, 0.85));

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
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + random.nextDouble() * 0.2 + 0.8;
            double z = pos.getZ() + random.nextDouble();
            level.addParticle(OperationStarcleaveParticleTypes.LARGE_GLIMMER_SMOKE, x, y, z, 0.0, 0.0, 0.0);
        }
    }

    public static boolean absorbWater(Level level, BlockPos pos, RandomSource random) {
        int posY = pos.getY();

        return BlockPos.breadthFirstTraversal(pos, 2, 65, (currentPos, queuer) -> {
            for(Direction direction : UPDATE_SHAPE_ORDER) {
                queuer.accept(currentPos.relative(direction));
            }
        }, currentPos -> {
            int yDif = currentPos.getY() - posY;

            // place vapor if current block is higher than the plasma/vapor, otherwise place filler block
            BlockState filler = OperationStarcleaveBlocks.PLASMA_ICE.defaultBlockState();
            BlockState vapor = OperationStarcleaveBlocks.PETRICHORIC_VAPOR.defaultBlockState();
            BlockState newState = yDif <= 0 ? filler : vapor;

            if (currentPos.equals(pos)) {
                // always spread from start
                return true;
            } else {
                BlockState blockState = level.getBlockState(currentPos);
                FluidState fluidState = level.getFluidState(currentPos);

                if (fluidState.isEmpty() || fluidState.is(OperationStarcleaveFluidTags.PETRICHORIC_PLASMA)) {
                    return false;
                } else {
                    Block block = blockState.getBlock();
                    if (block instanceof LiquidBlock) {
                        // replace liquids
                        BlockState st = fluidState.isSource() ? newState : vapor;
                        level.setBlock(currentPos, st, Block.UPDATE_ALL);
                        if(st.is(OperationStarcleaveBlocks.PLASMA_ICE)) {
                            level.scheduleTick(currentPos, newState.getBlock(), random.nextInt(2) + 1);
                        }
                        return true;
                    } else if (block instanceof BucketPickup fluidDrainable && !fluidDrainable.pickupBlock(null, level, currentPos, blockState).isEmpty()) {
                        // try to drain fluids from fluidlogged blocks
                        return true;
                    } else if (blockState.is(Blocks.KELP) || blockState.is(Blocks.KELP_PLANT) || blockState.is(Blocks.SEAGRASS) || blockState.is(Blocks.TALL_SEAGRASS)) {
                        // replace certain blocks

                        BlockEntity blockEntity = blockState.hasBlockEntity() ? level.getBlockEntity(currentPos) : null;
                        dropResources(blockState, level, currentPos, blockEntity);
                        level.setBlock(currentPos, newState, Block.UPDATE_ALL);
                        if(newState.is(OperationStarcleaveBlocks.PLASMA_ICE)) {
                            level.scheduleTick(currentPos, newState.getBlock(), random.nextInt(2) + 1);
                        }
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }) > 1;
    }
}

package phanastrae.operation_starcleave.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;
import phanastrae.operation_starcleave.registry.OperationStarcleaveFluidTags;

import java.util.Optional;

public abstract class PetrichoricPlasmaFluid extends FlowingFluid {

    @Override
    public Fluid getFlowing() {
        return OperationStarcleaveFluids.FLOWING_PETRICHORIC_PLASMA;
    }

    @Override
    public Fluid getSource() {
        return OperationStarcleaveFluids.PETRICHORIC_PLASMA;
    }

    @Override
    public Item getBucket() {
        return OperationStarcleaveItems.PETRICHORIC_PLASMA_BUCKET;
    }

    @Override
    protected void animateTick(Level level, BlockPos pos, FluidState state, RandomSource random) {
        // TODO improve sounds / particles
        if (level.getBlockState(pos).isAir() && !level.getBlockState(pos).isSolidRender(level, pos)) {
            if (random.nextInt(200) == 0) {
                level.playLocalSound(
                        pos.getX(),
                        pos.getY(),
                        pos.getZ(),
                        SoundEvents.LAVA_AMBIENT,
                        SoundSource.BLOCKS,
                        0.2F + random.nextFloat() * 0.2F,
                        0.9F + random.nextFloat() * 0.15F,
                        false
                );
            }
        }
    }

    @Override
    protected boolean isRandomlyTicking() {
        return false;
    }

    @Nullable
    @Override
    protected ParticleOptions getDripParticle() {
        return null;
        // TODO
        //return OperationStarcleaveFluids.DRIPPING_PETRICHORIC_PLASMA;
    }

    @Override
    protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state) {
        BlockEntity blockentity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
        Block.dropResources(state, level, pos, blockentity);
    }

    @Override
    public int getSlopeFindDistance(LevelReader level) {
        return 5;
    }

    @Override
    public BlockState createLegacyBlock(FluidState state) {
        return OperationStarcleaveBlocks.PETRICHORIC_PLASMA.defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
    }

    @Override
    public boolean isSame(Fluid fluid) {
        return fluid == OperationStarcleaveFluids.PETRICHORIC_PLASMA || fluid == OperationStarcleaveFluids.FLOWING_PETRICHORIC_PLASMA;
    }

    @Override
    public int getDropOff(LevelReader level) {
        return 1;
    }

    @Override
    public boolean canBeReplacedWith(FluidState fluidState, BlockGetter blockReader, BlockPos pos, Fluid fluid, Direction direction) {
        return direction == Direction.DOWN && !fluid.is(OperationStarcleaveFluidTags.PETRICHORIC_PLASMA);
    }

    @Override
    public int getTickDelay(LevelReader level) {
        return 4;
    }

    @Override
    protected boolean canConvertToSource(Level level) {
        // TODO add gamerule?
        return false;
    }

    @Override
    protected void spreadTo(LevelAccessor level, BlockPos pos, BlockState blockState, Direction direction, FluidState fluidState) {
        if (direction == Direction.DOWN) {
            FluidState fluidstate = level.getFluidState(pos);
            if (this.is(OperationStarcleaveFluidTags.PETRICHORIC_PLASMA) && !fluidstate.isEmpty() && !fluidstate.is(OperationStarcleaveFluidTags.PETRICHORIC_PLASMA)) {
                if (blockState.getBlock() instanceof LiquidBlock) {
                    level.setBlock(pos, OperationStarcleaveBlocks.COAGULATED_PLASMA.defaultBlockState(), 3);
                }

                this.fizz(level, pos);
                return;
            }
        }

        super.spreadTo(level, pos, blockState, direction, fluidState);
    }

    private void fizz(LevelAccessor level, BlockPos pos) {
        level.levelEvent(1501, pos, 0);
    }

    @Override
    protected float getExplosionResistance() {
        return 100.0F;
    }

    @Override
    public Optional<SoundEvent> getPickupSound() {
        return Optional.of(SoundEvents.BUCKET_FILL);
    }

    public static class Flowing extends PetrichoricPlasmaFluid {
        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }

        @Override
        public boolean isSource(FluidState state) {
            return false;
        }
    }

    public static class Source extends PetrichoricPlasmaFluid {
        @Override
        public int getAmount(FluidState state) {
            return 8;
        }

        @Override
        public boolean isSource(FluidState state) {
            return true;
        }
    }
}

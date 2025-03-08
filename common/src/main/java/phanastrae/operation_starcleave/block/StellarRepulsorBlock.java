package phanastrae.operation_starcleave.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityAttachment;

public class StellarRepulsorBlock extends Block {
    public static final MapCodec<StellarRepulsorBlock> CODEC = simpleCodec(StellarRepulsorBlock::new);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    @Override
    public MapCodec<StellarRepulsorBlock> codec() {
        return CODEC;
    }

    public StellarRepulsorBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.defaultBlockState().setValue(POWERED, Boolean.valueOf(false)));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(POWERED, Boolean.valueOf(ctx.getLevel().hasNeighborSignal(ctx.getClickedPos())));
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClientSide) {
            boolean bl = state.getValue(POWERED);
            if (bl != world.hasNeighborSignal(pos)) {
                world.setBlock(pos, state.cycle(POWERED), Block.UPDATE_CLIENTS);
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Override
    public void fallOn(Level world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        entity.causeFallDamage(fallDistance, 0.0F, world.damageSources().fall());
    }

    @Override
    public void stepOn(Level world, BlockPos pos, BlockState state, Entity entity) {
        if(state.getValue(POWERED)) {
            launch(entity);
        }
    }

    public static void tryLaunch(Entity entity) {
        BlockState blockState = entity.level().getBlockState(entity.blockPosition().below());
        if(blockState.is(OperationStarcleaveBlocks.STELLAR_REPULSOR)) {
            StellarRepulsorBlock.launch(entity);
        }
    }

    public static void launch(Entity entity) {
        if(!entity.onGround()) {
            return;
        }
        OperationStarcleaveEntityAttachment osea = OperationStarcleaveEntityAttachment.fromEntity(entity);

        // short cooldown between uses
        long worldTime = entity.level().getGameTime();
        long lastUseTime = osea.getLastStellarRepulsorUse();
        long dt = worldTime - lastUseTime;
        if(0 <= dt && dt < 2) {
            return;
        }
        osea.setLastStellarRepulsorUse(worldTime);

        if(entity.isControlledByLocalInstance()) {
            Vec3 vel = entity.getDeltaMovement();

            float yaw = entity.getYRot() * Mth.PI / 180;
            float cosYaw = Mth.cos(-yaw);
            float sinYaw = Mth.sin(-yaw);
            if(entity instanceof Minecart) {
                // minecart rotation is weird, so just use minecart velocity for launch direction instead
                double vx = vel.x;
                double vz = vel.z;
                double v = Math.sqrt(vx*vx+vz*vz);
                if(v > 0.001) {
                    vx /= v;
                    vz /= v;
                } else {
                    vx = 0;
                    vz = 0;
                }
                sinYaw = (float)vx;
                cosYaw = (float)vz;
            }

            double horizontalSpeed = Math.sqrt(vel.x * vel.x + vel.z * vel.z) * 3;
            if (horizontalSpeed < 4) horizontalSpeed = 4;
            double verticalSpeed = Math.max(vel.y, Math.sqrt(horizontalSpeed) * 0.4);

            entity.setDeltaMovement(horizontalSpeed * sinYaw, verticalSpeed, horizontalSpeed * cosYaw);
        }

        entity.level().playSeededSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.5F, 0.6F + 0.4F * entity.level().random.nextFloat(), entity.level().random.nextLong());
    }
}

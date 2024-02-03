package phanastrae.operation_starcleave.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntity;

public class StellarRepulsorBlock extends Block {
    public static final MapCodec<StellarRepulsorBlock> CODEC = createCodec(StellarRepulsorBlock::new);
    public static final BooleanProperty POWERED = Properties.POWERED;

    @Override
    public MapCodec<StellarRepulsorBlock> getCodec() {
        return CODEC;
    }

    public StellarRepulsorBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(POWERED, Boolean.valueOf(false)));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(POWERED, Boolean.valueOf(ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos())));
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient) {
            boolean bl = state.get(POWERED);
            if (bl != world.isReceivingRedstonePower(pos)) {
                world.setBlockState(pos, state.cycle(POWERED), Block.NOTIFY_LISTENERS);
            }
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Override
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        entity.handleFallDamage(fallDistance, 0.0F, world.getDamageSources().fall());
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if(state.get(POWERED)) {
            launch(entity);
        }
    }

    public static void tryLaunch(Entity entity) {
        BlockState blockState = entity.getWorld().getBlockState(entity.getBlockPos().down());
        if(blockState.isOf(OperationStarcleaveBlocks.STELLAR_REPULSOR)) {
            StellarRepulsorBlock.launch(entity);
        }
    }

    public static void launch(Entity entity) {
        if(!(entity instanceof OperationStarcleaveEntity operationStarcleaveEntity)) {
            return;
        }
        if(!entity.isOnGround()) {
            return;
        }

        // short cooldown between uses
        long worldTime = entity.getWorld().getTime();
        long lastUseTime = operationStarcleaveEntity.operation_starcleave$getLastStellarRepulsorUse();
        long dt = worldTime - lastUseTime;
        if(0 <= dt && dt < 2) {
            return;
        }
        operationStarcleaveEntity.operation_starcleave$setLastStellarRepulsorUse(worldTime);

        if(entity.isLogicalSideForUpdatingMovement()) {
            Vec3d vel = entity.getVelocity();

            float yaw = entity.getYaw() * MathHelper.PI / 180;
            float cosYaw = MathHelper.cos(-yaw);
            float sinYaw = MathHelper.sin(-yaw);
            if(entity instanceof MinecartEntity) {
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

            entity.setVelocity(horizontalSpeed * sinYaw, verticalSpeed, horizontalSpeed * cosYaw);

            entity.setOnGround(false);
        }

        entity.getWorld().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F, 0.6F + 0.4F * entity.getWorld().random.nextFloat(), entity.getWorld().random.nextLong());
    }
}

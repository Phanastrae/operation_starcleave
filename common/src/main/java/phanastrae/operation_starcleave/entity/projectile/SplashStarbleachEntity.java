package phanastrae.operation_starcleave.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;
import phanastrae.operation_starcleave.world.starbleach.Starbleach;

public class SplashStarbleachEntity extends ThrowableItemProjectile implements ItemSupplier {

    boolean canStarbleach = false;

    public SplashStarbleachEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level world) {
        super(entityType, world);
    }

    public SplashStarbleachEntity(Level world, LivingEntity owner) {
        super(OperationStarcleaveEntityTypes.SPLASH_STARBLEACH, owner, world);
    }

    public SplashStarbleachEntity(Level world, double x, double y, double z) {
        super(OperationStarcleaveEntityTypes.SPLASH_STARBLEACH, x, y, z, world);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("CanStarbleach", this.canStarbleach);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if(nbt.contains("CanStarbleach", Tag.TAG_BYTE)) {
            this.canStarbleach = nbt.getBoolean("CanStarbleach");
        } else {
            this.canStarbleach = false;
        }
    }

    @Override
    public void tick() {
        Level world = this.level();
        if(world.isClientSide) {
            Vec3 vel = this.getDeltaMovement();
            RandomSource random = this.random;
            for(int i = 0; i < 6; i++) {
                world.addParticle(OperationStarcleaveParticleTypes.FIRMAMENT_GLIMMER,
                        this.getX(), this.getY(), this.getZ(),
                        vel.x * -0.2 + random.nextFloat() * 0.06 - 0.03, vel.y * -0.2 + random.nextFloat() * 0.06 - 0.03, vel.z * -0.2 + random.nextFloat() * 0.06 - 0.03);
            }
        }
        super.tick();
    }

    @Override
    protected Item getDefaultItem() {
        return OperationStarcleaveItems.SPLASH_STARBLEACH_BOTTLE;
    }

    @Override
    protected double getDefaultGravity() {
        return 0.05F;
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide) {
            if(this.canStarbleach) {
                starbleach(blockPosition(), this.level());
            }
            this.discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        if (!this.level().isClientSide) {
            Direction direction = blockHitResult.getDirection();
            BlockPos blockPos = blockHitResult.getBlockPos().relative(direction);
            this.extinguishFire(blockPos);
            this.extinguishFire(blockPos.relative(direction.getOpposite()));
            for(Direction direction2 : Direction.Plane.HORIZONTAL) {
                this.extinguishFire(blockPos.relative(direction2));
            }
        }
    }

    private void extinguishFire(BlockPos pos) {
        BlockState blockState = this.level().getBlockState(pos);
        if (blockState.is(OperationStarcleaveBlocks.PHLOGISTIC_FIRE)) {
            this.level().destroyBlock(pos, false, this);
        }
    }

    public static void starbleach(BlockPos blockPos, Level world) {
        BlockPos.MutableBlockPos blockPosMutable = new BlockPos.MutableBlockPos();
        if(world instanceof ServerLevel serverWorld) {
            for (int i = -3; i <= 3; i++) {
                for (int j = -2; j <= 2; j++) {
                    for (int k = -3; k <= 3; k++) {
                        if(i*i+j*j+k*k >= 11) continue;
                        blockPosMutable.set(blockPos.getX() + i, blockPos.getY() + j, blockPos.getZ() + k);
                        for(int n = 0; n < 4; n++) {
                            Starbleach.starbleach(serverWorld, blockPosMutable, Starbleach.StarbleachTarget.NO_FILLING, 20);
                        }
                    }
                }
            }
            serverWorld.sendParticles(OperationStarcleaveParticleTypes.FIRMAMENT_GLIMMER, blockPos.getX()+0.5, blockPos.getY()+0.5, blockPos.getZ()+0.5, 400, 2, 1, 2, 0.01);
            world.playSeededSound(null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.SPLASH_POTION_BREAK, SoundSource.BLOCKS, 2f, 1.2F + 0.3F * world.random.nextFloat(), world.random.nextLong());
        }
    }

    public void setCanStarbleach(boolean canStarbleach) {
        this.canStarbleach = canStarbleach;
    }

    public boolean getCanStarbleach() {
        return this.canStarbleach;
    }
}

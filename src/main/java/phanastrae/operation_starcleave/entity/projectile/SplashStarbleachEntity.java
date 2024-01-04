package phanastrae.operation_starcleave.entity.projectile;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;
import phanastrae.operation_starcleave.world.starbleach.Starbleach;

public class SplashStarbleachEntity extends ThrownItemEntity implements FlyingItemEntity {

    boolean canStarbleach = false;

    public SplashStarbleachEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public SplashStarbleachEntity(World world, LivingEntity owner) {
        super(OperationStarcleaveEntityTypes.SPLASH_STARBLEACH, owner, world);
    }

    public SplashStarbleachEntity(World world, double x, double y, double z) {
        super(OperationStarcleaveEntityTypes.SPLASH_STARBLEACH, x, y, z, world);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("CanStarbleach", this.canStarbleach);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if(nbt.contains("CanStarbleach", NbtElement.BYTE_TYPE)) {
            this.canStarbleach = nbt.getBoolean("CanStarbleach");
        } else {
            this.canStarbleach = false;
        }
    }

    @Override
    public void tick() {
        World world = this.getWorld();
        if(world.isClient) {
            Vec3d vel = this.getVelocity();
            Random random = this.random;
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
    protected float getGravity() {
        return 0.05F;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.getWorld().isClient) {
            if(this.canStarbleach) {
                starbleach(getBlockPos());
            }
            this.discard();
        }
    }

    protected void starbleach(BlockPos blockPos) {
        BlockPos.Mutable blockPosMutable = new BlockPos.Mutable();
        World world = this.getWorld();
        if(world instanceof ServerWorld serverWorld) {
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
            serverWorld.spawnParticles(OperationStarcleaveParticleTypes.FIRMAMENT_GLIMMER, blockPos.getX()+0.5, blockPos.getY()+0.5, blockPos.getZ()+0.5, 400, 2, 1, 2, 0.01);
            world.playSound(null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.ENTITY_SPLASH_POTION_BREAK, SoundCategory.BLOCKS, 2f, 1.2F + 0.3F * world.random.nextFloat(), world.random.nextLong());
        }
    }

    public void setCanStarbleach(boolean canStarbleach) {
        this.canStarbleach = canStarbleach;
    }

    public boolean getCanStarbleach() {
        return this.canStarbleach;
    }
}

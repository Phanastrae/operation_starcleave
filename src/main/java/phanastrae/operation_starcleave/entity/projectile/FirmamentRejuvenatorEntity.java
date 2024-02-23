package phanastrae.operation_starcleave.entity.projectile;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;
import phanastrae.operation_starcleave.world.firmament.Firmament;
import phanastrae.operation_starcleave.world.firmament.FirmamentSubRegion;

public class FirmamentRejuvenatorEntity extends ThrownItemEntity {

    public static final int MAX_AGE = 140;

    public FirmamentRejuvenatorEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public FirmamentRejuvenatorEntity(World world, LivingEntity owner) {
        super(OperationStarcleaveEntityTypes.FIRMAMENT_REJUVENATOR, owner, world);
    }

    public FirmamentRejuvenatorEntity(World world, double x, double y, double z) {
        super(OperationStarcleaveEntityTypes.FIRMAMENT_REJUVENATOR, x, y, z, world);
    }

    @Override
    public void tick() {
        World world = this.getWorld();
        if(world != null && world.isClient) {
            Vec3d vel = this.getVelocity();
            Random random = this.random;
            for(int i = 0; i < 6; i++) {
                world.addParticle(OperationStarcleaveParticleTypes.FIRMAMENT_GLIMMER,
                        this.getX(), this.getY(), this.getZ(),
                        vel.x * -0.2 + random.nextFloat() * 0.06 - 0.03, vel.y * -0.2 + random.nextFloat() * 0.06 - 0.03, vel.z * -0.2 + random.nextFloat() * 0.06 - 0.03);
                world.addParticle(ParticleTypes.SOUL_FIRE_FLAME,
                        this.getX(), this.getY(), this.getZ(),
                        vel.x * -0.2 + random.nextFloat() * 0.06 - 0.03, vel.y * -0.2 + random.nextFloat() * 0.06 - 0.03, vel.z * -0.2 + random.nextFloat() * 0.06 - 0.03);
            }
        }
        super.tick();
        if(!this.isRemoved()) {
            if(this.age > MAX_AGE) {
                this.dropStack(this.getItem());
                this.discard();
            } else {
                float firmHeight = this.getWorld().getTopY() + 16;
                double dy = this.getPos().y - firmHeight;
                if (dy * dy < 1) {
                    this.explode();
                }
            }
        }
    }

    @Override
    protected Item getDefaultItem() {
        return OperationStarcleaveItems.SPLASH_STARBLEACH_BOTTLE;
    }

    @Override
    protected float getGravity() {
        return 0.01F;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        this.dropStack(this.getItem());
        this.discard();
    }

    public void explode() {
        if (!this.getWorld().isClient) {
            this.getWorld().createExplosion(this.getOwner(), this.getX(), this.getY(), this.getZ(), 4, World.ExplosionSourceType.NONE);
            if(this.getWorld() instanceof ServerWorld serverWorld) {
                Vec3d pos = this.getPos();
                serverWorld.spawnParticles(OperationStarcleaveParticleTypes.FIRMAMENT_GLIMMER, pos.getX(), pos.getY(), pos.getZ(), 400, 2, 1, 2, 0.01);
            }

            float firmHeight = this.getWorld().getTopY() + 16;
            double dy = this.getPos().y - firmHeight;
            if(dy*dy < 1) {
                Firmament firmament = Firmament.fromWorld(this.getWorld());
                if(firmament != null) {
                    int x = this.getBlockX();
                    int z = this.getBlockZ();
                    int n = 5;
                    for(int i = -n; i <= n; i++) {
                        for(int j = -n; j <= n; j++) {
                            if(i*i + j*j > n*n) continue;
                            firmament.setDisplacement(x+i*FirmamentSubRegion.TILE_SIZE, z+j*FirmamentSubRegion.TILE_SIZE, 0);
                            firmament.setVelocity(x+i*FirmamentSubRegion.TILE_SIZE, z+j*FirmamentSubRegion.TILE_SIZE, 0);
                            firmament.setDrip(x+i*FirmamentSubRegion.TILE_SIZE, z+j*FirmamentSubRegion.TILE_SIZE, 0);
                            firmament.setDamage(x+i*FirmamentSubRegion.TILE_SIZE, z+j*FirmamentSubRegion.TILE_SIZE, 0);
                        }
                    }
                }
            }

            this.discard();
        }
    }

    @Override
    protected ItemStack getItem() {
        return OperationStarcleaveItems.FIRMAMENT_REJUVENATOR.getDefaultStack();
    }
}

package phanastrae.operation_starcleave.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;
import phanastrae.operation_starcleave.world.firmament.Firmament;
import phanastrae.operation_starcleave.world.firmament.FirmamentSubRegion;

public class FirmamentRejuvenatorEntity extends ThrowableItemProjectile {

    public static final int MAX_AGE = 140;

    public FirmamentRejuvenatorEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level world) {
        super(entityType, world);
    }

    public FirmamentRejuvenatorEntity(Level world, LivingEntity owner) {
        super(OperationStarcleaveEntityTypes.FIRMAMENT_REJUVENATOR, owner, world);
    }

    public FirmamentRejuvenatorEntity(Level world, double x, double y, double z) {
        super(OperationStarcleaveEntityTypes.FIRMAMENT_REJUVENATOR, x, y, z, world);
    }

    @Override
    public void tick() {
        Level world = this.level();
        if(world != null && world.isClientSide) {
            Vec3 vel = this.getDeltaMovement();
            RandomSource random = this.random;
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
            if(this.tickCount > MAX_AGE) {
                this.spawnAtLocation(this.getItem());
                this.discard();
            } else {
                float firmHeight = this.level().getMaxBuildHeight() + 16;
                double dy = this.position().y - firmHeight;
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
    protected double getDefaultGravity() {
        return 0.01F;
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        this.spawnAtLocation(this.getItem());
        this.discard();
    }

    public void explode() {
        if (!this.level().isClientSide) {
            this.level().explode(this.getOwner(), this.getX(), this.getY(), this.getZ(), 4, Level.ExplosionInteraction.NONE);
            if(this.level() instanceof ServerLevel serverWorld) {
                Vec3 pos = this.position();
                serverWorld.sendParticles(OperationStarcleaveParticleTypes.FIRMAMENT_GLIMMER, pos.x(), pos.y(), pos.z(), 400, 2, 1, 2, 0.01);
            }

            float firmHeight = this.level().getMaxBuildHeight() + 16;
            double dy = this.position().y - firmHeight;
            if(dy*dy < 1) {
                Firmament firmament = Firmament.fromWorld(this.level());
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
    public ItemStack getItem() {
        return OperationStarcleaveItems.FIRMAMENT_REJUVENATOR.getDefaultInstance();
    }
}

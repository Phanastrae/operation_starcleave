package phanastrae.operation_starcleave.entity.projectile;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;
import phanastrae.operation_starcleave.network.packet.StarbleachedPearlLaunchPayload;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;
import phanastrae.operation_starcleave.services.XPlatInterface;

import java.util.function.Predicate;

public class StarbleachedPearlEntity extends ThrowableItemProjectile {
    public StarbleachedPearlEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level world) {
        super(entityType, world);
    }

    public StarbleachedPearlEntity(Level world, LivingEntity owner) {
        super(OperationStarcleaveEntityTypes.STARBLEACHED_PEARL, owner, world);
    }

    public StarbleachedPearlEntity(Level world, double x, double y, double z) {
        super(OperationStarcleaveEntityTypes.STARBLEACHED_PEARL, x, y, z, world);
    }

    @Override
    protected Item getDefaultItem() {
        return OperationStarcleaveItems.STARBLEACHED_PEARL;
    }

    @Override
    public void handleEntityEvent(byte status) {
        if (status == EntityEvent.DEATH) {
            for(int i = 0; i < 1000; ++i) {
                this.level()
                        .addParticle(
                                OperationStarcleaveParticleTypes.FIRMAMENT_GLIMMER,
                                this.getX(), this.getY(), this.getZ(),
                                this.random.nextGaussian() * 0.12, this.random.nextGaussian() * 0.12, this.random.nextGaussian() * 0.12);
            }
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);

        Vec3 pos = hitResult.getLocation();
        if(hitResult.getType() == HitResult.Type.ENTITY) {
            // hitresult position is at hit entity's feet, offset slightly to avoid launching hit entity directly upwards
            pos = pos.add(this.position().subtract(pos).scale(0.3));
        }

        if(!this.level().isClientSide && !this.isRemoved()) {
            repel(pos, 8, 1.5f, level(), this, 0.6f);
            this.level().broadcastEntityEvent(this, EntityEvent.DEATH);
            this.discard();
        }
    }

    @Override
    public void tick() {
        Level world = this.level();
        if(world != null && world.isClientSide) {
            Vec3 vel = this.getDeltaMovement();
            RandomSource random = this.random;
            for(int i = 0; i < 18; i++) {
                world.addParticle(OperationStarcleaveParticleTypes.FIRMAMENT_GLIMMER,
                        this.getX(), this.getY(), this.getZ(),
                        vel.x * -0.2 + random.nextFloat() * 0.06 - 0.03, vel.y * -0.2 + random.nextFloat() * 0.06 - 0.03, vel.z * -0.2 + random.nextFloat() * 0.06 - 0.03);
            }
        }
        super.tick();
    }

    public static void repel(Vec3 pos, float radius, float maxAddedSpeed, Level world, @Nullable Entity entity, float audioMultiplier) {
        if(world instanceof ServerLevel serverWorld) {
            // send packets to nearby players
            serverWorld.players().forEach(playerEntity -> {
                Entity e = playerEntity;
                Entity vehicle = playerEntity.getControlledVehicle();
                if(vehicle != null) {
                    e = vehicle;
                }
                double distance = e.position().subtract(pos).length();

                // expand radius to be safe
                float radiusBig = radius * 1.25f + 4;
                if(distance < radiusBig) {
                    XPlatInterface.INSTANCE.sendPayload(playerEntity, new StarbleachedPearlLaunchPayload(pos, radius, maxAddedSpeed, entity != null, entity == null ? -1 : entity.getId()));
                }
            });
        }

        doRepulsion(pos, radius, maxAddedSpeed, world, entity);

        if(!world.isClientSide) {
            RandomSource random = world.getRandom();
            world.playSound(null, pos.x(), pos.y(), pos.z(), SoundEvents.TRIDENT_THUNDER, SoundSource.PLAYERS, audioMultiplier, 0.9f + random.nextFloat() * 0.3f);
        }
    }

    public static void doRepulsion(Vec3 pos, float radius, float maxAddedSpeed, Level world, @Nullable Entity entity) {
        StarbleachedPearlEntity.doRepulsion(pos, radius, maxAddedSpeed, world, entity, EntitySelector.NO_SPECTATORS);
    }

    public static void doRepulsion(Vec3 pos, float radius, float maxAddedSpeed, Level world, @Nullable Entity entity, Predicate<? super Entity> predicate) {
        for (Entity e : world.getEntities(entity, AABB.unitCubeFromLowerCorner(pos).inflate(radius), predicate)) {

            boolean doRepel;
            // update players server side (and sync to client), update anything else on logical side
            if(e instanceof Player) {
                doRepel = !world.isClientSide;
            } else {
                doRepel = e.isControlledByLocalInstance();
            }
            if(doRepel) {
                repelEntity(e, pos, radius, maxAddedSpeed);
            }
        }
    }

    public static void repelEntity(Entity e, Vec3 pos, float radius, float maxAddedSpeed) {
        if(e instanceof Player player && player.getAbilities().flying) {
            return;
        }

        Vec3 pos2 = e.position().add(0, e.getBbHeight() / 2, 0);
        double dist = pos2.distanceTo(pos);
        if (dist > radius) return;
        double f = (radius - dist) / radius;
        f = 1 - (1 - f) * (1 - f);
        Vec3 offset = pos2.subtract(pos).normalize();

        e.push(offset.scale(f * maxAddedSpeed));
        e.hurtMarked = true;
        e.fallDistance = -5;
    }
}

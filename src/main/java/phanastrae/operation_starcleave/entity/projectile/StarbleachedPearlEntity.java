package phanastrae.operation_starcleave.entity.projectile;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;
import phanastrae.operation_starcleave.network.packet.s2c.StarbleachedPearlLaunchPacketS2C;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;

public class StarbleachedPearlEntity extends ThrownItemEntity {
    public StarbleachedPearlEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public StarbleachedPearlEntity(World world, LivingEntity owner) {
        super(OperationStarcleaveEntityTypes.STARBLEACHED_PEARL, owner, world);
    }

    public StarbleachedPearlEntity(World world, double x, double y, double z) {
        super(OperationStarcleaveEntityTypes.STARBLEACHED_PEARL, x, y, z, world);
    }

    @Override
    protected Item getDefaultItem() {
        return OperationStarcleaveItems.STARBLEACHED_PEARL;
    }

    @Override
    public void handleStatus(byte status) {
        if (status == EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES) {
            for(int i = 0; i < 1000; ++i) {
                this.getWorld()
                        .addParticle(
                                OperationStarcleaveParticleTypes.FIRMAMENT_GLIMMER,
                                this.getX(), this.getY(), this.getZ(),
                                this.random.nextGaussian() * 0.12, this.random.nextGaussian() * 0.12, this.random.nextGaussian() * 0.12);
            }
        }
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);

        Vec3d pos = hitResult.getPos();
        if(hitResult.getType() == HitResult.Type.ENTITY) {
            // hitresult position is at hit entity's feet, offset slightly to avoid launching hit entity directly upwards
            pos = pos.add(this.getPos().subtract(pos).multiply(0.3));
        }

        if(!this.getWorld().isClient && !this.isRemoved()) {
            repel(pos, 8, 1.5f, getWorld(), this, 0.6f);
            this.getWorld().sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES);
            this.discard();
        }
    }

    @Override
    public void tick() {
        World world = this.getWorld();
        if(world != null && world.isClient) {
            Vec3d vel = this.getVelocity();
            Random random = this.random;
            for(int i = 0; i < 18; i++) {
                world.addParticle(OperationStarcleaveParticleTypes.FIRMAMENT_GLIMMER,
                        this.getX(), this.getY(), this.getZ(),
                        vel.x * -0.2 + random.nextFloat() * 0.06 - 0.03, vel.y * -0.2 + random.nextFloat() * 0.06 - 0.03, vel.z * -0.2 + random.nextFloat() * 0.06 - 0.03);
            }
        }
        super.tick();
    }

    public static void repel(Vec3d pos, float radius, float maxAddedSpeed, World world, @Nullable Entity entity, float audioMultiplier) {
        if(world instanceof ServerWorld serverWorld) {
            // send packets to nearby players
            serverWorld.getPlayers().forEach(playerEntity -> {
                Entity e = playerEntity;
                Entity vehicle = playerEntity.getControllingVehicle();
                if(vehicle != null) {
                    e = vehicle;
                }
                double distance = e.getPos().subtract(pos).length();

                // expand radius to be safe
                float radiusBig = radius * 1.25f + 4;
                if(distance < radiusBig) {
                    ServerPlayNetworking.send(playerEntity, new StarbleachedPearlLaunchPacketS2C(pos, radius, maxAddedSpeed, entity));
                }
            });
        }

        doRepulsion(pos, radius, maxAddedSpeed, world, entity);

        if(!world.isClient) {
            Random random = world.getRandom();
            world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_TRIDENT_THUNDER, SoundCategory.PLAYERS, audioMultiplier, 0.9f + random.nextFloat() * 0.3f);
        }
    }

    public static void doRepulsion(Vec3d pos, float radius, float maxAddedSpeed, World world, @Nullable Entity entity) {
        for (Entity e : world.getOtherEntities(entity, Box.from(pos).expand(radius))) {
            boolean doRepel;
            // update players server side (and sync to client), update anything else on logical side
            if(e instanceof PlayerEntity) {
                doRepel = !world.isClient;
            } else {
                doRepel = e.isLogicalSideForUpdatingMovement();
            }
            if(doRepel) {
                repelEntity(e, pos, radius, maxAddedSpeed);
            }
        }
    }

    public static void repelEntity(Entity e, Vec3d pos, float radius, float maxAddedSpeed) {
        if(e instanceof PlayerEntity player && player.getAbilities().flying) {
            return;
        }

        Vec3d pos2 = e.getPos().add(0, e.getHeight() / 2, 0);
        double dist = pos2.distanceTo(pos);
        if (dist > radius) return;
        double f = (radius - dist) / radius;
        f = 1 - (1 - f) * (1 - f);
        Vec3d offset = pos2.subtract(pos).normalize();

        e.addVelocity(offset.multiply(f * maxAddedSpeed));
        e.velocityModified = true;
        e.fallDistance = -5;
    }
}

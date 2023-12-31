package phanastrae.operation_starcleave.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;

public class StarbleachedPearlEntity extends ThrownItemEntity {
    public StarbleachedPearlEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public StarbleachedPearlEntity(World world, LivingEntity owner) {
        super(OperationStarcleaveEntityTypes.STARBLEACHED_PEARL, owner, world);
    }

    @Override
    protected Item getDefaultItem() {
        return OperationStarcleaveItems.STARBLEACHED_PEARL;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);

        if(this.getWorld().isClient) {
            for(int i = 0; i < 1000; ++i) {
                this.getWorld()
                        .addParticle(
                                OperationStarcleaveParticleTypes.FIRMAMENT_GLIMMER,
                                hitResult.getPos().x, hitResult.getPos().y, hitResult.getPos().z,
                                this.random.nextGaussian() * 0.12, this.random.nextGaussian() * 0.12, this.random.nextGaussian() * 0.12);
            }
        }

        if(!this.getWorld().isClient && !this.isRemoved()) {
            Vec3d pos = hitResult.getPos();
            repel(pos, 8, 1.5f, getWorld(), this);
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

    public static void repel(Vec3d pos, float r, float m, World world, @Nullable Entity entity) {
        for (Entity e : world.getOtherEntities(entity, Box.from(pos).expand(r))) {
            if(e instanceof PlayerEntity player && player.getAbilities().flying) {
                continue;
            }
            Vec3d pos2 = e.getPos().add(0, e.getHeight() / 2, 0);
            double dist = pos2.distanceTo(pos);
            if (dist > r) continue;
            double f = (r - dist) / r;
            f = 1 - (1-f)*(1-f);
            Vec3d offset = pos2.subtract(pos).normalize();

            e.addVelocity(offset.multiply(f * m));
            e.velocityModified = true;
            e.fallDistance = -5;
        }

        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_TRIDENT_THUNDER, SoundCategory.PLAYERS);
    }
}

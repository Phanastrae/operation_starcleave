package phanastrae.operation_starcleave.entity.projectile;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.entity.OperationStarcleaveDamageTypes;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;
import phanastrae.operation_starcleave.entity.mob.HammertailGolemEntity;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;
import phanastrae.operation_starcleave.sound.OperationStarcleaveSoundEvents;

public class BismuthBlastEntity extends AbstractHurtingProjectile {
    public static final String KEY_AGE = "age";
    public static final String KEY_CAN_HIT_HAMMERTAILS = "can_hit_hammertails";

    private int age = 0;
    private boolean canHitHammertails = true;
    private int soundCooldown = -1;

    public BismuthBlastEntity(EntityType<? extends BismuthBlastEntity> entityType, Level level) {
        super(entityType, level);
    }

    public BismuthBlastEntity(Level level, LivingEntity owner) {
        super(OperationStarcleaveEntityTypes.BISMUTH_BLAST, owner, Vec3.ZERO, level);
        this.setPos(owner.getEyePosition());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt(KEY_AGE, this.age);
        compound.putBoolean(KEY_CAN_HIT_HAMMERTAILS, this.canHitHammertails);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains(KEY_AGE, Tag.TAG_INT)) {
            this.age = compound.getInt(KEY_AGE);
        }
        if (compound.contains(KEY_CAN_HIT_HAMMERTAILS, Tag.TAG_BYTE)) {
            this.canHitHammertails = compound.getBoolean(KEY_CAN_HIT_HAMMERTAILS);
        }
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Nullable
    @Override
    protected ParticleOptions getTrailParticle() {
        return null;
    }

    @Override
    public void tick() {
        super.tick();
        this.updateRotation();

        this.age++;
        if (!this.level().isClientSide()) {
            if (this.age >= 40) {
                this.fizzleOut(9, this.position().add(0, this.getBbHeight() / 2, 0));
            }
        } else {
            this.soundCooldown--;
            if (this.soundCooldown == 0) {
                this.level().playLocalSound(
                        this.getX(), this.getY(), this.getZ(),
                        OperationStarcleaveSoundEvents.BISMUTH_BLAST_TRAVEL, this.getSoundSource(),
                        1.0F, 0.5F + 1.5F * random.nextFloat(),
                        false
                );
            }

            if (this.soundCooldown <= 0) {
                this.soundCooldown = this.random.nextIntBetweenInclusive(5, 14);
            }
        }
    }

    public void fizzleOut(int particleCount, Vec3 position) {
        Level level = this.level();
        level.playSound(null,
                position.x, position.y, position.z,
                OperationStarcleaveSoundEvents.BISMUTH_BLAST_FIZZ, SoundSource.NEUTRAL,
                0.3F, 1.5F + 0.5F * random.nextFloat()
        );
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    OperationStarcleaveParticleTypes.GLIMMER_SMOKE,
                    position.x,
                    position.y,
                    position.z,
                    particleCount,
                    0.05,
                    0.05,
                    0.05,
                    0.01
            );
        }
        this.discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (this.level() instanceof ServerLevel serverLevel) {
            DamageSource damageSource = OperationStarcleaveDamageTypes.bismuthBlast(serverLevel, this, this.getOwner());
            Entity target = result.getEntity();

            if (target.hurt(damageSource, 2.0F)) {
                EnchantmentHelper.doPostAttackEffects(serverLevel, target, damageSource);
            }
        }
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        if (!this.canHitHammertails && target instanceof HammertailGolemEntity) {
            return false;
        } else {
            return super.canHitEntity(target);
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            if (result.getType() == HitResult.Type.ENTITY) {
                this.discard();
            } else {
                this.fizzleOut(3, result.getLocation());
            }
        }
    }

    public int getAge() {
        return this.age;
    }

    public void setCanHitHammertails(boolean canHitHammertails) {
        this.canHitHammertails = canHitHammertails;
    }
}

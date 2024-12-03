package phanastrae.operation_starcleave.entity.mob;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.entity.projectile.SplashStarbleachEntity;
import phanastrae.operation_starcleave.entity.projectile.StarbleachedPearlEntity;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;

import java.util.EnumSet;

public class SubcaelicTorpedoEntity extends AbstractSubcaelicEntity {
    private static final EntityDataAccessor<Integer> FUSE_SPEED = SynchedEntityData.defineId(SubcaelicTorpedoEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> PRIMED = SynchedEntityData.defineId(SubcaelicTorpedoEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> SPEED_MODIFIER = SynchedEntityData.defineId(SubcaelicTorpedoEntity.class, EntityDataSerializers.FLOAT);

    private int lastFuseTime;
    private int currentFuseTime;
    private int fuseTime = 140;

    @Nullable
    public SubcaelicDuxEntity dux;
    boolean inGroup = false;

    public SubcaelicTorpedoEntity(EntityType<? extends SubcaelicTorpedoEntity> entityType, Level world) {
        super(entityType, world);
        this.xpReward = Enemy.XP_REWARD_NONE;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 4.0)
                .add(Attributes.MOVEMENT_SPEED, 0.7)
                .add(Attributes.FOLLOW_RANGE, 192.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(FUSE_SPEED, -1);
        builder.define(PRIMED, false);
        builder.define(SPEED_MODIFIER, 1F);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SubcaelicTorpedoEntity.ChaseTarget(this));
        this.goalSelector.addGoal(2, new SubcaelicTorpedoEntity.FollowDuxGoal(this));
        this.goalSelector.addGoal(3, new AbstractSubcaelicEntity.SwimWanderGoal(this));
        this.targetSelector.addGoal(1, new ChooseNewDuxGoal(this));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);

        nbt.putShort("Fuse", (short)this.fuseTime);
        if(this.currentFuseTime > 0) {
            nbt.putShort("CurrentFuse", (short) this.currentFuseTime);
        }
        nbt.putFloat("SpeedModifier", this.getSpeedModifier());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);

        if (nbt.contains("Fuse", Tag.TAG_ANY_NUMERIC)) {
            this.fuseTime = nbt.getShort("Fuse");
        }
        if (nbt.contains("CurrentFuse", Tag.TAG_ANY_NUMERIC)) {
            this.fuseTime = nbt.getShort("CurrentFuse");
        }
        if (nbt.contains("SpeedModifier", Tag.TAG_FLOAT)) {
            this.setSpeedModifier(nbt.getFloat("SpeedModifier"));
        }
    }

    @Override
    public void tick() {
        if(!this.level().isClientSide()) {
            if(this.isPrimed() && this.level().getDifficulty().equals(Difficulty.PEACEFUL)) {
                this.unprimeAndUntarget();
                if(this.dux != null) {
                    if(!this.inGroup && this.dux.canAdoptTorpedo()) {
                        this.joinGroupOf(this.dux);
                    } else {
                        this.dux = null;
                    }
                }
            }

            if(this.isPrimed() && (this.horizontalCollision || this.verticalCollision)) {
                this.setFuseSpeedToAtLeast(5);
            }
        }

        this.lastFuseTime = this.currentFuseTime;

        int fuseSpeed = this.getFuseSpeed();
        if (this.isAlive() && fuseSpeed > 0 && this.currentFuseTime == 0) {
            this.playSound(SoundEvents.CREEPER_PRIMED, 1.0F, 0.5F);
            this.gameEvent(GameEvent.PRIME_FUSE);
        }

        this.currentFuseTime += fuseSpeed;
        if (this.currentFuseTime < 0) {
            this.currentFuseTime = 0;
        }

        if (this.currentFuseTime >= this.fuseTime) {
            this.currentFuseTime = this.fuseTime;
            if(this.isAlive()) {
                this.explode();
            }
        }

        if(this.isAlive()) {
            int lastBeep = this.lastFuseTime / 10;
            int currentBeep = this.currentFuseTime / 10;
            if(currentBeep > lastBeep) {
                this.playSound(SoundEvents.AMETHYST_BLOCK_STEP, 1.8F, 1F + 0.8F * this.getClientFuseTime(0));
            }
        }

        super.tick();
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if(!this.isAlive()) {
            this.setDeltaMovement(this.getDeltaMovement().x * 0.98, this.getDeltaMovement().y * 0.98 - 0.03, this.getDeltaMovement().z * 0.98);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if(this.isPrimed()) {
            if(!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                this.setFuseSpeedToAtLeast(60);
                return true;
            }
        }
        return super.hurt(source, amount);
    }

    @Override
    public void die(DamageSource damageSource) {
        this.setFuseSpeed(-10);
        super.die(damageSource);
    }

    @Override
    public void remove(RemovalReason reason) {
        this.leaveGroup(true);
        super.remove(reason);
    }

    @Override
    public void handleEntityEvent(byte status) {
        if (status == EntityEvent.FIREWORKS_EXPLODE) {
            for(int i = 0; i < 1000; ++i) {
                this.level()
                        .addParticle(
                                OperationStarcleaveParticleTypes.GLIMMER_SMOKE,
                                this.getX(), this.getY(), this.getZ(),
                                this.random.nextGaussian() * 0.12, this.random.nextGaussian() * 0.12, this.random.nextGaussian() * 0.12);
            }
        }
    }

    @Override
    public double getTurnFactor() {
        return this.isPrimed() ? 0.17 : 0.09;
    }

    public float getClientFuseTime(float timeDelta) {
        return Mth.lerp(timeDelta, (float)this.lastFuseTime, (float)this.currentFuseTime) / (float)(this.fuseTime - 2);
    }

    public boolean isPrimed() {
        return this.entityData.get(PRIMED);
    }

    public void setPrimed(boolean primed) {
        this.entityData.set(PRIMED, primed);
    }

    public int getFuseSpeed() {
        return this.entityData.get(FUSE_SPEED);
    }

    public void setFuseSpeed(int fuseSpeed) {
        this.entityData.set(FUSE_SPEED, fuseSpeed);
    }

    public float getSpeedModifier() {
        return this.entityData.get(SPEED_MODIFIER);
    }

    public void setSpeedModifier(float value) {
        this.entityData.set(SPEED_MODIFIER, value);
    }

    public void setFuseSpeedToAtLeast(int fuseSpeed) {
        if(fuseSpeed > this.getFuseSpeed()) {
            this.setFuseSpeed(fuseSpeed);
        }
    }

    public void explode() {
        if (!this.level().isClientSide) {
            this.dead = true;
            LivingEntity attacker = (this.dux != null && !this.dux.isRemoved()) ? this.dux : this;
            this.level().explode(attacker, this.getX(), this.getY()+this.getBbHeight()/2, this.getZ(), 1.5f, Level.ExplosionInteraction.NONE);
            StarbleachedPearlEntity.doRepulsion(this.position(), 4f, 2.0f, this.level(), this, EntitySelector.NO_SPECTATORS.and((entity -> !(entity instanceof AbstractSubcaelicEntity))));
            if(this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && this.getTarget() instanceof Player) {
                SplashStarbleachEntity.starbleach(blockPosition(), this.level());
            }
            this.level().broadcastEntityEvent(this, EntityEvent.FIREWORKS_EXPLODE);
            this.discard();
        }
    }

    public void joinGroupOf(SubcaelicDuxEntity dux) {
        if(this.inGroup) {
            this.leaveGroup(true);
        }

        dux.addTorpedo(this);
        this.inGroup = true;
        this.dux = dux;
    }

    public void leaveGroup(boolean forgetDux) {
        if(this.dux != null) {
            this.dux.removeTorpedo(this);
        }
        this.inGroup = false;
        if(forgetDux) {
            this.dux = null;
        }
    }

    public void primeAndTarget(LivingEntity target, float speedModifier) {
        this.setPrimed(true);
        this.setFuseSpeed(1);
        this.setTarget(target);
        this.setSpeedModifier(speedModifier);
    }

    public void unprimeAndUntarget() {
        this.setPrimed(false);
        this.setFuseSpeed(-1);
        this.setTarget(null);
        this.setSpeedModifier(1F);
    }

    static class ChaseTarget extends Goal {
        private final SubcaelicTorpedoEntity torpedo;

        private long lastUpdateTime;

        public ChaseTarget(SubcaelicTorpedoEntity entity) {
            this.torpedo = entity;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if(!torpedo.isPrimed()) {
                return false;
            }
            long l = this.torpedo.level().getGameTime();
            if (l - this.lastUpdateTime < 20L) {
                return false;
            } else {
                this.lastUpdateTime = l;
                LivingEntity targetEntity = this.torpedo.getTarget();
                return targetEntity != null && targetEntity.isAlive();
            }
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = this.torpedo.getTarget();
            if (target == null) {
                return false;
            } else if (!target.isAlive()) {
                return false;
            } else {
                return !(target instanceof Player) || !target.isSpectator() && !((Player)target).isCreative();
            }
        }

        @Override
        public void start() {
            this.torpedo.setAggressive(true);
        }

        @Override
        public void stop() {
            LivingEntity target = this.torpedo.getTarget();
            if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target)) {
                this.torpedo.setTarget(null);
            }

            this.torpedo.setAggressive(false);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity target = this.torpedo.getTarget();
            if(target != null) {
                Vec3 offset = target.position().subtract(torpedo.position());

                double sqrDistance = offset.lengthSqr();
                double speed = 1.5 * (1.0 - Math.exp(-0.0002 * sqrDistance)) + this.torpedo.getSpeedModifier() * (1.0 - Math.exp(-0.1 * sqrDistance));
                this.torpedo.moveControl.setWantedPosition(target.getX(), target.getY(), target.getZ(), speed);

                int fuseSpeed = (int) (64.0 / Math.max(1.0, sqrDistance));
                this.torpedo.setFuseSpeedToAtLeast(fuseSpeed);
            }
        }
    }

    static class FollowDuxGoal extends Goal {

        private final SubcaelicTorpedoEntity torpedo;

        public FollowDuxGoal(SubcaelicTorpedoEntity entity) {
            this.torpedo = entity;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if(this.torpedo.isPrimed()) {
                return false;
            }

            return this.torpedo.inGroup && this.torpedo.dux != null;
        }

        @Override
        public void tick() {
            SubcaelicDuxEntity dux = this.torpedo.dux;
            if(dux == null) return;

            if(this.torpedo.getRandom().nextInt(reducedTickDelay(10)) == 0) {
                this.torpedo.moveControl.setWantedPosition(dux.getX(), dux.getY(), dux.getZ(), 1.4);
            }
        }
    }

    static class ChooseNewDuxGoal extends Goal {
        private static final double DUX_STOP_FOLLOW_RANGE = 256.0;
        private static final double DUX_START_FOLLOW_RANGE = 48.0;
        private static final TargetingConditions CLOSE_DUX_PREDICATE = TargetingConditions.forNonCombat().range(DUX_START_FOLLOW_RANGE);

        private final SubcaelicTorpedoEntity torpedo;

        private int searchCooldown = 0;

        public ChooseNewDuxGoal(SubcaelicTorpedoEntity entity) {
            this.torpedo = entity;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if(this.torpedo.isPrimed()) {
                return false;
            }

            if(this.searchCooldown > 0) {
                this.searchCooldown--;
                return false;
            }
            this.searchCooldown = 80;
            return true;
        }

        @Override
        public void tick() {
            if(this.torpedo.inGroup && this.torpedo.dux != null) {
                if(this.torpedo.dux.isRemoved() || this.torpedo.distanceToSqr(this.torpedo.dux) > DUX_STOP_FOLLOW_RANGE * DUX_STOP_FOLLOW_RANGE) {
                    this.torpedo.leaveGroup(true);
                } else {
                    return;
                }
            }

            SubcaelicDuxEntity closestDux = this.torpedo.level().getNearestEntity(SubcaelicDuxEntity.class, CLOSE_DUX_PREDICATE, this.torpedo,
                    this.torpedo.getX(), this.torpedo.getY(), this.torpedo.getZ(), this.torpedo.getBoundingBox().inflate(DUX_START_FOLLOW_RANGE));
            if(closestDux != null && closestDux.canAdoptTorpedo()) {
                this.torpedo.joinGroupOf(closestDux);
            }
        }
    }
}

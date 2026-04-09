package phanastrae.operation_starcleave.entity.mob;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.entity.OperationStarcleaveDamageTypes;
import phanastrae.operation_starcleave.entity.projectile.BismuthBlastEntity;
import phanastrae.operation_starcleave.sound.OperationStarcleaveSoundEvents;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class HammertailGolemEntity extends AbstractGolem implements RangedAttackMob {
    private static final float CRUSH_DAMAGE_PER_DISTANCE = 1.5F;
    private static final float MAX_CRUSH_DAMAGE = 30;

    private boolean pushable = false;
    private boolean yawNeedsSyncing = true;

    public HammertailGolemEntity(EntityType<? extends AbstractGolem> entityType, Level world) {
        super(entityType, world);
        this.lookControl = new HammertailLookControl();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 28.0)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.ATTACK_DAMAGE, 2.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0)
                .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new HammertailRangedAttackGoal(this, 7, 24.0F));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 12.0F));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, HammertailGolemEntity.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false, e -> !(e instanceof HammertailGolemEntity)));
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);

        float yaw = this.getYRot();

        if (spawnType == MobSpawnType.SPAWN_EGG) {
            // spawn slightly in midair when using spawn eggs
            this.setPos(this.position().add(0, 0.25, 0));

            // spawn pointed towards the nearest player (presumably the one who spawned it)
            Player player = level.getNearestPlayer(this, 8.0);
            if (player != null) {
                // spawn pointed towards player
                Vec3 offset = player.position().subtract(this.position());
                yaw = (float) -Math.toDegrees(Math.atan2(offset.x, offset.z));
            }
        }

        // clamp yaw to a multiple of 45 degrees
        yaw = Math.round(yaw / 45) * 45;
        this.setYRot(yaw);
        this.yHeadRot = yaw;
        this.yBodyRot = yaw;

        return data;
    }

    @Override
    protected int decreaseAirSupply(int currentAir) {
        return currentAir;
    }

    @Override
    protected double getDefaultGravity() {
        if (this.onGround() || this.getDeltaMovement().y() > 0.0) {
            return super.getDefaultGravity() / 1.5;
        } else {
            return super.getDefaultGravity() * 1.5;
        }
    }

    @Override
    public boolean isPushable() {
        return this.pushable;
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide()) {
            // push other hammertail golems
            List<HammertailGolemEntity> list = this.level().getEntitiesOfClass(HammertailGolemEntity.class, this.getBoundingBox(), (e) -> e != this);
            if (!list.isEmpty()) {
                for (HammertailGolemEntity other : list) {
                    this.pushable = true;
                    other.pushable = true;
                    this.doPush(other);
                    this.pushable = false;
                    other.pushable = false;
                }
            }

            if (!this.onGround() && this.getDeltaMovement().y < 0.0) {
                this.crushCollidingMobs(this.fallDistance);
            }
        }
    }

    public Predicate<Entity> doesNotShareLeashholderPredicate() {
        // return false if entity is the leashholder or shares this entity's leashholder
        Entity leashHolder = this.getLeashHolder();
        if (leashHolder == null) {
            return e -> true;
        } else {
            return e -> e != leashHolder && !(e instanceof Leashable leashable && leashable.getLeashHolder() == leashHolder);
        }
    }

    public void crushCollidingMobs(float fallDistance) {
        int damagingFallDistance = Mth.ceil(fallDistance - 2.0F);
        float damage = Math.min(Mth.floor(damagingFallDistance * this.CRUSH_DAMAGE_PER_DISTANCE), this.MAX_CRUSH_DAMAGE);
        Predicate<Entity> predicate = EntitySelector.NO_CREATIVE_OR_SPECTATOR
                .and(EntitySelector.LIVING_ENTITY_STILL_ALIVE)
                .and(this.doesNotShareLeashholderPredicate());
        if (damage > 0) {
            DamageSource damageSource = OperationStarcleaveDamageTypes.fallingMob(this.level(), this);
            this.level().getEntities(this, this.getBoundingBox(), predicate).forEach(entity -> entity.hurt(damageSource, damage));
        }
    }

    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        Vec3 lookAngle = this.calculateViewVector(this.getXRot(), this.yHeadRot);
        Vec3 targetOffset = target.position().add(0, target.getBbHeight() / 2, 0).subtract(this.getEyePosition());
        double dot = lookAngle.dot(targetOffset.normalize());

        if (dot > 0.85) {
            BismuthBlastEntity projectile = new BismuthBlastEntity(this.level(), this, Vec3.ZERO);
            projectile.setPos(this.getEyePosition());
            projectile.shoot(targetOffset.x, targetOffset.y, targetOffset.z, 0.5F, 7.0F);
            this.playSound(OperationStarcleaveSoundEvents.HAMMERTAIL_GOLEM_SHOOT, 1.0F, 1.2F + 0.8F * random.nextFloat());
            this.level().addFreshEntity(projectile);
        }
    }

    @Override
    protected float tickHeadTurn(float yRot, float animStep) {
        if (this.level().isClientSide() && this.yawNeedsSyncing) {
            // yaw is changed in finalizeSpawn, but only yaw (not body and head rot) is synced with client, so need to set them equal here
            this.yBodyRot = this.getYRot();
            this.yHeadRot = this.getYRot();
            this.yawNeedsSyncing = false;
        } else {
            this.yBodyRot = Mth.rotateIfNecessary(this.getYRot(), this.yBodyRot, 10);
        }
        return animStep;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypeTags.IS_PLAYER_ATTACK)) {
            // block low damage hits
            amount -= 1.0F;
        }

        if (super.hurt(source, amount) && this.onGround() && source.is(DamageTypeTags.IS_PLAYER_ATTACK)) {
            Entity entity = source.getEntity();
            if (entity != null) {
                Vec3 offset = this.position().subtract(entity.position());
                float targetYaw = (float) -Math.toDegrees(Math.atan2(offset.x, offset.z));

                float yaw = Mth.rotateIfNecessary(targetYaw, this.getYRot(), 90);
                yaw = Math.round(yaw / 45) * 45;

                this.setYRot(yaw);
                this.yBodyRot = yaw;
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void knockback(double strength, double x, double z) {
        if (strength > 0.0 && this.onGround()) {
            this.hasImpulse = true;
            Vec3 vec3 = this.getDeltaMovement();
            this.setDeltaMovement(vec3.x, Math.min(0.4, vec3.y / 2.0 + strength), vec3.z);
        }
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return OperationStarcleaveSoundEvents.HAMMERTAIL_GOLEM_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return OperationStarcleaveSoundEvents.HAMMERTAIL_GOLEM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return OperationStarcleaveSoundEvents.HAMMERTAIL_GOLEM_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    @Override
    public Fallsounds getFallSounds() {
        return new LivingEntity.Fallsounds(OperationStarcleaveSoundEvents.HAMMERTAIL_GOLEM_SMALL_FALL, OperationStarcleaveSoundEvents.HAMMERTAIL_GOLEM_BIG_FALL);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        if (this.isAlive()) {
            this.playSound(fallDistance * multiplier > 7 ? this.getFallSounds().big() : this.getFallSounds().small(), 1.0F, 1.0F);
            this.playBlockFallSound();

            this.crushCollidingMobs(fallDistance);

            // push any hit hammertail golems in random directions to make sure they spread out
            Predicate<Entity> predicate = EntitySelector.LIVING_ENTITY_STILL_ALIVE.and(e -> e != this);
            this.level().getEntitiesOfClass(HammertailGolemEntity.class, this.getBoundingBox(), predicate).forEach(entity -> {
                        float t = this.random.nextFloat() * (float) Math.TAU;
                        float dx = Mth.cos(t) * 0.2F;
                        float dz = Mth.sin(t) * 0.2F;
                        entity.addDeltaMovement(new Vec3(dx, 0F, dz));
                    }
            );
        }
        return false;
    }

    @Override
    public int getMaxHeadYRot() {
        return 80;
    }

    @Override
    protected boolean shouldStayCloseToLeashHolder() {
        // return false to prevent triggering pathfinding
        return false;
    }

    public class HammertailLookControl extends LookControl {
        public HammertailLookControl() {
            super(HammertailGolemEntity.this);
        }

        @Override
        public void tick() {
            float oldXRot = this.mob.getXRot();
            float oldHeadRot = this.mob.yHeadRot;
            super.tick();
            this.mob.setXRot(Mth.rotateIfNecessary(this.mob.getXRot(), oldXRot, 3));
            this.mob.yHeadRot = Mth.rotateIfNecessary(this.mob.yHeadRot, oldHeadRot, 3);
            this.mob.yHeadRot = Mth.rotateIfNecessary(this.mob.yHeadRot, this.mob.yBodyRot, (float) this.mob.getMaxHeadYRot());
        }
    }

    public static class HammertailRangedAttackGoal extends Goal {
        private final Mob mob;
        private final RangedAttackMob rangedAttackMob;
        private final int attackIntervalMin;
        private final int attackIntervalMax;
        private final float attackRadius;

        @Nullable
        private LivingEntity target;
        private int attackTime = -1;

        public HammertailRangedAttackGoal(RangedAttackMob rangedAttackMob, int attackInterval, float attackRadius) {
            this(rangedAttackMob, attackInterval, attackInterval, attackRadius);
        }

        public HammertailRangedAttackGoal(RangedAttackMob rangedAttackMob, int attackIntervalMin, int attackIntervalMax, float attackRadius) {
            if (!(rangedAttackMob instanceof Mob)) {
                throw new IllegalArgumentException("HammertailRangedAttackGoal requires Mob implements RangedAttackMob");
            } else {
                this.rangedAttackMob = rangedAttackMob;
                this.mob = (Mob) rangedAttackMob;
                this.attackIntervalMin = attackIntervalMin;
                this.attackIntervalMax = attackIntervalMax;
                this.attackRadius = attackRadius;
                this.setFlags(EnumSet.of(Goal.Flag.LOOK));
            }
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.mob.getTarget();
            if (target != null && target.isAlive()) {
                this.target = target;
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse();
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void stop() {
            this.target = null;
            this.attackTime = -1;
        }

        @Override
        public void tick() {
            this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);

            this.attackTime--;
            if (this.attackTime <= 0) {
                double distSqr = this.mob.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
                double dist = Math.sqrt(distSqr);
                double distFraction = dist / this.attackRadius;

                boolean hasLineOfSight = this.mob.getSensing().hasLineOfSight(this.target);

                if (this.attackTime == 0 && hasLineOfSight) {
                    float velocity = Mth.clamp((float) distFraction, 0.1F, 1.0F);
                    this.rangedAttackMob.performRangedAttack(this.target, velocity);
                }

                this.attackTime = Mth.floor(Mth.lerp(distFraction, this.attackIntervalMin, this.attackIntervalMax));
            }
        }
    }
}

package phanastrae.operation_starcleave.entity.mob;

import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.entity.projectile.SplashStarbleachEntity;
import phanastrae.operation_starcleave.entity.projectile.StarbleachedPearlEntity;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;

import java.util.EnumSet;

public class SubcaelicTorpedoEntity extends AbstractSubcaelicEntity {
    private static final TrackedData<Integer> FUSE_SPEED = DataTracker.registerData(SubcaelicTorpedoEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> PRIMED = DataTracker.registerData(SubcaelicTorpedoEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Float> SPEED_MODIFIER = DataTracker.registerData(SubcaelicTorpedoEntity.class, TrackedDataHandlerRegistry.FLOAT);

    private int lastFuseTime;
    private int currentFuseTime;
    private int fuseTime = 140;

    @Nullable
    public SubcaelicDuxEntity dux;
    boolean inGroup = false;

    public SubcaelicTorpedoEntity(EntityType<? extends SubcaelicTorpedoEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = Monster.ZERO_XP;
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 4.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.7)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 192.0);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(FUSE_SPEED, -1);
        builder.add(PRIMED, false);
        builder.add(SPEED_MODIFIER, 1F);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SubcaelicTorpedoEntity.ChaseTarget(this));
        this.goalSelector.add(2, new SubcaelicTorpedoEntity.FollowDuxGoal(this));
        this.goalSelector.add(3, new AbstractSubcaelicEntity.SwimWanderGoal(this));
        this.targetSelector.add(1, new ChooseNewDuxGoal(this));
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);

        nbt.putShort("Fuse", (short)this.fuseTime);
        if(this.currentFuseTime > 0) {
            nbt.putShort("CurrentFuse", (short) this.currentFuseTime);
        }
        nbt.putFloat("SpeedModifier", this.getSpeedModifier());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        if (nbt.contains("Fuse", NbtElement.NUMBER_TYPE)) {
            this.fuseTime = nbt.getShort("Fuse");
        }
        if (nbt.contains("CurrentFuse", NbtElement.NUMBER_TYPE)) {
            this.fuseTime = nbt.getShort("CurrentFuse");
        }
        if (nbt.contains("SpeedModifier", NbtElement.FLOAT_TYPE)) {
            this.setSpeedModifier(nbt.getFloat("SpeedModifier"));
        }
    }

    @Override
    public void tick() {
        if(!this.getWorld().isClient()) {
            if(this.isPrimed() && this.getWorld().getDifficulty().equals(Difficulty.PEACEFUL)) {
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
            this.playSound(SoundEvents.ENTITY_CREEPER_PRIMED, 1.0F, 0.5F);
            this.emitGameEvent(GameEvent.PRIME_FUSE);
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
                this.playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_STEP, 1.8F, 1F + 0.8F * this.getClientFuseTime(0));
            }
        }

        super.tick();
    }

    @Override
    public void tickMovement() {
        super.tickMovement();

        if(!this.isAlive()) {
            this.setVelocity(this.getVelocity().x * 0.98, this.getVelocity().y * 0.98 - 0.03, this.getVelocity().z * 0.98);
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if(this.isPrimed()) {
            if(!source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                this.setFuseSpeedToAtLeast(60);
                return true;
            }
        }
        return super.damage(source, amount);
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        this.setFuseSpeed(-10);
        super.onDeath(damageSource);
    }

    @Override
    public void remove(RemovalReason reason) {
        this.leaveGroup(true);
        super.remove(reason);
    }

    @Override
    public void handleStatus(byte status) {
        if (status == EntityStatuses.EXPLODE_FIREWORK_CLIENT) {
            for(int i = 0; i < 1000; ++i) {
                this.getWorld()
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
        return MathHelper.lerp(timeDelta, (float)this.lastFuseTime, (float)this.currentFuseTime) / (float)(this.fuseTime - 2);
    }

    public boolean isPrimed() {
        return this.dataTracker.get(PRIMED);
    }

    public void setPrimed(boolean primed) {
        this.dataTracker.set(PRIMED, primed);
    }

    public int getFuseSpeed() {
        return this.dataTracker.get(FUSE_SPEED);
    }

    public void setFuseSpeed(int fuseSpeed) {
        this.dataTracker.set(FUSE_SPEED, fuseSpeed);
    }

    public float getSpeedModifier() {
        return this.dataTracker.get(SPEED_MODIFIER);
    }

    public void setSpeedModifier(float value) {
        this.dataTracker.set(SPEED_MODIFIER, value);
    }

    public void setFuseSpeedToAtLeast(int fuseSpeed) {
        if(fuseSpeed > this.getFuseSpeed()) {
            this.setFuseSpeed(fuseSpeed);
        }
    }

    public void explode() {
        if (!this.getWorld().isClient) {
            this.dead = true;
            LivingEntity attacker = (this.dux != null && !this.dux.isRemoved()) ? this.dux : this;
            this.getWorld().createExplosion(attacker, this.getX(), this.getY()+this.getHeight()/2, this.getZ(), 1.5f, World.ExplosionSourceType.NONE);
            StarbleachedPearlEntity.doRepulsion(this.getPos(), 4f, 2.0f, this.getWorld(), this, EntityPredicates.EXCEPT_SPECTATOR.and((entity -> !(entity instanceof AbstractSubcaelicEntity))));
            if(this.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING) && this.getTarget() instanceof PlayerEntity) {
                SplashStarbleachEntity.starbleach(getBlockPos(), this.getWorld());
            }
            this.getWorld().sendEntityStatus(this, EntityStatuses.EXPLODE_FIREWORK_CLIENT);
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
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            if(!torpedo.isPrimed()) {
                return false;
            }
            long l = this.torpedo.getWorld().getTime();
            if (l - this.lastUpdateTime < 20L) {
                return false;
            } else {
                this.lastUpdateTime = l;
                LivingEntity targetEntity = this.torpedo.getTarget();
                return targetEntity != null && targetEntity.isAlive();
            }
        }

        @Override
        public boolean shouldContinue() {
            LivingEntity target = this.torpedo.getTarget();
            if (target == null) {
                return false;
            } else if (!target.isAlive()) {
                return false;
            } else {
                return !(target instanceof PlayerEntity) || !target.isSpectator() && !((PlayerEntity)target).isCreative();
            }
        }

        @Override
        public void start() {
            this.torpedo.setAttacking(true);
        }

        @Override
        public void stop() {
            LivingEntity target = this.torpedo.getTarget();
            if (!EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(target)) {
                this.torpedo.setTarget(null);
            }

            this.torpedo.setAttacking(false);
        }

        @Override
        public boolean shouldRunEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity target = this.torpedo.getTarget();
            if(target != null) {
                Vec3d offset = target.getPos().subtract(torpedo.getPos());

                double sqrDistance = offset.lengthSquared();
                double speed = 1.5 * (1.0 - Math.exp(-0.0002 * sqrDistance)) + this.torpedo.getSpeedModifier() * (1.0 - Math.exp(-0.1 * sqrDistance));
                this.torpedo.moveControl.moveTo(target.getX(), target.getY(), target.getZ(), speed);

                int fuseSpeed = (int) (64.0 / Math.max(1.0, sqrDistance));
                this.torpedo.setFuseSpeedToAtLeast(fuseSpeed);
            }
        }
    }

    static class FollowDuxGoal extends Goal {

        private final SubcaelicTorpedoEntity torpedo;

        public FollowDuxGoal(SubcaelicTorpedoEntity entity) {
            this.torpedo = entity;
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            if(this.torpedo.isPrimed()) {
                return false;
            }

            return this.torpedo.inGroup && this.torpedo.dux != null;
        }

        @Override
        public void tick() {
            SubcaelicDuxEntity dux = this.torpedo.dux;
            if(dux == null) return;

            if(this.torpedo.getRandom().nextInt(toGoalTicks(10)) == 0) {
                this.torpedo.moveControl.moveTo(dux.getX(), dux.getY(), dux.getZ(), 1.4);
            }
        }
    }

    static class ChooseNewDuxGoal extends Goal {
        private static final double DUX_STOP_FOLLOW_RANGE = 256.0;
        private static final double DUX_START_FOLLOW_RANGE = 48.0;
        private static final TargetPredicate CLOSE_DUX_PREDICATE = TargetPredicate.createNonAttackable().setBaseMaxDistance(DUX_START_FOLLOW_RANGE);

        private final SubcaelicTorpedoEntity torpedo;

        private int searchCooldown = 0;

        public ChooseNewDuxGoal(SubcaelicTorpedoEntity entity) {
            this.torpedo = entity;
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
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
                if(this.torpedo.dux.isRemoved() || this.torpedo.squaredDistanceTo(this.torpedo.dux) > DUX_STOP_FOLLOW_RANGE * DUX_STOP_FOLLOW_RANGE) {
                    this.torpedo.leaveGroup(true);
                } else {
                    return;
                }
            }

            SubcaelicDuxEntity closestDux = this.torpedo.getWorld().getClosestEntity(SubcaelicDuxEntity.class, CLOSE_DUX_PREDICATE, this.torpedo,
                    this.torpedo.getX(), this.torpedo.getY(), this.torpedo.getZ(), this.torpedo.getBoundingBox().expand(DUX_START_FOLLOW_RANGE));
            if(closestDux != null && closestDux.canAdoptTorpedo()) {
                this.torpedo.joinGroupOf(closestDux);
            }
        }
    }
}

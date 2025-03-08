package phanastrae.operation_starcleave.entity.mob;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.block.StellarFarmlandBlock;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;
import phanastrae.operation_starcleave.world.firmament.Firmament;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class SubcaelicDuxEntity extends AbstractSubcaelicEntity implements NeutralMob {
    private static final UniformInt ANGER_TIME_RANGE = TimeUtil.rangeOfSeconds(15, 25);
    public static final int MAX_ADOPTED_GROUP_SIZE = 14;
    public static final int MAX_NATURAL_GROUP_SIZE = 7;

    private static final EntityDataAccessor<Boolean> HOLLOW = SynchedEntityData.defineId(SubcaelicDuxEntity.class, EntityDataSerializers.BOOLEAN);

    public float haloAngle;
    public float prevHaloAngle;

    private final List<SubcaelicTorpedoEntity> torpedos = new ArrayList<>();

    public int ticksSinceDeath;

    private int angerTime;
    @Nullable
    private UUID angryAt;

    public SubcaelicDuxEntity(EntityType<? extends SubcaelicDuxEntity> entityType, Level world) {
        super(entityType, world);
        this.xpReward = Enemy.XP_REWARD_HUGE;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 100.0)
                .add(Attributes.ARMOR, 8.0)
                .add(Attributes.ARMOR_TOUGHNESS, 4.0)
                .add(Attributes.MOVEMENT_SPEED, 0.9)
                .add(Attributes.FOLLOW_RANGE, 192.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HOLLOW, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SubcaelicDuxEntity.AscendGoal(this));
        this.goalSelector.addGoal(2, new SubcaelicDuxEntity.SendTorpedoGoal(this));
        this.goalSelector.addGoal(3, new SubcaelicDuxEntity.SpawnTorpedoGoal(this));
        this.goalSelector.addGoal(4, new AbstractSubcaelicEntity.SwimNearTargetGoal(this));
        this.goalSelector.addGoal(5, new AbstractSubcaelicEntity.SwimWanderGoal(this));
        this.targetSelector.addGoal(1, new SubcaelicDuxEntity.TargetAttackerGoal(this, SubcaelicDuxEntity.class, SubcaelicTorpedoEntity.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, 30, true, false, entity -> entity.getType().is(EntityTypeTags.UNDEAD)));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        this.addPersistentAngerSaveData(nbt);

        nbt.putInt("DuxDeathTime", this.ticksSinceDeath);
        nbt.putBoolean("Hollow", this.isHollow());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.readPersistentAngerSaveData(this.level(), nbt);

        if(nbt.contains("DuxDeathTime", Tag.TAG_INT)) {
            this.ticksSinceDeath = nbt.getInt("DuxDeathTime");
        }
        if(nbt.contains("Hollow", Tag.TAG_BYTE)) {
            this.setHollow(nbt.getBoolean("Hollow"));
        }
    }

    @Override
    public void tick() {
        super.tick();
        if(this.getTarget() != null) {
            if(this.level().getDifficulty().equals(Difficulty.PEACEFUL)) {
                this.setTarget(null);
            } else if(this.getTarget().isRemoved() || this.getTarget().isDeadOrDying() || this.distanceTo(this.getTarget()) > 128) {
                this.setTarget(null);
            }
        }

        if(this.level().isClientSide) {
            Firmament firmament = Firmament.fromLevel(this.level());
            if(firmament != null) {
                boolean starlit = StellarFarmlandBlock.isStarlit(this.level(), this.blockPosition(), firmament);
                if(starlit) {
                    Vec3 spawnCenter = this.position().add(0.0, this.getBbHeight()*0.5, 0.0);
                    double f = this.getBbWidth();
                    int count = (int)(this.getBbWidth() * 8);
                    for(int i = 0; i < count; ++i) {
                        double x = spawnCenter.x + (this.random.nextDouble() - 0.5) * f;
                        double y = spawnCenter.y + (this.random.nextDouble() - 0.5) * this.getBbHeight();
                        double z = spawnCenter.z + (this.random.nextDouble() - 0.5) * f;
                        this.level().addParticle(OperationStarcleaveParticleTypes.FIRMAMENT_GLIMMER, x, y, z,
                                this.getDeltaMovement().x,
                                this.getDeltaMovement().y + 0.2,
                                this.getDeltaMovement().z);
                    }
                }
            }
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide) {
            this.updatePersistentAnger((ServerLevel)this.level(), false);
        } else {
            this.prevHaloAngle = this.haloAngle;

            float h = this.getHealthFraction();
            this.haloAngle += 2.5f + 40f * (1f - h * h);
        }

        if(this.isDeadOrDying()) {
            float d = this.getExplosionGlowProgress();

            this.rollAngle += (float) Math.PI * d * 5F;
            this.tentacleRollAngle += (float) Math.PI * d * 20F;
        }

        if(this.isHollow()) {
            this.tiltAngle += (180.0 - this.tiltAngle) * 0.05;

            this.setDeltaMovement(this.getDeltaMovement().x * 0.8, this.getDeltaMovement().y * 0.98 - 0.05, this.getDeltaMovement().z * 0.8);
        }
    }

    @Override
    protected void customServerAiStep() {
        Firmament firmament = Firmament.fromLevel(this.level());
        if(firmament != null) {
            boolean starlit = StellarFarmlandBlock.isStarlit(this.level(), this.blockPosition(), firmament);
            if(starlit) {
                if (this.tickCount % 20 == 0) {
                    this.heal(1.0F);
                }
            }
        }
    }

    @Override
    protected void tickDeath() {
        ++this.ticksSinceDeath;
        if(this.ticksSinceDeath >= 200 && !this.isHollow()) {
            this.becomeHollow();
        }

        if(this.level().isClientSide && this.getRandom().nextInt(8) == 0) {
            this.spawnSmokeBurst();
            this.level().playLocalSound(this, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.NEUTRAL, 6f, 0.7F + 0.5F * this.getRandom().nextFloat());
        }

        if(!this.isRemoved() && (this.ticksSinceDeath >= 400 || (this.ticksSinceDeath >= 240 && this.onGround()))) {
            boolean doMobLoot = this.level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT);

            if (this.level() instanceof ServerLevel serverWorld) {
                if (doMobLoot) {
                    ExperienceOrb.award(serverWorld, this.position(), this.getBaseExperienceReward());
                }

                this.gameEvent(GameEvent.ENTITY_DIE);

                serverWorld.explode(this, this.getX(), this.getY(), this.getZ(), 10, true, Level.ExplosionInteraction.MOB);
            }
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    protected boolean isImmobile() {
        return this.isHollow();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if(source.getEntity() instanceof AbstractSubcaelicEntity) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    protected float getSoundVolume() {
        return 5.0F;
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(ANGER_TIME_RANGE.sample(this.random));
    }

    @Override
    public void setRemainingPersistentAngerTime(int angerTime) {
        this.angerTime = angerTime;
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return this.angerTime;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID angryAt) {
        this.angryAt = angryAt;
    }

    @Nullable
    @Override
    public UUID getPersistentAngerTarget() {
        return this.angryAt;
    }

    @Override
    public boolean isAngryAtAllPlayers(Level world) {
        return this.isAngry() && this.getPersistentAngerTarget() == null;
    }

    @Override
    public void playerDied(Player player) {
        //
    }

    @Override
    public void remove(RemovalReason reason) {
        orphanTorpedos();
        super.remove(reason);
    }

    @Override
    public void kill() {
        this.remove(Entity.RemovalReason.KILLED);
        this.gameEvent(GameEvent.ENTITY_DIE);
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return super.getBoundingBoxForCulling().inflate(this.getBbWidth() * 0.6);
    }

    @Override
    protected boolean shouldTickAngles() {
        return !this.isHollow();
    }

    @Override
    public double getTurnFactor() {
        return this.getTarget() != null ? 0.12 : 0.04;
    }

    @Override
    public void spawnTrailParticles() {
        Vec3 spawnCenter = this.position().add(0.0, this.getBbHeight()*0.5, 0.0).subtract(this.getLookAngle().scale(this.getBbWidth()*0.5));
        double f = this.getBbWidth() * 0.2;
        int count = (int)(this.getBbWidth() * 4 * Math.min(1.0, 2.0 * this.getDeltaMovement().length()));
        for(int i = 0; i < count; ++i) {
            double x = spawnCenter.x + (this.random.nextDouble() - 0.5) * f;
            double y = spawnCenter.y + (this.random.nextDouble() - 0.5) * f;
            double z = spawnCenter.z + (this.random.nextDouble() - 0.5) * f;
            this.level().addParticle(OperationStarcleaveParticleTypes.LARGE_GLIMMER_SMOKE, true, x, y, z,
                    this.getDeltaMovement().x * - 1.5,
                    this.getDeltaMovement().y * - 1.5,
                    this.getDeltaMovement().z * - 1.5);
        }
    }

    public void spawnSmokeBurst() {
        RandomSource random = this.getRandom();
        float height = this.getBbHeight();
        float width = this.getBbWidth();

        Vec3 offset = new Vec3(random.nextFloat() - 0.5, random.nextFloat() - 0.5, random.nextFloat() - 0.5).normalize().multiply(width, height, width);

        for(int i = 0; i < 240; ++i) {
            double speed = 0.15F + random.nextFloat() * 0.15F;
            double variation = 0.3F;
            this.level().addParticle(OperationStarcleaveParticleTypes.LARGE_GLIMMER_SMOKE, false,
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    this.getDeltaMovement().x + offset.x * speed + random.nextGaussian() * variation,
                    this.getDeltaMovement().y + offset.y * speed + random.nextGaussian() * variation,
                    this.getDeltaMovement().z + offset.z * speed + random.nextGaussian() * variation);
        }
    }

    public boolean isHollow() {
        return this.entityData.get(HOLLOW);
    }

    private void setHollow(boolean value) {
        this.entityData.set(HOLLOW, value);
    }

    public void becomeHollow() {
        this.setHollow(true);
        this.ticksSinceDeath = 200;
    }

    public float getExplosionGlowProgress() {
        if(this.isDeadOrDying()) {
            return this.ticksSinceDeath / 400f;
        } else {
            return 0;
        }
    }

    protected void addTorpedo(SubcaelicTorpedoEntity torpedo) {
        this.torpedos.add(torpedo);
    }

    protected void removeTorpedo(SubcaelicTorpedoEntity torpedo) {
        this.torpedos.remove(torpedo);
    }

    public boolean canAdoptTorpedo() {
        return this.torpedos.size() < MAX_ADOPTED_GROUP_SIZE;
    }

    public void orphanTorpedos() {
        for(SubcaelicTorpedoEntity torpedo : this.torpedos) {
            torpedo.dux = null;
            torpedo.inGroup = false;
        }
        this.torpedos.clear();
    }

    public float getHealthFraction() {
        if(this.getMaxHealth() == 0) return 0f;
        float f = this.getHealth() / this.getMaxHealth();
        return Mth.clamp(f, 0f, 1f);
    }

    static class SendTorpedoGoal extends Goal {

        private final SubcaelicDuxEntity dux;

        private int cooldown;

        public SendTorpedoGoal(SubcaelicDuxEntity dux) {
            this.dux = dux;
        }

        @Override
        public boolean canUse() {
            if(this.dux.torpedos.isEmpty()) {
                return false;
            } else if(this.cooldown > 0) {
                this.cooldown--;
                return false;
            } else if(this.dux.level().getDifficulty().equals(Difficulty.PEACEFUL)) {
                return false;
            } else {
                this.cooldown = reducedTickDelay(8 + (int)(Math.sqrt(this.dux.getHealthFraction()) * (22 + this.dux.getRandom().nextInt(20))));

                LivingEntity target = this.dux.getTarget();
                if (target == null) {
                    return false;
                } else if (!target.isAlive()) {
                    return false;
                } else {
                    return !(target instanceof Player) || !target.isSpectator() && !((Player)target).isCreative();
                }
            }
        }

        @Override
        public void tick() {
            LivingEntity target = this.dux.getTarget();
            if(target != null) {
                // at high health chance to fire torpedo is 25%, at low health chance is 100%
                int torpedoReciprocalChance = 1 + (int)(Math.sqrt(this.dux.getHealthFraction()) * 3.5f);

                for(SubcaelicTorpedoEntity torpedo : this.dux.torpedos) {
                    if(!torpedo.isAlive()) continue;
                    if(torpedo.isPrimed()) continue;

                    if(torpedo.getRandom().nextInt(torpedoReciprocalChance) == 0) {
                        torpedo.primeAndTarget(target, 1F - 0.25F * this.dux.getHealthFraction());
                    }
                }
                this.dux.torpedos.removeIf((SubcaelicTorpedoEntity::isPrimed));
            }
        }
    }

    static class AscendGoal extends Goal {

        private final SubcaelicDuxEntity dux;

        public AscendGoal(SubcaelicDuxEntity dux) {
            this.dux = dux;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return this.dux.isDeadOrDying() && !this.dux.isHollow();
        }

        @Override
        public void tick() {
            Level world = this.dux.level();
            double x = this.dux.getX();
            double z = this.dux.getZ();
            LivingEntity target = this.dux.getTarget();
            if(target != null) {
                x = target.getX();
                z = target.getZ();
            }
            int topY = world.getHeight(Heightmap.Types.MOTION_BLOCKING, (int)x, (int)z);
            Firmament firmament = Firmament.fromLevel(world);
            int goalHeight = topY + 80;
            if(firmament != null) {
                int maxHeight = firmament.getY() - 12;
                if(goalHeight > maxHeight) {
                    goalHeight = maxHeight;
                }
            }

            this.dux.moveControl.setWantedPosition(x, goalHeight, z, 1.5);

            if(this.dux.getY() > goalHeight && this.dux.ticksSinceDeath > 60) {
                this.dux.becomeHollow();
            }
        }
    }

    static class SpawnTorpedoGoal extends Goal {
        private static final double TORPEDO_CHECK_RANGE = 256.0;
        private static final int MAX_NEARBY_TORPEDOES = 48;

        private final SubcaelicDuxEntity dux;

        private int cooldown;

        public SpawnTorpedoGoal(SubcaelicDuxEntity dux) {
            this.dux = dux;
        }

        @Override
        public boolean canUse() {
            if(this.dux.torpedos.size() >= SubcaelicDuxEntity.MAX_NATURAL_GROUP_SIZE) {
                return false;
            } else if(this.cooldown > 0 && (this.dux.isAlive() || this.cooldown <= reducedTickDelay(20))) {
                this.cooldown--;
                return false;
            } else {
                if(this.dux.isDeadOrDying()) {
                    this.cooldown = reducedTickDelay(20);
                } else {
                    this.cooldown = reducedTickDelay(6 + (int) (Math.sqrt(this.dux.getHealthFraction()) * (14 + this.dux.getRandom().nextInt(20))));
                }

                int nearbyTorpedoes = this.dux.level().getEntities(OperationStarcleaveEntityTypes.SUBCAELIC_TORPEDO, this.dux.getBoundingBox().inflate(TORPEDO_CHECK_RANGE), Entity::isAlive).size();
                return nearbyTorpedoes < MAX_NEARBY_TORPEDOES;
            }
        }

        @Override
        public void tick() {
            boolean deadDux = this.dux.isDeadOrDying();
            LivingEntity target = this.dux.getTarget();
            if(deadDux && target == null) return;

            Level world = this.dux.level();

            int torpedoCount = this.dux.isDeadOrDying() ? (this.dux.random.nextBoolean() ? 4 : 3) : 1;
            Vec3 rv = this.dux.getLookAngle().scale(this.dux.getBbWidth() * 0.5);
            for(int i = 0; i < torpedoCount; i++) {
                SubcaelicTorpedoEntity torpedo = OperationStarcleaveEntityTypes.SUBCAELIC_TORPEDO.create(world);
                if (torpedo != null) {
                    torpedo.moveTo(this.dux.getX() - rv.x, this.dux.getY() + this.dux.getBbHeight() / 2 - rv.y, this.dux.getZ() - rv.z, this.dux.getYRot(), this.dux.getXRot());
                    torpedo.setDeltaMovement(this.dux.getDeltaMovement());
                    torpedo.joinGroupOf(this.dux);
                    if (deadDux) {
                        torpedo.setDeltaMovement(target.position().subtract(torpedo.position()).normalize().scale(1.5).add(this.dux.random.nextGaussian() * 0.4, this.dux.random.nextGaussian() * 0.4, this.dux.random.nextGaussian() * 0.4));
                        torpedo.primeAndTarget(target, 1.5F);
                        torpedo.leaveGroup(false);
                    }
                    world.addFreshEntity(torpedo);
                }
            }
        }
    }

    static class TargetAttackerGoal extends TargetGoal {

        private static final TargetingConditions VALID_AVOIDABLES_PREDICATE = TargetingConditions.forCombat().ignoreLineOfSight().ignoreInvisibilityTesting();
        private int lastAttackedTime;
        private final Class<?>[] noRevengeTypes;

        private final SubcaelicDuxEntity dux;

        public TargetAttackerGoal(SubcaelicDuxEntity dux, Class<?>... noRevengeTypes) {
            super(dux, true);
            this.dux = dux;
            this.noRevengeTypes = noRevengeTypes;
            this.setFlags(EnumSet.of(Goal.Flag.TARGET));
        }

        @Override
        public boolean canUse() {
            int i = this.mob.getLastHurtByMobTimestamp();
            LivingEntity livingEntity = this.mob.getLastHurtByMob();
            if (i != this.lastAttackedTime && livingEntity != null) {
                for(Class<?> class_ : this.noRevengeTypes) {
                    if (class_.isAssignableFrom(livingEntity.getClass())) {
                        return false;
                    }
                }

                return this.canAttack(livingEntity, VALID_AVOIDABLES_PREDICATE);
            } else {
                return false;
            }
        }

        @Override
        public void start() {
            LivingEntity target = this.mob.getLastHurtByMob();
            if(target instanceof Player) {
                this.dux.forgetCurrentTargetAndRefreshUniversalAnger();
                this.lastAttackedTime = this.mob.getLastHurtByMobTimestamp();
            } else {
                this.mob.setTarget(target);
                this.targetMob = this.mob.getTarget();
                this.lastAttackedTime = this.mob.getLastHurtByMobTimestamp();
                this.unseenMemoryTicks = 300;
            }

            super.start();
        }
    }
}

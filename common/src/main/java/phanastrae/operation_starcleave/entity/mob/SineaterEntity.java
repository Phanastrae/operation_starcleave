package phanastrae.operation_starcleave.entity.mob;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;
import phanastrae.operation_starcleave.sound.OperationStarcleaveSoundEvents;
import phanastrae.operation_starcleave.world.OperationStarcleaveLootTables;

import java.util.EnumSet;
import java.util.function.IntFunction;

public class SineaterEntity extends PathfinderMob implements Enemy {
    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(SineaterEntity.class, EntityDataSerializers.INT);

    public float prevSquishiness = 0F;
    public float squishiness = 0F;

    public SineaterEntity(EntityType<? extends SineaterEntity> entityType, Level level) {
        super(entityType, level);
        this.xpReward = Enemy.XP_REWARD_MEDIUM;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 26.0)
                .add(Attributes.ARMOR, 14.0)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new JumpAtTargetGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0F, true));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.7));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, ZombifiedPiglin.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Piglin.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Zombie.class, true));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Chicken.class, true));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Variant", this.getVariant().getId());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setVariant(Variant.byId(compound.getInt("Variant")));
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        this.setVariant(Variant.getSpawnVariant(level.getRandom()));
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    public Variant getVariant() {
        return Variant.byId(this.entityData.get(DATA_VARIANT));
    }

    public void setVariant(Variant variant) {
        this.entityData.set(DATA_VARIANT, variant.getId());
    }

    @Override
    public ResourceKey<LootTable> getDefaultLootTable() {
        return switch (this.getVariant()) {
            case GOLDEN -> OperationStarcleaveLootTables.SINEATER_GOLDEN;
            case SPECTRAL -> OperationStarcleaveLootTables.SINEATER_SPECTRAL;
            case PHANTASMAL -> OperationStarcleaveLootTables.SINEATER_PHANTASMAL;
        };
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.level().isClientSide()) {
            this.prevSquishiness = this.squishiness;

            this.squishiness *= 0.97F;
            if (this.hasPose(Pose.INHALING)) {
                this.squishiness += 1 / 20F;
            } else if (this.hasPose(Pose.LONG_JUMPING) && this.getDeltaMovement().y > 0) {
                this.squishiness -= 3 / 20F;
            } else if (this.getDeltaMovement().y > 0) {
                this.squishiness -= 1 / 20F;
            } else if (this.onGround()) {
                this.squishiness *= 0.73F;
            }
            this.squishiness = Math.clamp(this.squishiness, -1, 1);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.hasPose(Pose.STANDING)) {
            this.yBodyRot = this.yHeadRot;
            this.setYRot(this.yHeadRot);
        }
    }

    @Override
    protected int calculateFallDamage(float fallDistance, float damageMultiplier) {
        if (this.hasPose(Pose.LONG_JUMPING)) {
            return super.calculateFallDamage(fallDistance - 6, damageMultiplier * 0.5F);
        } else {
            return super.calculateFallDamage(fallDistance, damageMultiplier);
        }
    }

    @Override
    public boolean isPushable() {
        if (this.hasPose(Pose.LONG_JUMPING)) {
            return false;
        } else {
            return super.isPushable();
        }
    }

    @Override
    public void knockback(double strength, double x, double z) {
        if (this.hasPose(Pose.LONG_JUMPING)) {
            super.knockback(strength * 0.25, x, z);
        } else {
            super.knockback(strength, x, z);
        }
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return OperationStarcleaveSoundEvents.SINEATER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return OperationStarcleaveSoundEvents.SINEATER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return OperationStarcleaveSoundEvents.SINEATER_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(OperationStarcleaveSoundEvents.SINEATER_STEP, 0.15F, 1.0F);
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.HOSTILE_SWIM;
    }

    @Override
    protected SoundEvent getSwimSplashSound() {
        return SoundEvents.HOSTILE_SPLASH;
    }

    public void playInhaleSound() {
        this.playSound(OperationStarcleaveSoundEvents.SINEATER_PREPARE_JUMP, 0.85F, 0.7F);
    }

    public void playJumpSound() {
        this.playSound(OperationStarcleaveSoundEvents.SINEATER_JUMP, 0.75F, 1.0F);
    }

    public void playLandSound() {
        this.playSound(OperationStarcleaveSoundEvents.SINEATER_STEP, 0.7F, 1.0F);
    }

    @Override
    public boolean canBeLeashed() {
        return true;
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        BlockState state = level.getBlockState(pos.below());
        return state.is(OperationStarcleaveBlocks.STELLAR_MULCH) ? 10.0F : state.is(OperationStarcleaveBlocks.HOLY_MOSS) ? 5.0F : 0.0F;
    }

    public enum Variant implements StringRepresentable {
        GOLDEN(0, "golden"),
        SPECTRAL(1, "spectral"),
        PHANTASMAL(2, "phantasmal");

        private static final IntFunction<Variant> BY_ID = ByIdMap.continuous(Variant::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        public static final Codec<Variant> CODEC = StringRepresentable.fromEnum(Variant::values);
        private final int id;
        private final String name;

        Variant(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public static Variant byId(int id) {
            return BY_ID.apply(id);
        }

        private static Variant getSpawnVariant(RandomSource random) {
            if (random.nextInt(13) == 0) {
                return PHANTASMAL;
            } else if (random.nextInt(4) == 0) {
                return SPECTRAL;
            } else {
                return GOLDEN;
            }
        }
    }

    public static class JumpAtTargetGoal extends Goal {
        private final SineaterEntity mob;
        private LivingEntity target;
        private int jumpTimer = 0;
        private boolean jumping = false;

        public JumpAtTargetGoal(SineaterEntity mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (this.mob.hasControllingPassenger()) {
                return false;
            } else {
                this.target = this.mob.getTarget();
                if (this.target == null) {
                    return false;
                } else {
                    double distSqr = this.mob.distanceToSqr(this.target);
                    if (1.0 < distSqr && distSqr < 10.0 * 10.0) {
                        return this.mob.onGround() && this.mob.getRandom().nextInt(reducedTickDelay(30)) == 0;
                    } else {
                        return false;
                    }
                }
            }
        }

        @Override
        public void start() {
            this.mob.setPose(Pose.INHALING);
            this.jumpTimer = 0;
            this.jumping = false;

            this.mob.playInhaleSound();
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            if (!this.jumping) {
                return this.jumpTimer > 0;
            } else {
                return !this.mob.onGround();
            }
        }

        @Override
        public void tick() {
            this.mob.getLookControl().setLookAt(this.target);

            if (!this.jumping) {
                if (this.mob.onGround()) {
                    this.jumpTimer++;
                } else {
                    if (this.mob.random.nextBoolean()) {
                        this.jumpTimer--;
                    }
                }

                if (this.jumpTimer >= 20) {
                    this.startJumping();
                }
            } else {
                for (Entity entity : this.mob.level().getEntities(this.mob, this.mob.getBoundingBox(), entity -> entity instanceof LivingEntity && !entity.getType().equals(OperationStarcleaveEntityTypes.SINEATER))) {
                    this.mob.doHurtTarget(entity);
                }
            }
        }

        protected void startJumping() {
            Vec3 vec3 = this.mob.getDeltaMovement();
            Vec3 horizontalTargetOffset = new Vec3(this.target.getX() - this.mob.getX(), 0.0, this.target.getZ() - this.mob.getZ());
            if (horizontalTargetOffset.lengthSqr() > 1.0E-7) {
                horizontalTargetOffset = horizontalTargetOffset.normalize().scale(0.8).add(vec3.scale(0.2));
            }
            this.mob.setDeltaMovement(horizontalTargetOffset.x, 0.65F, horizontalTargetOffset.z);

            this.jumpTimer = 0;
            this.jumping = true;
            this.mob.setPose(Pose.LONG_JUMPING);

            this.mob.playJumpSound();
        }

        @Override
        public void stop() {
            if (this.jumping && this.mob.onGround()) {
                this.mob.playLandSound();
            }
            this.jumpTimer = 0;
            this.jumping = false;
            this.mob.setPose(Pose.STANDING);
        }
    }
}

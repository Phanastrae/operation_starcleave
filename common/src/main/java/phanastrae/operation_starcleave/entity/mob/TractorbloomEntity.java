package phanastrae.operation_starcleave.entity.mob;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.sound.OperationStarcleaveSoundEvents;

import java.util.EnumSet;

public class TractorbloomEntity extends PathfinderMob implements Enemy {

    public float petalSpinAngle;
    public float prevPetalSpinAngle;

    public float deltaX;
    public float deltaZ;
    public float prevDeltaX;
    public float prevDeltaZ;

    public TractorbloomEntity(EntityType<? extends TractorbloomEntity> entityType, Level level) {
        super(entityType, level);
        this.xpReward = Enemy.XP_REWARD_MEDIUM;
        this.moveControl = new FlyingMoveControl(this, 5, false);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation flyingPathNavigation = new FlyingPathNavigation(this, level);
        flyingPathNavigation.setCanOpenDoors(false);
        flyingPathNavigation.setCanFloat(true);
        flyingPathNavigation.setCanPassDoors(true);
        return flyingPathNavigation;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 18.0)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.MOVEMENT_SPEED, 0.165)
                .add(Attributes.FLYING_SPEED, 0.165)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FollowAboveTargetGoal(this, 1.0, true, 5, 1));
        this.goalSelector.addGoal(2, new HoveringRandomFlyingGoal(this, 6, 1));
        this.goalSelector.addGoal(3, new HoverAboveGroundGoal(this, 6));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 16));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Cow.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Villager.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Sheep.class, true));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide()) {
            this.prevPetalSpinAngle = this.petalSpinAngle;
            this.petalSpinAngle += 12;

            this.prevDeltaX = this.deltaX;
            this.prevDeltaZ = this.deltaZ;
            this.deltaX = (float) Mth.lerp(0.2, this.deltaX, this.getDeltaMovement().x);
            this.deltaZ = (float) Mth.lerp(0.2, this.deltaZ, this.getDeltaMovement().z);
        }
    }

    @Override
    public void travel(Vec3 travelVector) {
        this.applyGravity();

        // same thing as allay but with drag changed
        if (this.isControlledByLocalInstance()) {
            if (this.isInWater()) {
                this.moveRelative(0.02F, travelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.8F));
            } else if (this.isInLava()) {
                this.moveRelative(0.02F, travelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.5F));
            } else {
                this.moveRelative(this.getSpeed(), travelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.93F));
            }
        }

        this.calculateEntityAnimation(false);
    }

    @Override
    protected double getDefaultGravity() {
        double gravMultiplier = this.isAlive() ? 0.05 : 0.9;
        return super.getDefaultGravity() * gravMultiplier;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return OperationStarcleaveSoundEvents.TRACTORBLOOM_AMBIENT;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return OperationStarcleaveSoundEvents.TRACTORBLOOM_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return OperationStarcleaveSoundEvents.TRACTORBLOOM_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
    }

    @Override
    public boolean onClimbable() {
        return false;
    }

    public static int getHeightAvailableAbovePosition(Level level, double x, double y, double z, int targetHoverGap, double bbHeight) {
        BlockPos.MutableBlockPos pos = BlockPos.containing(x, y, z).mutable();

        int mobHeight = Mth.ceil(bbHeight);

        int emptyBlocks = 0;
        for (int i = 0; i < targetHoverGap + mobHeight; i++) {
            pos.move(Direction.UP);
            BlockState state = level.getBlockState(pos);
            if (state.getCollisionShape(level, pos).isEmpty()) {
                emptyBlocks++;
            } else {
                break;
            }
        }
        return Math.max(emptyBlocks - mobHeight, 0);
    }

    public static class HoveringRandomFlyingGoal extends WaterAvoidingRandomFlyingGoal {

        protected final int targetHoverGap;
        protected final int minHoverGap;

        public HoveringRandomFlyingGoal(PathfinderMob mob, int hoverGap, int minHoverGap) {
            super(mob, 1.0);
            this.targetHoverGap = hoverGap;
            this.minHoverGap = minHoverGap;
        }

        @Nullable
        @Override
        protected Vec3 getPosition() {
            Vec3 position = super.getPosition();
            if (position == null) {
                return null;
            }
            BlockPos blockPos = BlockPos.containing(position);

            Level level = this.mob.level();

            BlockPos.MutableBlockPos pos = blockPos.mutable();
            int blocksAboveGround = 0;
            for (int i = 0; i < this.targetHoverGap; i++) {
                pos.move(Direction.DOWN);
                BlockState state = level.getBlockState(pos);
                if (state.getCollisionShape(level, pos).isEmpty()) {
                    blocksAboveGround++;
                } else {
                    break;
                }
            }

            if (blocksAboveGround >= this.targetHoverGap) {
                return position;
            }

            double groundHeight = blockPos.getY() - blocksAboveGround;
            int availableHeightAboveGround = getHeightAvailableAbovePosition(level, position.x, groundHeight, position.z, this.targetHoverGap, this.mob.getBbHeight());
            if (availableHeightAboveGround < this.minHoverGap) {
                return null;
            } else {
                return position.add(0, availableHeightAboveGround, 0);
            }
        }
    }

    public static class FollowAboveTargetGoal extends Goal {
        private static final long COOLDOWN_BETWEEN_CAN_USE_CHECKS = 20L;

        protected final PathfinderMob mob;
        private final double speedModifier;
        private final boolean followingTargetEvenIfNotSeen;
        protected final int targetHoverGap;
        protected final int minHoverGap;

        private Path path;
        private double pathedTargetX;
        private double pathedTargetY;
        private double pathedTargetZ;
        private int ticksUntilNextPathRecalculation;
        private long lastCanUseCheck;

        public FollowAboveTargetGoal(PathfinderMob mob, double speedModifier, boolean followingTargetEvenIfNotSeen, int hoverGap, int minHoverGap) {
            this.mob = mob;
            this.speedModifier = speedModifier;
            this.followingTargetEvenIfNotSeen = followingTargetEvenIfNotSeen;
            this.targetHoverGap = hoverGap;
            this.minHoverGap = minHoverGap;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            long gameTime = this.mob.level().getGameTime();
            if (gameTime - this.lastCanUseCheck < COOLDOWN_BETWEEN_CAN_USE_CHECKS) {
                return false;
            } else {
                this.lastCanUseCheck = gameTime;
                LivingEntity target = this.mob.getTarget();
                if (target == null || !target.isAlive()) {
                    return false;
                } else {
                    this.path = createPathAbove(target);
                    return this.path != null;
                }
            }
        }

        @Nullable
        public Path createPathAbove(Entity target) {
            int heightAboveTarget = getHeightAvailableAboveTarget(target);
            if (heightAboveTarget < this.minHoverGap) {
                return null;
            } else {
                // TODO check if region offset should be 16
                return this.mob.getNavigation().createPath(BlockPos.containing(target.getX(), getPositionAboveTarget(target, heightAboveTarget), target.getZ()), 0);
            }
        }

        public boolean moveToAbove(double targetX, double targetY, double targetZ) {
            PathNavigation nav = this.mob.getNavigation();
            Path path = nav.createPath(BlockPos.containing(targetX, targetY, targetZ), 1);
            return path != null && nav.moveTo(path, this.speedModifier);
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = this.mob.getTarget();
            if (target == null || !target.isAlive()) {
                return false;
            } else if (!this.followingTargetEvenIfNotSeen) {
                return !this.mob.getNavigation().isDone();
            } else {
                return this.mob.isWithinRestriction(BlockPos.containing(target.getX(), getPositionAboveTarget(target), target.getZ()))
                        && (!(target instanceof Player) || !(target.isSpectator() || ((Player) target).isCreative()));
            }
        }

        @Override
        public void start() {
            this.mob.getNavigation().moveTo(this.path, this.speedModifier);
            this.mob.setAggressive(true);
            this.ticksUntilNextPathRecalculation = 0;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public int getHeightAvailableAboveTarget(Entity target) {
            double targetTop = target.getY() + target.getBbHeight();
            return getHeightAvailableAbovePosition(target.level(), target.getX(), targetTop, target.getZ(), this.targetHoverGap, this.mob.getBbHeight());
        }

        public double getPositionAboveTarget(Entity target, double availableHeight) {
            return target.getY() + target.getBbHeight() + availableHeight;
        }

        public double getPositionAboveTarget(Entity target) {
            return getPositionAboveTarget(target, getHeightAvailableAboveTarget(target));
        }

        @Override
        public void tick() {
            LivingEntity target = this.mob.getTarget();
            if (target == null) {
                return;
            }

            this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
            this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);

            if ((this.followingTargetEvenIfNotSeen || this.mob.getSensing().hasLineOfSight(target))
                    && this.ticksUntilNextPathRecalculation <= 0
                    && (
                    this.pathedTargetX == 0.0 && this.pathedTargetY == 0.0 && this.pathedTargetZ == 0.0
                            || target.distanceToSqr(this.pathedTargetX, this.pathedTargetY, this.pathedTargetZ) >= 1.0
                            || this.mob.getRandom().nextFloat() < 0.05F
            )) {
                int heightAboveTarget = getHeightAvailableAboveTarget(target);
                if (heightAboveTarget < this.minHoverGap) {
                    return;
                }

                this.pathedTargetX = target.getX();
                this.pathedTargetY = getPositionAboveTarget(target, heightAboveTarget);
                this.pathedTargetZ = target.getZ();
                this.ticksUntilNextPathRecalculation = 4 + this.mob.getRandom().nextInt(7);

                double distToTargetSqr = this.mob.distanceToSqr(new Vec3(this.pathedTargetX, this.pathedTargetY, this.pathedTargetZ));
                if (distToTargetSqr > 1024.0) {
                    this.ticksUntilNextPathRecalculation += 10;
                } else if (distToTargetSqr > 256.0) {
                    this.ticksUntilNextPathRecalculation += 5;
                }

                if (!moveToAbove(this.pathedTargetX, this.pathedTargetY, this.pathedTargetZ)) {
                    this.ticksUntilNextPathRecalculation += 15;
                }

                this.ticksUntilNextPathRecalculation = this.adjustedTickDelay(this.ticksUntilNextPathRecalculation);
            }
        }

        @Override
        public void stop() {
            LivingEntity target = this.mob.getTarget();
            if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target)) {
                this.mob.setTarget(null);
            }

            this.mob.setAggressive(false);
            this.mob.getNavigation().stop();
        }
    }

    public static class HoverAboveGroundGoal extends Goal {

        protected final PathfinderMob mob;
        protected final int targetHoverGap;

        protected int cooldownTime = 0;

        public HoverAboveGroundGoal(PathfinderMob mob, int targetHoverGap) {
            this.mob = mob;
            this.targetHoverGap = targetHoverGap;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return true;
        }

        @Override
        public void start() {
            this.cooldownTime = 0;
        }

        @Override
        public void tick() {
            this.cooldownTime--;
            if(this.cooldownTime <= 0) {
                // TODO deduplicate logic, maybe just combine with the random flying goal
                Vec3 position = this.mob.position();
                BlockPos blockPos = BlockPos.containing(position);
                Level level = this.mob.level();

                BlockPos.MutableBlockPos pos = blockPos.mutable();
                int blocksAboveGround = 0;
                for (int i = 0; i < this.targetHoverGap; i++) {
                    pos.move(Direction.DOWN);
                    BlockState state = level.getBlockState(pos);
                    if (state.getCollisionShape(level, pos).isEmpty()) {
                        blocksAboveGround++;
                    } else {
                        break;
                    }
                }

                if (blocksAboveGround >= this.targetHoverGap) {
                    this.cooldownTime = this.mob.getRandom().nextIntBetweenInclusive(70, 90);
                } else {

                    double groundHeight = blockPos.getY() - blocksAboveGround;
                    int availableHeightAboveGround = getHeightAvailableAbovePosition(level, position.x, groundHeight, position.z, this.targetHoverGap, this.mob.getBbHeight());

                    double targetHeight = groundHeight + availableHeightAboveGround;
                    this.mob.getNavigation().moveTo(position.x, targetHeight, position.z, 1.0);

                    this.cooldownTime = this.mob.getRandom().nextIntBetweenInclusive(40, 50);
                }
            }
        }
    }
}

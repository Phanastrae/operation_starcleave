package phanastrae.operation_starcleave.entity.mob;

import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;
import phanastrae.operation_starcleave.world.firmament.Firmament;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractSubcaelicEntity extends Mob implements Enemy {

    public float tiltAngle;
    public float prevTiltAngle;
    public float rollAngle;
    public float prevRollAngle;
    public float tentacleRollAngle;
    public float prevTentacleRollAngle;

    protected AbstractSubcaelicEntity(EntityType<? extends AbstractSubcaelicEntity> entityType, Level world) {
        super(entityType, world);
        this.moveControl = new SubcaelicMoveControl(this);
    }

    @Override
    public void travel(Vec3 movementInput) {
        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.prevTiltAngle = this.tiltAngle;
        this.prevRollAngle = this.rollAngle;
        this.prevTentacleRollAngle = this.tentacleRollAngle;

        if(this.shouldTickAngles()) {
            Vec3 velocity = this.getDeltaMovement();
            double horizontalSpeed = velocity.horizontalDistance();
            if (horizontalSpeed > 0.01) {
                double targetYaw = Math.toDegrees(-Mth.atan2(velocity.x, velocity.z));
                this.yBodyRot = (float)Mth.rotLerp(0.2, this.yBodyRot, targetYaw);
                this.setYRot(this.yBodyRot);
            }
            this.rollAngle += (float) Math.PI * 1.5F;
            this.tentacleRollAngle += (float) Math.PI * 1.75F;
            this.tiltAngle += (Math.toDegrees(-Mth.atan2(horizontalSpeed, velocity.y)) - this.tiltAngle) * 0.1F;
            this.setXRot( - this.tiltAngle - 90);

            if (this.level().isClientSide) {
                spawnTrailParticles();
            }
        }
    }

    protected boolean shouldTickAngles() {
        return this.isAlive();
    }

    @Override
    protected void checkFallDamage(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
    }

    @Override
    public boolean onClimbable() {
        return false;
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.GLOW_SQUID_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.GLOW_SQUID_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.GLOW_SQUID_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 2.0F;
    }

    public void spawnTrailParticles() {
        Vec3 spawnCenter = this.position().add(0.0, this.getBbHeight()*0.5, 0.0).subtract(this.getLookAngle().scale(this.getBbWidth()*0.5));
        double f = this.getBbWidth() * 0.2;
        int count = (int)(this.getBbWidth() * 10 * Math.min(1.0, 2.0 * this.getDeltaMovement().length()));
        for(int i = 0; i < count; ++i) {
            double x = spawnCenter.x + (this.random.nextDouble() - 0.5) * f;
            double y = spawnCenter.y + (this.random.nextDouble() - 0.5) * f;
            double z = spawnCenter.z + (this.random.nextDouble() - 0.5) * f;
            this.level().addParticle(OperationStarcleaveParticleTypes.GLIMMER_SMOKE, x, y, z,
                    this.getDeltaMovement().x * - 1.5,
                    this.getDeltaMovement().y * - 1.5,
                    this.getDeltaMovement().z * - 1.5);
        }
    }

    public abstract double getTurnFactor();

    static class SubcaelicMoveControl extends MoveControl {

        private final AbstractSubcaelicEntity entity;

        public double aimX;
        public double aimY;
        public double aimZ;

        public SubcaelicMoveControl(AbstractSubcaelicEntity entity) {
            super(entity);
            this.entity = entity;
        }

        @Override
        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                double movementSpeed = this.entity.getAttributeValue(Attributes.MOVEMENT_SPEED) * this.getSpeedModifier();
                this.tickAim(this.entity.getTurnFactor(), movementSpeed);

                double lerpFactor = getLerpFactor(movementSpeed);
                double dragFactor = 0.98;
                Firmament firmament = Firmament.fromWorld(this.entity.level());
                if(this.entity.isAlive() && firmament != null) {
                    int damage = firmament.getDamage(this.entity.getBlockX(), this.entity.getBlockZ());
                    dragFactor *= 0.7 + 0.3 * damage/7.0;
                }
                Vec3 velocity = this.entity.getDeltaMovement();
                double vX = Mth.lerp(lerpFactor, velocity.x, this.aimX * movementSpeed);
                double vY = Mth.lerp(lerpFactor, velocity.y, this.aimY * movementSpeed);
                double vZ = Mth.lerp(lerpFactor, velocity.z, this.aimZ * movementSpeed);
                this.entity.setDeltaMovement(vX * dragFactor, vY * dragFactor, vZ * dragFactor);
            }
        }

        private void tickAim(double turnFactor, double movementSpeed) {
            double targetAimX = this.wantedX - this.entity.getX();
            double targetAimY = this.wantedY - this.entity.getY();
            double targetAimZ = this.wantedZ - this.entity.getZ();
            double targetAimSqr = targetAimX*targetAimX + targetAimY*targetAimY + targetAimZ*targetAimZ;
            if(targetAimSqr > movementSpeed * movementSpeed) {
                double targetAimLength = Math.sqrt(targetAimSqr);
                targetAimX /= targetAimLength;
                targetAimY /= targetAimLength;
                targetAimZ /= targetAimLength;
            } else {
                targetAimX /= movementSpeed;
                targetAimY /= movementSpeed;
                targetAimZ /= movementSpeed;
            }

            double aX = Mth.lerp(turnFactor, this.aimX, targetAimX);
            double aY = Mth.lerp(turnFactor, this.aimY, targetAimY);
            double aZ = Mth.lerp(turnFactor, this.aimZ, targetAimZ);
            double aSqr = aX*aX + aY*aY + aZ*aZ;
            if (aSqr > 1) {
                double aLength = Math.sqrt(aSqr);
                aX /= aLength;
                aY /= aLength;
                aZ /= aLength;
            }
            this.aimX = aX;
            this.aimY = aY;
            this.aimZ = aZ;
        }

        private double getLerpFactor(double movementSpeed) {
            Vec3 velocity = this.entity.getDeltaMovement();
            double vSqr = velocity.lengthSqr();
            double dot = this.aimX * velocity.x + this.aimY * velocity.y + this.aimZ * velocity.z;
            if (vSqr > movementSpeed * movementSpeed) {
                dot = dot / Math.sqrt(vSqr);
            } else {
                dot = dot / movementSpeed;
            }

            dot = dot * 0.5 + 0.5;
            return dot * dot * dot * 0.98 + 0.02;
        }
    }

    static class SwimWanderGoal extends Goal {
        protected static final double TARGET_ACHIEVED_DISTANCE = 8.0;
        private static final double SEARCH_RADIUS_MIN = 8.0;
        private static final double SEARCH_RADIUS_MAX = 20.0;

        protected final AbstractSubcaelicEntity entity;

        public SwimWanderGoal(AbstractSubcaelicEntity entity) {
            this.entity = entity;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return true;
        }

        @Override
        public void tick() {
            if(needsNewTarget() || this.entity.getRandom().nextInt(reducedTickDelay(40)) == 0) {
                Vec3 target = pickTargetPosition();
                if(target != null) {
                    this.entity.moveControl.setWantedPosition(target.x, target.y, target.z, 1.0);
                }
            }
        }

        protected boolean needsNewTarget() {
            MoveControl moveControl = this.entity.moveControl;
            if(!moveControl.hasWanted()) {
                return true;
            }
            return !isTargetValid(moveControl.getWantedX(), moveControl.getWantedY(), moveControl.getWantedZ());
        }

        protected boolean isTargetValid(double x, double y, double z) {
            return this.entity.position().subtract(x, y, z).horizontalDistanceSqr() > TARGET_ACHIEVED_DISTANCE * TARGET_ACHIEVED_DISTANCE;
        }

        @Nullable
        protected Vec3 pickTargetPosition() {
            MoveControl moveControl = this.entity.moveControl;
            double leastBadness = !moveControl.hasWanted() ? Double.POSITIVE_INFINITY : getTargetBadness(new Vec3(moveControl.getWantedX(), moveControl.getWantedY(), moveControl.getWantedZ()));
            Vec3 bestTarget = null;

            int ATTEMPTS = 7;
            for(int i = 0; i < ATTEMPTS; i++) {
                Vec3 candidatePosition = getCandidatePosition();
                double badness = getTargetBadness(candidatePosition);
                if(badness < leastBadness) {
                    leastBadness = badness;
                    bestTarget = candidatePosition;
                }
            }

            return bestTarget;
        }

        protected Vec3 getCandidatePosition() {
            RandomSource random = this.entity.getRandom();
            double radius = SEARCH_RADIUS_MIN + (SEARCH_RADIUS_MAX - SEARCH_RADIUS_MIN) * random.nextFloat();
            double angle = Math.PI * 2 * random.nextFloat();
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);

            double targetX = this.entity.getX() + radius * cos;
            double targetZ = this.entity.getZ() + radius * sin;

            double targetY = getTargetY(targetX, targetZ, this.entity.level().getMinBuildHeight(), this.entity.level().getMaxBuildHeight());
            return new Vec3(targetX, targetY, targetZ);
        }

        protected double getTargetY(double x, double z, double bottomY, double topY) {
            Level world = entity.level();
            int heightStart = world.getHeight(Heightmap.Types.MOTION_BLOCKING, (int)this.entity.getX(), (int)this.entity.getZ());
            int heightEnd = world.getHeight(Heightmap.Types.MOTION_BLOCKING, (int)x, (int)z);
            int height = Math.min(Math.max(heightStart, heightEnd), (int)this.entity.getY() + 12);
            double h = (height == world.getMinBuildHeight()) ? this.entity.getY() : height + 16;
            return Mth.clamp(h, bottomY, topY);
        }

        protected double getTargetBadness(Vec3 target) {
            Vec3 offset = target.subtract(this.entity.position()).multiply(1.,0.25,1.);
            if(offset.lengthSqr() > 1) {
                offset = offset.normalize();
            }
            double dot = offset.dot(this.entity.getViewVector(1F));

            Firmament firmament = Firmament.fromWorld(this.entity.level());
            int damage = firmament == null ? 0 : firmament.getDamage((int)target.x(), (int)target.z());

            double dotFactor = 0.5 - 0.5 * dot;
            double damageFactor = 1.0 - (damage / 7.0) * (damage / 7.0);

            return dotFactor * 0.5 + damageFactor * 4.0;
        }
    }

    static class SwimNearTargetGoal extends SwimWanderGoal {
        private static final double MAX_DISTANCE_FROM_TARGET = 32.0;
        private static final double SEARCH_RADIUS_MIN = 2.0;
        private static final double SEARCH_RADIUS_MAX = 48.0;

        @Nullable
        LivingEntity targetEntity = null;

        public SwimNearTargetGoal(AbstractSubcaelicEntity entity) {
            super(entity);
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.entity.getTarget();
            if(isEntityValid(target)) {
                this.targetEntity = target;
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean canContinueToUse() {
            return isEntityValid(this.targetEntity);
        }

        @Override
        public void stop() {
            this.targetEntity = null;
        }

        protected boolean isEntityValid(LivingEntity target) {
            if (target == null) {
                return false;
            } else if (!target.isAlive()) {
                return false;
            } else {
                return !(target instanceof Player) || !target.isSpectator() && !((Player)target).isCreative();
            }
        }

        @Override
        protected Vec3 getCandidatePosition() {
            if(targetEntity == null) return this.entity.position();

            RandomSource random = this.entity.getRandom();
            double radius = SEARCH_RADIUS_MIN + (SEARCH_RADIUS_MAX - SEARCH_RADIUS_MIN) * random.nextFloat();
            double angle = Math.PI * 2 * random.nextFloat();
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);

            float l = random.nextFloat();
            double targetX = Mth.lerp(l, this.entity.getX(), this.targetEntity.getX()) + radius * cos;
            double targetZ = Mth.lerp(l, this.entity.getZ(), this.targetEntity.getZ()) + radius * sin;

            double targetY = getTargetY(targetX, targetZ, this.targetEntity.getY() + 12, this.entity.level().getMaxBuildHeight());
            return new Vec3(targetX, targetY, targetZ);
        }

        @Override
        protected double getTargetBadness(Vec3 target) {
            if(targetEntity == null) return 0.;

            Vec3 offset = target.subtract(this.targetEntity.position());
            double offsetFactor = 1.0 - Math.exp(-offset.lengthSqr() * 0.01);

            Vec3 offset2 = target.subtract(this.entity.position());
            if(offset2.lengthSqr() > 1) {
                offset2 = offset2.normalize();
            }
            Vec3 offset3 = this.targetEntity.position().subtract(this.entity.position());
            if(offset3.lengthSqr() > 1) {
                offset3 = offset3.normalize();
            }

            double dot = offset2.dot(offset3);
            double dotFactor = 0.5 - 0.5 * dot;

            return super.getTargetBadness(target) + offsetFactor + dotFactor * 0.7;
        }
    }
}

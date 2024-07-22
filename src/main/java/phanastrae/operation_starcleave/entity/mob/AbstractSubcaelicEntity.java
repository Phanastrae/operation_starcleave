package phanastrae.operation_starcleave.entity.mob;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;
import phanastrae.operation_starcleave.world.firmament.Firmament;

import java.util.EnumSet;

public abstract class AbstractSubcaelicEntity extends MobEntity implements Monster {

    public float tiltAngle;
    public float prevTiltAngle;
    public float rollAngle;
    public float prevRollAngle;
    public float tentacleRollAngle;
    public float prevTentacleRollAngle;

    protected AbstractSubcaelicEntity(EntityType<? extends AbstractSubcaelicEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new SubcaelicMoveControl(this);
    }

    @Override
    public void travel(Vec3d movementInput) {
        this.move(MovementType.SELF, this.getVelocity());
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        this.prevTiltAngle = this.tiltAngle;
        this.prevRollAngle = this.rollAngle;
        this.prevTentacleRollAngle = this.tentacleRollAngle;

        if(this.shouldTickAngles()) {
            Vec3d velocity = this.getVelocity();
            double horizontalSpeed = velocity.horizontalLength();
            if (horizontalSpeed > 0.01) {
                double targetYaw = Math.toDegrees(-MathHelper.atan2(velocity.x, velocity.z));
                this.bodyYaw = (float)MathHelper.lerpAngleDegrees(0.2, this.bodyYaw, targetYaw);
                this.setYaw(this.bodyYaw);
            }
            this.rollAngle += (float) Math.PI * 1.5F;
            this.tentacleRollAngle += (float) Math.PI * 1.75F;
            this.tiltAngle += (Math.toDegrees(-MathHelper.atan2(horizontalSpeed, velocity.y)) - this.tiltAngle) * 0.1F;
            this.setPitch( - this.tiltAngle - 90);

            if (this.getWorld().isClient) {
                spawnTrailParticles();
            }
        }
    }

    protected boolean shouldTickAngles() {
        return this.isAlive();
    }

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
    }

    @Override
    public boolean isClimbing() {
        return false;
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_GLOW_SQUID_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_GLOW_SQUID_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_GLOW_SQUID_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 2.0F;
    }

    public void spawnTrailParticles() {
        Vec3d spawnCenter = this.getPos().add(0.0, this.getHeight()*0.5, 0.0).subtract(this.getRotationVector().multiply(this.getWidth()*0.5));
        double f = this.getWidth() * 0.2;
        int count = (int)(this.getWidth() * 10 * Math.min(1.0, 2.0 * this.getVelocity().length()));
        for(int i = 0; i < count; ++i) {
            double x = spawnCenter.x + (this.random.nextDouble() - 0.5) * f;
            double y = spawnCenter.y + (this.random.nextDouble() - 0.5) * f;
            double z = spawnCenter.z + (this.random.nextDouble() - 0.5) * f;
            this.getWorld().addParticle(OperationStarcleaveParticleTypes.GLIMMER_SMOKE, x, y, z,
                    this.getVelocity().x * - 1.5,
                    this.getVelocity().y * - 1.5,
                    this.getVelocity().z * - 1.5);
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
            if (this.state == MoveControl.State.MOVE_TO) {
                double movementSpeed = this.entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * this.getSpeed();
                this.tickAim(this.entity.getTurnFactor(), movementSpeed);

                double lerpFactor = getLerpFactor(movementSpeed);
                double dragFactor = 0.98;
                Firmament firmament = Firmament.fromWorld(this.entity.getWorld());
                if(this.entity.isAlive() && firmament != null) {
                    int damage = firmament.getDamage(this.entity.getBlockX(), this.entity.getBlockZ());
                    dragFactor *= 0.7 + 0.3 * damage/7.0;
                }
                Vec3d velocity = this.entity.getVelocity();
                double vX = MathHelper.lerp(lerpFactor, velocity.x, this.aimX * movementSpeed);
                double vY = MathHelper.lerp(lerpFactor, velocity.y, this.aimY * movementSpeed);
                double vZ = MathHelper.lerp(lerpFactor, velocity.z, this.aimZ * movementSpeed);
                this.entity.setVelocity(vX * dragFactor, vY * dragFactor, vZ * dragFactor);
            }
        }

        private void tickAim(double turnFactor, double movementSpeed) {
            double targetAimX = this.targetX - this.entity.getX();
            double targetAimY = this.targetY - this.entity.getY();
            double targetAimZ = this.targetZ - this.entity.getZ();
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

            double aX = MathHelper.lerp(turnFactor, this.aimX, targetAimX);
            double aY = MathHelper.lerp(turnFactor, this.aimY, targetAimY);
            double aZ = MathHelper.lerp(turnFactor, this.aimZ, targetAimZ);
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
            Vec3d velocity = this.entity.getVelocity();
            double vSqr = velocity.lengthSquared();
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
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            return true;
        }

        @Override
        public void tick() {
            if(needsNewTarget() || this.entity.getRandom().nextInt(toGoalTicks(40)) == 0) {
                Vec3d target = pickTargetPosition();
                if(target != null) {
                    this.entity.moveControl.moveTo(target.x, target.y, target.z, 1.0);
                }
            }
        }

        protected boolean needsNewTarget() {
            MoveControl moveControl = this.entity.moveControl;
            if(!moveControl.isMoving()) {
                return true;
            }
            return !isTargetValid(moveControl.getTargetX(), moveControl.getTargetY(), moveControl.getTargetZ());
        }

        protected boolean isTargetValid(double x, double y, double z) {
            return this.entity.getPos().subtract(x, y, z).horizontalLengthSquared() > TARGET_ACHIEVED_DISTANCE * TARGET_ACHIEVED_DISTANCE;
        }

        @Nullable
        protected Vec3d pickTargetPosition() {
            MoveControl moveControl = this.entity.moveControl;
            double leastBadness = !moveControl.isMoving() ? Double.POSITIVE_INFINITY : getTargetBadness(new Vec3d(moveControl.getTargetX(), moveControl.getTargetY(), moveControl.getTargetZ()));
            Vec3d bestTarget = null;

            int ATTEMPTS = 7;
            for(int i = 0; i < ATTEMPTS; i++) {
                Vec3d candidatePosition = getCandidatePosition();
                double badness = getTargetBadness(candidatePosition);
                if(badness < leastBadness) {
                    leastBadness = badness;
                    bestTarget = candidatePosition;
                }
            }

            return bestTarget;
        }

        protected Vec3d getCandidatePosition() {
            Random random = this.entity.getRandom();
            double radius = SEARCH_RADIUS_MIN + (SEARCH_RADIUS_MAX - SEARCH_RADIUS_MIN) * random.nextFloat();
            double angle = Math.PI * 2 * random.nextFloat();
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);

            double targetX = this.entity.getX() + radius * cos;
            double targetZ = this.entity.getZ() + radius * sin;

            double targetY = getTargetY(targetX, targetZ, this.entity.getWorld().getBottomY(), this.entity.getWorld().getTopY());
            return new Vec3d(targetX, targetY, targetZ);
        }

        protected double getTargetY(double x, double z, double bottomY, double topY) {
            World world = entity.getWorld();
            int heightStart = world.getTopY(Heightmap.Type.MOTION_BLOCKING, (int)this.entity.getX(), (int)this.entity.getZ());
            int heightEnd = world.getTopY(Heightmap.Type.MOTION_BLOCKING, (int)x, (int)z);
            int height = Math.min(Math.max(heightStart, heightEnd), (int)this.entity.getY() + 12);
            double h = (height == world.getBottomY()) ? this.entity.getY() : height + 16;
            return MathHelper.clamp(h, bottomY, topY);
        }

        protected double getTargetBadness(Vec3d target) {
            Vec3d offset = target.subtract(this.entity.getPos()).multiply(1.,0.25,1.);
            if(offset.lengthSquared() > 1) {
                offset = offset.normalize();
            }
            double dot = offset.dotProduct(this.entity.getRotationVec(1F));

            Firmament firmament = Firmament.fromWorld(this.entity.getWorld());
            int damage = firmament == null ? 0 : firmament.getDamage((int)target.getX(), (int)target.getZ());

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
        public boolean canStart() {
            LivingEntity target = this.entity.getTarget();
            if(isEntityValid(target)) {
                this.targetEntity = target;
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean shouldContinue() {
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
                return !(target instanceof PlayerEntity) || !target.isSpectator() && !((PlayerEntity)target).isCreative();
            }
        }

        @Override
        protected Vec3d getCandidatePosition() {
            if(targetEntity == null) return this.entity.getPos();

            Random random = this.entity.getRandom();
            double radius = SEARCH_RADIUS_MIN + (SEARCH_RADIUS_MAX - SEARCH_RADIUS_MIN) * random.nextFloat();
            double angle = Math.PI * 2 * random.nextFloat();
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);

            float l = random.nextFloat();
            double targetX = MathHelper.lerp(l, this.entity.getX(), this.targetEntity.getX()) + radius * cos;
            double targetZ = MathHelper.lerp(l, this.entity.getZ(), this.targetEntity.getZ()) + radius * sin;

            double targetY = getTargetY(targetX, targetZ, this.targetEntity.getY() + 12, this.entity.getWorld().getTopY());
            return new Vec3d(targetX, targetY, targetZ);
        }

        @Override
        protected double getTargetBadness(Vec3d target) {
            if(targetEntity == null) return 0.;

            Vec3d offset = target.subtract(this.targetEntity.getPos());
            double offsetFactor = 1.0 - Math.exp(-offset.lengthSquared() * 0.01);

            Vec3d offset2 = target.subtract(this.entity.getPos());
            if(offset2.lengthSquared() > 1) {
                offset2 = offset2.normalize();
            }
            Vec3d offset3 = this.targetEntity.getPos().subtract(this.entity.getPos());
            if(offset3.lengthSquared() > 1) {
                offset3 = offset3.normalize();
            }

            double dot = offset2.dotProduct(offset3);
            double dotFactor = 0.5 - 0.5 * dot;

            return super.getTargetBadness(target) + offsetFactor + dotFactor * 0.7;
        }
    }
}

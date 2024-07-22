package phanastrae.operation_starcleave.entity.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.block.StellarFarmlandBlock;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;
import phanastrae.operation_starcleave.world.firmament.Firmament;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class SubcaelicDuxEntity extends AbstractSubcaelicEntity implements Angerable {
    private static final UniformIntProvider ANGER_TIME_RANGE = TimeHelper.betweenSeconds(15, 25);
    public static final int MAX_ADOPTED_GROUP_SIZE = 14;
    public static final int MAX_NATURAL_GROUP_SIZE = 7;

    private static final TrackedData<Boolean> HOLLOW = DataTracker.registerData(SubcaelicDuxEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public float haloAngle;
    public float prevHaloAngle;

    private final List<SubcaelicTorpedoEntity> torpedos = new ArrayList<>();

    public int ticksSinceDeath;

    private int angerTime;
    @Nullable
    private UUID angryAt;

    public SubcaelicDuxEntity(EntityType<? extends SubcaelicDuxEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = Monster.STRONGER_MONSTER_XP;
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 100.0)
                .add(EntityAttributes.GENERIC_ARMOR, 8.0)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 4.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.9)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 192.0);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(HOLLOW, false);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SubcaelicDuxEntity.AscendGoal(this));
        this.goalSelector.add(2, new SubcaelicDuxEntity.SendTorpedoGoal(this));
        this.goalSelector.add(3, new SubcaelicDuxEntity.SpawnTorpedoGoal(this));
        this.goalSelector.add(4, new AbstractSubcaelicEntity.SwimNearTargetGoal(this));
        this.goalSelector.add(5, new AbstractSubcaelicEntity.SwimWanderGoal(this));
        this.targetSelector.add(1, new SubcaelicDuxEntity.TargetAttackerGoal(this, SubcaelicDuxEntity.class, SubcaelicTorpedoEntity.class));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::shouldAngerAt));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, MobEntity.class, 30, true, false, entity -> entity.getType().isIn(EntityTypeTags.UNDEAD)));
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        this.writeAngerToNbt(nbt);

        nbt.putInt("DuxDeathTime", this.ticksSinceDeath);
        nbt.putBoolean("Hollow", this.isHollow());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.readAngerFromNbt(this.getWorld(), nbt);

        if(nbt.contains("DuxDeathTime", NbtElement.INT_TYPE)) {
            this.ticksSinceDeath = nbt.getInt("DuxDeathTime");
        }
        if(nbt.contains("Hollow", NbtElement.BYTE_TYPE)) {
            this.setHollow(nbt.getBoolean("Hollow"));
        }
    }

    @Override
    public void tick() {
        super.tick();
        if(this.getTarget() != null) {
            if(this.getWorld().getDifficulty().equals(Difficulty.PEACEFUL)) {
                this.setTarget(null);
            } else if(this.getTarget().isRemoved() || this.getTarget().isDead() || this.distanceTo(this.getTarget()) > 128) {
                this.setTarget(null);
            }
        }

        if(this.getWorld().isClient) {
            Firmament firmament = Firmament.fromWorld(this.getWorld());
            if(firmament != null) {
                boolean starlit = StellarFarmlandBlock.isStarlit(this.getWorld(), this.getBlockPos(), firmament);
                if(starlit) {
                    Vec3d spawnCenter = this.getPos().add(0.0, this.getHeight()*0.5, 0.0);
                    double f = this.getWidth();
                    int count = (int)(this.getWidth() * 8);
                    for(int i = 0; i < count; ++i) {
                        double x = spawnCenter.x + (this.random.nextDouble() - 0.5) * f;
                        double y = spawnCenter.y + (this.random.nextDouble() - 0.5) * this.getHeight();
                        double z = spawnCenter.z + (this.random.nextDouble() - 0.5) * f;
                        this.getWorld().addParticle(OperationStarcleaveParticleTypes.FIRMAMENT_GLIMMER, x, y, z,
                                this.getVelocity().x,
                                this.getVelocity().y + 0.2,
                                this.getVelocity().z);
                    }
                }
            }
        }
    }

    @Override
    public void tickMovement() {
        super.tickMovement();

        if (!this.getWorld().isClient) {
            this.tickAngerLogic((ServerWorld)this.getWorld(), false);
        } else {
            this.prevHaloAngle = this.haloAngle;

            float h = this.getHealthFraction();
            this.haloAngle += 2.5f + 40f * (1f - h * h);
        }

        if(this.isDead()) {
            float d = this.getExplosionGlowProgress();

            this.rollAngle += (float) Math.PI * d * 5F;
            this.tentacleRollAngle += (float) Math.PI * d * 20F;
        }

        if(this.isHollow()) {
            this.tiltAngle += (180.0 - this.tiltAngle) * 0.05;

            this.setVelocity(this.getVelocity().x * 0.8, this.getVelocity().y * 0.98 - 0.05, this.getVelocity().z * 0.8);
        }
    }

    @Override
    protected void mobTick() {
        Firmament firmament = Firmament.fromWorld(this.getWorld());
        if(firmament != null) {
            boolean starlit = StellarFarmlandBlock.isStarlit(this.getWorld(), this.getBlockPos(), firmament);
            if(starlit) {
                if (this.age % 20 == 0) {
                    this.heal(1.0F);
                }
            }
        }
    }

    @Override
    protected void updatePostDeath() {
        ++this.ticksSinceDeath;
        if(this.ticksSinceDeath >= 200 && !this.isHollow()) {
            this.becomeHollow();
        }

        if(this.getWorld().isClient && this.getRandom().nextInt(8) == 0) {
            this.spawnSmokeBurst();
            this.getWorld().playSoundFromEntity(this, SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundCategory.NEUTRAL, 6f, 0.7F + 0.5F * this.getRandom().nextFloat());
        }

        if(!this.isRemoved() && (this.ticksSinceDeath >= 400 || (this.ticksSinceDeath >= 240 && this.isOnGround()))) {
            boolean doMobLoot = this.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_LOOT);

            if (this.getWorld() instanceof ServerWorld serverWorld) {
                if (doMobLoot) {
                    ExperienceOrbEntity.spawn(serverWorld, this.getPos(), this.getXpToDrop());
                }

                this.emitGameEvent(GameEvent.ENTITY_DIE);

                serverWorld.createExplosion(this, this.getX(), this.getY(), this.getZ(), 10, true, World.ExplosionSourceType.MOB);
            }
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    protected boolean isImmobile() {
        return this.isHollow();
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if(source.getAttacker() instanceof AbstractSubcaelicEntity) {
            return false;
        }
        return super.damage(source, amount);
    }

    @Override
    protected float getSoundVolume() {
        return 5.0F;
    }

    @Override
    public void chooseRandomAngerTime() {
        this.setAngerTime(ANGER_TIME_RANGE.get(this.random));
    }

    @Override
    public void setAngerTime(int angerTime) {
        this.angerTime = angerTime;
    }

    @Override
    public int getAngerTime() {
        return this.angerTime;
    }

    @Override
    public void setAngryAt(@Nullable UUID angryAt) {
        this.angryAt = angryAt;
    }

    @Nullable
    @Override
    public UUID getAngryAt() {
        return this.angryAt;
    }

    @Override
    public boolean isUniversallyAngry(World world) {
        return this.hasAngerTime() && this.getAngryAt() == null;
    }

    @Override
    public void forgive(PlayerEntity player) {
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
        this.emitGameEvent(GameEvent.ENTITY_DIE);
    }

    @Override
    public Box getVisibilityBoundingBox() {
        return super.getVisibilityBoundingBox().expand(this.getWidth() * 0.6);
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
        Vec3d spawnCenter = this.getPos().add(0.0, this.getHeight()*0.5, 0.0).subtract(this.getRotationVector().multiply(this.getWidth()*0.5));
        double f = this.getWidth() * 0.2;
        int count = (int)(this.getWidth() * 4 * Math.min(1.0, 2.0 * this.getVelocity().length()));
        for(int i = 0; i < count; ++i) {
            double x = spawnCenter.x + (this.random.nextDouble() - 0.5) * f;
            double y = spawnCenter.y + (this.random.nextDouble() - 0.5) * f;
            double z = spawnCenter.z + (this.random.nextDouble() - 0.5) * f;
            this.getWorld().addParticle(OperationStarcleaveParticleTypes.LARGE_GLIMMER_SMOKE, true, x, y, z,
                    this.getVelocity().x * - 1.5,
                    this.getVelocity().y * - 1.5,
                    this.getVelocity().z * - 1.5);
        }
    }

    public void spawnSmokeBurst() {
        Random random = this.getRandom();
        float height = this.getHeight();
        float width = this.getWidth();

        Vec3d offset = new Vec3d(random.nextFloat() - 0.5, random.nextFloat() - 0.5, random.nextFloat() - 0.5).normalize().multiply(width, height, width);

        for(int i = 0; i < 240; ++i) {
            double speed = 0.15F + random.nextFloat() * 0.15F;
            double variation = 0.3F;
            this.getWorld().addParticle(OperationStarcleaveParticleTypes.LARGE_GLIMMER_SMOKE, false,
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    this.getVelocity().x + offset.x * speed + random.nextGaussian() * variation,
                    this.getVelocity().y + offset.y * speed + random.nextGaussian() * variation,
                    this.getVelocity().z + offset.z * speed + random.nextGaussian() * variation);
        }
    }

    public boolean isHollow() {
        return this.dataTracker.get(HOLLOW);
    }

    private void setHollow(boolean value) {
        this.dataTracker.set(HOLLOW, value);
    }

    public void becomeHollow() {
        this.setHollow(true);
        this.ticksSinceDeath = 200;
    }

    public float getExplosionGlowProgress() {
        if(this.isDead()) {
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
        return MathHelper.clamp(f, 0f, 1f);
    }

    static class SendTorpedoGoal extends Goal {

        private final SubcaelicDuxEntity dux;

        private int cooldown;

        public SendTorpedoGoal(SubcaelicDuxEntity dux) {
            this.dux = dux;
        }

        @Override
        public boolean canStart() {
            if(this.dux.torpedos.isEmpty()) {
                return false;
            } else if(this.cooldown > 0) {
                this.cooldown--;
                return false;
            } else if(this.dux.getWorld().getDifficulty().equals(Difficulty.PEACEFUL)) {
                return false;
            } else {
                this.cooldown = toGoalTicks(8 + (int)(Math.sqrt(this.dux.getHealthFraction()) * (22 + this.dux.getRandom().nextInt(20))));

                LivingEntity target = this.dux.getTarget();
                if (target == null) {
                    return false;
                } else if (!target.isAlive()) {
                    return false;
                } else {
                    return !(target instanceof PlayerEntity) || !target.isSpectator() && !((PlayerEntity)target).isCreative();
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
            this.setControls(EnumSet.of(Control.MOVE));
        }

        @Override
        public boolean canStart() {
            return this.dux.isDead() && !this.dux.isHollow();
        }

        @Override
        public void tick() {
            World world = this.dux.getWorld();
            double x = this.dux.getX();
            double z = this.dux.getZ();
            LivingEntity target = this.dux.getTarget();
            if(target != null) {
                x = target.getX();
                z = target.getZ();
            }
            int topY = world.getTopY(Heightmap.Type.MOTION_BLOCKING, (int)x, (int)z);
            Firmament firmament = Firmament.fromWorld(world);
            int goalHeight = topY + 80;
            if(firmament != null) {
                int maxHeight = firmament.getY() - 12;
                if(goalHeight > maxHeight) {
                    goalHeight = maxHeight;
                }
            }

            this.dux.moveControl.moveTo(x, goalHeight, z, 1.5);

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
        public boolean canStart() {
            if(this.dux.torpedos.size() >= SubcaelicDuxEntity.MAX_NATURAL_GROUP_SIZE) {
                return false;
            } else if(this.cooldown > 0 && (this.dux.isAlive() || this.cooldown <= toGoalTicks(20))) {
                this.cooldown--;
                return false;
            } else {
                if(this.dux.isDead()) {
                    this.cooldown = toGoalTicks(20);
                } else {
                    this.cooldown = toGoalTicks(6 + (int) (Math.sqrt(this.dux.getHealthFraction()) * (14 + this.dux.getRandom().nextInt(20))));
                }

                int nearbyTorpedoes = this.dux.getWorld().getEntitiesByType(OperationStarcleaveEntityTypes.SUBCAELIC_TORPEDO, this.dux.getBoundingBox().expand(TORPEDO_CHECK_RANGE), Entity::isAlive).size();
                return nearbyTorpedoes < MAX_NEARBY_TORPEDOES;
            }
        }

        @Override
        public void tick() {
            boolean deadDux = this.dux.isDead();
            LivingEntity target = this.dux.getTarget();
            if(deadDux && target == null) return;

            World world = this.dux.getWorld();

            int torpedoCount = this.dux.isDead() ? (this.dux.random.nextBoolean() ? 4 : 3) : 1;
            Vec3d rv = this.dux.getRotationVector().multiply(this.dux.getWidth() * 0.5);
            for(int i = 0; i < torpedoCount; i++) {
                SubcaelicTorpedoEntity torpedo = OperationStarcleaveEntityTypes.SUBCAELIC_TORPEDO.create(world);
                if (torpedo != null) {
                    torpedo.refreshPositionAndAngles(this.dux.getX() - rv.x, this.dux.getY() + this.dux.getHeight() / 2 - rv.y, this.dux.getZ() - rv.z, this.dux.getYaw(), this.dux.getPitch());
                    torpedo.setVelocity(this.dux.getVelocity());
                    torpedo.joinGroupOf(this.dux);
                    if (deadDux) {
                        torpedo.setVelocity(target.getPos().subtract(torpedo.getPos()).normalize().multiply(1.5).add(this.dux.random.nextGaussian() * 0.4, this.dux.random.nextGaussian() * 0.4, this.dux.random.nextGaussian() * 0.4));
                        torpedo.primeAndTarget(target, 1.5F);
                        torpedo.leaveGroup(false);
                    }
                    world.spawnEntity(torpedo);
                }
            }
        }
    }

    static class TargetAttackerGoal extends TrackTargetGoal {

        private static final TargetPredicate VALID_AVOIDABLES_PREDICATE = TargetPredicate.createAttackable().ignoreVisibility().ignoreDistanceScalingFactor();
        private int lastAttackedTime;
        private final Class<?>[] noRevengeTypes;

        private final SubcaelicDuxEntity dux;

        public TargetAttackerGoal(SubcaelicDuxEntity dux, Class<?>... noRevengeTypes) {
            super(dux, true);
            this.dux = dux;
            this.noRevengeTypes = noRevengeTypes;
            this.setControls(EnumSet.of(Goal.Control.TARGET));
        }

        @Override
        public boolean canStart() {
            int i = this.mob.getLastAttackedTime();
            LivingEntity livingEntity = this.mob.getAttacker();
            if (i != this.lastAttackedTime && livingEntity != null) {
                for(Class<?> class_ : this.noRevengeTypes) {
                    if (class_.isAssignableFrom(livingEntity.getClass())) {
                        return false;
                    }
                }

                return this.canTrack(livingEntity, VALID_AVOIDABLES_PREDICATE);
            } else {
                return false;
            }
        }

        @Override
        public void start() {
            LivingEntity target = this.mob.getAttacker();
            if(target instanceof PlayerEntity) {
                this.dux.universallyAnger();
                this.lastAttackedTime = this.mob.getLastAttackedTime();
            } else {
                this.mob.setTarget(target);
                this.target = this.mob.getTarget();
                this.lastAttackedTime = this.mob.getLastAttackedTime();
                this.maxTimeWithoutVisibility = 300;
            }

            super.start();
        }
    }
}

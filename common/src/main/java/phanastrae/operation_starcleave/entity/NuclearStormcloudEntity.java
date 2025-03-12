package phanastrae.operation_starcleave.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import phanastrae.operation_starcleave.block.StellarFarmlandBlock;
import phanastrae.operation_starcleave.entity.projectile.NuclearStardropEntity;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;
import phanastrae.operation_starcleave.world.firmament.Firmament;

public class NuclearStormcloudEntity extends Entity {
    public static String KEY_AGE = "age";

    private int age;

    public NuclearStormcloudEntity(EntityType<? extends NuclearStormcloudEntity> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
    }

    public NuclearStormcloudEntity(Level level, double x, double y, double z) {
        this(OperationStarcleaveEntityTypes.NUCLEAR_STORMCLOUD, level);
        this.setPos(x, y, z);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt(KEY_AGE, this.age);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if(tag.contains(KEY_AGE, Tag.TAG_INT)) {
            this.age = tag.getInt(KEY_AGE);
        }
    }

    @Override
    public void tick() {
        super.tick();

        Level level = this.level();
        RandomSource random = this.getRandom();

        if(level.isClientSide()) {
            this.spawnParticles();
        }

        this.move(MoverType.SELF, this.getDeltaMovement());

        if(!level.isClientSide()) {
            this.adjustVelocity();

            if(this.age > 60 && (random.nextInt(49) == 0 || this.age % 49 == 0)) {
                this.launchSeed();
            }
        }

        this.age++;
        Firmament firmament = Firmament.fromLevel(level);
        boolean starlit = firmament != null && StellarFarmlandBlock.isStarlit(level, this.blockPosition(), firmament);
        if(!starlit) {
            this.age += 8;
        }

        if(!level.isClientSide() && this.age >= 1000) {
            this.discard();
        }
    }

    public void spawnParticles() {
        Level level = this.level();
        RandomSource random = this.getRandom();

        for(int i = 0; i < 70; i++) {
            level.addParticle(
                    random.nextInt(4) == 0 ? OperationStarcleaveParticleTypes.LARGE_GLIMMER_SMOKE : ParticleTypes.LARGE_SMOKE,
                    this.getX() + (random.nextFloat() * 2 - 1) * this.getBbWidth() / 2F,
                    this.getY() + (random.nextFloat() * 2) * this.getBbHeight(),
                    this.getZ() + (random.nextFloat() * 2 - 1) * this.getBbWidth() / 2F,
                    random.nextGaussian() * 0.15,
                    random.nextGaussian() * 0.15,
                    random.nextGaussian() * 0.15
            );
        }
        for(int i = 0; i < 10; i++) {
            level.addParticle(
                    ParticleTypes.FALLING_OBSIDIAN_TEAR,
                    this.getX() + (random.nextFloat() * 2 - 1) * this.getBbWidth() / 2F,
                    this.getY() + 0.25 * (random.nextFloat() * 2) * this.getBbHeight(),
                    this.getZ() + (random.nextFloat() * 2 - 1) * this.getBbWidth() / 2F,
                    random.nextGaussian() * 0.15,
                    random.nextGaussian() * 0.05,
                    random.nextGaussian() * 0.15
            );
        }
    }

    public void adjustVelocity() {
        Level level = this.level();
        RandomSource random = this.getRandom();

        int height = level.getHeight(Heightmap.Types.MOTION_BLOCKING, this.getBlockX(), this.getBlockZ());
        int targetHeight = height + 9;
        double yDif = targetHeight - this.getY();

        double targetYaw = random.nextFloat() * Math.TAU;
        double cosYaw = Math.cos(targetYaw);
        double sinYaw = Math.sin(targetYaw);

        double vy = yDif * 0.07;
        float maxVY = 1.5F;
        if(Math.abs(vy) > maxVY) {
            vy = maxVY * vy / Math.abs(vy);
        }
        Vec3 targetVelocity = new Vec3(cosYaw, vy, sinYaw);

        Vec3 currentVelocity = this.getDeltaMovement();

        Vec3 newVelocity = currentVelocity.lerp(targetVelocity, 0.05);

        float targetSpeed = 0.4F;
        Vec3 newHorizontalVelocity = newVelocity.multiply(1, 0, 1).normalize().scale(targetSpeed);

        this.setDeltaMovement(newHorizontalVelocity.x, newVelocity.y * 0.97, newHorizontalVelocity.z);
    }

    public void launchSeed() {
        Level level = this.level();
        RandomSource random = this.getRandom();

        Vec3 startPos = new Vec3(
                this.getX() + (random.nextFloat() * 2 - 1) * this.getBbWidth() / 2F,
                this.getY() + 0.25F * random.nextFloat() * this.getBbHeight(),
                this.getZ() + (random.nextFloat() * 2 - 1) * this.getBbWidth() / 2F
        );

        NuclearStardropEntity stardrop = new NuclearStardropEntity(
                startPos.x,
                startPos.y,
                startPos.z,
                Vec3.ZERO,
                level
        );
        stardrop.shoot(0, -1, 0, 0.5F, 10);
        level.addFreshEntity(stardrop);
    }

    public int getAge() {
        return this.age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}

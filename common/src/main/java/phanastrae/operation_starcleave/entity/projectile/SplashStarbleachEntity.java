package phanastrae.operation_starcleave.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityAttachment;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;
import phanastrae.operation_starcleave.entity.StarbleachChargeEntity;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;
import phanastrae.operation_starcleave.world.starbleach.Starbleach;

import java.util.function.Predicate;

public class SplashStarbleachEntity extends ThrowableItemProjectile implements ItemSupplier {
    public static final Predicate<LivingEntity> ON_PHLOGISTIC_FIRE = entity -> OperationStarcleaveEntityAttachment.fromEntity(entity).getPhlogisticFireTicks() > 0;

    private boolean canStarbleach = false;

    public SplashStarbleachEntity(EntityType<? extends SplashStarbleachEntity> entityType, Level level) {
        super(entityType, level);
    }

    public SplashStarbleachEntity(Level level, LivingEntity owner) {
        super(OperationStarcleaveEntityTypes.SPLASH_STARBLEACH, owner, level);
    }

    public SplashStarbleachEntity(Level level, double x, double y, double z) {
        super(OperationStarcleaveEntityTypes.SPLASH_STARBLEACH, x, y, z, level);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("CanStarbleach", this.canStarbleach);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("CanStarbleach", Tag.TAG_BYTE)) {
            this.canStarbleach = nbt.getBoolean("CanStarbleach");
        } else {
            this.canStarbleach = false;
        }
    }

    @Override
    public void tick() {
        Level level = this.level();
        if (level.isClientSide) {
            Vec3 vel = this.getDeltaMovement();
            RandomSource random = this.random;
            for (int i = 0; i < 4; i++) {
                level.addParticle(OperationStarcleaveParticleTypes.FIRMAMENT_GLIMMER,
                        this.getX(), this.getY(), this.getZ(),
                        vel.x * -0.2 + random.nextFloat() * 0.06 - 0.03,
                        vel.y * -0.2 + random.nextFloat() * 0.06 - 0.03,
                        vel.z * -0.2 + random.nextFloat() * 0.06 - 0.03
                );
            }
            if (random.nextBoolean()) {
                level.addParticle(OperationStarcleaveParticleTypes.STARBLEACH_SWIRL,
                        this.getX(), this.getY(), this.getZ(),
                        vel.x * -0.1 + random.nextFloat() * 0.04 - 0.02,
                        vel.y * -0.1 + random.nextFloat() * 0.04 - 0.01,
                        vel.z * -0.1 + random.nextFloat() * 0.04 - 0.02
                );
            }
        }
        super.tick();
    }

    @Override
    protected Item getDefaultItem() {
        return OperationStarcleaveItems.SPLASH_STARBLEACH_BOTTLE;
    }

    @Override
    protected double getDefaultGravity() {
        return 0.05F;
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        Level level = this.level();
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            applyStarbleach();

            if (this.canStarbleach) {
                BlockPos starbleachPos;
                BlockPos extinguishPos;
                if (hitResult instanceof BlockHitResult blockHitResult) {
                    starbleachPos = blockHitResult.getBlockPos();
                    extinguishPos = starbleachPos.offset(blockHitResult.getDirection().getNormal());
                } else {
                    starbleachPos = BlockPos.containing(hitResult.getLocation());
                    extinguishPos = starbleachPos;
                }

                starbleach(starbleachPos, serverLevel);
                extinguishFire(extinguishPos);
            }
            createStarbleachEffects(hitResult.getLocation(), serverLevel);

            this.discard();
        }
    }

    private void applyStarbleach() {
        AABB aabb = this.getBoundingBox().inflate(4.0, 2.0, 4.0);

        for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, aabb, ON_PHLOGISTIC_FIRE)) {
            double distSqr = this.distanceToSqr(entity);
            if (distSqr < 16.0) {
                OperationStarcleaveEntityAttachment osea = OperationStarcleaveEntityAttachment.fromEntity(entity);
                if (entity.isAlive()) {
                    RandomSource random = entity.getRandom();
                    entity.playSound(SoundEvents.GENERIC_EXTINGUISH_FIRE, 0.7F, 1.6F + (random.nextFloat() - random.nextFloat()) * 0.4F);

                    osea.setPhlogisticFireTicks(0);
                }
            }
        }
    }

    private void extinguishFire(BlockPos pos) {
        Level level = this.level();
        for (BlockPos targetPos : BlockPos.betweenClosed(pos.offset(-2, -1, -2), pos.offset(2, 1, 2))) {
            if (pos.distSqr(targetPos) < 16.0) {
                BlockState blockState = level.getBlockState(targetPos);
                if (blockState.is(OperationStarcleaveBlocks.PHLOGISTIC_FIRE)) {
                    level.destroyBlock(targetPos, false, this);
                }
            }
        }
    }

    public static void starbleach(BlockPos blockPos, ServerLevel level) {
        // directly bleach aoe
        BlockPos.MutableBlockPos blockPosMutable = new BlockPos.MutableBlockPos();
        for (int i = -2; i <= 2; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int k = -2; k <= 2; k++) {
                    if (i * i + j * j + k * k >= 6) continue;
                    blockPosMutable.set(blockPos.getX() + i, blockPos.getY() + j, blockPos.getZ() + k);
                    Starbleach.starbleachPos(level, blockPosMutable, level.getBlockState(blockPosMutable), Starbleach.StarbleachTarget.NO_FILLING, 20);
                }
            }
        }
        // spawn charges
        for (int i = 0; i < 7; i++) {
            StarbleachChargeEntity charge = Starbleach.spawnCharge(level, blockPos, 14 + 2 * i * i);
            if (charge != null) {
                charge.setDelay(level.getRandom().nextIntBetweenInclusive(2, 8));
            }
        }
    }

    public static void createStarbleachEffects(Vec3 pos, ServerLevel level) {
        level.sendParticles(OperationStarcleaveParticleTypes.FIRMAMENT_GLIMMER, pos.x, pos.y, pos.z,
                60,
                1, 0.5, 1,
                0.02
        );
        level.sendParticles(OperationStarcleaveParticleTypes.STARBLEACH_SWIRL, pos.x, pos.y, pos.z,
                25,
                0.7, 0.5, 0.7,
                0.08
        );

        level.playSeededSound(null, pos.x, pos.y, pos.z, SoundEvents.SPLASH_POTION_BREAK, SoundSource.BLOCKS, 2f, 1.2F + 0.3F * level.random.nextFloat(), level.random.nextLong());
    }

    public void setCanStarbleach(boolean canStarbleach) {
        this.canStarbleach = canStarbleach;
    }

    public boolean getCanStarbleach() {
        return this.canStarbleach;
    }
}

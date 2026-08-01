package phanastrae.operation_starcleave.entity.projectile;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;
import phanastrae.operation_starcleave.entity.OperationStarcleaveDamageTypes;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;

import java.util.ArrayList;
import java.util.List;

public class CometChargeEntity extends ThrowableProjectile {
    public static final String KEY_CAN_DESTROY = "can_destroy";

    private boolean canDestroy = true;

    public CometChargeEntity(EntityType<? extends CometChargeEntity> entityType, Level level) {
        super(entityType, level);
    }

    public CometChargeEntity(double x, double y, double z, Level level) {
        super(OperationStarcleaveEntityTypes.COMET_CHARGE, x, y, z, level);
    }

    public CometChargeEntity(LivingEntity shooter, Level level) {
        super(OperationStarcleaveEntityTypes.COMET_CHARGE, shooter, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean(KEY_CAN_DESTROY, this.canDestroy);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains(KEY_CAN_DESTROY, Tag.TAG_BYTE)) {
            this.canDestroy = nbt.getBoolean(KEY_CAN_DESTROY);
        }
    }

    @Override
    public void tick() {
        Level level = this.level();
        if (level.isClientSide) {
            Vec3 vel = this.getDeltaMovement();
            RandomSource random = this.random;
            double y = this.getY() + this.getBbHeight() / 2.0;
            if (random.nextBoolean()) {
                level.addParticle(OperationStarcleaveParticleTypes.STARBLEACH_SWIRL,
                        this.getX(), y, this.getZ(),
                        vel.x * -0.1 + random.nextFloat() * 0.04 - 0.02,
                        vel.y * -0.1 + random.nextFloat() * 0.04 - 0.01,
                        vel.z * -0.1 + random.nextFloat() * 0.04 - 0.02
                );
            }
        }
        super.tick();
    }

    @Override
    protected double getDefaultGravity() {
        return this.isInWater() ? 0.03 : 0.01;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity entity = result.getEntity();

        double speed = this.getDeltaMovement().length() * 20;
        float damage = (float) Math.clamp(speed * 0.6, 3, 8);

        entity.hurt(OperationStarcleaveDamageTypes.cometCharge(this.level(), this), damage);
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        Level level = this.level();
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            RandomSource random = level.getRandom();

            BlockPos hitPos;
            Direction faceDirection;
            if (result instanceof BlockHitResult blockHitResult) {
                hitPos = blockHitResult.getBlockPos();
                faceDirection = blockHitResult.getDirection();
            } else {
                hitPos = BlockPos.containing(result.getLocation());
                faceDirection = null;
            }

            if (this.canDestroy) {
                double speed = this.getDeltaMovement().length() * 20;
                float statMultiplier = (float) Math.clamp(speed * 0.15, 0.75, 1.5);

                float remainingAllowedDestroyTime = 3.0F * statMultiplier + 1.5F * random.nextFloat();
                float remainingAllowedExplosionDestruction = 18.0F * statMultiplier;

                List<BlockPos> positions = new ArrayList<>();
                for (Direction direction : Direction.values()) {
                    if (faceDirection == null || faceDirection.getAxis() != direction.getAxis()) {
                        positions.add(hitPos.offset(direction.getNormal()));
                    }
                }
                Util.shuffle(positions, random);
                positions.addFirst(hitPos);

                for (BlockPos pos : positions) {
                    BlockState state = level.getBlockState(pos);
                    Block block = state.getBlock();
                    float destroySpeed = state.getDestroySpeed(level, pos);
                    float explosionResistance = block.getExplosionResistance();

                    if (destroySpeed >= 0.0F && !state.is(BlockTags.INCORRECT_FOR_WOODEN_TOOL) && !state.canBeReplaced()
                            && explosionResistance < 6.5F
                            && remainingAllowedDestroyTime >= destroySpeed
                            && remainingAllowedExplosionDestruction >= explosionResistance
                    ) {
                        level.destroyBlock(pos, true, this);

                        if (destroySpeed > 0) {
                            remainingAllowedDestroyTime -= destroySpeed + 1.0F;
                        }
                        if (explosionResistance > 0) {
                            remainingAllowedExplosionDestruction -= explosionResistance + 4.0F;
                        }
                    }
                }
            }

            this.playSound(SoundEvents.GLASS_BREAK, 0.8F, 0.8F + (random.nextFloat() - random.nextFloat()) * 0.4F);
            this.playSound(SoundEvents.TUFF_BREAK, 0.8F, 0.8F + (random.nextFloat() - random.nextFloat()) * 0.4F);

            double y = this.getY() + this.getBbHeight() / 2.0;
            serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, OperationStarcleaveBlocks.STARDUST_BLOCK.defaultBlockState()),
                    this.getX(), y, this.getZ(),
                    12, 0.15, 0.15, 0.15,
                    0.06
            );
            serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, OperationStarcleaveBlocks.ASTERUBBLE.defaultBlockState()),
                    this.getX(), y, this.getZ(),
                    6, 0.1, 0.1, 0.1,
                    0.04
            );

            this.discard();
        }
    }

    public void setCanDestroy(boolean canDestroy) {
        this.canDestroy = canDestroy;
    }
}

package phanastrae.operation_starcleave.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.block.NucleosyntheseedBlock;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;

public class NucleosyntheseedEntity extends Entity implements TraceableEntity {
    private static final EntityDataAccessor<Integer> DATA_FUSE_ID = SynchedEntityData.defineId(NucleosyntheseedEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<BlockState> DATA_BLOCK_STATE_ID = SynchedEntityData.defineId(NucleosyntheseedEntity.class, EntityDataSerializers.BLOCK_STATE);
    private static final int DEFAULT_FUSE_TIME = 140;
    public static final String KEY_FUSE = "fuse";
    public static final String KEY_BLOCK_STATE = "block_state";

    @Nullable
    private LivingEntity owner;

    public NucleosyntheseedEntity(EntityType<? extends NucleosyntheseedEntity> entityType, Level level) {
        super(entityType, level);
        this.blocksBuilding = true;
    }

    public NucleosyntheseedEntity(Level level, double x, double y, double z, @Nullable LivingEntity owner) {
        this(OperationStarcleaveEntityTypes.NUCLEOSYNTHESEED, level);
        this.setPos(x, y, z);
        this.hop(level, 0.02, 0.2);
        this.setFuse(DEFAULT_FUSE_TIME);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.owner = owner;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_FUSE_ID, DEFAULT_FUSE_TIME);
        builder.define(DATA_BLOCK_STATE_ID, OperationStarcleaveBlocks.NUCLEOSYNTHESEED.defaultBlockState());
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putShort(KEY_FUSE, (short) this.getFuse());
        compound.put(KEY_BLOCK_STATE, NbtUtils.writeBlockState(this.getBlockState()));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.setFuse(compound.getShort(KEY_FUSE));
        if (compound.contains(KEY_BLOCK_STATE, Tag.TAG_COMPOUND)) {
            this.setBlockState(NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), compound.getCompound(KEY_BLOCK_STATE)));
        }
    }

    @Override
    public void restoreFrom(Entity entity) {
        super.restoreFrom(entity);
        if (entity instanceof NucleosyntheseedEntity seed) {
            this.owner = seed.owner;
        }
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Nullable
    public LivingEntity getOwner() {
        return this.owner;
    }

    @Override
    protected double getDefaultGravity() {
        return 0.04;
    }

    public void hop(Level level, double horizontalSpeed, double verticalSpeed) {
        double angle = level.random.nextDouble() * Math.TAU;
        Vec3 dm = this.getDeltaMovement();
        this.setDeltaMovement(0.8 * dm.x() - Math.sin(angle) * horizontalSpeed, verticalSpeed, 0.8 * dm.z() - Math.cos(angle) * horizontalSpeed);
    }

    @Override
    public void tick() {
        Level level = this.level();

        int fuse = this.getFuse() - 1;

        this.handlePortal();
        if (!level.isClientSide() && this.onGround()) {
            this.hop(level, 0.22, -this.getDeltaMovement().y() * 0.6 + 0.26);
            if (fuse > 7) {
                fuse = Math.max(7, fuse - 40);
            }
            level.playSound(null, this.getX(), this.getY() + 0.5, this.getZ(), SoundEvents.NOTE_BLOCK_BIT, SoundSource.BLOCKS, 1.0F, 0.5F + random.nextFloat());
        }
        this.applyGravity();
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.92));

        this.setFuse(fuse);
        if (fuse <= 0) {
            this.discard();
            if (!level.isClientSide) {
                this.explode();
            }
        } else {
            if (level.isClientSide) {
                if (this.random.nextInt(4) == 0) {
                    level.addParticle(OperationStarcleaveParticleTypes.NUCLEAR_SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 0.0, 0.0, 0.0);
                }
                if (this.random.nextInt(14) == 0) {
                    double lightningScale = 1.6 / Math.clamp(fuse / 28.0, 1.0, 5.0);
                    level.addParticle(OperationStarcleaveParticleTypes.NUCLEO_LIGHTNING, this.getX(), this.getY() + 0.5, this.getZ(),
                            this.random.nextGaussian() * lightningScale,
                            this.random.nextGaussian() * lightningScale,
                            this.random.nextGaussian() * lightningScale
                    );
                }
            }
        }
    }

    private void explode() {
        Level level = this.level();

        NucleosyntheseedBlock.detonate(this.level(), this.blockPosition());
        NucleosyntheseedBlock.instantlyIgniteNearbyHyperflammables(this.level(), this.blockPosition(), 6);

        // spawn lightning particles
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    OperationStarcleaveParticleTypes.NUCLEO_LIGHTNING,
                    this.getX(),
                    this.getY() + 0.5,
                    this.getZ(),
                    9,
                    0.25,
                    0.25,
                    0.25,
                    2.25
            );
        }

        level.playSound(null, this.getX(), this.getY() + 0.5, this.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.gameEvent(this, GameEvent.EXPLODE, this.position());
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.level().isClientSide) {
            Entity entity = source.getEntity();
            if (entity != null && source.is(DamageTypeTags.IS_PLAYER_ATTACK)) {
                Vec3 thisPos = this.position();
                Vec3 entityPos = entity.getPosition(1F);
                Vec3 offsetVec = thisPos.subtract(entityPos).normalize();
                Vec3 currentDm = this.getDeltaMovement();
                double dot = offsetVec.dot(currentDm);

                this.addDeltaMovement(offsetVec.scale(Math.clamp(0.5 - dot, 0.0, 0.5)));
            }
        }
        return super.hurt(source, amount);
    }

    public void setFuse(int life) {
        this.entityData.set(DATA_FUSE_ID, life);
    }

    public int getFuse() {
        return this.entityData.get(DATA_FUSE_ID);
    }

    public void setBlockState(BlockState blockState) {
        this.entityData.set(DATA_BLOCK_STATE_ID, blockState);
    }

    public BlockState getBlockState() {
        return this.entityData.get(DATA_BLOCK_STATE_ID);
    }

}

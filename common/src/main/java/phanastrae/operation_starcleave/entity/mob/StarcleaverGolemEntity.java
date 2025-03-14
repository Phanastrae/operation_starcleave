package phanastrae.operation_starcleave.entity.mob;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.advancement.criterion.OperationStarcleaveAdvancementCriteria;
import phanastrae.operation_starcleave.entity.ai.goal.FollowFavoriteGoal;
import phanastrae.operation_starcleave.entity.projectile.SplashStarbleachEntity;
import phanastrae.operation_starcleave.item.FirmamentManipulatorItem;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;
import phanastrae.operation_starcleave.sound.OperationStarcleaveSoundEvents;
import phanastrae.operation_starcleave.world.firmament.Firmament;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class StarcleaverGolemEntity extends AbstractGolem implements Bucketable {

    private static final EntityDataAccessor<Boolean> IGNITED = SynchedEntityData.defineId(StarcleaverGolemEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> PLUMMETING = SynchedEntityData.defineId(StarcleaverGolemEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> GUNPOWDER_TICKS = SynchedEntityData.defineId(StarcleaverGolemEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Optional<UUID>> FAVORITE_UUID = SynchedEntityData.defineId(StarcleaverGolemEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    public float drillBasePitch = -45;
    public float prevDrillBasePitch = -45;
    public float drillHeadAngle = 0;
    public float prevDrillHeadAngle = 0;
    public float drillTipAngle = 0;
    public float prevDrillTipAngle = 0;

    public boolean openingDoor;
    public float doorProgress;
    public float prevDoorProgress;

    private final List<UUID> launcherUuids = new ArrayList<>();

    public StarcleaverGolemEntity(EntityType<? extends AbstractGolem> entityType, Level world) {
        super(entityType, world);
        this.setPathfindingMalus(PathType.WATER, 0.0F);
        this.setPathfindingMalus(PathType.LAVA, 0.0F);
        this.setPathfindingMalus(PathType.DANGER_FIRE, 0.0F);
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, 0.0F);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.3));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.2, Ingredient.of(Items.GOLD_NUGGET), false));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.2, Ingredient.of(Items.GUNPOWDER), false));
        this.goalSelector.addGoal(4, new FollowFavoriteGoal(this, 1.2, 8, 4, 64));
        this.goalSelector.addGoal(6, new RandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 12.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 12.0)
                .add(Attributes.ARMOR, 8.0)
                .add(Attributes.MOVEMENT_SPEED, 0.32)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.ARMOR_TOUGHNESS, 3.0)
                .add(Attributes.ATTACK_DAMAGE, 2.0)
                .add(Attributes.STEP_HEIGHT, 1.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IGNITED, false);
        builder.define(PLUMMETING, false);
        builder.define(GUNPOWDER_TICKS, 0);
        builder.define(FAVORITE_UUID, Optional.empty());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);

        nbt.putBoolean("Ignited", this.isIgnited());
        nbt.putBoolean("Plummeting", this.isPlummeting());
        nbt.putInt("GunpowderTicks", this.getGunpowderTicks());

        if (this.getFavoriteUuid() != null) {
            nbt.putUUID("Favorite", this.getFavoriteUuid());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);

        readLegacy(nbt);

        if (nbt.getBoolean("Ignited")) {
            this.setIgnited(true);
        }

        if (nbt.getBoolean("Plummeting")) {
            this.setPlummeting(true);
        }

        if(nbt.contains("GunpowderTicks", Tag.TAG_INT)) {
            this.setGunpowderTicks(nbt.getInt("GunpowderTicks"));
        }

        if (nbt.hasUUID("Favorite")) {
            UUID uUID = nbt.getUUID("Favorite");
            try {
                this.setFavoriteUuid(uUID);
            } catch (Throwable var4) {
                // empty
            }
        }
    }

    public void readLegacy(CompoundTag nbt) {
        // consider removing in some distant future update, backwards compatability here isn't really that important
        if (nbt.getBoolean("ignited")) {
            this.setIgnited(true);
        }
        if (nbt.getBoolean("plummeting")) {
            this.setPlummeting(true);
        }
        if(nbt.contains("gunpowderTicks", Tag.TAG_INT)) {
            this.setGunpowderTicks(nbt.getInt("gunpowderTicks"));
        }
    }

    @Override
    public void tick() {
        Level world = this.level();
        if(this.isAlive()) {
            if(this.isIgnited()) {
                this.push(0, 0.085 + Mth.clamp(this.getDeltaMovement().y - 0.1, 0, 4) * 0.03, 0);
                if(this.getGunpowderTicks() > 0) {
                    this.setGunpowderTicks(this.getGunpowderTicks() - 1);
                }
                if(this.getGunpowderTicks() <= 0) {
                    this.setIgnited(false);
                }

                if(world.isClientSide) {
                    world.addParticle(
                            ParticleTypes.FIREWORK,
                            this.getX() + Mth.sin((float)Math.toRadians(this.yBodyRot)) * 0.25,
                            this.getY(),
                            this.getZ() - Mth.cos((float)Math.toRadians(this.yBodyRot)) * 0.25,
                            this.random.nextGaussian() * 0.05,
                            -this.getDeltaMovement().y * 0.5,
                            this.random.nextGaussian() * 0.05
                    );
                }

                if(!world.isClientSide) {
                    boolean collided = false;

                    for(int i = -1; i <= 1; i++) {
                        for(int j = -1; j <= 1; j++) {
                            float hw = this.getBbWidth() * 0.501f;

                            Vec3 drillTarget = this.position().add(hw * i, this.getBbHeight() + 0.125, hw * j);
                            BlockPos blockPos = new BlockPos(Mth.floor(drillTarget.x), Mth.floor(drillTarget.y), Mth.floor(drillTarget.z));
                            BlockState state = world.getBlockState(blockPos);

                            if(state.getCollisionShape(world, blockPos).isEmpty()) continue;
                            if(this.canDestroy(state, blockPos)) {
                                world.destroyBlock(blockPos, true, this);
                                int g = this.getGunpowderTicks() - 10;
                                if(g < 0) g = 0;
                                this.setGunpowderTicks(g);
                            } else {
                                collided = true;
                            }
                        }
                    }

                    if(collided) {
                        this.setIgnited(false);
                        this.clearLaunchers();
                    }
                }

                if(!world.isClientSide) {
                    if(this.position().y > world.getMaxBuildHeight() + 16) {
                        this.cleave();
                    }
                }
            }

            if(this.isPlummeting()) {
                if(!world.isClientSide) {
                    if(this.onGround()) {
                        this.setPlummeting(false);
                        world.explode(this, this.getX(), this.getY(), this.getZ(), 3, Level.ExplosionInteraction.MOB);
                        for(int i = 0; i < 6; i++) {
                            BlockPos pos = this.blockPosition().offset(random.nextInt(7) - 3, random.nextInt(7) - 3, random.nextInt(7) - 3);
                            SplashStarbleachEntity.starbleach(pos, this.level());
                        }
                    }
                }

                if(world.isClientSide) {
                    for(int i = 0; i < 8; i++) {
                        world.addParticle(
                                ParticleTypes.FLAME,
                                this.getX(),
                                this.getY() - 0.25,
                                this.getZ(),
                                this.random.nextGaussian() * 0.75,
                                this.getDeltaMovement().y * -0.95,
                                this.random.nextGaussian() * 0.75
                        );
                    }
                }
            }

            if(world.isClientSide) {
                updateAnimations();
            }
        }

        super.tick();
    }

    public void updateAnimations() {
            prevDrillBasePitch = drillBasePitch;
            prevDrillTipAngle = drillTipAngle;
            prevDrillHeadAngle = drillHeadAngle;

            if(this.isIgnited() || this.isPlummeting()) {
                drillBasePitch += 5;
                if(drillBasePitch > 0) {
                    drillBasePitch = 0;
                }
            } else {
                drillBasePitch -= 5;
                if(drillBasePitch < -45) {
                    drillBasePitch = -45;
                }
            }

            if(this.isIgnited()) {
                drillHeadAngle += 15;
                if(drillHeadAngle > 360) {
                    drillHeadAngle %= 360;
                }

                drillTipAngle += 10;
                if(drillTipAngle > 360) {
                    drillTipAngle %= 360;
                }
            } else {
                if(drillHeadAngle > 0) {
                    drillHeadAngle += 6;
                    if(drillHeadAngle > 360) {
                        drillHeadAngle = 0;
                    }
                }

                if(drillTipAngle > 0) {
                    drillTipAngle += 4;
                    if(drillTipAngle > 360) {
                        drillTipAngle = 0;
                    }
                }
            }

            this.prevDoorProgress = this.doorProgress;
            if(this.openingDoor) {
                this.doorProgress += 0.25f;
                if(this.doorProgress >= 1) {
                    this.doorProgress = 1;
                    this.openingDoor = false;
                }
            } else {
                this.doorProgress -= 0.25f;
                if(this.doorProgress <= 0) {
                    this.doorProgress = 0;
                }
            }
    }

    @Override
    protected int decreaseAirSupply(int air) {
        return air;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        if(damageSource.is(DamageTypeTags.IS_EXPLOSION)) {
            return true;
        }

        return super.isInvulnerableTo(damageSource);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if(!this.isAlive()) {
            return InteractionResult.FAIL;
        }

        Level world = this.level();
        ItemStack itemStack = player.getItemInHand(hand);

        if (itemStack.is(Items.BUCKET) && !this.isIgnited() && !this.isPlummeting()) {
            this.playSound(this.getPickupSound(), 1.0F, 1.0F);
            ItemStack itemStack2 = this.getBucketItemStack();
            this.saveToBucketTag(itemStack2);
            ItemStack itemStack3 = ItemUtils.createFilledResult(itemStack, player, itemStack2, false);
            player.setItemInHand(hand, itemStack3);
            if (!world.isClientSide) {
                CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer)player, itemStack2);
            }

            this.discard();
            return InteractionResult.sidedSuccess(world.isClientSide);
        }

        if(itemStack.is(Items.GOLD_NUGGET)) {
            boolean consumeItem = false;
            if(!this.isFavorite(player)) {
                this.setFavorite(player);
                consumeItem = true;
            }

            float oldHealth = this.getHealth();
            this.heal(10.0F);
            if(this.getHealth() != oldHealth) {
                consumeItem = true;
            }

            if (!consumeItem) {
                return InteractionResult.PASS;
            } else {
                float g = 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F;
                this.playSound(this.getAmbientSound(), 1.0F, g);
                if (!player.getAbilities().instabuild) {
                    itemStack.shrink(1);
                }

                this.level().broadcastEntityEvent(this, EntityEvent.TAMING_SUCCEEDED);
                sendOpenDoor();
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        }

        if(itemStack.is(Items.GUNPOWDER) && this.getGunpowderTicks() + 60 <= 600) {
            SoundEvent soundEvent = SoundEvents.SAND_PLACE;
            world.playSound(player, this.getX(), this.getY(), this.getZ(), soundEvent, this.getSoundSource(), 1.0F, this.random.nextFloat() * 0.4F + 0.8F);
            if (!world.isClientSide) {
                this.setGunpowderTicks(this.getGunpowderTicks() + 60);
            }
            if (!player.getAbilities().instabuild) {
                itemStack.shrink(1);
            }

            sendOpenDoor();
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        if (player.getAbilities().mayBuild && this.getGunpowderTicks() > 20 && !this.isIgnited() && !this.isPlummeting() && itemStack.is(ItemTags.CREEPER_IGNITERS)) {
            SoundEvent soundEvent = itemStack.is(Items.FIRE_CHARGE) ? SoundEvents.FIRECHARGE_USE : SoundEvents.FLINTANDSTEEL_USE;
            world.playSound(player, this.getX(), this.getY(), this.getZ(), soundEvent, this.getSoundSource(), 1.0F, this.random.nextFloat() * 0.4F + 0.8F);
            if (!world.isClientSide) {
                this.setIgnited(true);
                if (!itemStack.isDamageableItem()) {
                    itemStack.shrink(1);
                } else {
                    itemStack.hurtAndBreak(1, player, getSlotForHand(hand));
                }

                if(player instanceof ServerPlayer serverPlayerEntity) {
                    OperationStarcleaveAdvancementCriteria.LAUNCH_STARCLEAVER_GOLEM.trigger(serverPlayerEntity);
                    this.addLauncher(serverPlayerEntity);
                }
                for(ServerPlayer serverPlayerEntity : world.getEntitiesOfClass(ServerPlayer.class, this.getBoundingBox().inflate(5.0))) {
                    if(serverPlayerEntity != player) {
                        OperationStarcleaveAdvancementCriteria.LAUNCH_STARCLEAVER_GOLEM.trigger(serverPlayerEntity);
                        this.addLauncher(serverPlayerEntity);
                    }
                }
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public boolean isOnFire() {
        return this.isPlummeting();
    }

    public boolean isIgnited() {
        return this.entityData.get(IGNITED);
    }

    public void setIgnited(boolean val) {
        this.entityData.set(IGNITED, val);
    }

    public boolean isPlummeting() {
        return this.entityData.get(PLUMMETING);
    }

    public void setPlummeting(boolean val) {
        this.entityData.set(PLUMMETING, val);
    }

    public int getGunpowderTicks() {
        return this.entityData.get(GUNPOWDER_TICKS);
    }

    public void setGunpowderTicks(int val) {
        this.entityData.set(GUNPOWDER_TICKS, val);
    }

    @Nullable
    public UUID getFavoriteUuid() {
        return (UUID)((Optional)this.entityData.get(FAVORITE_UUID)).orElse(null);
    }

    public void setFavoriteUuid(@Nullable UUID uuid) {
        this.entityData.set(FAVORITE_UUID, Optional.ofNullable(uuid));
    }

    public void setFavorite(@Nullable Player player) {
        this.setFavoriteUuid(player == null ? null : player.getUUID());
    }

    @Nullable
    public LivingEntity getFavorite() {
        UUID uUID = this.getFavoriteUuid();
        return uUID == null ? null : this.level().getPlayerByUUID(uUID);
    }

    public boolean isFavorite(LivingEntity entity) {
        return entity == this.getFavorite();
    }

    public void addLauncher(ServerPlayer serverPlayerEntity) {
        this.launcherUuids.add(serverPlayerEntity.getUUID());
    }

    public void forEachLauncher(Consumer<ServerPlayer> method) {
        for(UUID uuid : this.launcherUuids) {
            Entity e = this.level().getPlayerByUUID(uuid);
            if(e instanceof ServerPlayer spe) {
                method.accept(spe);
            }
        }
    }

    public void clearLaunchers() {
        this.launcherUuids.clear();
    }

    public boolean canDestroy(BlockState blockState, BlockPos blockPos) {
        if(!this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            return false;
        }
        if(blockState.is(BlockTags.WITHER_IMMUNE)) {
            return false;
        }

        return true;
    }

    public void cleave() {
        Firmament firmament = Firmament.fromLevel(this.level());
        if(firmament != null) {
            FirmamentManipulatorItem.fractureFirmament(firmament, this.getBlockX(), this.getBlockZ(), this.getRandom());
        }

        this.level().explode(this, this.getX(), this.getY(), this.getZ(), 7, Level.ExplosionInteraction.MOB);
        float angle = this.random.nextFloat() * Mth.TWO_PI;
        this.setDeltaMovement(2 * Mth.sin(angle), -3, 2 * Mth.cos(angle));
        this.setIgnited(false);
        this.setPlummeting(true);

        this.forEachLauncher((OperationStarcleaveAdvancementCriteria.CLEAVE_FIRMAMENT::trigger));
        this.clearLaunchers();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(OperationStarcleaveSoundEvents.ENTITY_STARCLEAVER_GOLEM_STEP, 0.45F, 0.8F + this.random.nextFloat() * 0.4F);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return OperationStarcleaveSoundEvents.ENTITY_STARCLEAVER_GOLEM_AMBIENT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return OperationStarcleaveSoundEvents.ENTITY_STARCLEAVER_GOLEM_DEATH;
    }

    @Override
    public boolean fromBucket() {
        return false;
    }

    @Override
    public void setFromBucket(boolean fromBucket) {
        // empty
    }

    @Override
    public void saveToBucketTag(ItemStack stack) {
        Bucketable.saveDefaultDataToBucketTag(this, stack);
        CustomData.update(DataComponents.BUCKET_ENTITY_DATA, stack, nbt -> {
            nbt.putInt("GunpowderTicks", this.getGunpowderTicks());
            if (this.getFavoriteUuid() != null) {
                nbt.putUUID("Favorite", this.getFavoriteUuid());
            }
        });
    }

    @Override
    public void loadFromBucketTag(CompoundTag nbt) {
        Bucketable.loadDefaultDataFromBucketTag(this, nbt);
        if(nbt.contains("GunpowderTicks", Tag.TAG_INT)) {
            this.setGunpowderTicks(nbt.getInt("GunpowderTicks"));
        }
        if (nbt.hasUUID("Favorite")) {
            UUID uUID = nbt.getUUID("Favorite");
            try {
                this.setFavoriteUuid(uUID);
            } catch (Throwable var4) {
                // empty
            }
        }
    }

    @Override
    public ItemStack getBucketItemStack() {
        return OperationStarcleaveItems.STARCLEAVER_GOLEM_BUCKET.getDefaultInstance();
    }

    @Override
    public SoundEvent getPickupSound() {
        return OperationStarcleaveSoundEvents.ENTITY_STARCLEAVER_GOLEM_AMBIENT;
    }

    @Override
    public void handleEntityEvent(byte status) {
        if (status == EntityEvent.TAMING_SUCCEEDED) {
            this.showEmoteParticle(true);
        } else if (status == EntityEvent.TAMING_FAILED) {
            this.showEmoteParticle(false);
        } else if (status == EntityEvent.USE_ITEM_COMPLETE) {
            this.openingDoor = true;
        } else {
            super.handleEntityEvent(status);
        }
    }

    public void sendOpenDoor() {
        this.level().broadcastEntityEvent(this, EntityEvent.USE_ITEM_COMPLETE);
    }

    protected void showEmoteParticle(boolean positive) {
        ParticleOptions particleEffect = ParticleTypes.HEART;
        if (!positive) {
            particleEffect = ParticleTypes.SMOKE;
        }

        for(int i = 0; i < 7; ++i) {
            double d = this.random.nextGaussian() * 0.02;
            double e = this.random.nextGaussian() * 0.02;
            double f = this.random.nextGaussian() * 0.02;
            this.level().addParticle(particleEffect, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), d, e, f);
        }
    }

    @Override
    public boolean canStandOnFluid(FluidState state) {
        return !state.isEmpty();
    }

    @Override
    public void setLastHurtByMob(@Nullable LivingEntity attacker) {
        if (attacker != null && this.level() instanceof ServerLevel) {
            if (this.isAlive() && attacker instanceof Player) {
                if(this.isFavorite(attacker)) {
                    this.level().broadcastEntityEvent(this, EntityEvent.TAMING_FAILED);
                    this.setFavorite(null);
                }
            }
        }
        super.setLastHurtByMob(attacker);
    }
}

package phanastrae.operation_starcleave.entity.mob;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
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

public class StarcleaverGolemEntity extends GolemEntity implements Bucketable {

    private static final TrackedData<Boolean> IGNITED = DataTracker.registerData(StarcleaverGolemEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> PLUMMETING = DataTracker.registerData(StarcleaverGolemEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> GUNPOWDER_TICKS = DataTracker.registerData(StarcleaverGolemEntity.class, TrackedDataHandlerRegistry.INTEGER);
    protected static final TrackedData<Optional<UUID>> FAVORITE_UUID = DataTracker.registerData(StarcleaverGolemEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);

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

    public StarcleaverGolemEntity(EntityType<? extends GolemEntity> entityType, World world) {
        super(entityType, world);
        this.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
        this.setPathfindingPenalty(PathNodeType.LAVA, 0.0F);
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 0.0F);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, 0.0F);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.3));
        this.goalSelector.add(2, new TemptGoal(this, 1.2, Ingredient.ofItems(Items.GOLD_NUGGET), false));
        this.goalSelector.add(3, new TemptGoal(this, 1.2, Ingredient.ofItems(Items.GUNPOWDER), false));
        this.goalSelector.add(4, new FollowFavoriteGoal(this, 1.2, 8, 4, 64));
        this.goalSelector.add(6, new WanderAroundGoal(this, 1.0));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 12.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 12.0)
                .add(EntityAttributes.GENERIC_ARMOR, 8.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.32)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 40.0)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 3.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0)
                .add(EntityAttributes.GENERIC_STEP_HEIGHT, 1.0);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(IGNITED, false);
        builder.add(PLUMMETING, false);
        builder.add(GUNPOWDER_TICKS, 0);
        builder.add(FAVORITE_UUID, Optional.empty());
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);

        nbt.putBoolean("Ignited", this.isIgnited());
        nbt.putBoolean("Plummeting", this.isPlummeting());
        nbt.putInt("GunpowderTicks", this.getGunpowderTicks());

        if (this.getFavoriteUuid() != null) {
            nbt.putUuid("Favorite", this.getFavoriteUuid());
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        readLegacy(nbt);

        if (nbt.getBoolean("Ignited")) {
            this.setIgnited(true);
        }

        if (nbt.getBoolean("Plummeting")) {
            this.setPlummeting(true);
        }

        if(nbt.contains("GunpowderTicks", NbtElement.INT_TYPE)) {
            this.setGunpowderTicks(nbt.getInt("GunpowderTicks"));
        }

        if (nbt.containsUuid("Favorite")) {
            UUID uUID = nbt.getUuid("Favorite");
            try {
                this.setFavoriteUuid(uUID);
            } catch (Throwable var4) {
                // empty
            }
        }
    }

    public void readLegacy(NbtCompound nbt) {
        // consider removing in some distant future update, backwards compatability here isn't really that important
        if (nbt.getBoolean("ignited")) {
            this.setIgnited(true);
        }
        if (nbt.getBoolean("plummeting")) {
            this.setPlummeting(true);
        }
        if(nbt.contains("gunpowderTicks", NbtElement.INT_TYPE)) {
            this.setGunpowderTicks(nbt.getInt("gunpowderTicks"));
        }
    }

    @Override
    public void tick() {
        World world = this.getWorld();
        if(this.isAlive()) {
            if(this.isIgnited()) {
                this.addVelocity(0, 0.085 + MathHelper.clamp(this.getVelocity().y - 0.1, 0, 4) * 0.03, 0);
                if(this.getGunpowderTicks() > 0) {
                    this.setGunpowderTicks(this.getGunpowderTicks() - 1);
                }
                if(this.getGunpowderTicks() <= 0) {
                    this.setIgnited(false);
                }

                if(world.isClient) {
                    world.addParticle(
                            ParticleTypes.FIREWORK,
                            this.getX() + MathHelper.sin((float)Math.toRadians(this.bodyYaw)) * 0.25,
                            this.getY(),
                            this.getZ() - MathHelper.cos((float)Math.toRadians(this.bodyYaw)) * 0.25,
                            this.random.nextGaussian() * 0.05,
                            -this.getVelocity().y * 0.5,
                            this.random.nextGaussian() * 0.05
                    );
                }

                if(!world.isClient) {
                    boolean collided = false;

                    for(int i = -1; i <= 1; i++) {
                        for(int j = -1; j <= 1; j++) {
                            float hw = this.getWidth() * 0.501f;

                            Vec3d drillTarget = this.getPos().add(hw * i, this.getHeight() + 0.125, hw * j);
                            BlockPos blockPos = new BlockPos(MathHelper.floor(drillTarget.x), MathHelper.floor(drillTarget.y), MathHelper.floor(drillTarget.z));
                            BlockState state = world.getBlockState(blockPos);

                            if(state.getCollisionShape(world, blockPos).isEmpty()) continue;
                            if(this.canDestroy(state, blockPos)) {
                                world.breakBlock(blockPos, true, this);
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

                if(!world.isClient) {
                    if(this.getPos().y > world.getTopY() + 16) {
                        this.cleave();
                    }
                }
            }

            if(this.isPlummeting()) {
                if(!world.isClient) {
                    if(this.isOnGround()) {
                        this.setPlummeting(false);
                        world.createExplosion(this, this.getX(), this.getY(), this.getZ(), 3, World.ExplosionSourceType.MOB);
                        for(int i = 0; i < 6; i++) {
                            BlockPos pos = this.getBlockPos().add(random.nextInt(7) - 3, random.nextInt(7) - 3, random.nextInt(7) - 3);
                            SplashStarbleachEntity.starbleach(pos, this.getWorld());
                        }
                    }
                }

                if(world.isClient) {
                    for(int i = 0; i < 8; i++) {
                        world.addParticle(
                                ParticleTypes.FLAME,
                                this.getX(),
                                this.getY() - 0.25,
                                this.getZ(),
                                this.random.nextGaussian() * 0.75,
                                this.getVelocity().y * -0.95,
                                this.random.nextGaussian() * 0.75
                        );
                    }
                }
            }

            if(world.isClient) {
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
    protected int getNextAirUnderwater(int air) {
        return air;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        if(damageSource.isIn(DamageTypeTags.IS_EXPLOSION)) {
            return true;
        }

        return super.isInvulnerableTo(damageSource);
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        if(!this.isAlive()) {
            return ActionResult.FAIL;
        }

        World world = this.getWorld();
        ItemStack itemStack = player.getStackInHand(hand);

        if (itemStack.isOf(Items.BUCKET) && !this.isIgnited() && !this.isPlummeting()) {
            this.playSound(this.getBucketFillSound(), 1.0F, 1.0F);
            ItemStack itemStack2 = this.getBucketItem();
            this.copyDataToStack(itemStack2);
            ItemStack itemStack3 = ItemUsage.exchangeStack(itemStack, player, itemStack2, false);
            player.setStackInHand(hand, itemStack3);
            if (!world.isClient) {
                Criteria.FILLED_BUCKET.trigger((ServerPlayerEntity)player, itemStack2);
            }

            this.discard();
            return ActionResult.success(world.isClient);
        }

        if(itemStack.isOf(Items.GOLD_NUGGET)) {
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
                return ActionResult.PASS;
            } else {
                float g = 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F;
                this.playSound(this.getAmbientSound(), 1.0F, g);
                if (!player.getAbilities().creativeMode) {
                    itemStack.decrement(1);
                }

                this.getWorld().sendEntityStatus(this, EntityStatuses.ADD_POSITIVE_PLAYER_REACTION_PARTICLES);
                sendOpenDoor();
                return ActionResult.success(this.getWorld().isClient);
            }
        }

        if(itemStack.isOf(Items.GUNPOWDER) && this.getGunpowderTicks() + 60 <= 600) {
            SoundEvent soundEvent = SoundEvents.BLOCK_SAND_PLACE;
            world.playSound(player, this.getX(), this.getY(), this.getZ(), soundEvent, this.getSoundCategory(), 1.0F, this.random.nextFloat() * 0.4F + 0.8F);
            if (!world.isClient) {
                this.setGunpowderTicks(this.getGunpowderTicks() + 60);
            }
            if (!player.getAbilities().creativeMode) {
                itemStack.decrement(1);
            }

            sendOpenDoor();
            return ActionResult.success(this.getWorld().isClient);
        }

        if (player.getAbilities().allowModifyWorld && this.getGunpowderTicks() > 20 && !this.isIgnited() && !this.isPlummeting() && itemStack.isIn(ItemTags.CREEPER_IGNITERS)) {
            SoundEvent soundEvent = itemStack.isOf(Items.FIRE_CHARGE) ? SoundEvents.ITEM_FIRECHARGE_USE : SoundEvents.ITEM_FLINTANDSTEEL_USE;
            world.playSound(player, this.getX(), this.getY(), this.getZ(), soundEvent, this.getSoundCategory(), 1.0F, this.random.nextFloat() * 0.4F + 0.8F);
            if (!world.isClient) {
                this.setIgnited(true);
                if (!itemStack.isDamageable()) {
                    itemStack.decrement(1);
                } else {
                    itemStack.damage(1, player, getSlotForHand(hand));
                }

                if(player instanceof ServerPlayerEntity serverPlayerEntity) {
                    OperationStarcleaveAdvancementCriteria.LAUNCH_STARCLEAVER_GOLEM.trigger(serverPlayerEntity);
                    this.addLauncher(serverPlayerEntity);
                }
                for(ServerPlayerEntity serverPlayerEntity : world.getNonSpectatingEntities(ServerPlayerEntity.class, this.getBoundingBox().expand(5.0))) {
                    if(serverPlayerEntity != player) {
                        OperationStarcleaveAdvancementCriteria.LAUNCH_STARCLEAVER_GOLEM.trigger(serverPlayerEntity);
                        this.addLauncher(serverPlayerEntity);
                    }
                }
            }

            return ActionResult.success(this.getWorld().isClient);
        }

        return super.interactMob(player, hand);
    }

    @Override
    public boolean isOnFire() {
        return this.isPlummeting();
    }

    public boolean isIgnited() {
        return this.dataTracker.get(IGNITED);
    }

    public void setIgnited(boolean val) {
        this.dataTracker.set(IGNITED, val);
    }

    public boolean isPlummeting() {
        return this.dataTracker.get(PLUMMETING);
    }

    public void setPlummeting(boolean val) {
        this.dataTracker.set(PLUMMETING, val);
    }

    public int getGunpowderTicks() {
        return this.dataTracker.get(GUNPOWDER_TICKS);
    }

    public void setGunpowderTicks(int val) {
        this.dataTracker.set(GUNPOWDER_TICKS, val);
    }

    @Nullable
    public UUID getFavoriteUuid() {
        return (UUID)((Optional)this.dataTracker.get(FAVORITE_UUID)).orElse(null);
    }

    public void setFavoriteUuid(@Nullable UUID uuid) {
        this.dataTracker.set(FAVORITE_UUID, Optional.ofNullable(uuid));
    }

    public void setFavorite(@Nullable PlayerEntity player) {
        this.setFavoriteUuid(player == null ? null : player.getUuid());
    }

    @Nullable
    public LivingEntity getFavorite() {
        UUID uUID = this.getFavoriteUuid();
        return uUID == null ? null : this.getWorld().getPlayerByUuid(uUID);
    }

    public boolean isFavorite(LivingEntity entity) {
        return entity == this.getFavorite();
    }

    public void addLauncher(ServerPlayerEntity serverPlayerEntity) {
        this.launcherUuids.add(serverPlayerEntity.getUuid());
    }

    public void forEachLauncher(Consumer<ServerPlayerEntity> method) {
        for(UUID uuid : this.launcherUuids) {
            Entity e = this.getWorld().getPlayerByUuid(uuid);
            if(e instanceof ServerPlayerEntity spe) {
                method.accept(spe);
            }
        }
    }

    public void clearLaunchers() {
        this.launcherUuids.clear();
    }

    public boolean canDestroy(BlockState blockState, BlockPos blockPos) {
        if(!this.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            return false;
        }
        if(blockState.isIn(BlockTags.WITHER_IMMUNE)) {
            return false;
        }

        return true;
    }

    public void cleave() {
        Firmament firmament = Firmament.fromWorld(this.getWorld());
        if(firmament != null) {
            FirmamentManipulatorItem.fractureFirmament(firmament, this.getBlockX(), this.getBlockZ(), this.getRandom());
        }

        this.getWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(), 7, World.ExplosionSourceType.MOB);
        float angle = this.random.nextFloat() * MathHelper.TAU;
        this.setVelocity(2 * MathHelper.sin(angle), -3, 2 * MathHelper.cos(angle));
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
    public boolean isFromBucket() {
        return false;
    }

    @Override
    public void setFromBucket(boolean fromBucket) {
        // empty
    }

    @Override
    public void copyDataToStack(ItemStack stack) {
        Bucketable.copyDataToStack(this, stack);
        NbtComponent.set(DataComponentTypes.BUCKET_ENTITY_DATA, stack, nbt -> {
            nbt.putInt("GunpowderTicks", this.getGunpowderTicks());
            if (this.getFavoriteUuid() != null) {
                nbt.putUuid("Favorite", this.getFavoriteUuid());
            }
        });
    }

    @Override
    public void copyDataFromNbt(NbtCompound nbt) {
        Bucketable.copyDataFromNbt(this, nbt);
        if(nbt.contains("GunpowderTicks", NbtElement.INT_TYPE)) {
            this.setGunpowderTicks(nbt.getInt("GunpowderTicks"));
        }
        if (nbt.containsUuid("Favorite")) {
            UUID uUID = nbt.getUuid("Favorite");
            try {
                this.setFavoriteUuid(uUID);
            } catch (Throwable var4) {
                // empty
            }
        }
    }

    @Override
    public ItemStack getBucketItem() {
        return OperationStarcleaveItems.STARCLEAVER_GOLEM_BUCKET.getDefaultStack();
    }

    @Override
    public SoundEvent getBucketFillSound() {
        return OperationStarcleaveSoundEvents.ENTITY_STARCLEAVER_GOLEM_AMBIENT;
    }

    @Override
    public void handleStatus(byte status) {
        if (status == EntityStatuses.ADD_POSITIVE_PLAYER_REACTION_PARTICLES) {
            this.showEmoteParticle(true);
        } else if (status == EntityStatuses.ADD_NEGATIVE_PLAYER_REACTION_PARTICLES) {
            this.showEmoteParticle(false);
        } else if (status == EntityStatuses.CONSUME_ITEM) {
            this.openingDoor = true;
        } else {
            super.handleStatus(status);
        }
    }

    public void sendOpenDoor() {
        this.getWorld().sendEntityStatus(this, EntityStatuses.CONSUME_ITEM);
    }

    protected void showEmoteParticle(boolean positive) {
        ParticleEffect particleEffect = ParticleTypes.HEART;
        if (!positive) {
            particleEffect = ParticleTypes.SMOKE;
        }

        for(int i = 0; i < 7; ++i) {
            double d = this.random.nextGaussian() * 0.02;
            double e = this.random.nextGaussian() * 0.02;
            double f = this.random.nextGaussian() * 0.02;
            this.getWorld().addParticle(particleEffect, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), d, e, f);
        }
    }

    @Override
    public boolean canWalkOnFluid(FluidState state) {
        return !state.isEmpty();
    }

    @Override
    public void setAttacker(@Nullable LivingEntity attacker) {
        if (attacker != null && this.getWorld() instanceof ServerWorld) {
            if (this.isAlive() && attacker instanceof PlayerEntity) {
                if(this.isFavorite(attacker)) {
                    this.getWorld().sendEntityStatus(this, EntityStatuses.ADD_NEGATIVE_PLAYER_REACTION_PARTICLES);
                    this.setFavorite(null);
                }
            }
        }
        super.setAttacker(attacker);
    }
}

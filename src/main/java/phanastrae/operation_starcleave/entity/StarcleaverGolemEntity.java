package phanastrae.operation_starcleave.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import phanastrae.operation_starcleave.advancement.criterion.OperationStarcleaveAdvancementCriteria;
import phanastrae.operation_starcleave.item.FirmamentManipulatorItem;
import phanastrae.operation_starcleave.world.firmament.Firmament;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class StarcleaverGolemEntity extends GolemEntity {

    private static final TrackedData<Boolean> IGNITED = DataTracker.registerData(StarcleaverGolemEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> PLUMMETING = DataTracker.registerData(StarcleaverGolemEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public float drillBasePitch = -45;
    public float prevDrillBasePitch = -45;
    public float drillHeadAngle = 0;
    public float prevDrillHeadAngle = 0;
    public float drillTipAngle = 0;
    public float prevDrillTipAngle = 0;

    private final List<UUID> launcherUuids = new ArrayList<>();

    protected StarcleaverGolemEntity(EntityType<? extends GolemEntity> entityType, World world) {
        super(entityType, world);
        this.setStepHeight(1.0F);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.2));
        this.goalSelector.add(3, new TemptGoal(this, 1.1, Ingredient.ofItems(Items.GOLD_NUGGET), false));
        this.goalSelector.add(6, new WanderAroundGoal(this, 1.0));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 12.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
    }

    public static DefaultAttributeContainer.Builder createStarcleaverGolemAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 12.0)
                .add(EntityAttributes.GENERIC_ARMOR, 8.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.32)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 40.0)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 3.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(IGNITED, false);
        this.dataTracker.startTracking(PLUMMETING, false);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);

        nbt.putBoolean("ignited", this.isIgnited());
        nbt.putBoolean("plummeting", this.isPlummeting());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        if (nbt.getBoolean("ignited")) {
            this.setIgnited(true);
        }

        if (nbt.getBoolean("plummeting")) {
            this.setPlummeting(true);
        }
    }

    @Override
    public void tick() {
        World world = this.getWorld();
        if(this.isAlive()) {
            if(this.isIgnited()) {
                this.addVelocity(0, 0.085 + MathHelper.clamp(this.getVelocity().y - 0.1, 0, 4) * 0.03, 0);

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
            }
        }

        super.tick();
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
        World world = this.getWorld();
        ItemStack itemStack = player.getStackInHand(hand);
        if (!this.isIgnited() && !this.isPlummeting() && itemStack.isIn(ItemTags.CREEPER_IGNITERS)) {
            SoundEvent soundEvent = itemStack.isOf(Items.FIRE_CHARGE) ? SoundEvents.ITEM_FIRECHARGE_USE : SoundEvents.ITEM_FLINTANDSTEEL_USE;
            world.playSound(player, this.getX(), this.getY(), this.getZ(), soundEvent, this.getSoundCategory(), 1.0F, this.random.nextFloat() * 0.4F + 0.8F);
            if (!world.isClient) {
                this.setIgnited(true);
                if (!itemStack.isDamageable()) {
                    itemStack.decrement(1);
                } else {
                    itemStack.damage(1, player, playerx -> playerx.sendToolBreakStatus(hand));
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
        } else {
            return super.interactMob(player, hand);
        }
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
            FirmamentManipulatorItem.formCrack(firmament, this.getBlockX(), this.getBlockZ(), this.getRandom());
        }

        this.getWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(), 7, World.ExplosionSourceType.MOB);
        float angle = this.random.nextFloat() * MathHelper.TAU;
        this.setVelocity(2 * MathHelper.sin(angle), -3, 2 * MathHelper.cos(angle));
        this.setIgnited(false);
        this.setPlummeting(true);

        this.forEachLauncher((OperationStarcleaveAdvancementCriteria.CLEAVE_FIRMAMENT::trigger));
        this.clearLaunchers();
    }
}

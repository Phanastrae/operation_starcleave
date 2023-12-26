package phanastrae.operation_starcleave.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
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
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import phanastrae.operation_starcleave.item.FirmamentManipulatorItem;
import phanastrae.operation_starcleave.world.firmament.Firmament;

public class StarcleaverGolemEntity extends GolemEntity {

    private static final TrackedData<Boolean> IGNITED = DataTracker.registerData(StarcleaverGolemEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> PLUMMETING = DataTracker.registerData(StarcleaverGolemEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    protected StarcleaverGolemEntity(EntityType<? extends GolemEntity> entityType, World world) {
        super(entityType, world);
        this.setStepHeight(1.0F);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.5));
        this.goalSelector.add(3, new TemptGoal(this, 1.1, Ingredient.ofItems(Items.GOLD_NUGGET), false));
        this.goalSelector.add(6, new WanderAroundGoal(this, 1.0));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 12.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
    }

    public static DefaultAttributeContainer.Builder createStarcleaverGolemAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 12.0)
                .add(EntityAttributes.GENERIC_ARMOR, 8.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.4)
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
            if(!world.isClient) {
                if(this.isOnFire() && !this.isIgnited()) {
                    this.setIgnited(true);
                }
            }

            if(this.isIgnited()) {
                this.addVelocity(0, 0.1, 0);

                if(world.isClient) {
                    world.addParticle(
                            ParticleTypes.FIREWORK,
                            this.getX(),
                            this.getY(),
                            this.getZ(),
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
                            if(state.isIn(BlockTags.DRAGON_IMMUNE) || state.isIn(BlockTags.WITHER_IMMUNE)) {
                                collided = true;
                            } else {
                                world.breakBlock(blockPos, true, this);
                            }
                        }
                    }

                    if(collided) {
                        this.setIgnited(false);
                    }
                }

                if(!world.isClient) {
                    if(this.getPos().y > world.getTopY() + 16) {
                        this.cleave();
                    }
                }

                this.wasIgnited = true;
            } else if(this.wasIgnited) {
                this.wasIgnited = false;
                if(this.getWorld().isClient) {
                    // TODO make firmament serverside
                    FirmamentManipulatorItem.formCrack(Firmament.getInstance(), this.getBlockX(), this.getBlockZ(), this.getRandom());
                }
            }

            if(this.isPlummeting()) {
                this.addVelocity(0, -0.01, 0);
                if(!world.isClient) {
                    if(this.isOnGround()) {
                        this.setPlummeting(false);
                        world.createExplosion(this, this.getX(), this.getY(), this.getZ(), 3, World.ExplosionSourceType.MOB);
                    }
                }
            }
        }

        super.tick();
    }

    private boolean wasIgnited = false; // TODO remove and implement properly

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
        ItemStack itemStack = player.getStackInHand(hand);
        if (!this.isIgnited() && !this.isPlummeting() && itemStack.isIn(ItemTags.CREEPER_IGNITERS)) {
            SoundEvent soundEvent = itemStack.isOf(Items.FIRE_CHARGE) ? SoundEvents.ITEM_FIRECHARGE_USE : SoundEvents.ITEM_FLINTANDSTEEL_USE;
            this.getWorld().playSound(player, this.getX(), this.getY(), this.getZ(), soundEvent, this.getSoundCategory(), 1.0F, this.random.nextFloat() * 0.4F + 0.8F);
            if (!this.getWorld().isClient) {
                this.setIgnited(true);
                if (!itemStack.isDamageable()) {
                    itemStack.decrement(1);
                } else {
                    itemStack.damage(1, player, playerx -> playerx.sendToolBreakStatus(hand));
                }
            }

            return ActionResult.success(this.getWorld().isClient);
        } else {
            return super.interactMob(player, hand);
        }
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

    public void cleave() {
        this.getWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(), 7, World.ExplosionSourceType.MOB);
        this.setVelocity(0, -3, 0);
        this.setIgnited(false);
        this.setPlummeting(true);
    }
}

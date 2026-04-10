package phanastrae.operation_starcleave.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.entity.projectile.BismuthBlastEntity;
import phanastrae.operation_starcleave.sound.OperationStarcleaveSoundEvents;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class BismuthBlasterItem extends ProjectileWeaponItem {
    public static final Predicate<ItemStack> IS_CANISTER = stack -> stack.is(OperationStarcleaveItems.BISBLAST_CANISTER);

    public BismuthBlasterItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        ChargedProjectiles chargedProjectiles = stack.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);

        boolean hasAmmoToLoad = !player.getProjectile(stack).isEmpty();
        boolean blasterHasAmmo = !chargedProjectiles.isEmpty();

        if (hasAmmoToLoad && canInsertInto(stack) && (!blasterHasAmmo || player.isCrouching())) {
            this.playStartLoadingSound(player);
            player.startUsingItem(usedHand);
            return InteractionResultHolder.consume(stack);
        } else if (blasterHasAmmo) {
            this.performShooting(level, player, usedHand, stack, 2.8F, 2.0F, null);
            return InteractionResultHolder.consume(stack);
        } else {
            return InteractionResultHolder.fail(stack);
        }
    }

    public static int getStoreSize() {
        return 7;
    }

    public static int getCanisterLoadTime() {
        return 2;
    }

    public static int getAmmoFiredPerShot() {
        return 1;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return getLoadSetupTime(stack, entity) + getStoreSize() * getCanisterLoadTime() + 3;
    }

    public static int getLoadSetupTime(ItemStack stack, LivingEntity shooter) {
        float time = EnchantmentHelper.modifyCrossbowChargingTime(stack, shooter, 0.3F);
        return Mth.floor(time * 20.0F);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        int usedTicks = this.getUseDuration(stack, livingEntity) - remainingUseDuration + 1;

        int loadSetupTime = getLoadSetupTime(stack, livingEntity);
        int canisterLoadTime = getCanisterLoadTime();

        if (usedTicks > loadSetupTime && (usedTicks - loadSetupTime) % canisterLoadTime == 0) {
            if (this.tryLoadProjectiles(livingEntity, stack)) {
                this.playLoadAmmoSound(livingEntity);
                if (!canInsertInto(stack)) {
                    this.playFullyLoadedSound(livingEntity);
                }
            }
        }
    }

    @Override
    public boolean useOnRelease(ItemStack stack) {
        return stack.is(this);
    }

    private static boolean canInsertInto(ItemStack stack) {
        ChargedProjectiles chargedProjectiles = stack.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
        return canInsertInto(chargedProjectiles);
    }

    private static boolean canInsertInto(ChargedProjectiles chargedProjectiles) {
        return currentStoredAmmo(chargedProjectiles) < getStoreSize();
    }

    public static int currentStoredAmmo(ItemStack stack) {
        ChargedProjectiles chargedProjectiles = stack.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
        return currentStoredAmmo(chargedProjectiles);
    }

    public static int currentStoredAmmo(ChargedProjectiles chargedProjectiles) {
        return chargedProjectiles.getItems().size();
    }

    public static boolean isCharged(ItemStack crossbowStack) {
        ChargedProjectiles chargedprojectiles = crossbowStack.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
        return !chargedprojectiles.isEmpty();
    }

    private boolean tryLoadProjectiles(LivingEntity shooter, ItemStack stack) {
        ChargedProjectiles chargedProjectiles = stack.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
        if (!canInsertInto(chargedProjectiles)) {
            return false;
        }

        List<ItemStack> drawnList = draw(stack, shooter.getProjectile(stack), shooter);
        if (drawnList.isEmpty()) {
            return false;
        } else {
            List<ItemStack> ammoList = new ArrayList<>(chargedProjectiles.getItems());
            ammoList.addAll(drawnList);

            stack.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.of(ammoList));
            return true;
        }
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return IS_CANISTER;
    }

    public void performShooting(
            Level level, LivingEntity shooter, InteractionHand hand, ItemStack weapon, float velocity, float inaccuracy, @Nullable LivingEntity target
    ) {
        if (level instanceof ServerLevel serverLevel) {
            ChargedProjectiles chargedProjectiles = weapon.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
            if (!chargedProjectiles.isEmpty()) {
                List<ItemStack> storedItems = new ArrayList<>(chargedProjectiles.getItems());
                List<ItemStack> firedItems = new ArrayList<>();

                int firedCount = getAmmoFiredPerShot();
                for (int i = 0; i < firedCount; i++) {
                    if (storedItems.isEmpty()) {
                        break;
                    } else {
                        firedItems.add(storedItems.removeFirst());
                    }
                }

                weapon.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.of(storedItems));

                if (!firedItems.isEmpty()) {
                    this.shoot(serverLevel, shooter, hand, weapon, firedItems, velocity, inaccuracy, shooter instanceof Player, target);
                    if (shooter instanceof ServerPlayer serverPlayer) {
                        serverPlayer.awardStat(Stats.ITEM_USED.get(weapon.getItem()));
                        serverPlayer.getCooldowns().addCooldown(this, 4);
                    }
                }
            }
        }
    }

    @Override
    protected void shootProjectile(LivingEntity shooter, Projectile projectile, int index, float velocity, float inaccuracy, float angle, @Nullable LivingEntity target) {
        projectile.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot() + angle, 0.0F, velocity, inaccuracy);
        this.playShootAmmoSound(shooter);
    }

    @Override
    protected Projectile createProjectile(Level level, LivingEntity shooter, ItemStack weapon, ItemStack ammo, boolean isCrit) {
        return new BismuthBlastEntity(level, shooter);
    }

    @Override
    public int getDefaultProjectileRange() {
        return 24;
    }

    @Override
    public int getEnchantmentValue() {
        return 20;
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
        return repairCandidate.is(OperationStarcleaveItems.STARFLAKED_BISMUTH);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        ChargedProjectiles chargedProjectiles = stack.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
        if (!chargedProjectiles.isEmpty()) {
            for (ItemStack ammo : chargedProjectiles.getItems()) {
                tooltipComponents.add(Component.translatable("item.minecraft.crossbow.projectile").append(CommonComponents.SPACE).append(ammo.getDisplayName()));
            }
        }
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.CROSSBOW;
    }

    public void playStartLoadingSound(Entity entity) {
        entity.playSound(
                OperationStarcleaveSoundEvents.BISMUTH_BLASTER_START_LOADING,
                1.0F,
                1.2F
        );
    }

    public void playLoadAmmoSound(Entity entity) {
        entity.playSound(
                OperationStarcleaveSoundEvents.BISMUTH_BLASTER_LOAD,
                1.0F,
                1.0F / (entity.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F
        );
    }

    public void playFullyLoadedSound(Entity entity) {
        entity.playSound(
                OperationStarcleaveSoundEvents.BISMUTH_BLASTER_FINISH_LOADING,
                1.0F,
                1.8F
        );
    }

    public void playShootAmmoSound(Entity entity) {
        // this only gets called serverside so need to make sure shooter also receives it
        playSound(
                entity,
                OperationStarcleaveSoundEvents.BISMUTH_BLASTER_SHOOT,
                1.0F,
                1.2F + 0.8F * entity.getRandom().nextFloat()
        );
    }

    public void playSound(Entity entity, SoundEvent soundEvent, float volume, float pitch) {
        entity.level().playSound(
                null,
                entity.getX(),
                entity.getY(),
                entity.getZ(),
                soundEvent,
                entity.getSoundSource(),
                volume,
                pitch
        );
    }
}

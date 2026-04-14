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
import phanastrae.operation_starcleave.item.enchantment.EnchantmentUtil;
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

        if (hasAmmoToLoad && canInsertInto(stack, player) && (!blasterHasAmmo || player.isCrouching())) {
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

    public static int getStoreSize(ItemStack stack, LivingEntity shooter) {
        int storageBonus = EnchantmentUtil.processStorageBonus(stack, shooter, 0);
        // base storage of 4
        // each level N adds another N storage
        // for a max of 4 + 1 + 2 + 3 + 4 = 14 storage
        return 4 + storageBonus * (storageBonus + 1) / 2;
    }

    public static int getLoadSetupTime(ItemStack stack, LivingEntity shooter) {
        // base time of 0.15s + 0.2s = 0.35s (7 ticks)
        // with quick charge III, time is reduced to 0.15s + 0.2s * 0.25 = 0.2s (4 ticks)
        float time = 0.15F + 0.2F * EnchantmentHelper.modifyCrossbowChargingTime(stack, shooter, 1.0F);
        // minimum value of 0 ticks of setup time
        return Math.max(0, Mth.floor(time * 20.0F));
    }

    public static int getCanisterLoadTime(ItemStack stack, LivingEntity shooter) {
        // base time of 0.2s (4 ticks)
        // with quick charge III, time is reduced to 0.2 * 0.25s = 0.05s (1 tick)
        float time = 0.2F * EnchantmentHelper.modifyCrossbowChargingTime(stack, shooter, 1.0F);
        // minimum value of 1 tick per load
        return Math.max(1, Mth.floor(time * 20.0F));
    }

    public static int getAmmoFiredPerShot() {
        return 1;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return getLoadSetupTime(stack, entity) + (getStoreSize(stack, entity) - 1) * getCanisterLoadTime(stack, entity) + 3;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        int usedTicks = this.getUseDuration(stack, livingEntity) - remainingUseDuration + 1;

        int loadSetupTime = getLoadSetupTime(stack, livingEntity);
        int canisterLoadTime = getCanisterLoadTime(stack, livingEntity);

        if (usedTicks >= loadSetupTime && (usedTicks - loadSetupTime) % canisterLoadTime == 0) {
            if (this.tryLoadProjectiles(livingEntity, stack)) {
                this.playLoadAmmoSound(livingEntity);
                if (!canInsertInto(stack, livingEntity)) {
                    this.playFullyLoadedSound(livingEntity);
                }
            }
        }
    }

    @Override
    public boolean useOnRelease(ItemStack stack) {
        return stack.is(this);
    }

    private static boolean canInsertInto(ItemStack stack, LivingEntity entity) {
        ChargedProjectiles chargedProjectiles = stack.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
        return currentStoredAmmo(chargedProjectiles) < getStoreSize(stack, entity);
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
        if (!canInsertInto(stack, shooter)) {
            return false;
        }

        List<ItemStack> drawnList = draw(stack, shooter.getProjectile(stack), shooter);
        if (drawnList.isEmpty()) {
            return false;
        } else {
            ChargedProjectiles chargedProjectiles = stack.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
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

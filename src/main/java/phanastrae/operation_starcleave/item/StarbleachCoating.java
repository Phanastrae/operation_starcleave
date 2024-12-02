package phanastrae.operation_starcleave.item;

import phanastrae.operation_starcleave.component.OperationStarcleaveComponentTypes;
import phanastrae.operation_starcleave.component.type.StarbleachComponent;
import phanastrae.operation_starcleave.entity.effect.OperationStarcleaveStatusEffects;

import static net.minecraft.core.component.DataComponents.FOOD;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class StarbleachCoating {

    public static void onEat(LivingEntity livingEntity, Level world, ItemStack itemStack) {
        if(world.isClientSide) {
            return;
        }

        if(hasStarbleachCoating(itemStack)) {
            livingEntity.addEffect(new MobEffectInstance(OperationStarcleaveStatusEffects.STARBLEACHED_INSIDES_ENTRY, 200, 1));
        }
    }

    public static boolean hasStarbleachCoating(ItemStack itemStack) {
        return itemStack.has(OperationStarcleaveComponentTypes.STARBLEACH_COMPONENT);
    }

    public static void addStarbleach(ItemStack itemStack) {
        itemStack.set(OperationStarcleaveComponentTypes.STARBLEACH_COMPONENT, new StarbleachComponent());
    }

    public static boolean canAddStarbleach(ItemStack itemStack) {
        FoodProperties foodComponent = itemStack.get(FOOD);
        if(foodComponent == null) {
            return false;
        }

        if(hasStarbleachCoating(itemStack)) {
            return false;
        }

        if(itemStack.is(OperationStarcleaveItems.STARBLEACH_BOTTLE)) {
            return false;
        }

        if(itemStack.is(OperationStarcleaveItems.STARFRUIT)) {
            return false;
        }

        if(itemStack.is(Items.CHORUS_FRUIT)) {
            return false;
        }

        return true;
    }

    public static Component getText() {
        return getText("operation_starcleave.tooltip.starbleached");
    }

    public static Component getText(String key) {
        float fl = (System.currentTimeMillis() % 4000) / 4000f;
        float twopi = 2 * Mth.PI;
        float red = Mth.sin(fl * twopi) * 0.2f + 0.8f;
        float green = Mth.sin((fl + 1/3f) * twopi) * 0.2f + 0.8f;
        float blue = Mth.sin((fl + 2/3f) * twopi) * 0.2f + 0.8f;

        int r = (int)(red * 255f) & 0xFF;
        int g = (int)(green * 255f) & 0xFF;
        int b = (int)(blue * 255f) & 0xFF;

        return Component.translatable(key).withColor(r | (g << 8) | (b << 16) | (0xFF << 24));
    }
}

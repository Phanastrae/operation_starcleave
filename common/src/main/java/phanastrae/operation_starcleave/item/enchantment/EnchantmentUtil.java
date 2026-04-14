package phanastrae.operation_starcleave.item.enchantment;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.apache.commons.lang3.mutable.MutableFloat;

public class EnchantmentUtil {

    public static int processStorageBonus(ItemStack stack, LivingEntity entity, int count) {
        MutableFloat mCount = new MutableFloat(count);
        runIterationOnItem(stack, (holder, lvl) -> modifyStorageBonus(holder.value(), entity.getRandom(), lvl, mCount));
        return Math.max(0, mCount.intValue());
    }

    private static void runIterationOnItem(ItemStack stack, EnchantmentVisitor visitor) {
        ItemEnchantments enchants = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchants.entrySet()) {
            visitor.accept(entry.getKey(), entry.getIntValue());
        }
    }

    @FunctionalInterface
    interface EnchantmentVisitor {
        void accept(Holder<Enchantment> enchantment, int level);
    }

    public static void modifyStorageBonus(Enchantment enchantment, RandomSource random, int enchantmentLevel, MutableFloat value) {
        enchantment.modifyUnfilteredValue(OperationStarcleaveEnchantmentEffectComponents.STORAGE_BONUS, random, enchantmentLevel, value);
    }
}

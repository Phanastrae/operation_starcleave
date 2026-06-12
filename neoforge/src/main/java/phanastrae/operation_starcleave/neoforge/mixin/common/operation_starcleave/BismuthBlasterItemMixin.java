package phanastrae.operation_starcleave.neoforge.mixin.common.operation_starcleave;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.extensions.IItemExtension;
import org.spongepowered.asm.mixin.Mixin;
import phanastrae.operation_starcleave.item.BismuthBlasterItem;
import phanastrae.operation_starcleave.item.enchantment.tag.OperationStarcleaveEnchantmentTags;

@Mixin(BismuthBlasterItem.class)
public abstract class BismuthBlasterItemMixin extends ProjectileWeaponItem implements IItemExtension {
    private BismuthBlasterItemMixin(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isPrimaryItemFor(ItemStack stack, Holder<Enchantment> enchantment) {
        if (enchantment.is(OperationStarcleaveEnchantmentTags.PRIMARY_BISMUTH_BLASTER)) {
            return true;
        } else {
            return super.isPrimaryItemFor(stack, enchantment);
        }
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        if (enchantment.is(OperationStarcleaveEnchantmentTags.SUPPORTS_BISMUTH_BLASTER)) {
            return true;
        } else {
            return super.supportsEnchantment(stack, enchantment);
        }
    }
}

package phanastrae.operation_starcleave.fabric.mixin;

import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import phanastrae.operation_starcleave.item.BismuthBlasterItem;
import phanastrae.operation_starcleave.item.enchantment.tag.OperationStarcleaveEnchantmentTags;

@Mixin(BismuthBlasterItem.class)
public abstract class BismuthBlasterItemMixin extends ProjectileWeaponItem implements FabricItem {
    private BismuthBlasterItemMixin(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canBeEnchantedWith(ItemStack stack, Holder<Enchantment> enchantment, EnchantingContext context) {
        if (context == EnchantingContext.PRIMARY && enchantment.is(OperationStarcleaveEnchantmentTags.PRIMARY_BISMUTH_BLASTER)) {
            return true;
        } else if (context == EnchantingContext.ACCEPTABLE && enchantment.is(OperationStarcleaveEnchantmentTags.SUPPORTS_BISMUTH_BLASTER)) {
            return true;
        } else {
            return super.canBeEnchantedWith(stack, enchantment, context);
        }
    }
}

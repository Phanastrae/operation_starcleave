package phanastrae.operation_starcleave.fabric.mixin.common.brewing;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;

@Mixin(PotionBrewing.class)
public class PotionBrewingMixin {

    @Inject(method = "hasMix", at = @At("HEAD"), cancellable = true)
    private void operation_starcleave$allowCustomMixes(ItemStack reagent, ItemStack potionItem, CallbackInfoReturnable<Boolean> cir) {
        if (reagent.is(OperationStarcleaveItems.STARBLEACH_BOTTLE) && potionItem.is(Items.GUNPOWDER)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "mix", at = @At("HEAD"), cancellable = true)
    private void operation_starcleave$doCustomMixing(ItemStack potion, ItemStack potionItem, CallbackInfoReturnable<ItemStack> cir) {
        if (potion.is(Items.GUNPOWDER) && potionItem.is(OperationStarcleaveItems.STARBLEACH_BOTTLE)) {
            cir.setReturnValue(new ItemStack(OperationStarcleaveItems.SPLASH_STARBLEACH_BOTTLE));
        }
    }
}

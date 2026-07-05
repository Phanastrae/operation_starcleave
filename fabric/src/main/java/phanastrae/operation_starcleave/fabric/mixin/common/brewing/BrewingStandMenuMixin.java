package phanastrae.operation_starcleave.fabric.mixin.common.brewing;

import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;

@Mixin(BrewingStandMenu.PotionSlot.class)
public class BrewingStandMenuMixin {

    @Inject(method = "mayPlaceItem", at = @At("HEAD"), cancellable = true)
    private static void operation_starcleave$allowPlacingCustomItems(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.is(OperationStarcleaveItems.STARBLEACH_BOTTLE)) {
            cir.setReturnValue(true);
        }
    }
}

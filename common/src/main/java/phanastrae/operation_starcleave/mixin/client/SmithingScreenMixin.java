package phanastrae.operation_starcleave.mixin.client;

import net.minecraft.client.gui.screens.inventory.SmithingScreen;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.item.NetheritePumpkinItem;

@Mixin(SmithingScreen.class)
public class SmithingScreenMixin {

    @Shadow private @Nullable ArmorStand armorStandPreview;

    @Inject(method = "updateArmorStandPreview", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;"), cancellable = true)
    private void operation_starcleave$equipArmorStandPumpkin(ItemStack stack, CallbackInfo ci) {
        ItemStack itemStack = stack.copy();
        Item item = stack.getItem();
        if (item instanceof NetheritePumpkinItem) {
            this.armorStandPreview.setItemSlot(EquipmentSlot.HEAD, itemStack);
            ci.cancel();
        }
    }

}

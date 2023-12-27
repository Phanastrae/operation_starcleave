package phanastrae.operation_starcleave.mixin.client;

import net.minecraft.client.gui.screen.ingame.SmithingScreen;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.item.NetheritePumpkinItem;

@Mixin(SmithingScreen.class)
public class SmithingScreenMixin {

    @Shadow
    private ArmorStandEntity armorStand;

    @Inject(method = "equipArmorStand", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"), cancellable = true)
    private void operation_starcleave$equipArmorStandPumpkin(ItemStack stack, CallbackInfo ci) {
        ItemStack itemStack = stack.copy();
        Item item = stack.getItem();
        if (item instanceof NetheritePumpkinItem) {
            this.armorStand.equipStack(EquipmentSlot.HEAD, itemStack);
            ci.cancel();
        }
    }

}

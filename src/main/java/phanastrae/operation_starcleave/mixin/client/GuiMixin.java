package phanastrae.operation_starcleave.mixin.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Shadow @Final private Minecraft minecraft;

    @Shadow @Final private static ResourceLocation PUMPKIN_BLUR_LOCATION;

    @Shadow protected abstract void renderTextureOverlay(GuiGraphics guiGraphics, ResourceLocation shaderLocation, float alpha);

    @Inject(method = "renderCameraOverlays", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;getArmor(I)Lnet/minecraft/world/item/ItemStack;", ordinal = 0, shift = At.Shift.BEFORE))
    private void operation_starcleave$netheritePumpkinOverlay(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci) {
        ItemStack itemStack = this.minecraft.player.getInventory().getArmor(3);
        if (itemStack.is(OperationStarcleaveBlocks.NETHERITE_PUMPKIN.asItem())) {
            this.renderTextureOverlay(context, PUMPKIN_BLUR_LOCATION, 1.0F);
        }
    }
}

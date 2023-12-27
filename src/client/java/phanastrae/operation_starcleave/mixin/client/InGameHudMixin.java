package phanastrae.operation_starcleave.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Final
    @Shadow
    private MinecraftClient client;

    @Final
    @Shadow
    private static Identifier PUMPKIN_BLUR;

    @Shadow
    private void renderOverlay(DrawContext context, Identifier texture, float opacity){}

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;getArmorStack(I)Lnet/minecraft/item/ItemStack;", ordinal = 0, shift = At.Shift.BEFORE))
    private void operation_starcleave$netheritePumpkinOverlay(DrawContext context, float tickDelta, CallbackInfo ci) {
        ItemStack itemStack = this.client.player.getInventory().getArmorStack(3);
        if (itemStack.isOf(OperationStarcleaveBlocks.NETHERITE_PUMPKIN.asItem())) {
            this.renderOverlay(context, PUMPKIN_BLUR, 1.0F);
        }
    }
}

package phanastrae.operation_starcleave.mixin.client.renderer.extra_layers;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.client.render.extras_baking.RenderExtras;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;renderWithTooltip(Lnet/minecraft/client/gui/GuiGraphics;IIF)V"))
    private void operation_starcleave$setupScreenRender(DeltaTracker deltaTracker, boolean renderLevel, CallbackInfo ci) {
        RenderExtras.setInScreen(true);
        RenderExtras.setPosOffset(0F, 0F, -5000F);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;renderWithTooltip(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", shift = At.Shift.AFTER))
    private void operation_starcleave$cleanupScreenRender(DeltaTracker deltaTracker, boolean renderLevel, CallbackInfo ci) {
        RenderExtras.resetPosOffset();
        RenderExtras.setInScreen(false);
    }
}

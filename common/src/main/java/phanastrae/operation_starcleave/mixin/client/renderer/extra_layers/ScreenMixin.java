package phanastrae.operation_starcleave.mixin.client.renderer.extra_layers;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.client.render.extras_baking.RenderExtras;

@Mixin(Screen.class)
public class ScreenMixin {

    @Inject(method = "renderWithTooltip", at = @At(value = "HEAD"))
    private void operation_starcleave$setupScreenRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        RenderExtras.setInScreen(true);
        RenderExtras.setPosOffset(0F, 0F, -5000F);
    }

    @Inject(method = "renderWithTooltip", at = @At(value = "RETURN"))
    private void operation_starcleave$cleanupScreenRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        RenderExtras.resetPosOffset();
        RenderExtras.setInScreen(false);
    }
}

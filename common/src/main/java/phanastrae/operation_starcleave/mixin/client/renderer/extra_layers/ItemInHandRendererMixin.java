package phanastrae.operation_starcleave.mixin.client.renderer.extra_layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.client.render.extras_baking.RenderExtras;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {

    @Inject(method = "renderHandsWithItems", at = @At("HEAD"))
    private void operation_starcleave$setupIridescence(float partialTicks, PoseStack poseStack, MultiBufferSource.BufferSource buffer, LocalPlayer playerEntity, int combinedLight, CallbackInfo ci) {
        float progress = (System.currentTimeMillis() % 3000) / 3000F;
        double angle = progress * Math.TAU;
        float dx = (float) Math.cos(angle) * 0.25F;
        float dz = (float) Math.sin(angle) * 0.25F;

        RenderExtras.setPosOffset(dx, 0.125F, dz);
    }

    @Inject(method = "renderHandsWithItems", at = @At("RETURN"))
    private void operation_starcleave$cleanupIridescence(float partialTicks, PoseStack poseStack, MultiBufferSource.BufferSource buffer, LocalPlayer playerEntity, int combinedLight, CallbackInfo ci) {
        RenderExtras.resetPosOffset();
    }
}

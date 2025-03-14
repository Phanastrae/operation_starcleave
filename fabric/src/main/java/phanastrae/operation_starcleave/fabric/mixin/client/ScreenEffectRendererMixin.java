package phanastrae.operation_starcleave.fabric.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.client.fluid.OperationStarcleaveClientFluids;

@Mixin(ScreenEffectRenderer.class)
public class ScreenEffectRendererMixin {

    @Inject(method = "renderScreenEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z", shift = At.Shift.AFTER))
    private static void operationStarcleave$renderScreenEffect(Minecraft minecraft, PoseStack poseStack, CallbackInfo ci) {
        OperationStarcleaveClientFluids.forEachXPGCF(xpgcf -> {
            if (minecraft.player.isEyeInFluid(xpgcf.getFluidTag())) {
                ResourceLocation texture = xpgcf.getScreenEffectTexture();
                if(texture != null) {
                    operationStarcleave$renderFluid(minecraft, poseStack, texture);
                }
            }
        });
    }

    private static void operationStarcleave$renderFluid(Minecraft minecraft, PoseStack poseStack, ResourceLocation textureLocation) {
        // this is essentially just renderWater() but with the texture swapped
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, textureLocation);

        BlockPos blockpos = BlockPos.containing(minecraft.player.getX(), minecraft.player.getEyeY(), minecraft.player.getZ());
        float brightness = LightTexture.getBrightness(minecraft.player.level().dimensionType(), minecraft.player.level().getMaxLocalRawBrightness(blockpos));

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(brightness, brightness, brightness, 0.1F);

        float uOffset = -minecraft.player.getYRot() / 64.0F;
        float zOffset = minecraft.player.getXRot() / 64.0F;
        Matrix4f matrix4f = poseStack.last().pose();

        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.addVertex(matrix4f, -1.0F, -1.0F, -0.5F).setUv(4.0F + uOffset, 4.0F + zOffset);
        bufferbuilder.addVertex(matrix4f, 1.0F, -1.0F, -0.5F).setUv(0.0F + uOffset, 4.0F + zOffset);
        bufferbuilder.addVertex(matrix4f, 1.0F, 1.0F, -0.5F).setUv(0.0F + uOffset, 0.0F + zOffset);
        bufferbuilder.addVertex(matrix4f, -1.0F, 1.0F, -0.5F).setUv(4.0F + uOffset, 0.0F + zOffset);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }
}

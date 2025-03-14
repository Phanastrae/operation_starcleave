package phanastrae.operation_starcleave.fabric.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.client.fluid.OperationStarcleaveClientFluids;

@Mixin(FogRenderer.class)
public class FogRendererMixin {

    @Shadow private static float fogRed;
    @Shadow private static float fogGreen;
    @Shadow private static float fogBlue;

    @Inject(method = "setupFog", at = @At(value = "RETURN"))
    private static void operation_starcleave$setupFog(Camera camera, FogRenderer.FogMode fogMode, float farPlaneDistance, boolean shouldCreateFog, float partialTick, CallbackInfo ci) {
        OperationStarcleaveClientFluids.XPlatGenericClientFluid xpgcf = OperationStarcleaveClientFluids.getXPGCF(camera);
        if(xpgcf != null) {
            xpgcf.setupFog(farPlaneDistance);
        }
    }

    @Inject(method = "setupColor", at = @At(value = "RETURN"))
    private static void operation_starcleave$setupFogColor(Camera activeRenderInfo, float partialTicks, ClientLevel level, int renderDistanceChunks, float bossColorModifier, CallbackInfo ci) {
        OperationStarcleaveClientFluids.XPlatGenericClientFluid xpgcf = OperationStarcleaveClientFluids.getXPGCF(activeRenderInfo);
        if(xpgcf != null) {
            Vector3f newFogColor = xpgcf.getFogColor();
            fogRed = newFogColor.x;
            fogGreen = newFogColor.y;
            fogBlue = newFogColor.z;

            RenderSystem.clearColor(fogRed, fogGreen, fogBlue, 0.0F);
        }
    }
}

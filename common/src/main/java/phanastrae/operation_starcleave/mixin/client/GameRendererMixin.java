package phanastrae.operation_starcleave.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.operation_starcleave.client.OperationStarcleaveClient;
import phanastrae.operation_starcleave.client.fluid.OperationStarcleaveClientFluids;
import phanastrae.operation_starcleave.client.render.ScreenShakeManager;
import phanastrae.operation_starcleave.client.render.shader.FirmamentPostShader;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;bobHurt(Lcom/mojang/blaze3d/vertex/PoseStack;F)V", shift = At.Shift.AFTER))
    private void operation_starcleave$screenShake(DeltaTracker tickCounter, CallbackInfo ci,
                                                  @Local(ordinal = 0) PoseStack matrices) {
        ScreenShakeManager.getInstance().updateScreenMatrices(matrices, tickCounter.getGameTimeDeltaPartialTick(false));
    }

    @Inject(method = "pick(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V", shift = At.Shift.BEFORE))
    private void operation_starcleave$updateFirmamentTarget(float tickDelta, CallbackInfo ci) {
        OperationStarcleaveClient.firmamentOutlineRenderer.updateHitTile(tickDelta);
    }

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderLevel(Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V", shift = At.Shift.AFTER))
    private void operation_starcleave$applyPostShaders(DeltaTracker tickCounter, CallbackInfo ci) {
        FirmamentPostShader.draw();
    }

    @Inject(method = "getFov", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;getFluidInCamera()Lnet/minecraft/world/level/material/FogType;", shift = At.Shift.AFTER))
    private void operation_starcleave$fluidTweakFOV(Camera activeRenderInfo, float partialTicks, boolean useFOVSetting, CallbackInfoReturnable<Double> cir, @Local(ordinal = 0) LocalDoubleRef LDR_d) {
        OperationStarcleaveClientFluids.XPlatGenericClientFluid xpgcf = OperationStarcleaveClientFluids.getXPGCF(activeRenderInfo);
        if(xpgcf != null) {
            LDR_d.set(LDR_d.get() * Mth.lerp(this.minecraft.options.fovEffectScale().get(), 1.0, xpgcf.getFovScaleFactor()));
        }
    }
}

package phanastrae.operation_starcleave.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.OperationStarcleaveClient;
import phanastrae.operation_starcleave.render.ScreenShakeManager;
import phanastrae.operation_starcleave.render.shader.FirmamentPostShader;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;tiltViewWhenHurt(Lnet/minecraft/client/util/math/MatrixStack;F)V", shift = At.Shift.AFTER))
    private void operation_starcleave$screenShake(RenderTickCounter tickCounter, CallbackInfo ci,
                                                  @Local(ordinal = 0) MatrixStack matrices) {
        ScreenShakeManager.getInstance().updateScreenMatrices(matrices, tickCounter.getTickDelta(false));
    }

    @Inject(method = "updateCrosshairTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;pop()V", shift = At.Shift.BEFORE))
    private void operation_starcleave$updateFirmamentTarget(float tickDelta, CallbackInfo ci) {
        OperationStarcleaveClient.FirmamentOutlineRenderer.updateHitTile(tickDelta);
    }

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;render(Lnet/minecraft/client/render/RenderTickCounter;ZLnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/GameRenderer;Lnet/minecraft/client/render/LightmapTextureManager;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V", shift = At.Shift.AFTER))
    private void operation_starcleave$applyPostShaders(RenderTickCounter tickCounter, CallbackInfo ci) {
        FirmamentPostShader.draw();
    }
}

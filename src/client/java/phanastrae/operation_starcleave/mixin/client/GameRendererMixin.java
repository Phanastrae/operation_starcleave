package phanastrae.operation_starcleave.mixin.client;

import net.minecraft.client.render.GameRenderer;
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
    private void operation_starcleave$screenShake(float tickDelta, long limitTime, MatrixStack matrices, CallbackInfo ci) {
        ScreenShakeManager.getInstance().updateScreenMatrices(matrices, tickDelta);
    }

    @Inject(method = "updateTargetedEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;pop()V", shift = At.Shift.BEFORE))
    private void operation_starcleave$updateFirmamentTarget(float tickDelta, CallbackInfo ci) {
        OperationStarcleaveClient.FirmamentOutlineRenderer.updateHitTile(tickDelta);
    }

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;FJZLnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/GameRenderer;Lnet/minecraft/client/render/LightmapTextureManager;Lorg/joml/Matrix4f;)V", shift = At.Shift.AFTER))
    private void operation_starcleave$applyPostShaders(float tickDelta, long limitTime, MatrixStack matrices, CallbackInfo ci) {
        FirmamentPostShader.draw();
    }
}

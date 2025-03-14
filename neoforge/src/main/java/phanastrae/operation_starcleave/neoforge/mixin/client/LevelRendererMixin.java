package phanastrae.operation_starcleave.neoforge.mixin.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.client.OperationStarcleaveClient;

import javax.annotation.Nullable;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Shadow @Nullable private ClientLevel level;

    @Inject(method = "allChanged", at = @At("HEAD"))
    private void operation_starcleave$allChanged(CallbackInfo ci) {
        if(this.level != null) {
            OperationStarcleaveClient.invalidateRenderState();
        }
    }
}

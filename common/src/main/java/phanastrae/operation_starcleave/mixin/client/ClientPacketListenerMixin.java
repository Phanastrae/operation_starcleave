package phanastrae.operation_starcleave.mixin.client;

import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.client.render.firmament.FirmamentTextureStorage;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    @Inject(method = "clearLevel", at = @At("RETURN"))
    private void operation_starcleave$unloadLevel(CallbackInfo ci) {
        FirmamentTextureStorage.getMainInstance().clearData();
    }
}

package phanastrae.operation_starcleave.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ChunkBatchSizeCalculator;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.client.duck.ClientPlayNetworkHandlerDuck;
import phanastrae.operation_starcleave.client.render.firmament.FirmamentTextureStorage;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin implements ClientPlayNetworkHandlerDuck {

    private ChunkBatchSizeCalculator operation_starcleave$firmamentRegionBatchSizeCalculator;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void operation_starcleave$onInit(Minecraft client, Connection clientConnection, CommonListenerCookie clientConnectionState, CallbackInfo ci) {
        this.operation_starcleave$firmamentRegionBatchSizeCalculator = new ChunkBatchSizeCalculator();
    }

    @Inject(method = "clearLevel", at = @At("RETURN"))
    private void operation_starcleave$unloadWorld(CallbackInfo ci) {
        FirmamentTextureStorage.getInstance().clearData();
    }

    @Override
    public ChunkBatchSizeCalculator operation_starcleave$getFirmamentRegionBatchSizeCalculator() {
        return this.operation_starcleave$firmamentRegionBatchSizeCalculator;
    }
}

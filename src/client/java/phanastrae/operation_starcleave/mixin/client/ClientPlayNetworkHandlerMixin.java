package phanastrae.operation_starcleave.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ChunkBatchSizeCalculator;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.network.OperationStarcleaveClientPlayNetworkHandler;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin implements OperationStarcleaveClientPlayNetworkHandler {

    private ChunkBatchSizeCalculator operation_starcleave$firmamentRegionBatchSizeCalculator;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void operation_starcleave$onInit(MinecraftClient client, ClientConnection clientConnection, ClientConnectionState clientConnectionState, CallbackInfo ci) {
        this.operation_starcleave$firmamentRegionBatchSizeCalculator = new ChunkBatchSizeCalculator();
    }


    @Override
    public ChunkBatchSizeCalculator operation_starcleave$getFirmamentRegionBatchSizeCalculator() {
        return this.operation_starcleave$firmamentRegionBatchSizeCalculator;
    }
}

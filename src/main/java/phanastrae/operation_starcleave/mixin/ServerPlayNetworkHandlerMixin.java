package phanastrae.operation_starcleave.mixin;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.server.network.FirmamentRegionDataSender;
import phanastrae.operation_starcleave.duck.ServerPlayNetworkHandlerDuck;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin implements ServerPlayNetworkHandlerDuck {

    FirmamentRegionDataSender firmamentRegionDataSender;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void operation_starcleave$onInit(MinecraftServer server, ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) {
        this.firmamentRegionDataSender = new FirmamentRegionDataSender(connection.isLocal());
    }

    @Override
    public FirmamentRegionDataSender operation_starcleave$getFirmamentRegionDataSender() {
        return this.firmamentRegionDataSender;
    }
}

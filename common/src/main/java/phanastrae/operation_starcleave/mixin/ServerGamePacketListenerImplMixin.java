package phanastrae.operation_starcleave.mixin;

import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.server.network.FirmamentRegionDataSender;
import phanastrae.operation_starcleave.duck.ServerGamePacketListenerImplDuckInterface;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin implements ServerGamePacketListenerImplDuckInterface {

    FirmamentRegionDataSender firmamentRegionDataSender;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void operation_starcleave$onInit(MinecraftServer server, Connection connection, ServerPlayer player, CommonListenerCookie clientData, CallbackInfo ci) {
        this.firmamentRegionDataSender = new FirmamentRegionDataSender(connection.isMemoryConnection());
    }

    @Override
    public FirmamentRegionDataSender operation_starcleave$getFirmamentRegionDataSender() {
        return this.firmamentRegionDataSender;
    }
}

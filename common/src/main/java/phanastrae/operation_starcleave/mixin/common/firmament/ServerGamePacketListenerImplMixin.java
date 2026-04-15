package phanastrae.operation_starcleave.mixin.common.firmament;

import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.duck.FirmamentRegionDataSenderHolder;
import phanastrae.operation_starcleave.server.network.FirmamentRegionDataSender;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin implements FirmamentRegionDataSenderHolder {

    @Unique
    private FirmamentRegionDataSender operation_starcleave$firmamentRegionDataSender;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void operation_starcleave$onInit(MinecraftServer server, Connection connection, ServerPlayer player, CommonListenerCookie clientData, CallbackInfo ci) {
        this.operation_starcleave$firmamentRegionDataSender = new FirmamentRegionDataSender();
    }

    @Override
    public FirmamentRegionDataSender operation_starcleave$getFirmamentRegionDataSender() {
        return this.operation_starcleave$firmamentRegionDataSender;
    }
}

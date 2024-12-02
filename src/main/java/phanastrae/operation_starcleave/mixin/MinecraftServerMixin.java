package phanastrae.operation_starcleave.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import phanastrae.operation_starcleave.server.network.FirmamentRegionDataSender;

import java.util.Iterator;
import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Inject(method = "tickChildren", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/PlayerChunkSender;sendNextChunks(Lnet/minecraft/server/level/ServerPlayer;)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void operation_starcleave$sendFirmamentData(BooleanSupplier shouldKeepTicking, CallbackInfo ci, Iterator var2, ServerPlayer serverPlayerEntity) {
        FirmamentRegionDataSender.getFirmamentRegionDataSender(serverPlayerEntity.connection).sendChunkBatches(serverPlayerEntity);
    }
}

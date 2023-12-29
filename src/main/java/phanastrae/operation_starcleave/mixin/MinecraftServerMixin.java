package phanastrae.operation_starcleave.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
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

    @Inject(method = "tickWorlds", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ChunkDataSender;sendChunkBatches(Lnet/minecraft/server/network/ServerPlayerEntity;)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void operation_starcleave$sendFirmamentData(BooleanSupplier shouldKeepTicking, CallbackInfo ci, Iterator var2, ServerPlayerEntity serverPlayerEntity) {
        FirmamentRegionDataSender.getFirmamentRegionDataSender(serverPlayerEntity.networkHandler).sendChunkBatches(serverPlayerEntity);
    }
}

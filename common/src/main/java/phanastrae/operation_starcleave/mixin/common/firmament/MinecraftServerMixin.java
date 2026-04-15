package phanastrae.operation_starcleave.mixin.common.firmament;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.server.network.FirmamentRegionDataSender;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Inject(method = "tickChildren", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/PlayerChunkSender;sendNextChunks(Lnet/minecraft/server/level/ServerPlayer;)V", shift = At.Shift.AFTER))
    private void operation_starcleave$sendFirmamentData(BooleanSupplier shouldKeepTicking, CallbackInfo ci, @Local ServerPlayer serverPlayerEntity) {
        FirmamentRegionDataSender.getFirmamentRegionDataSender(serverPlayerEntity.connection).sendRegions(serverPlayerEntity);
    }
}

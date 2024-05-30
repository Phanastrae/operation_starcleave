package phanastrae.operation_starcleave.mixin;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.duck.EntityDuck;
import phanastrae.operation_starcleave.network.packet.s2c.EntityPhlogisticFireS2CPacket;

@Mixin(EntityTrackerEntry.class)
public class EntityTrackerEntryMixin {
    @Shadow @Final private Entity entity;

    @Inject(method = "startTracking", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V", shift = At.Shift.AFTER))
    private void operation_starcleave$sendBonusPackets(ServerPlayerEntity player, CallbackInfo ci) {
        if(this.entity instanceof EntityDuck opsce) {
            if(opsce.operation_starcleave$getPhlogisticFireTicks() > 0) {
                ServerPlayNetworking.send(player, new EntityPhlogisticFireS2CPacket(this.entity, true));
            }
        }
    }
}

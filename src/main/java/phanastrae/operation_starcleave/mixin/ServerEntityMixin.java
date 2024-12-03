package phanastrae.operation_starcleave.mixin;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.duck.EntityDuck;
import phanastrae.operation_starcleave.network.packet.EntityPhlogisticFirePayload;
import phanastrae.operation_starcleave.services.XPlatInterface;

@Mixin(ServerEntity.class)
public class ServerEntityMixin {
    @Shadow @Final private Entity entity;

    @Inject(method = "addPairing", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V", shift = At.Shift.AFTER))
    private void operation_starcleave$sendBonusPackets(ServerPlayer player, CallbackInfo ci) {
        if(this.entity instanceof EntityDuck opsce) {
            if(opsce.operation_starcleave$getPhlogisticFireTicks() > 0) {
                XPlatInterface.INSTANCE.sendPayload(player, new EntityPhlogisticFirePayload(this.entity.getId(), true));
            }
        }
    }
}

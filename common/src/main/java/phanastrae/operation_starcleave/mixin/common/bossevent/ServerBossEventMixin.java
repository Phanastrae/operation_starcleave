package phanastrae.operation_starcleave.mixin.common.bossevent;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.duck.BossEventDuck;
import phanastrae.operation_starcleave.world.BossEventExtras;
import phanastrae.operation_starcleave.world.ServerBossEventExtras;

import java.util.Set;
import java.util.UUID;

@Mixin(ServerBossEvent.class)
public abstract class ServerBossEventMixin extends BossEvent implements BossEventDuck {
    @Shadow
    @Final
    private Set<ServerPlayer> players;

    private ServerBossEventMixin(UUID id, Component name, BossBarColor color, BossBarOverlay overlay) {
        super(id, name, color, overlay);
    }

    @Unique
    private ServerBossEventExtras operation_starcleave$extras;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void operation_starcleave$init(Component name, BossBarColor color, BossBarOverlay overlay, CallbackInfo ci) {
        ServerBossEvent event = (ServerBossEvent) (Object) this;
        this.operation_starcleave$extras = new ServerBossEventExtras(event);
    }

    @Override
    public BossEventExtras operation_starcleave$getExtras() {
        return this.operation_starcleave$extras;
    }

    @Inject(method = "addPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V", shift = At.Shift.AFTER))
    private void operation_starcleave$sendBonusPacketsOnAdd(ServerPlayer player, CallbackInfo ci) {
        ServerBossEventExtras extras = (ServerBossEventExtras) this.operation_starcleave$getExtras();
        extras.sendExtraPacketsOnAdd(player);
    }

    @Inject(method = "setVisible", at = @At(value = "RETURN", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V"))
    private void operation_Starcleave$sendBonusPacketsOnVisible(boolean visible, CallbackInfo ci) {
        if (visible) {
            ServerBossEventExtras extras = (ServerBossEventExtras) this.operation_starcleave$getExtras();
            for (ServerPlayer player : this.players) {
                extras.sendExtraPacketsOnAdd(player);
            }
        }
    }
}

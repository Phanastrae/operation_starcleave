package phanastrae.operation_starcleave.world;

import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import phanastrae.operation_starcleave.duck.BossEventDuck;
import phanastrae.operation_starcleave.network.packet.ClientboundBossEventExtrasPayload;
import phanastrae.operation_starcleave.services.XPlatInterface;

import java.util.function.Function;

public class ServerBossEventExtras extends BossEventExtras {

    private final ServerBossEvent serverBossEvent;

    public ServerBossEventExtras(ServerBossEvent event) {
        super(event);
        this.serverBossEvent = event;
    }

    public static ServerBossEventExtras fromEvent(BossEvent event) {
        BossEventExtras bossEventExtras = ((BossEventDuck) event).operation_starcleave$getExtras();
        // any ServerBossEvent should have a ServerBossEventExtras instead of a regular BossEventExtras, so this should be fine
        return (ServerBossEventExtras) bossEventExtras;
    }

    @Override
    public ServerBossEvent getEvent() {
        return this.serverBossEvent;
    }

    private void broadcast(Function<BossEventExtras, ClientboundBossEventExtrasPayload> packetGetter) {
        if (this.serverBossEvent.isVisible()) {
            ClientboundBossEventExtrasPayload payload = packetGetter.apply(this);

            for (ServerPlayer player : this.serverBossEvent.getPlayers()) {
                XPlatInterface.INSTANCE.sendPayload(player, payload);
            }
        }
    }

    public void sendExtraPacketsOnAdd(ServerPlayer serverPlayer) {
        if (this.isMini) {
            ClientboundBossEventExtrasPayload payload = ClientboundBossEventExtrasPayload.createAddBonusPacket(this);
            XPlatInterface.INSTANCE.sendPayload(serverPlayer, payload);
        }
    }

    @Override
    public ServerBossEventExtras setMini(boolean value) {
        if (this.isMini != value) {
            super.setMini(value);
            this.broadcast(ClientboundBossEventExtrasPayload::createUpdateBonusPropertiesPacket);
        }
        return this;
    }
}

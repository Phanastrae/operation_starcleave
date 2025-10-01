package phanastrae.operation_starcleave.client.duck;

import phanastrae.operation_starcleave.network.packet.ClientboundBossEventExtrasPayload;

public interface BossHealthOverlayDuck {
    void operation_starcleave$updateExtras(ClientboundBossEventExtrasPayload payload);
}

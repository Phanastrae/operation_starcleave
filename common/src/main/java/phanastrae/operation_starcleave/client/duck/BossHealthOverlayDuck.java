package phanastrae.operation_starcleave.client.duck;

import phanastrae.operation_starcleave.network.packet.s2c.BossEventExtrasPayload;

public interface BossHealthOverlayDuck {
    void operation_starcleave$updateExtras(BossEventExtrasPayload payload);
}

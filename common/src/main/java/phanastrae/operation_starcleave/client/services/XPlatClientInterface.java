package phanastrae.operation_starcleave.client.services;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.block.Block;
import phanastrae.operation_starcleave.services.Services;

public interface XPlatClientInterface {
    XPlatClientInterface INSTANCE = Services.load(XPlatClientInterface.class);

    void registerBlockRenderLayers(RenderType renderLayer, Block... blocks);

    void sendPayload(CustomPacketPayload payload);
}

package phanastrae.operation_starcleave.fabric.client.services;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.block.Block;
import phanastrae.operation_starcleave.client.services.XPlatClientInterface;

public class XPlatClientFabric implements XPlatClientInterface {

    @Override
    public void registerBlockRenderLayers(RenderType renderLayer, Block... blocks) {
        BlockRenderLayerMap.INSTANCE.putBlocks(renderLayer, blocks);
    }

    @Override
    public void sendPayload(CustomPacketPayload payload) {
        ClientPlayNetworking.send(payload);
    }
}

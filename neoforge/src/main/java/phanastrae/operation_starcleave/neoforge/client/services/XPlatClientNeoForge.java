package phanastrae.operation_starcleave.neoforge.client.services;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.network.PacketDistributor;
import phanastrae.operation_starcleave.client.services.XPlatClientInterface;

public class XPlatClientNeoForge implements XPlatClientInterface {

    @Override
    public void registerBlockRenderLayers(RenderType renderLayer, Block... blocks) {
        for(Block block : blocks) {
            ItemBlockRenderTypes.setRenderLayer(block, renderLayer);
        }
    }

    @Override
    public void sendPayload(CustomPacketPayload payload) {
        PacketDistributor.sendToServer(payload);
    }
}

package phanastrae.operation_starcleave.fabric.services;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;
import phanastrae.operation_starcleave.services.XPlatInterface;

public class XPlatFabric implements XPlatInterface {

    @Override
    public String getLoader() {
        return "fabric";
    }

    @Override
    public boolean isModLoaded(String id) {
        return FabricLoader.getInstance().isModLoaded(id);
    }

    @Override
    public void sendPayload(ServerPlayer player, CustomPacketPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }

    @Override
    public CreativeModeTab.Builder createCreativeModeTabBuilder() {
        return FabricItemGroup.builder();
    }
}

package phanastrae.operation_starcleave.fabric.services;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
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

    @Override
    public int getFireSpreadChance(BlockState state, BlockGetter blockGetter, BlockPos blockPos, Direction face) {
        return FlammableBlockRegistry.getDefaultInstance().get(state.getBlock()).getSpreadChance();
    }

    @Override
    public int getFireBurnChance(BlockState state, BlockGetter blockGetter, BlockPos blockPos, Direction face) {
        return FlammableBlockRegistry.getDefaultInstance().get(state.getBlock()).getBurnChance();
    }

    @Override
    public boolean canBurn(BlockState state) {
        return FlammableBlockRegistry.getDefaultInstance().get(state.getBlock()).getBurnChance() > 0;
    }

    @Override
    public void sendToPlayersTrackingEntity(Entity entity, CustomPacketPayload payload) {
        for(ServerPlayer serverPlayer : PlayerLookup.tracking(entity)) {
            XPlatInterface.INSTANCE.sendPayload(serverPlayer, payload);
        }
    }
}

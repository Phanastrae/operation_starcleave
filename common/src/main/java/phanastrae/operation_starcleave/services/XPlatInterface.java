package phanastrae.operation_starcleave.services;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collection;

public interface XPlatInterface {
    XPlatInterface INSTANCE = Services.load(XPlatInterface.class);

    String getLoader();

    boolean isModLoaded(String modId);

    void sendPayload(ServerPlayer player, CustomPacketPayload payload);

    CreativeModeTab.Builder createCreativeModeTabBuilder();

    int getFireSpreadChance(BlockState state, BlockGetter blockGetter, BlockPos blockPos, Direction face);

    int getFireBurnChance(BlockState state, BlockGetter blockGetter, BlockPos blockPos, Direction face);

    boolean canBurn(BlockState state);

    void sendToPlayersTrackingEntity(Entity entity, CustomPacketPayload payload);
}

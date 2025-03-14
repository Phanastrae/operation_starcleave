package phanastrae.operation_starcleave.neoforge.services;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.PacketDistributor;
import phanastrae.operation_starcleave.services.XPlatInterface;

public class XPlatNeoForge implements XPlatInterface {

    @Override
    public String getLoader() {
        return "neoforge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public void sendPayload(ServerPlayer player, CustomPacketPayload payload) {
        PacketDistributor.sendToPlayer(player, payload);
    }

    @Override
    public CreativeModeTab.Builder createCreativeModeTabBuilder() {
        return CreativeModeTab.builder();
    }

    @Override
    public int getFireSpreadChance(BlockState state, BlockGetter blockGetter, BlockPos blockPos, Direction face) {
        return state.getFireSpreadSpeed(blockGetter, blockPos, face);
    }

    @Override
    public int getFireBurnChance(BlockState state, BlockGetter blockGetter, BlockPos blockPos, Direction face) {
        return state.getFlammability(blockGetter, blockPos, face);
    }

    @Override
    public boolean canBurn(BlockState state) {
        if(Blocks.FIRE instanceof FireBlock fireBlock) {
            return fireBlock.getIgniteOdds(state) > 0;
        } else {
            return false;
        }
    }

    @Override
    public void sendToPlayersTrackingEntity(Entity entity, CustomPacketPayload payload) {
        PacketDistributor.sendToPlayersTrackingEntity(entity, payload);
    }
}

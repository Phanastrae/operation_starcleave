package phanastrae.operation_starcleave.client.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.multiplayer.ChunkBatchSizeCalculator;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import phanastrae.operation_starcleave.client.world.firmament.ClientFirmamentRegionManager;
import phanastrae.operation_starcleave.client.world.firmament.FirmamentDamageGlowActor;
import phanastrae.operation_starcleave.client.duck.ClientPlayNetworkHandlerDuck;
import phanastrae.operation_starcleave.duck.EntityDuck;
import phanastrae.operation_starcleave.duck.WorldDuck;
import phanastrae.operation_starcleave.entity.projectile.StarbleachedPearlEntity;
import phanastrae.operation_starcleave.network.packet.*;
import phanastrae.operation_starcleave.client.render.ScreenShakeManager;
import phanastrae.operation_starcleave.client.render.firmament.FirmamentTextureStorage;
import phanastrae.operation_starcleave.world.firmament.*;

public class OperationStarcleaveClientPacketHandler {

    public static void init() {
        register(StartFirmamentRegionSendPayload.PACKET_ID, OperationStarcleaveClientPacketHandler::startFirmamentRegionSend);
        register(FirmamentRegionDataPayload.PACKET_ID, OperationStarcleaveClientPacketHandler::receiveFirmamentRegionData);
        register(FirmamentRegionSentPayload.PACKET_ID, OperationStarcleaveClientPacketHandler::sentFirmamentRegion);
        register(UpdateFirmamentSubRegionPayload.PACKET_ID, OperationStarcleaveClientPacketHandler::updateFirmamentSubRegion);
        register(UnloadFirmamentRegionPayload.PACKET_ID, OperationStarcleaveClientPacketHandler::unloadFirmamentRegion);

        register(FirmamentCleavedPayload.PACKET_ID, OperationStarcleaveClientPacketHandler::onFirmamentCleaved);

        register(StarbleachedPearlLaunchPayload.PACKET_ID, OperationStarcleaveClientPacketHandler::onStarbleachedPearlLaunch);
        register(EntityPhlogisticFirePayload.PACKET_ID, OperationStarcleaveClientPacketHandler::handleEntityPhlogisticFire);
    }

    public static <T extends CustomPacketPayload> boolean register(CustomPacketPayload.Type<T> type, ClientPlayNetworking.PlayPayloadHandler<T> handler) {
        return ClientPlayNetworking.registerGlobalReceiver(type, handler);
    }


    private static void startFirmamentRegionSend(StartFirmamentRegionSendPayload packet, ClientPlayNetworking.Context context) {
        LocalPlayer player = context.player();
        ((ClientPlayNetworkHandlerDuck)player.connection).operation_starcleave$getFirmamentRegionBatchSizeCalculator().onBatchStart();
    }

    private static void receiveFirmamentRegionData(FirmamentRegionDataPayload packet, ClientPlayNetworking.Context context) {
        LocalPlayer player = context.player();
        Level world = player.level();
        Firmament firmament = Firmament.fromWorld(world);
        if(firmament != null) {
            FirmamentRegion firmamentRegion = firmament.getFirmamentRegion(packet.regionId());
            if(firmamentRegion == null) {
                if(firmament.getFirmamentRegionManager() instanceof ClientFirmamentRegionManager clientFirmamentRegionManager) {
                    FirmamentRegionHolder firmamentRegionHolder = clientFirmamentRegionManager.loadRegion(packet.regionId());
                    firmamentRegion = firmamentRegionHolder.getFirmamentRegion();
                }
            }

            if(firmamentRegion != null) {
                firmamentRegion.readFromData(packet.firmamentRegionData());
                FirmamentTextureStorage.getInstance().onRegionAdded(firmamentRegion, world);
            }
        }
    }

    private static void sentFirmamentRegion(FirmamentRegionSentPayload packet, ClientPlayNetworking.Context context) {
        LocalPlayer player = context.player();
        PacketSender responseSender = context.responseSender();
        ChunkBatchSizeCalculator firmamentRegionBatchSizeCalculator = ((ClientPlayNetworkHandlerDuck)player.connection).operation_starcleave$getFirmamentRegionBatchSizeCalculator();
        firmamentRegionBatchSizeCalculator.onBatchFinished(packet.batchSize());

        responseSender.sendPacket(new AcknowledgeFirmamentRegionDataPayload(firmamentRegionBatchSizeCalculator.getDesiredChunksPerTick()));
    }

    private static void updateFirmamentSubRegion(UpdateFirmamentSubRegionPayload packet, ClientPlayNetworking.Context context) {
        LocalPlayer player = context.player();
        Level world = player.level();
        Firmament firmament = Firmament.fromWorld(world);
        if(firmament != null) {
            FirmamentSubRegion firmamentSubRegion = firmament.getSubRegionFromId(packet.id());

            if(firmamentSubRegion != null) {
                firmamentSubRegion.readFromData(packet.subRegionData());

                FirmamentTextureStorage.getInstance().onSubRegionUpdated(firmamentSubRegion, world);
            }
        }
    }

    private static void unloadFirmamentRegion(UnloadFirmamentRegionPayload packet, ClientPlayNetworking.Context context) {
        LocalPlayer player = context.player();
        Firmament firmament = Firmament.fromWorld(player.level());
        if(firmament != null) {
            if(firmament.getFirmamentRegionManager() instanceof ClientFirmamentRegionManager clientFirmamentRegionManager) {
                clientFirmamentRegionManager.unloadRegion(packet.regionId());
            }
        }
    }

    private static void onFirmamentCleaved(FirmamentCleavedPayload packet, ClientPlayNetworking.Context context) {
        LocalPlayer player = context.player();
        Level world = player.level();
        ((WorldDuck)world).operation_starcleave$setCleavingFlashTicksLeft(24);
        Vec3 pos = new Vec3(packet.x(), world.getMaxBuildHeight() + 16, packet.z());
        world.playLocalSound(
                pos.x,
                pos.y,
                pos.z,
                SoundEvents.TRIDENT_THUNDER.value(),
                SoundSource.BLOCKS,
                500.0F,
                1.6F + world.random.nextFloat() * 0.2F,
                false);

        ParticleOptions particleEffect = ParticleTypes.FLASH;
        world.addAlwaysVisibleParticle(particleEffect, pos.x, pos.y - 1, pos.z, 0, 0, 0);

        ScreenShakeManager.getInstance().setShakeAmount(3);
        Firmament firmament = Firmament.fromWorld(world);
        if(firmament != null) {
            firmament.addActor(new FirmamentDamageGlowActor(firmament, (int)pos.x, (int)pos.z));
        }
    }

    public static void onStarbleachedPearlLaunch(StarbleachedPearlLaunchPayload packet, ClientPlayNetworking.Context context) {
        LocalPlayer player = context.player();
        Entity except = null;
        if(packet.exceptExists()) {
            Entity e = player.level().getEntity(packet.exceptId());
            if(e != null) {
                except = e;
            }
        }
        StarbleachedPearlEntity.doRepulsion(packet.pos(), packet.radius(), packet.maxAddedSpeed(), player.level(), except);
    }

    public static void handleEntityPhlogisticFire(EntityPhlogisticFirePayload packet, ClientPlayNetworking.Context context) {
        LocalPlayer player = context.player();
        Level world = player.level();
        Entity entity = world.getEntity(packet.id());
        if (entity != null) {
            ((EntityDuck)entity).operation_starcleave$setOnPhlogisticFire(packet.onPhlogisticFire());
        }
    }
}

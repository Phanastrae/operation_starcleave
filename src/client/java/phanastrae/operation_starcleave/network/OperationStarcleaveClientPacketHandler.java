package phanastrae.operation_starcleave.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.network.ChunkBatchSizeCalculator;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import phanastrae.operation_starcleave.duck.ClientPlayNetworkHandlerDuck;
import phanastrae.operation_starcleave.duck.EntityDuck;
import phanastrae.operation_starcleave.duck.WorldDuck;
import phanastrae.operation_starcleave.entity.projectile.StarbleachedPearlEntity;
import phanastrae.operation_starcleave.network.packet.*;
import phanastrae.operation_starcleave.render.ScreenShakeManager;
import phanastrae.operation_starcleave.render.firmament.FirmamentTextureStorage;
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

    public static <T extends CustomPayload> boolean register(CustomPayload.Id<T> type, ClientPlayNetworking.PlayPayloadHandler<T> handler) {
        return ClientPlayNetworking.registerGlobalReceiver(type, handler);
    }


    private static void startFirmamentRegionSend(StartFirmamentRegionSendPayload packet, ClientPlayNetworking.Context context) {
        ClientPlayerEntity player = context.player();
        ((ClientPlayNetworkHandlerDuck)player.networkHandler).operation_starcleave$getFirmamentRegionBatchSizeCalculator().onStartChunkSend();
    }

    private static void receiveFirmamentRegionData(FirmamentRegionDataPayload packet, ClientPlayNetworking.Context context) {
        ClientPlayerEntity player = context.player();
        World world = player.getWorld();
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
        ClientPlayerEntity player = context.player();
        PacketSender responseSender = context.responseSender();
        ChunkBatchSizeCalculator firmamentRegionBatchSizeCalculator = ((ClientPlayNetworkHandlerDuck)player.networkHandler).operation_starcleave$getFirmamentRegionBatchSizeCalculator();
        firmamentRegionBatchSizeCalculator.onChunkSent(packet.batchSize());

        responseSender.sendPacket(new AcknowledgeFirmamentRegionDataPayload(firmamentRegionBatchSizeCalculator.getDesiredChunksPerTick()));
    }

    private static void updateFirmamentSubRegion(UpdateFirmamentSubRegionPayload packet, ClientPlayNetworking.Context context) {
        ClientPlayerEntity player = context.player();
        World world = player.getWorld();
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
        ClientPlayerEntity player = context.player();
        Firmament firmament = Firmament.fromWorld(player.getWorld());
        if(firmament != null) {
            if(firmament.getFirmamentRegionManager() instanceof ClientFirmamentRegionManager clientFirmamentRegionManager) {
                clientFirmamentRegionManager.unloadRegion(packet.regionId());
            }
        }
    }

    private static void onFirmamentCleaved(FirmamentCleavedPayload packet, ClientPlayNetworking.Context context) {
        ClientPlayerEntity player = context.player();
        World world = player.getWorld();
        ((WorldDuck)world).operation_starcleave$setCleavingFlashTicksLeft(24);
        Vec3d pos = new Vec3d(packet.x(), world.getTopY() + 16, packet.z());
        world.playSound(
                pos.x,
                pos.y,
                pos.z,
                SoundEvents.ITEM_TRIDENT_THUNDER.value(),
                SoundCategory.BLOCKS,
                500.0F,
                1.6F + world.random.nextFloat() * 0.2F,
                false);

        ParticleEffect particleEffect = ParticleTypes.FLASH;
        world.addImportantParticle(particleEffect, pos.x, pos.y - 1, pos.z, 0, 0, 0);

        ScreenShakeManager.getInstance().setShakeAmount(3);
        Firmament firmament = Firmament.fromWorld(world);
        if(firmament != null) {
            firmament.addActor(new FirmamentDamageGlowActor(firmament, (int)pos.x, (int)pos.z));
        }
    }

    public static void onStarbleachedPearlLaunch(StarbleachedPearlLaunchPayload packet, ClientPlayNetworking.Context context) {
        ClientPlayerEntity player = context.player();
        Entity except = null;
        if(packet.exceptExists()) {
            Entity e = player.getWorld().getEntityById(packet.exceptId());
            if(e != null) {
                except = e;
            }
        }
        StarbleachedPearlEntity.doRepulsion(packet.pos(), packet.radius(), packet.maxAddedSpeed(), player.getWorld(), except);
    }

    public static void handleEntityPhlogisticFire(EntityPhlogisticFirePayload packet, ClientPlayNetworking.Context context) {
        ClientPlayerEntity player = context.player();
        World world = player.getWorld();
        Entity entity = world.getEntityById(packet.id());
        if (entity != null) {
            ((EntityDuck)entity).operation_starcleave$setOnPhlogisticFire(packet.onPhlogisticFire());
        }
    }
}

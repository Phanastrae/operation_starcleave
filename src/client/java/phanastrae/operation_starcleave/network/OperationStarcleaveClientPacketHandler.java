package phanastrae.operation_starcleave.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.network.ChunkBatchSizeCalculator;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.entity.projectile.StarbleachedPearlEntity;
import phanastrae.operation_starcleave.network.packet.OperationStarcleavePacketTypes;
import phanastrae.operation_starcleave.network.packet.c2s.AcknowledgeFirmamentRegionDataC2SPacket;
import phanastrae.operation_starcleave.network.packet.s2c.*;
import phanastrae.operation_starcleave.render.firmament.FirmamentBuiltSubRegionHolder;
import phanastrae.operation_starcleave.render.firmament.FirmamentBuiltSubRegionStorage;
import phanastrae.operation_starcleave.world.OperationStarcleaveWorld;
import phanastrae.operation_starcleave.world.firmament.*;

public class OperationStarcleaveClientPacketHandler {

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(OperationStarcleavePacketTypes.START_FIRMAMENT_REGION_SEND_S2C, OperationStarcleaveClientPacketHandler::startFirmamentRegionSend);
        ClientPlayNetworking.registerGlobalReceiver(OperationStarcleavePacketTypes.FIRMAMENT_REGION_DATA_S2C, OperationStarcleaveClientPacketHandler::receiveFirmamentRegionData);
        ClientPlayNetworking.registerGlobalReceiver(OperationStarcleavePacketTypes.FIRMAMENT_REGION_SENT_S2C, OperationStarcleaveClientPacketHandler::sentFirmamentRegion);
        ClientPlayNetworking.registerGlobalReceiver(OperationStarcleavePacketTypes.UPDATE_FIRMAMENT_SUB_REGION_S2C, OperationStarcleaveClientPacketHandler::updateFirmamentSubRegion);
        ClientPlayNetworking.registerGlobalReceiver(OperationStarcleavePacketTypes.UNLOAD_FIRMAMENT_REGION_S2C, OperationStarcleaveClientPacketHandler::unloadFirmamentRegion);

        ClientPlayNetworking.registerGlobalReceiver(OperationStarcleavePacketTypes.FIRMAMENT_CLEAVED_S2C, OperationStarcleaveClientPacketHandler::onFirmamentCleaved);

        ClientPlayNetworking.registerGlobalReceiver(OperationStarcleavePacketTypes.STARBLEACHED_PEARL_LAUNCH_PACKET_S2C, OperationStarcleaveClientPacketHandler::onStarbleachedPearlLaunch);
    }

    private static void startFirmamentRegionSend(StartFirmamentRegionSendS2CPacket packet, ClientPlayerEntity player, PacketSender responseSender) {
        ((OperationStarcleaveClientPlayNetworkHandler)player.networkHandler).operation_starcleave$getFirmamentRegionBatchSizeCalculator().onStartChunkSend();
    }

    private static void receiveFirmamentRegionData(FirmamentRegionDataS2CPacket packet, ClientPlayerEntity player, PacketSender responseSender) {
        Firmament firmament = Firmament.fromWorld(player.getWorld());
        if(firmament != null) {
            FirmamentRegion firmamentRegion = firmament.getFirmamentRegion(packet.regionId);
            if(firmamentRegion == null) {
                if(firmament.getFirmamentRegionManager() instanceof ClientFirmamentRegionManager clientFirmamentRegionManager) {
                    FirmamentRegionHolder firmamentRegionHolder = clientFirmamentRegionManager.loadRegion(packet.regionId);
                    firmamentRegion = firmamentRegionHolder.getFirmamentRegion();
                }
            }

            if(firmamentRegion != null) {
                firmamentRegion.readFromData(packet.firmamentRegionData);

                RegionPos regionPos = new RegionPos(packet.regionId);
                for(int i = 0; i < FirmamentRegion.SUBREGIONS; i++) {
                    for(int j = 0; j < FirmamentRegion.SUBREGIONS; j++) {
                        SubRegionPos subRegionPos = SubRegionPos.fromWorldCoords(regionPos.worldX + i * FirmamentSubRegion.SUBREGION_SIZE, regionPos.worldZ + j * FirmamentSubRegion.SUBREGION_SIZE);
                        FirmamentBuiltSubRegionHolder firmamentBuiltSubRegionHolder = new FirmamentBuiltSubRegionHolder(subRegionPos.id, firmament.getWorld().getTopY() + 16);

                        FirmamentBuiltSubRegionStorage.getInstance().add(firmamentBuiltSubRegionHolder);
                    }
                }

                SubRegionPos subRegionPos = SubRegionPos.fromWorldCoords(regionPos.worldX, regionPos.worldZ);
                for(int i = -1; i <= FirmamentRegion.SUBREGIONS; i++) {
                    for(int j = -1; j <= FirmamentRegion.SUBREGIONS; j++) {
                        SubRegionPos subRegionPos2 = new SubRegionPos(subRegionPos.srx+i, subRegionPos.srz+j);
                        FirmamentBuiltSubRegionHolder subRegionHolder = FirmamentBuiltSubRegionStorage.getInstance().get(subRegionPos2.id);
                        if(subRegionHolder != null) {
                            subRegionHolder.build(firmament, subRegionPos2);
                        }
                    }
                }
            }
        }
    }

    private static void sentFirmamentRegion(FirmamentRegionSentS2CPacket packet, ClientPlayerEntity player, PacketSender responseSender) {
        ChunkBatchSizeCalculator firmamentRegionBatchSizeCalculator = ((OperationStarcleaveClientPlayNetworkHandler)player.networkHandler).operation_starcleave$getFirmamentRegionBatchSizeCalculator();
        firmamentRegionBatchSizeCalculator.onChunkSent(packet.batchSize);

        responseSender.sendPacket(new AcknowledgeFirmamentRegionDataC2SPacket(firmamentRegionBatchSizeCalculator.getDesiredChunksPerTick()));
    }

    private static void updateFirmamentSubRegion(UpdateFirmamentSubRegionS2CPacket packet, ClientPlayerEntity player, PacketSender responseSender) {
        Firmament firmament = Firmament.fromWorld(player.getWorld());
        if(firmament != null) {
            FirmamentSubRegion firmamentSubRegion = firmament.getSubRegionFromId(packet.id);

            if(firmamentSubRegion != null) {
                firmamentSubRegion.readFromData(packet.subRegionData);

                SubRegionPos subRegionPos = new SubRegionPos(packet.id);
                for(int i = -1; i <= 1; i++) {
                    for(int j = -1; j <= 1; j++) {
                        SubRegionPos subRegionPos2 = new SubRegionPos(subRegionPos.srx+i, subRegionPos.srz+j);
                        FirmamentBuiltSubRegionHolder subRegionHolder = FirmamentBuiltSubRegionStorage.getInstance().get(subRegionPos2.id);
                        if(subRegionHolder != null) {
                            subRegionHolder.build(firmament, subRegionPos2);
                        }
                    }
                }
            }
        }
    }

    private static void unloadFirmamentRegion(UnloadFirmamentRegionS2CPacket packet, ClientPlayerEntity player, PacketSender responseSender) {
        Firmament firmament = Firmament.fromWorld(player.getWorld());
        if(firmament != null) {
            if(firmament.getFirmamentRegionManager() instanceof ClientFirmamentRegionManager clientFirmamentRegionManager) {
                clientFirmamentRegionManager.unloadRegion(packet.regionId);
            }
        }
    }

    private static void onFirmamentCleaved(FirmamentCleavedS2CPacket packet, ClientPlayerEntity player, PacketSender responseSender) {
        World world = player.getWorld();
        ((OperationStarcleaveWorld)world).operation_starcleave$setCleavingFlashTicksLeft(24);
        Vec3d pos = new Vec3d(packet.x, world.getTopY() + 16, packet.z);
        world.playSound(
                pos.x,
                pos.y,
                pos.z,
                SoundEvents.ITEM_TRIDENT_THUNDER,
                SoundCategory.BLOCKS,
                500.0F,
                1.6F + world.random.nextFloat() * 0.2F,
                false);

        ParticleEffect particleEffect = ParticleTypes.FLASH;
        world.addImportantParticle(particleEffect, pos.x, pos.y - 1, pos.z, 0, 0, 0);
    }

    public static void onStarbleachedPearlLaunch(StarbleachedPearlLaunchPacketS2C packet, ClientPlayerEntity player, PacketSender responseSender) {
        Entity except = null;
        if(packet.exceptExists) {
            Entity e = player.getWorld().getEntityById(packet.exceptId);
            if(e != null) {
                except = e;
            }
        }
        StarbleachedPearlEntity.doRepulsion(packet.pos, packet.radius, packet.maxAddedSpeed, player.getWorld(), except);
    }
}

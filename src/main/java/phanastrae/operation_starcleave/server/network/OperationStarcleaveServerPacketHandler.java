package phanastrae.operation_starcleave.server.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import phanastrae.operation_starcleave.network.packet.OperationStarcleavePacketTypes;

public class OperationStarcleaveServerPacketHandler {
    
    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(OperationStarcleavePacketTypes.ACKNOWLEDGE_FIRMAMENT_REGION_DATA_C2S, ((packet, player, responseSender) -> FirmamentRegionDataSender.getFirmamentRegionDataSender(player.networkHandler).onAcknowledgeRegions(packet.desiredChunksPerTick)));
    }
}

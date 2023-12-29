package phanastrae.operation_starcleave.network.packet;

import net.fabricmc.fabric.api.networking.v1.PacketType;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.network.packet.c2s.AcknowledgeFirmamentRegionDataC2SPacket;
import phanastrae.operation_starcleave.network.packet.s2c.*;

public class OperationStarcleavePacketTypes {

    public static final PacketType<StartFirmamentRegionSendS2CPacket> START_FIRMAMENT_REGION_SEND_S2C = PacketType.create(OperationStarcleave.id("start_firmament_region_send_s2c"), (StartFirmamentRegionSendS2CPacket::new));
    public static final PacketType<FirmamentRegionDataS2CPacket> FIRMAMENT_REGION_DATA_S2C = PacketType.create(OperationStarcleave.id("firmament_region_data_s2c"), (FirmamentRegionDataS2CPacket::new));
    public static final PacketType<FirmamentRegionSentS2CPacket> FIRMAMENT_REGION_SENT_S2C = PacketType.create(OperationStarcleave.id("firmament_region_sent_s2c"), (FirmamentRegionSentS2CPacket::new));
    public static final PacketType<UpdateFirmamentSubRegionS2CPacket> UPDATE_FIRMAMENT_SUB_REGION_S2C = PacketType.create(OperationStarcleave.id("update_firmament_subregion_s2c"), (UpdateFirmamentSubRegionS2CPacket::new));
    public static final PacketType<UnloadFirmamentRegionS2CPacket> UNLOAD_FIRMAMENT_REGION_S2C = PacketType.create(OperationStarcleave.id("unload_firmament_region_s2c"), (UnloadFirmamentRegionS2CPacket::new));

    public static final PacketType<FirmamentCleavedS2CPacket> FIRMAMENT_CLEAVED_S2C = PacketType.create(OperationStarcleave.id("firmament_cleaved_s2c"), (FirmamentCleavedS2CPacket::new));


    public static final PacketType<AcknowledgeFirmamentRegionDataC2SPacket> ACKNOWLEDGE_FIRMAMENT_REGION_DATA_C2S = PacketType.create(OperationStarcleave.id("acknowledge_firmament_region_data_c2s"), (AcknowledgeFirmamentRegionDataC2SPacket::new));

    public static void init() {
    }
}

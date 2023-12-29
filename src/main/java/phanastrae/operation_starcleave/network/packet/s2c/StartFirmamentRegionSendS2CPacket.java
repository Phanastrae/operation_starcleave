package phanastrae.operation_starcleave.network.packet.s2c;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import phanastrae.operation_starcleave.network.packet.OperationStarcleavePacketTypes;

public class StartFirmamentRegionSendS2CPacket implements FabricPacket {

    public StartFirmamentRegionSendS2CPacket() {
    }

    public StartFirmamentRegionSendS2CPacket(PacketByteBuf packetByteBuf) {
    }

    @Override
    public void write(PacketByteBuf buf) {
    }

    @Override
    public PacketType<?> getType() {
        return OperationStarcleavePacketTypes.START_FIRMAMENT_REGION_SEND_S2C;
    }
}

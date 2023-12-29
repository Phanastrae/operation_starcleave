package phanastrae.operation_starcleave.network.packet.s2c;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import phanastrae.operation_starcleave.network.packet.OperationStarcleavePacketTypes;

public class FirmamentRegionSentS2CPacket implements FabricPacket {

    public final int batchSize;

    public FirmamentRegionSentS2CPacket(int batchSize) {
        this.batchSize = batchSize;
    }

    public FirmamentRegionSentS2CPacket(PacketByteBuf packetByteBuf) {
        this.batchSize = packetByteBuf.readInt();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(this.batchSize);
    }

    @Override
    public PacketType<?> getType() {
        return OperationStarcleavePacketTypes.FIRMAMENT_REGION_SENT_S2C;
    }
}

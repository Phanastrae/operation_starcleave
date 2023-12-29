package phanastrae.operation_starcleave.network.packet.c2s;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import phanastrae.operation_starcleave.network.packet.OperationStarcleavePacketTypes;

public class AcknowledgeFirmamentRegionDataC2SPacket implements FabricPacket {

    public final float desiredChunksPerTick;

    public AcknowledgeFirmamentRegionDataC2SPacket(float desiredChunksPerTick) {
        this.desiredChunksPerTick = desiredChunksPerTick;
    }

    public AcknowledgeFirmamentRegionDataC2SPacket(PacketByteBuf packetByteBuf) {
        this.desiredChunksPerTick = packetByteBuf.readFloat();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeFloat(this.desiredChunksPerTick);
    }

    @Override
    public PacketType<?> getType() {
        return OperationStarcleavePacketTypes.ACKNOWLEDGE_FIRMAMENT_REGION_DATA_C2S;
    }
}

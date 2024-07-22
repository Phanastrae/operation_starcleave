package phanastrae.operation_starcleave.network.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import phanastrae.operation_starcleave.OperationStarcleave;

public record AcknowledgeFirmamentRegionDataPayload(float desiredChunksPerTick) implements CustomPayload {
    public static final PacketCodec<RegistryByteBuf, AcknowledgeFirmamentRegionDataPayload> PACKET_CODEC = CustomPayload.codecOf(AcknowledgeFirmamentRegionDataPayload::write, AcknowledgeFirmamentRegionDataPayload::new);
    public static final CustomPayload.Id<AcknowledgeFirmamentRegionDataPayload> PACKET_ID = new CustomPayload.Id<>(OperationStarcleave.id("acknowledge_firmament_region_data"));

    public AcknowledgeFirmamentRegionDataPayload(PacketByteBuf buf) {
        this(buf.readFloat());
    }

    public void write(PacketByteBuf buf) {
        buf.writeFloat(this.desiredChunksPerTick);
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}

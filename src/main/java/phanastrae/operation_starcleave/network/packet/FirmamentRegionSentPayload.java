package phanastrae.operation_starcleave.network.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import phanastrae.operation_starcleave.OperationStarcleave;

public record FirmamentRegionSentPayload(int batchSize) implements CustomPayload {
    public static final PacketCodec<RegistryByteBuf, FirmamentRegionSentPayload> PACKET_CODEC = CustomPayload.codecOf(FirmamentRegionSentPayload::write, FirmamentRegionSentPayload::new);
    public static final CustomPayload.Id<FirmamentRegionSentPayload> PACKET_ID = new CustomPayload.Id<>(OperationStarcleave.id("firmament_region_sent"));

    public FirmamentRegionSentPayload(PacketByteBuf buf) {
        this(buf.readInt());
    }

    public void write(PacketByteBuf buf) {
        buf.writeInt(this.batchSize);
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}

package phanastrae.operation_starcleave.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import phanastrae.operation_starcleave.OperationStarcleave;

public record FirmamentRegionSentPayload(int batchSize) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, FirmamentRegionSentPayload> PACKET_CODEC = CustomPacketPayload.codec(FirmamentRegionSentPayload::write, FirmamentRegionSentPayload::new);
    public static final CustomPacketPayload.Type<FirmamentRegionSentPayload> PACKET_ID = new CustomPacketPayload.Type<>(OperationStarcleave.id("firmament_region_sent"));

    public FirmamentRegionSentPayload(FriendlyByteBuf buf) {
        this(buf.readInt());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(this.batchSize);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}

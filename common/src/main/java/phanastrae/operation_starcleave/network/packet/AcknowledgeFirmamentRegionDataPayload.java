package phanastrae.operation_starcleave.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import phanastrae.operation_starcleave.OperationStarcleave;

public record AcknowledgeFirmamentRegionDataPayload(float desiredChunksPerTick) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, AcknowledgeFirmamentRegionDataPayload> PACKET_CODEC = CustomPacketPayload.codec(AcknowledgeFirmamentRegionDataPayload::write, AcknowledgeFirmamentRegionDataPayload::new);
    public static final CustomPacketPayload.Type<AcknowledgeFirmamentRegionDataPayload> PACKET_ID = new CustomPacketPayload.Type<>(OperationStarcleave.id("acknowledge_firmament_region_data"));

    public AcknowledgeFirmamentRegionDataPayload(FriendlyByteBuf buf) {
        this(buf.readFloat());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeFloat(this.desiredChunksPerTick);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}

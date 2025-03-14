package phanastrae.operation_starcleave.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.world.firmament.FirmamentRegionData;

public record FirmamentRegionDataPayload(long regionId, FirmamentRegionData firmamentRegionData) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, FirmamentRegionDataPayload> PACKET_CODEC = CustomPacketPayload.codec(FirmamentRegionDataPayload::write, FirmamentRegionDataPayload::new);
    public static final CustomPacketPayload.Type<FirmamentRegionDataPayload> PACKET_ID = new CustomPacketPayload.Type<>(OperationStarcleave.id("firmament_region_data"));

    public FirmamentRegionDataPayload(FriendlyByteBuf buf) {
        this(buf.readLong(), new FirmamentRegionData(buf));
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeLong(this.regionId);
        this.firmamentRegionData.write(buf);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}

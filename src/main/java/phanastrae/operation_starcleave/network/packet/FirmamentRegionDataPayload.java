package phanastrae.operation_starcleave.network.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.world.firmament.FirmamentRegionData;

public record FirmamentRegionDataPayload(long regionId, FirmamentRegionData firmamentRegionData) implements CustomPayload {
    public static final PacketCodec<RegistryByteBuf, FirmamentRegionDataPayload> PACKET_CODEC = CustomPayload.codecOf(FirmamentRegionDataPayload::write, FirmamentRegionDataPayload::new);
    public static final CustomPayload.Id<FirmamentRegionDataPayload> PACKET_ID = new CustomPayload.Id<>(OperationStarcleave.id("firmament_region_data"));

    public FirmamentRegionDataPayload(PacketByteBuf buf) {
        this(buf.readLong(), new FirmamentRegionData(buf));
    }

    public void write(PacketByteBuf buf) {
        buf.writeLong(this.regionId);
        this.firmamentRegionData.write(buf);
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}

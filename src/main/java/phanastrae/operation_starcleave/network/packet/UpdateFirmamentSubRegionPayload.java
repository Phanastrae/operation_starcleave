package phanastrae.operation_starcleave.network.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.world.firmament.FirmamentSubRegionData;

public record UpdateFirmamentSubRegionPayload(long id, FirmamentSubRegionData subRegionData) implements CustomPayload {
    public static final PacketCodec<RegistryByteBuf, UpdateFirmamentSubRegionPayload> PACKET_CODEC = CustomPayload.codecOf(UpdateFirmamentSubRegionPayload::write, UpdateFirmamentSubRegionPayload::new);
    public static final CustomPayload.Id<UpdateFirmamentSubRegionPayload> PACKET_ID = new CustomPayload.Id<>(OperationStarcleave.id("update_firmament_sub_region"));

    public UpdateFirmamentSubRegionPayload(PacketByteBuf buf) {
        this(buf.readLong(), new FirmamentSubRegionData(buf));
    }

    public void write(PacketByteBuf buf) {
        buf.writeLong(this.id);
        this.subRegionData.write(buf);
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}

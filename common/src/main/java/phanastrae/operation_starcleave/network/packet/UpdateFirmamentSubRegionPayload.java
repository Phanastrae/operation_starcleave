package phanastrae.operation_starcleave.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.world.firmament.FirmamentSubRegionData;

public record UpdateFirmamentSubRegionPayload(long id, FirmamentSubRegionData subRegionData) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateFirmamentSubRegionPayload> PACKET_CODEC = CustomPacketPayload.codec(UpdateFirmamentSubRegionPayload::write, UpdateFirmamentSubRegionPayload::new);
    public static final CustomPacketPayload.Type<UpdateFirmamentSubRegionPayload> PACKET_ID = new CustomPacketPayload.Type<>(OperationStarcleave.id("update_firmament_sub_region"));

    public UpdateFirmamentSubRegionPayload(FriendlyByteBuf buf) {
        this(buf.readLong(), new FirmamentSubRegionData(buf));
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeLong(this.id);
        this.subRegionData.write(buf);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}

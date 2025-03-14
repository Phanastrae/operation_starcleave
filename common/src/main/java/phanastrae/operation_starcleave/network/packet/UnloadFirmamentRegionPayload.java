package phanastrae.operation_starcleave.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import phanastrae.operation_starcleave.OperationStarcleave;

public record UnloadFirmamentRegionPayload(long regionId) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, UnloadFirmamentRegionPayload> PACKET_CODEC = CustomPacketPayload.codec(UnloadFirmamentRegionPayload::write, UnloadFirmamentRegionPayload::new);
    public static final CustomPacketPayload.Type<UnloadFirmamentRegionPayload> PACKET_ID = new CustomPacketPayload.Type<>(OperationStarcleave.id("unload_firmament_region"));

    public UnloadFirmamentRegionPayload(FriendlyByteBuf buf) {
        this(buf.readLong());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeLong(this.regionId);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}

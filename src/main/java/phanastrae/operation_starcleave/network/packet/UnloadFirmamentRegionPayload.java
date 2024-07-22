package phanastrae.operation_starcleave.network.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import phanastrae.operation_starcleave.OperationStarcleave;

public record UnloadFirmamentRegionPayload(long regionId) implements CustomPayload {
    public static final PacketCodec<RegistryByteBuf, UnloadFirmamentRegionPayload> PACKET_CODEC = CustomPayload.codecOf(UnloadFirmamentRegionPayload::write, UnloadFirmamentRegionPayload::new);
    public static final CustomPayload.Id<UnloadFirmamentRegionPayload> PACKET_ID = new CustomPayload.Id<>(OperationStarcleave.id("unload_firmament_region"));

    public UnloadFirmamentRegionPayload(PacketByteBuf buf) {
        this(buf.readLong());
    }

    public void write(PacketByteBuf buf) {
        buf.writeLong(this.regionId);
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}

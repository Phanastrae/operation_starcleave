package phanastrae.operation_starcleave.network.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import phanastrae.operation_starcleave.OperationStarcleave;

public record StartFirmamentRegionSendPayload() implements CustomPayload {
    public static final PacketCodec<RegistryByteBuf, StartFirmamentRegionSendPayload> PACKET_CODEC = CustomPayload.codecOf(StartFirmamentRegionSendPayload::write, StartFirmamentRegionSendPayload::new);
    public static final CustomPayload.Id<StartFirmamentRegionSendPayload> PACKET_ID = new CustomPayload.Id<>(OperationStarcleave.id("start_firmament_region_send"));

    public StartFirmamentRegionSendPayload(PacketByteBuf buf) {
        this();
    }

    public void write(PacketByteBuf buf) {
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}

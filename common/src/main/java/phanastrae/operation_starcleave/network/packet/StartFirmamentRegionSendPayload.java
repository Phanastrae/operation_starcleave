package phanastrae.operation_starcleave.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import phanastrae.operation_starcleave.OperationStarcleave;

public record StartFirmamentRegionSendPayload() implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, StartFirmamentRegionSendPayload> PACKET_CODEC = CustomPacketPayload.codec(StartFirmamentRegionSendPayload::write, StartFirmamentRegionSendPayload::new);
    public static final CustomPacketPayload.Type<StartFirmamentRegionSendPayload> PACKET_ID = new CustomPacketPayload.Type<>(OperationStarcleave.id("start_firmament_region_send"));

    public StartFirmamentRegionSendPayload(FriendlyByteBuf buf) {
        this();
    }

    public void write(FriendlyByteBuf buf) {
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}

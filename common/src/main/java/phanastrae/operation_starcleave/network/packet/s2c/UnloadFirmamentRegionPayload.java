package phanastrae.operation_starcleave.network.packet.s2c;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import phanastrae.operation_starcleave.OperationStarcleave;

public record UnloadFirmamentRegionPayload(long regionId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UnloadFirmamentRegionPayload> TYPE = new CustomPacketPayload.Type<>(OperationStarcleave.id("unload_firmament_region"));
    public static final StreamCodec<RegistryFriendlyByteBuf, UnloadFirmamentRegionPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG,
            UnloadFirmamentRegionPayload::regionId,
            UnloadFirmamentRegionPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

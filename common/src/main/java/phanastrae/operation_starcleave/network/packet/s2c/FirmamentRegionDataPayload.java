package phanastrae.operation_starcleave.network.packet.s2c;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.world.firmament.data.FirmamentRegionData;

public record FirmamentRegionDataPayload(long regionId, FirmamentRegionData firmamentRegionData) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<FirmamentRegionDataPayload> TYPE = new CustomPacketPayload.Type<>(OperationStarcleave.id("firmament_region_data"));
    public static final StreamCodec<RegistryFriendlyByteBuf, FirmamentRegionDataPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG,
            FirmamentRegionDataPayload::regionId,
            FirmamentRegionData.STREAM_CODEC,
            FirmamentRegionDataPayload::firmamentRegionData,
            FirmamentRegionDataPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

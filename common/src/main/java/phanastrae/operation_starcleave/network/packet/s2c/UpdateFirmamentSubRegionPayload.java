package phanastrae.operation_starcleave.network.packet.s2c;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.world.firmament.data.FirmamentSubRegionData;

public record UpdateFirmamentSubRegionPayload(long id, FirmamentSubRegionData subRegionData) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UpdateFirmamentSubRegionPayload> TYPE = new CustomPacketPayload.Type<>(OperationStarcleave.id("update_firmament_sub_region"));
    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateFirmamentSubRegionPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG,
            UpdateFirmamentSubRegionPayload::id,
            FirmamentSubRegionData.STREAM_CODEC,
            UpdateFirmamentSubRegionPayload::subRegionData,
            UpdateFirmamentSubRegionPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

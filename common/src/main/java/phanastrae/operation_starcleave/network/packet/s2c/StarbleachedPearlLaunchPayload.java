package phanastrae.operation_starcleave.network.packet.s2c;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import phanastrae.operation_starcleave.OperationStarcleave;

import java.util.Optional;

public record StarbleachedPearlLaunchPayload(double x, double y, double z, float radius, float maxAddedSpeed, Optional<Integer> exceptId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<StarbleachedPearlLaunchPayload> TYPE = new CustomPacketPayload.Type<>(OperationStarcleave.id("starbleached_pearl_launch"));
    public static final StreamCodec<RegistryFriendlyByteBuf, StarbleachedPearlLaunchPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE,
            StarbleachedPearlLaunchPayload::x,
            ByteBufCodecs.DOUBLE,
            StarbleachedPearlLaunchPayload::y,
            ByteBufCodecs.DOUBLE,
            StarbleachedPearlLaunchPayload::z,
            ByteBufCodecs.FLOAT,
            StarbleachedPearlLaunchPayload::radius,
            ByteBufCodecs.FLOAT,
            StarbleachedPearlLaunchPayload::maxAddedSpeed,
            ByteBufCodecs.optional(ByteBufCodecs.INT),
            StarbleachedPearlLaunchPayload::exceptId,
            StarbleachedPearlLaunchPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

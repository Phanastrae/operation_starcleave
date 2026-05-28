package phanastrae.operation_starcleave.network.packet.s2c;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import phanastrae.operation_starcleave.OperationStarcleave;

public record FirmamentCleavedPayload(int x, int z) implements CustomPacketPayload {
    public static final Type<FirmamentCleavedPayload> TYPE = new Type<>(OperationStarcleave.id("firmament_cleaved"));
    public static final StreamCodec<RegistryFriendlyByteBuf, FirmamentCleavedPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            FirmamentCleavedPayload::x,
            ByteBufCodecs.INT,
            FirmamentCleavedPayload::z,
            FirmamentCleavedPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

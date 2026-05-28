package phanastrae.operation_starcleave.network.packet.s2c;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import phanastrae.operation_starcleave.OperationStarcleave;

public record EntityPegasusFlyingPayload(int id, boolean pegasusFlying) implements CustomPacketPayload {
    public static final Type<EntityPegasusFlyingPayload> TYPE = new Type<>(OperationStarcleave.id("entity_pegasus_flying"));
    public static final StreamCodec<RegistryFriendlyByteBuf, EntityPegasusFlyingPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            EntityPegasusFlyingPayload::id,
            ByteBufCodecs.BOOL,
            EntityPegasusFlyingPayload::pegasusFlying,
            EntityPegasusFlyingPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

package phanastrae.operation_starcleave.network.packet.s2c;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import phanastrae.operation_starcleave.OperationStarcleave;

public record EntityPegasusGlidingPayload(int id, boolean pegasusGliding) implements CustomPacketPayload {
    public static final Type<EntityPegasusGlidingPayload> TYPE = new Type<>(OperationStarcleave.id("entity_pegasus_gliding"));
    public static final StreamCodec<RegistryFriendlyByteBuf, EntityPegasusGlidingPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            EntityPegasusGlidingPayload::id,
            ByteBufCodecs.BOOL,
            EntityPegasusGlidingPayload::pegasusGliding,
            EntityPegasusGlidingPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

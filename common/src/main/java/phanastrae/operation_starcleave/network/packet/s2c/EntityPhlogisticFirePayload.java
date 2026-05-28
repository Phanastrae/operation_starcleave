package phanastrae.operation_starcleave.network.packet.s2c;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import phanastrae.operation_starcleave.OperationStarcleave;

public record EntityPhlogisticFirePayload(int id, boolean onPhlogisticFire) implements CustomPacketPayload {
    public static final Type<EntityPhlogisticFirePayload> TYPE = new Type<>(OperationStarcleave.id("entity_phlogistic_fire"));
    public static final StreamCodec<RegistryFriendlyByteBuf, EntityPhlogisticFirePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            EntityPhlogisticFirePayload::id,
            ByteBufCodecs.BOOL,
            EntityPhlogisticFirePayload::onPhlogisticFire,
            EntityPhlogisticFirePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

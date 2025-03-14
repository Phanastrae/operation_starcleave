package phanastrae.operation_starcleave.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import phanastrae.operation_starcleave.OperationStarcleave;

public record EntityPhlogisticFirePayload(int id, boolean onPhlogisticFire) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, EntityPhlogisticFirePayload> PACKET_CODEC = CustomPacketPayload.codec(EntityPhlogisticFirePayload::write, EntityPhlogisticFirePayload::new);
    public static final Type<EntityPhlogisticFirePayload> PACKET_ID = new Type<>(OperationStarcleave.id("entity_phlogistic_fire"));

    public EntityPhlogisticFirePayload(FriendlyByteBuf buf) {
        this(buf.readInt(), buf.readBoolean());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(this.id);
        buf.writeBoolean(this.onPhlogisticFire);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}

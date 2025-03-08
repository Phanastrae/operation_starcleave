package phanastrae.operation_starcleave.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import phanastrae.operation_starcleave.OperationStarcleave;

public record EntityPegasusFlyingPayload(int id, boolean pegasusFlying) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, EntityPegasusFlyingPayload> PACKET_CODEC = CustomPacketPayload.codec(EntityPegasusFlyingPayload::write, EntityPegasusFlyingPayload::new);
    public static final Type<EntityPegasusFlyingPayload> PACKET_ID = new Type<>(OperationStarcleave.id("entity_pegasus_flying"));

    public EntityPegasusFlyingPayload(FriendlyByteBuf buf) {
        this(buf.readInt(), buf.readBoolean());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(this.id);
        buf.writeBoolean(this.pegasusFlying);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}

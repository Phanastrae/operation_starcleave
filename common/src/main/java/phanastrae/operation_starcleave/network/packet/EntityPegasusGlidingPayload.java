package phanastrae.operation_starcleave.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import phanastrae.operation_starcleave.OperationStarcleave;

public record EntityPegasusGlidingPayload(int id, boolean pegasusGliding) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, EntityPegasusGlidingPayload> PACKET_CODEC = CustomPacketPayload.codec(EntityPegasusGlidingPayload::write, EntityPegasusGlidingPayload::new);
    public static final Type<EntityPegasusGlidingPayload> PACKET_ID = new Type<>(OperationStarcleave.id("entity_pegasus_gliding"));

    public EntityPegasusGlidingPayload(FriendlyByteBuf buf) {
        this(buf.readInt(), buf.readBoolean());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(this.id);
        buf.writeBoolean(this.pegasusGliding);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}

package phanastrae.operation_starcleave.network.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import phanastrae.operation_starcleave.OperationStarcleave;

public record EntityPhlogisticFirePayload(int id, boolean onPhlogisticFire) implements CustomPayload {
    public static final PacketCodec<RegistryByteBuf, EntityPhlogisticFirePayload> PACKET_CODEC = CustomPayload.codecOf(EntityPhlogisticFirePayload::write, EntityPhlogisticFirePayload::new);
    public static final Id<EntityPhlogisticFirePayload> PACKET_ID = new Id<>(OperationStarcleave.id("entity_phlogistic_fire"));

    public EntityPhlogisticFirePayload(PacketByteBuf buf) {
        this(buf.readInt(), buf.readBoolean());
    }

    public void write(PacketByteBuf buf) {
        buf.writeInt(this.id);
        buf.writeBoolean(this.onPhlogisticFire);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}

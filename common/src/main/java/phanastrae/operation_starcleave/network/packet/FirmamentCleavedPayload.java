package phanastrae.operation_starcleave.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import phanastrae.operation_starcleave.OperationStarcleave;

public record FirmamentCleavedPayload(int x, int z) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, FirmamentCleavedPayload> PACKET_CODEC = CustomPacketPayload.codec(FirmamentCleavedPayload::write, FirmamentCleavedPayload::new);
    public static final Type<FirmamentCleavedPayload> PACKET_ID = new Type<>(OperationStarcleave.id("firmament_cleaved"));

    public FirmamentCleavedPayload(FriendlyByteBuf buf) {
        this(buf.readInt(), buf.readInt());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.z);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}

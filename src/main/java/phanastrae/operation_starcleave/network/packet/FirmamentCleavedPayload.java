package phanastrae.operation_starcleave.network.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import phanastrae.operation_starcleave.OperationStarcleave;

public record FirmamentCleavedPayload(int x, int z) implements CustomPayload {
    public static final PacketCodec<RegistryByteBuf, FirmamentCleavedPayload> PACKET_CODEC = CustomPayload.codecOf(FirmamentCleavedPayload::write, FirmamentCleavedPayload::new);
    public static final Id<FirmamentCleavedPayload> PACKET_ID = new Id<>(OperationStarcleave.id("firmament_cleaved"));

    public FirmamentCleavedPayload(PacketByteBuf buf) {
        this(buf.readInt(), buf.readInt());
    }

    public void write(PacketByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.z);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}

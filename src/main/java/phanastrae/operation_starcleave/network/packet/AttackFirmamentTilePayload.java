package phanastrae.operation_starcleave.network.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import phanastrae.operation_starcleave.OperationStarcleave;

public record AttackFirmamentTilePayload(int tileX, int tileZ) implements CustomPayload {
    public static final PacketCodec<RegistryByteBuf, AttackFirmamentTilePayload> PACKET_CODEC = CustomPayload.codecOf(AttackFirmamentTilePayload::write, AttackFirmamentTilePayload::new);
    public static final CustomPayload.Id<AttackFirmamentTilePayload> PACKET_ID = new CustomPayload.Id<>(OperationStarcleave.id("attack_firmament_tile"));

    public AttackFirmamentTilePayload(PacketByteBuf buf) {
        this(buf.readInt(), buf.readInt());
    }

    public void write(PacketByteBuf buf) {
        buf.writeInt(this.tileX);
        buf.writeInt(this.tileZ);
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}

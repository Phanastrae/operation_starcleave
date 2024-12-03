package phanastrae.operation_starcleave.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import phanastrae.operation_starcleave.OperationStarcleave;

public record AttackFirmamentTilePayload(int tileX, int tileZ) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, AttackFirmamentTilePayload> PACKET_CODEC = CustomPacketPayload.codec(AttackFirmamentTilePayload::write, AttackFirmamentTilePayload::new);
    public static final CustomPacketPayload.Type<AttackFirmamentTilePayload> PACKET_ID = new CustomPacketPayload.Type<>(OperationStarcleave.id("attack_firmament_tile"));

    public AttackFirmamentTilePayload(FriendlyByteBuf buf) {
        this(buf.readInt(), buf.readInt());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(this.tileX);
        buf.writeInt(this.tileZ);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}

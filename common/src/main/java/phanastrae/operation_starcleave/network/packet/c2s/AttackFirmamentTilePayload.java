package phanastrae.operation_starcleave.network.packet.c2s;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import phanastrae.operation_starcleave.OperationStarcleave;

public record AttackFirmamentTilePayload(int tileX, int tileZ) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<AttackFirmamentTilePayload> TYPE = new CustomPacketPayload.Type<>(OperationStarcleave.id("attack_firmament_tile"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AttackFirmamentTilePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            AttackFirmamentTilePayload::tileX,
            ByteBufCodecs.INT,
            AttackFirmamentTilePayload::tileZ,
            AttackFirmamentTilePayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

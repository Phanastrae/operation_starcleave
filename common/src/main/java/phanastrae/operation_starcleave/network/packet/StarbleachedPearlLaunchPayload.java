package phanastrae.operation_starcleave.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;
import phanastrae.operation_starcleave.OperationStarcleave;

public record StarbleachedPearlLaunchPayload(Vec3 pos, float radius, float maxAddedSpeed, boolean exceptExists, int exceptId) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, StarbleachedPearlLaunchPayload> PACKET_CODEC = CustomPacketPayload.codec(StarbleachedPearlLaunchPayload::write, StarbleachedPearlLaunchPayload::new);
    public static final CustomPacketPayload.Type<StarbleachedPearlLaunchPayload> PACKET_ID = new CustomPacketPayload.Type<>(OperationStarcleave.id("starbleached_pearl_launch"));

    public StarbleachedPearlLaunchPayload(FriendlyByteBuf buf) {
        this(buf.readVec3(), buf.readFloat(), buf.readFloat(), buf.readBoolean(), buf.readInt());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeVec3(this.pos);
        buf.writeFloat(this.radius);
        buf.writeFloat(this.maxAddedSpeed);
        buf.writeBoolean(this.exceptExists);
        buf.writeInt(this.exceptId);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}

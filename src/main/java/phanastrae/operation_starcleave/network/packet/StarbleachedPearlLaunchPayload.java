package phanastrae.operation_starcleave.network.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.Vec3d;
import phanastrae.operation_starcleave.OperationStarcleave;

public record StarbleachedPearlLaunchPayload(Vec3d pos, float radius, float maxAddedSpeed, boolean exceptExists, int exceptId) implements CustomPayload {
    public static final PacketCodec<RegistryByteBuf, StarbleachedPearlLaunchPayload> PACKET_CODEC = CustomPayload.codecOf(StarbleachedPearlLaunchPayload::write, StarbleachedPearlLaunchPayload::new);
    public static final CustomPayload.Id<StarbleachedPearlLaunchPayload> PACKET_ID = new CustomPayload.Id<>(OperationStarcleave.id("starbleached_pearl_launch"));

    public StarbleachedPearlLaunchPayload(PacketByteBuf buf) {
        this(buf.readVec3d(), buf.readFloat(), buf.readFloat(), buf.readBoolean(), buf.readInt());
    }

    public void write(PacketByteBuf buf) {
        buf.writeVec3d(this.pos);
        buf.writeFloat(this.radius);
        buf.writeFloat(this.maxAddedSpeed);
        buf.writeBoolean(this.exceptExists);
        buf.writeInt(this.exceptId);
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}

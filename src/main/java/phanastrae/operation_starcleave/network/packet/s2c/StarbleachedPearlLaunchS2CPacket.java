package phanastrae.operation_starcleave.network.packet.s2c;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.network.packet.OperationStarcleavePacketTypes;

public class StarbleachedPearlLaunchS2CPacket implements FabricPacket {

    public final Vec3d pos;
    public final float radius;
    public final float maxAddedSpeed;
    public final boolean exceptExists;
    public final int exceptId;
    public StarbleachedPearlLaunchS2CPacket(Vec3d pos, float radius, float maxAddedSpeed, @Nullable Entity except) {
        this.pos = pos;
        this.radius = radius;
        this.maxAddedSpeed = maxAddedSpeed;
        if(except == null) {
            this.exceptExists = false;
            this.exceptId = 0;
        } else {
            this.exceptExists = true;
            this.exceptId = except.getId();
        }

    }

    public StarbleachedPearlLaunchS2CPacket(PacketByteBuf buf) {
        this.pos = buf.readVec3d();
        this.radius = buf.readFloat();
        this.maxAddedSpeed = buf.readFloat();
        this.exceptExists = buf.readBoolean();
        this.exceptId = buf.readInt();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeVec3d(this.pos);
        buf.writeFloat(this.radius);
        buf.writeFloat(this.maxAddedSpeed);
        buf.writeBoolean(exceptExists);
        buf.writeInt(exceptId);
    }

    @Override
    public PacketType<?> getType() {
        return OperationStarcleavePacketTypes.STARBLEACHED_PEARL_LAUNCH_PACKET_S2C;
    }
}

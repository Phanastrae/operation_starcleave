package phanastrae.operation_starcleave.network.packet.s2c;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import phanastrae.operation_starcleave.network.packet.OperationStarcleavePacketTypes;

public class FirmamentCleavedS2CPacket implements FabricPacket {

    public final int x;
    public final int z;
    public FirmamentCleavedS2CPacket(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public FirmamentCleavedS2CPacket(PacketByteBuf buf) {
        this.x = buf.readInt();
        this.z = buf.readInt();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.z);
    }

    @Override
    public PacketType<?> getType() {
        return OperationStarcleavePacketTypes.FIRMAMENT_CLEAVED_S2C;
    }
}

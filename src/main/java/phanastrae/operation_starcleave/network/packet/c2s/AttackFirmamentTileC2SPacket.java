package phanastrae.operation_starcleave.network.packet.c2s;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import phanastrae.operation_starcleave.network.packet.OperationStarcleavePacketTypes;
import phanastrae.operation_starcleave.world.firmament.FirmamentTilePos;

public class AttackFirmamentTileC2SPacket implements FabricPacket {

    public final int tileX;
    public final int tileZ;
    public AttackFirmamentTileC2SPacket(FirmamentTilePos tilePos) {
        this.tileX = tilePos.tileX;
        this.tileZ = tilePos.tileZ;
    }

    public AttackFirmamentTileC2SPacket(PacketByteBuf buf) {
        this.tileX = buf.readInt();
        this.tileZ = buf.readInt();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(this.tileX);
        buf.writeInt(this.tileZ);
    }

    @Override
    public PacketType<?> getType() {
        return OperationStarcleavePacketTypes.ATTACK_FIRMAMENT_C2S;
    }
}

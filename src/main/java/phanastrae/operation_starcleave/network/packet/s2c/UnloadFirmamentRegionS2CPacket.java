package phanastrae.operation_starcleave.network.packet.s2c;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import phanastrae.operation_starcleave.network.packet.OperationStarcleavePacketTypes;

public class UnloadFirmamentRegionS2CPacket implements FabricPacket {

    public final long regionId;
    public UnloadFirmamentRegionS2CPacket(long id) {
        this.regionId = id;
    }

    public UnloadFirmamentRegionS2CPacket(PacketByteBuf buf) {
        this.regionId = buf.readLong();
    }
    @Override
    public void write(PacketByteBuf buf) {
        buf.writeLong(this.regionId);
    }

    @Override
    public PacketType<?> getType() {
        return OperationStarcleavePacketTypes.UNLOAD_FIRMAMENT_REGION_S2C;
    }
}

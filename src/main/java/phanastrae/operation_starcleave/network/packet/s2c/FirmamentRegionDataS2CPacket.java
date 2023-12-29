package phanastrae.operation_starcleave.network.packet.s2c;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import phanastrae.operation_starcleave.network.packet.OperationStarcleavePacketTypes;
import phanastrae.operation_starcleave.world.firmament.*;

public class FirmamentRegionDataS2CPacket implements FabricPacket {

    public final long regionId;
    public final FirmamentRegionData firmamentRegionData;

    public FirmamentRegionDataS2CPacket(FirmamentRegion region) {
        this.regionId = RegionPos.fromWorldCoords(region.x, region.z).id;
        this.firmamentRegionData = new FirmamentRegionData(region);
    }

    public FirmamentRegionDataS2CPacket(PacketByteBuf packetByteBuf) {
        this.regionId = packetByteBuf.readLong();
        this.firmamentRegionData = new FirmamentRegionData(packetByteBuf);
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeLong(this.regionId);
        this.firmamentRegionData.write(buf);
    }

    @Override
    public PacketType<?> getType() {
        return OperationStarcleavePacketTypes.FIRMAMENT_REGION_DATA_S2C;
    }
}

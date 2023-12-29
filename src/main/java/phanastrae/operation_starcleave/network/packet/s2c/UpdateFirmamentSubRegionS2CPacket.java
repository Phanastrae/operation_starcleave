package phanastrae.operation_starcleave.network.packet.s2c;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import phanastrae.operation_starcleave.network.packet.OperationStarcleavePacketTypes;
import phanastrae.operation_starcleave.world.firmament.FirmamentSubRegion;
import phanastrae.operation_starcleave.world.firmament.FirmamentSubRegionData;

public class UpdateFirmamentSubRegionS2CPacket implements FabricPacket {

    public final long id;
    public final FirmamentSubRegionData subRegionData;

    public UpdateFirmamentSubRegionS2CPacket(FirmamentSubRegion subRegion) {
        this.id = subRegion.getPosAsLong();
        this.subRegionData = new FirmamentSubRegionData(subRegion);
    }

    public UpdateFirmamentSubRegionS2CPacket(PacketByteBuf buf) {
        this.id = buf.readLong();
        this.subRegionData = new FirmamentSubRegionData(buf);
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeLong(this.id);
        this.subRegionData.write(buf);
    }

    @Override
    public PacketType<?> getType() {
        return OperationStarcleavePacketTypes.UPDATE_FIRMAMENT_SUB_REGION_S2C;
    }
}

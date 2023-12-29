package phanastrae.operation_starcleave.world.firmament;

import net.minecraft.network.PacketByteBuf;
import phanastrae.operation_starcleave.world.firmament.FirmamentSubRegion;

public class FirmamentSubRegionData {

    public final byte[] damageData;

    public FirmamentSubRegionData(FirmamentSubRegion firmamentSubRegion) {
        this.damageData = firmamentSubRegion.getAsByteArray();
    }

    public FirmamentSubRegionData(PacketByteBuf packetByteBuf) {
        this.damageData = packetByteBuf.readByteArray();
    }

    public void write(PacketByteBuf packetByteBuf) {
        packetByteBuf.writeByteArray(this.damageData);
    }
}

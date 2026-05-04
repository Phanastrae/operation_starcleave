package phanastrae.operation_starcleave.world.firmament.data;

import net.minecraft.network.FriendlyByteBuf;
import phanastrae.operation_starcleave.world.firmament.FirmamentSubRegion;

public class FirmamentSubRegionData {

    public final byte[] damageData;

    public FirmamentSubRegionData(FirmamentSubRegion firmamentSubRegion) {
        this.damageData = FirmamentSubRegion.getAsByteArray(firmamentSubRegion.damage);
    }

    public FirmamentSubRegionData(FriendlyByteBuf packetByteBuf) {
        this.damageData = packetByteBuf.readByteArray();
    }

    public void write(FriendlyByteBuf packetByteBuf) {
        packetByteBuf.writeByteArray(this.damageData);
    }
}

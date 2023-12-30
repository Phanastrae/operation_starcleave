package phanastrae.operation_starcleave.world.firmament;

import net.minecraft.network.PacketByteBuf;
import phanastrae.operation_starcleave.world.firmament.FirmamentSubRegion;

public class FirmamentSubRegionData {

    //public final byte[] displacementData;
    //public final byte[] velocityData;
    public final byte[] damageData;
    //public final byte[] dripData;

    public FirmamentSubRegionData(FirmamentSubRegion firmamentSubRegion) {
        //this.displacementData = firmamentSubRegion.getAsByteArray(firmamentSubRegion.displacement);
        //this.velocityData = firmamentSubRegion.getAsByteArray(firmamentSubRegion.velocity);
        this.damageData = firmamentSubRegion.getAsByteArray(firmamentSubRegion.damage);
        //this.dripData = firmamentSubRegion.getAsByteArray(firmamentSubRegion.drip);
    }

    public FirmamentSubRegionData(PacketByteBuf packetByteBuf) {
        //this.displacementData = packetByteBuf.readByteArray();
        //this.velocityData = packetByteBuf.readByteArray();
        this.damageData = packetByteBuf.readByteArray();
        //this.dripData = packetByteBuf.readByteArray();
    }

    public void write(PacketByteBuf packetByteBuf) {
        //packetByteBuf.writeByteArray(this.displacementData);
        //packetByteBuf.writeByteArray(this.velocityData);
        packetByteBuf.writeByteArray(this.damageData);
        //packetByteBuf.writeByteArray(this.dripData);
    }
}

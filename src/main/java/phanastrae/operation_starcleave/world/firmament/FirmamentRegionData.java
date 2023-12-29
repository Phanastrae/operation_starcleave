package phanastrae.operation_starcleave.world.firmament;

import net.minecraft.network.PacketByteBuf;

public class FirmamentRegionData {

    public final FirmamentSubRegionData[][] subRegionData;
    public FirmamentRegionData(FirmamentRegion region) {
        this.subRegionData = new FirmamentSubRegionData[FirmamentRegion.SUBREGIONS][FirmamentRegion.SUBREGIONS];
        for(int i = 0; i < FirmamentRegion.SUBREGIONS; i++) {
            for(int j = 0; j < FirmamentRegion.SUBREGIONS; j++) {
                this.subRegionData[i][j] = new FirmamentSubRegionData(region.subRegions[i][j]);
            }
        }
    }

    public FirmamentRegionData(PacketByteBuf buf) {
        this.subRegionData = new FirmamentSubRegionData[FirmamentRegion.SUBREGIONS][FirmamentRegion.SUBREGIONS];
        for(int i = 0; i < FirmamentRegion.SUBREGIONS; i++) {
            for(int j = 0; j < FirmamentRegion.SUBREGIONS; j++) {
                this.subRegionData[i][j] = new FirmamentSubRegionData(buf);
            }
        }
    }

    public void write(PacketByteBuf buf) {
        for(int i = 0; i < FirmamentRegion.SUBREGIONS; i++) {
            for(int j = 0; j < FirmamentRegion.SUBREGIONS; j++) {
                this.subRegionData[i][j].write(buf);
            }
        }
    }
}

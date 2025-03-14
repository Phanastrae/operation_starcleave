package phanastrae.operation_starcleave.world.firmament;

import net.minecraft.network.FriendlyByteBuf;

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

    public FirmamentRegionData(FriendlyByteBuf buf) {
        this.subRegionData = new FirmamentSubRegionData[FirmamentRegion.SUBREGIONS][FirmamentRegion.SUBREGIONS];
        for(int i = 0; i < FirmamentRegion.SUBREGIONS; i++) {
            for(int j = 0; j < FirmamentRegion.SUBREGIONS; j++) {
                this.subRegionData[i][j] = new FirmamentSubRegionData(buf);
            }
        }
    }

    public void write(FriendlyByteBuf buf) {
        for(int i = 0; i < FirmamentRegion.SUBREGIONS; i++) {
            for(int j = 0; j < FirmamentRegion.SUBREGIONS; j++) {
                this.subRegionData[i][j].write(buf);
            }
        }
    }
}

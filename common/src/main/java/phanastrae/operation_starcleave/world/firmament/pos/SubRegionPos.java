package phanastrae.operation_starcleave.world.firmament.pos;

import phanastrae.operation_starcleave.world.firmament.FirmamentRegion;

public class SubRegionPos {

    public final long id;
    public final int srx;
    public final int srz;
    public final int minWorldX;
    public final int minWorldZ;

    public SubRegionPos(int srx, int srz) {
        this.srx = srx;
        this.srz = srz;
        this.id = (long)srx & 0xFFFFFFFFL | ((long)srz & 0xFFFFFFFFL) << 32;
        this.minWorldX = srx << FirmamentRegion.SUBREGION_SIZE_BITS;
        this.minWorldZ = srz << FirmamentRegion.SUBREGION_SIZE_BITS;
    }

    public SubRegionPos(long id) {
        this.id = id;
        this.srx = (int)(id & 0xFFFFFFFFL);
        this.srz = (int)((id >>> 32) & 0xFFFFFFFFL);
        this.minWorldX = srx << FirmamentRegion.SUBREGION_SIZE_BITS;
        this.minWorldZ = srz << FirmamentRegion.SUBREGION_SIZE_BITS;
    }

    public static SubRegionPos fromWorldCoords(int x, int z) {
        return new SubRegionPos(x >> FirmamentRegion.SUBREGION_SIZE_BITS, z >> FirmamentRegion.SUBREGION_SIZE_BITS);
    }
}

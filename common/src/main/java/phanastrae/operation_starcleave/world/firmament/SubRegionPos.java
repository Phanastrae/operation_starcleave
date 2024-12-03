package phanastrae.operation_starcleave.world.firmament;

public class SubRegionPos {

    public final long id;
    public final int srx;
    public final int srz;
    public final int worldX;
    public final int worldZ;

    public SubRegionPos(int srx, int srz) {
        this.srx = srx;
        this.srz = srz;
        this.id = (long)srx & 4294967295L | ((long)srz & 4294967295L) << 32;
        this.worldX = srx << FirmamentRegion.SUBREGION_SIZE_BITS;
        this.worldZ = srz << FirmamentRegion.SUBREGION_SIZE_BITS;
    }

    public SubRegionPos(long id) {
        this.id = id;
        this.srx = (int)(id & 4294967295L);
        this.srz = (int)((id >>> 32) & 4294967295L);
        this.worldX = srx << FirmamentRegion.SUBREGION_SIZE_BITS;
        this.worldZ = srz << FirmamentRegion.SUBREGION_SIZE_BITS;
    }


    public static SubRegionPos fromWorldCoords(int x, int z) {
        return new SubRegionPos(x >> FirmamentRegion.SUBREGION_SIZE_BITS, z >> FirmamentRegion.SUBREGION_SIZE_BITS);
    }
}

package phanastrae.operation_starcleave.world.firmament;

public class SubRegionPos {

    public final long id;
    public final int srx;
    public final int srz;
    public final int worldX;
    public final int worldZ;

    public SubRegionPos(int rx, int rz) {
        this.srx = rx;
        this.srz = rz;
        this.id = (long)rx & 4294967295L | ((long)rz & 4294967295L) << 32;
        this.worldX = rx << FirmamentRegion.SUBREGION_SIZE_BITS;
        this.worldZ = rz << FirmamentRegion.SUBREGION_SIZE_BITS;
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

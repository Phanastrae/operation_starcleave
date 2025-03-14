package phanastrae.operation_starcleave.world.firmament;

import net.minecraft.world.entity.Entity;

public class RegionPos {

    public final long id;
    public final int rx;
    public final int rz;
    public final int worldX;
    public final int worldZ;

    public RegionPos(int rx, int rz) {
        this.rx = rx;
        this.rz = rz;
        this.id = (long)rx & 4294967295L | ((long)rz & 4294967295L) << 32;
        this.worldX = rx << FirmamentRegion.REGION_SIZE_BITS;
        this.worldZ = rz << FirmamentRegion.REGION_SIZE_BITS;
    }

    public RegionPos(long id) {
        this.id = id;
        this.rx = (int)(id & 4294967295L);
        this.rz = (int)((id >>> 32) & 4294967295L);
        this.worldX = rx << FirmamentRegion.REGION_SIZE_BITS;
        this.worldZ = rz << FirmamentRegion.REGION_SIZE_BITS;
    }

    public static RegionPos fromWorldCoords(int x, int z) {
        return new RegionPos(x >> FirmamentRegion.REGION_SIZE_BITS, z >> FirmamentRegion.REGION_SIZE_BITS);
    }

    public static RegionPos fromSubRegion(SubRegionPos subRegionPos) {
        return fromWorldCoords(subRegionPos.worldX, subRegionPos.worldZ);
    }

    public static RegionPos fromEntity(Entity entity) {
        return fromWorldCoords(entity.getBlockX(), entity.getBlockZ());
    }
}

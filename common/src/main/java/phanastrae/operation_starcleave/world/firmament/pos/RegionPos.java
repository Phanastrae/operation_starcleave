package phanastrae.operation_starcleave.world.firmament.pos;

import net.minecraft.world.entity.Entity;
import phanastrae.operation_starcleave.world.firmament.FirmamentRegion;

public class RegionPos {

    public final long id;
    public final int rx;
    public final int rz;
    public final int minWorldX;
    public final int minWorldZ;

    public RegionPos(int rx, int rz) {
        this.rx = rx;
        this.rz = rz;
        this.id = idFromRegionCoords(rx, rz);
        this.minWorldX = rx << FirmamentRegion.REGION_SIZE_BITS;
        this.minWorldZ = rz << FirmamentRegion.REGION_SIZE_BITS;
    }

    public RegionPos(long id) {
        this.id = id;
        this.rx = (int) (id & 0xFFFFFFFFL);
        this.rz = (int) ((id >>> 32) & 0xFFFFFFFFL);
        this.minWorldX = this.rx << FirmamentRegion.REGION_SIZE_BITS;
        this.minWorldZ = this.rz << FirmamentRegion.REGION_SIZE_BITS;
    }

    public static long idFromRegionCoords(int rx, int rz) {
        return (long) rx & 0xFFFFFFFFL | ((long) rz & 0xFFFFFFFFL) << 32;
    }

    public static long idFromWorldCoords(int x, int z) {
        int rx = x >> FirmamentRegion.REGION_SIZE_BITS;
        int rz = z >> FirmamentRegion.REGION_SIZE_BITS;
        return (long) rx & 0xFFFFFFFFL | ((long) rz & 0xFFFFFFFFL) << 32;
    }

    public static RegionPos fromWorldCoords(int x, int z) {
        return new RegionPos(x >> FirmamentRegion.REGION_SIZE_BITS, z >> FirmamentRegion.REGION_SIZE_BITS);
    }

    public static RegionPos fromSubRegion(SubRegionPos subRegionPos) {
        return fromWorldCoords(subRegionPos.minWorldX, subRegionPos.minWorldZ);
    }

    public static RegionPos fromEntity(Entity entity) {
        return fromWorldCoords(entity.getBlockX(), entity.getBlockZ());
    }
}

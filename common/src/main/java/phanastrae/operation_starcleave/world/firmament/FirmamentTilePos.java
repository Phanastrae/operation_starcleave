package phanastrae.operation_starcleave.world.firmament;

import net.minecraft.world.phys.Vec3;

public class FirmamentTilePos {
    final public int tileX;
    final public int tileZ;
    final public int blockX;
    final public int blockZ;
    final public int y;
    public FirmamentTilePos(int tileX, int y, int tileZ) {
        this.tileX = tileX;
        this.tileZ = tileZ;
        this.blockX = this.tileX << 2;
        this.blockZ = this.tileZ << 2;
        this.y = y;
    }
    public FirmamentTilePos(int tileX, int tileZ, Firmament firmament) {
        this(tileX, firmament.getY(), tileZ);
    }

    public static FirmamentTilePos fromBlockCoords(int x, int y, int z) {
        return new FirmamentTilePos(x >> 2, y, z >> 2);
    }

    public static FirmamentTilePos fromBlockCoords(int x, int z, Firmament firmament) {
        return fromBlockCoords(x, firmament.getY(), z);
    }

    public Vec3 getCenter() {
        return new Vec3(this.blockX + 2, this.y, this.blockZ + 2);
    }
}

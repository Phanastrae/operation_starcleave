package phanastrae.operation_starcleave.world.firmament;

import org.apache.logging.log4j.util.TriConsumer;

import static phanastrae.operation_starcleave.world.firmament.FirmamentSubRegion.TILE_SIZE;

public class FirmamentUpdater {

    public static final int nCount = 8;
    public static final int[] nXs = new int[]{TILE_SIZE, -TILE_SIZE, 0, 0, TILE_SIZE, TILE_SIZE, -TILE_SIZE, -TILE_SIZE};
    public static final int[] nZs = new int[]{0, 0, TILE_SIZE, -TILE_SIZE, TILE_SIZE, -TILE_SIZE, TILE_SIZE, -TILE_SIZE};
    static final float aw = 0.1464f;
    static final float dw = 0.1036f;
    public static final float[] nWeights = new float[]{aw, aw, aw, aw, dw, dw, dw, dw};

    public static void forEachNeighbor(TriConsumer<Integer, Integer, Float> method) {
        for (int n = 0; n < nCount; n++) {
            int nx = nXs[n];
            int nz = nZs[n];
            float nWeight = nWeights[n];
            method.accept(nx, nz, nWeight);
        }
    }

    // TODO should these be offset by TILE_SIZE instead of 1?
    public static float dFdxDamage(Firmament firmament, int x, int z) {
        return (firmament.getDamage(x + 1, z) - firmament.getDamage(x - 1, z)) / 2f;
    }

    public static float dFdzDamage(Firmament firmament, int x, int z) {
        return (firmament.getDamage(x, z + 1) - firmament.getDamage(x, z - 1)) / 2f;
    }

    public static float dFdxBigDamage(Firmament firmament, int x, int z) {
        return 0.5f * dFdxDamage(firmament, x, z) + 0.25f * (dFdxDamage(firmament, x + 1, z) + dFdxDamage(firmament, x - 1, z));
    }

    public static float dFdzBigDamage(Firmament firmament, int x, int z) {
        return 0.5f * dFdzDamage(firmament, x, z) + 0.25f * (dFdzDamage(firmament, x, z + 1) + dFdzDamage(firmament, x, z - 1));
    }
}

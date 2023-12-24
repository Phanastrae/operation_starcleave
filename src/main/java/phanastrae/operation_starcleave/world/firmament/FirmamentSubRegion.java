package phanastrae.operation_starcleave.world.firmament;

import java.util.function.BiConsumer;

public class FirmamentSubRegion {
    // the Firmament for a 4x4 Chunk (16x16 Block) subregion of a Region
    public static final int GRID_SIZE = 8;

    public Property velocity;
    public Property displacement;

    public Property damage;
    public Property drip;
    public Property dDrip;

    public FirmamentSubRegion() {
        this.damage = new Property(GRID_SIZE, GRID_SIZE, 0);
        this.drip = new Property(GRID_SIZE, GRID_SIZE, 0);
        this.dDrip = new Property(GRID_SIZE, GRID_SIZE, 0);
        this.displacement = new Property(GRID_SIZE, GRID_SIZE, 0);
        this.velocity = new Property(GRID_SIZE, GRID_SIZE, 0);
    }

    public float getDrip(int x, int z) {
        return drip.get(x, z);
    }

    public float getDDrip(int x, int z) {
        return dDrip.get(x, z);
    }

    public float getDamage(int x, int z) {
        return damage.get(x, z);
    }

    public float getDisplacement(int x, int z) {
        return displacement.get(x, z);
    }

    public float getVelocity(int x, int z) {
        return velocity.get(x, z);
    }

    public void setDrip(int x, int z, float value) {
        drip.set(x, z, value);
    }

    public void setDDrip(int x, int z, float value) {
        dDrip.set(x, z, value);
    }

    public void setDamage(int x, int z, float value) {
        damage.set(x, z, value);
    }

    public void setDisplacement(int x, int z, float value) {
        displacement.set(x, z, value);
    }

    public void setVelocity(int x, int z, float value) {
        velocity.set(x, z, value);
    }

    public void forEachPos(BiConsumer<Integer, Integer> method) {
        for(int i = 0; i < GRID_SIZE; i++) {
            for(int j = 0; j < GRID_SIZE; j++) {
                method.accept(i, j);
            }
        }
    }

    boolean[] active = new boolean[9];
    //    x -->
    // z  0 1 2
    // |  3 4 5
    // \/ 6 7 8
    public final int[] xOffset = new int[]{
            -1, 0, GRID_SIZE,
            -1, 0, GRID_SIZE,
            -1, 0, GRID_SIZE
    };
    public final int[] zOffset = new int[]{
            -1, -1, -1,
            0, 0, 0,
            GRID_SIZE, GRID_SIZE, GRID_SIZE
    };

    boolean shouldUpdate = false;

    public void clearActive() {
        for(int i = 0; i < 9; i++) {
            active[i] = false;
        }
    }

    public void markActive(int x, int z) {
        boolean xMin = x == 0;
        boolean zMin = z == GRID_SIZE - 1;
        boolean xMax = x == 0;
        boolean zMax = z == GRID_SIZE - 1;

        active[4] = true;
        if(xMin) {
            active[3] = true;
            if(zMin) active[0] = true;
            if(zMax) active[6] = true;
        }
        if(xMax) {
            active[5] = true;
            if(zMin) active[2] = true;
            if(zMax) active[8] = true;
        }
        if(zMin) active[1] = true;
        if(zMax) active[7] = true;
    }

    public void clearShouldUpdate() {
        shouldUpdate = false;
    }

    public void markShouldUpdate() {
        shouldUpdate = true;
    }
}

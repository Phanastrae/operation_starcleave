package phanastrae.operation_starcleave.world.firmament;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class FirmamentSubRegion implements FirmamentAccess {

    public static final int SUBREGION_SIZE = 32;

    public static final int TILES = 8;
    public static final int TILE_MASK = 0x3;
    public static final int TILE_SIZE_BITS = 2;
    public static final int TILE_SIZE = 4;

    //    x -->
    // z  0 1 2
    // |  3 4 5
    // \/ 6 7 8
    public final int[] xOffset = new int[]{
            -1, 0, SUBREGION_SIZE,
            -1, 0, SUBREGION_SIZE,
            -1, 0, SUBREGION_SIZE
    };
    public final int[] zOffset = new int[]{
            -1, -1, -1,
            0, 0, 0,
            SUBREGION_SIZE, SUBREGION_SIZE, SUBREGION_SIZE
    };

    public float[][] velocity;
    public float[][] displacement;

    public float[][] damage;
    public float[][] drip;
    public float[][] dDrip;

    private final List<FirmamentActor> actors = new ArrayList<>();
    private final List<FirmamentActor> newActors = new ArrayList<>();

    boolean[] active = new boolean[9];
    boolean shouldUpdate = false;

    // world coords of minimum x-z corner
    public final int x;
    public final int z;

    public final FirmamentRegion firmamentRegion;

    public FirmamentSubRegion(FirmamentRegion firmamentRegion, int x, int z) {
        this.firmamentRegion = firmamentRegion;
        this.x = x;
        this.z = z;

        this.damage = new float[TILES][TILES];
        this.drip = new float[TILES][TILES];
        this.dDrip = new float[TILES][TILES];
        this.displacement = new float[TILES][TILES];
        this.velocity = new float[TILES][TILES];
    }

    public void markShouldUpdate() {
        this.shouldUpdate = true;
    }

    @Override
    public void clearActors() {
        this.actors.clear();
        this.newActors.clear();
    }

    @Override
    public void addActor(FirmamentActor actor) {
        this.newActors.add(actor);
    }

    @Override
    public void manageActors() {
        actors.addAll(newActors);
        newActors.clear();

        actors.removeIf((actor) -> !actor.active);
    }

    @Override
    public void tickActors() {
        for(FirmamentActor actor : actors) {
            actor.tick();
        }
    }

    @Override
    public void forEachPosition(BiConsumer<Integer, Integer> method) {
        for(int i = 0; i < TILES; i++) {
            for(int j = 0; j < TILES; j++) {
                method.accept(i * TILE_SIZE, j * TILE_SIZE);
            }
        }
    }

    @Override
    public void forEachActivePosition(BiConsumer<Integer, Integer> method) {
        forEachPosition(method);
    }

    @Override
    public float getDrip(int x, int z) {
        return drip[x >> TILE_SIZE_BITS][z >> TILE_SIZE_BITS];
    }

    @Override
    public float getDamage(int x, int z) {
        return damage[x >> TILE_SIZE_BITS][z >> TILE_SIZE_BITS];
    }

    @Override
    public float getDisplacement(int x, int z) {
        return displacement[x >> TILE_SIZE_BITS][z >> TILE_SIZE_BITS];
    }

    @Override
    public float getVelocity(int x, int z) {
        return velocity[x >> TILE_SIZE_BITS][z >> TILE_SIZE_BITS];
    }

    @Override
    public float getDDrip(int x, int z) {
        return dDrip[x >> TILE_SIZE_BITS][z >> TILE_SIZE_BITS];
    }

    @Override
    public void setDrip(int x, int z, float value) {
        drip[x >> TILE_SIZE_BITS][z >> TILE_SIZE_BITS] = value;
    }

    @Override
    public void setDamage(int x, int z, float value) {
        damage[x >> TILE_SIZE_BITS][z >> TILE_SIZE_BITS] = value;
    }

    @Override
    public void setDisplacement(int x, int z, float value) {
        displacement[x >> TILE_SIZE_BITS][z >> TILE_SIZE_BITS] = value;
    }

    @Override
    public void setVelocity(int x, int z, float value) {
        velocity[x >> TILE_SIZE_BITS][z >> TILE_SIZE_BITS] = value;
    }

    @Override
    public void setDDrip(int x, int z, float value) {
        dDrip[x >> TILE_SIZE_BITS][z >> TILE_SIZE_BITS] = value;
    }

    @Override
    public void markShouldUpdate(int x, int z) {
        markShouldUpdate();
    }

    @Override
    public void clearShouldUpdate() {
        shouldUpdate = false;
    }

    @Override
    public boolean shouldUpdate() {
        return shouldUpdate;
    }

    @Override
    public void markActive(int x, int z) {
        int tx = x >> TILE_SIZE_BITS;
        int tz = z >> TILE_SIZE_BITS;

        boolean xMin = tx == 0;
        boolean zMin = tz == TILES - 1;
        boolean xMax = tx == 0;
        boolean zMax = tz == TILES - 1;

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

    @Override
    public void clearActive() {
        for(int i = 0; i < 9; i++) {
            active[i] = false;
        }
    }

    @Override
    public void markUpdatesFromActivity() {
        for(int k = 0; k < 9; k++) {
            if(active[k]) {
                this.firmamentRegion.firmament.markShouldUpdate(x + xOffset[k], z + zOffset[k]);
            }
        }
    }
}

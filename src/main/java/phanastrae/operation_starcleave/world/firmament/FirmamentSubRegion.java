package phanastrae.operation_starcleave.world.firmament;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class FirmamentSubRegion implements FirmamentAccess {

    public static final int GRID_SIZE = 8;

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
    final int x;
    final int z;

    public FirmamentSubRegion(int x, int z) {
        this.x = x;
        this.z = z;

        this.damage = new float[GRID_SIZE][GRID_SIZE];
        this.drip = new float[GRID_SIZE][GRID_SIZE];
        this.dDrip = new float[GRID_SIZE][GRID_SIZE];
        this.displacement = new float[GRID_SIZE][GRID_SIZE];
        this.velocity = new float[GRID_SIZE][GRID_SIZE];
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
        for(int i = 0; i < GRID_SIZE; i++) {
            for(int j = 0; j < GRID_SIZE; j++) {
                method.accept(i, j);
            }
        }
    }

    @Override
    public void forEachActivePosition(BiConsumer<Integer, Integer> method) {
        for(int x = 0; x < GRID_SIZE; x++) {
            for(int z = 0; z < GRID_SIZE; z++) {
                method.accept(x, z);
            }
        }
    }

    @Override
    public float getDrip(int x, int z) {
        return drip[x][z];
    }

    @Override
    public float getDamage(int x, int z) {
        return damage[x][z];
    }

    @Override
    public float getDisplacement(int x, int z) {
        return displacement[x][z];
    }

    @Override
    public float getVelocity(int x, int z) {
        return velocity[x][z];
    }

    @Override
    public float getDDrip(int x, int z) {
        return dDrip[x][z];
    }

    @Override
    public void setDrip(int x, int z, float value) {
        drip[x][z] = value;
    }

    @Override
    public void setDamage(int x, int z, float value) {
        damage[x][z] = value;
    }

    @Override
    public void setDisplacement(int x, int z, float value) {
        displacement[x][z] = value;
    }

    @Override
    public void setVelocity(int x, int z, float value) {
        velocity[x][z] = value;
    }

    @Override
    public void setDDrip(int x, int z, float value) {
        dDrip[x][z] = value;
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
                markShouldUpdate(x + xOffset[k], z + zOffset[k]);
            }
        }
    }
}

package phanastrae.operation_starcleave.world.firmament;

import java.util.function.BiConsumer;

public class FirmamentRegion implements FirmamentAccess {

    public static final int GRID_SIZE = 512;

    public static final int SUBREGIONS = 64;
    public static final int REGION_MASK = 0x1FF;
    public static final int SUBREGION_MASK = 0x7;
    public static final int SUBREGION_SIZE_BITS = 3;

    public FirmamentSubRegion[][] subRegions;
    boolean shouldUpdate = false;
    boolean active = false;

    // world coords of minimum x-z corner
    final int x;
    final int z;

    public FirmamentRegion(int x, int z) {
        this.x = x;
        this.z = z;

        this.subRegions = new FirmamentSubRegion[SUBREGIONS][SUBREGIONS];
        for(int i = 0; i < SUBREGIONS; i++) {
            for(int j = 0; j < SUBREGIONS; j++) {
                subRegions[i][j] = new FirmamentSubRegion(x + i * FirmamentSubRegion.GRID_SIZE, z + j * FirmamentSubRegion.GRID_SIZE);
            }
        }
    }

    public boolean getActive(int x, int z) {
        x = x&REGION_MASK;
        z = z&REGION_MASK;
        return subRegions[x>>SUBREGION_SIZE_BITS][z>>SUBREGION_SIZE_BITS].active[4];
    }

    @Override
    public void clearActors() {
        for(int i = 0; i < SUBREGIONS; i++) {
            for(int j = 0; j < SUBREGIONS; j++) {
                subRegions[i][j].clearActors();
            }
        }
    }

    @Override
    public void addActor(FirmamentActor actor) {
        int x = actor.originX;
        int z = actor.originZ;
        x = x&REGION_MASK;
        z = z&REGION_MASK;
        subRegions[x>>SUBREGION_SIZE_BITS][z>>SUBREGION_SIZE_BITS].addActor(actor);
    }

    @Override
    public void manageActors() {
        for(int i = 0; i < SUBREGIONS; i++) {
            for(int j = 0; j < SUBREGIONS; j++) {
                subRegions[i][j].manageActors();
            }
        }
    }

    @Override
    public void tickActors() {
        for(int i = 0; i < SUBREGIONS; i++) {
            for(int j = 0; j < SUBREGIONS; j++) {
                subRegions[i][j].tickActors();
            }
        }
    }

    @Override
    public void forEachPosition(BiConsumer<Integer, Integer> method) {
        for(int i = 0; i < SUBREGIONS; i++) {
            for(int j = 0; j < SUBREGIONS; j++) {
                int finalI = i;
                int finalJ = j;
                subRegions[i][j].forEachActivePosition((x, z) -> method.accept(x + finalI * FirmamentSubRegion.GRID_SIZE, z + finalJ * FirmamentSubRegion.GRID_SIZE));
            }
        }
    }

    @Override
    public void forEachActivePosition(BiConsumer<Integer, Integer> method) {
        for(int i = 0; i < SUBREGIONS; i++) {
            for(int j = 0; j < SUBREGIONS; j++) {
                if(subRegions[i][j].shouldUpdate) {
                    int finalI = i;
                    int finalJ = j;
                    subRegions[i][j].forEachActivePosition((x, z) -> method.accept(x + finalI * FirmamentSubRegion.GRID_SIZE, z + finalJ * FirmamentSubRegion.GRID_SIZE));
                }
            }
        }
    }

    @Override
    public float getDrip(int x, int z) {
        x = x&REGION_MASK;
        z = z&REGION_MASK;
        return subRegions[x>>SUBREGION_SIZE_BITS][z>>SUBREGION_SIZE_BITS].getDrip(x&SUBREGION_MASK, z&SUBREGION_MASK);
    }

    @Override
    public float getDamage(int x, int z) {
        x = x&REGION_MASK;
        z = z&REGION_MASK;
        return subRegions[x>>SUBREGION_SIZE_BITS][z>>SUBREGION_SIZE_BITS].getDamage(x&SUBREGION_MASK, z&SUBREGION_MASK);
    }

    @Override
    public float getDisplacement(int x, int z) {
        x = x&REGION_MASK;
        z = z&REGION_MASK;
        return subRegions[x>>SUBREGION_SIZE_BITS][z>>SUBREGION_SIZE_BITS].getDisplacement(x&SUBREGION_MASK, z&SUBREGION_MASK);
    }

    @Override
    public float getVelocity(int x, int z) {
        x = x&REGION_MASK;
        z = z&REGION_MASK;
        return subRegions[x>>SUBREGION_SIZE_BITS][z>>SUBREGION_SIZE_BITS].getVelocity(x&SUBREGION_MASK, z&SUBREGION_MASK);
    }

    @Override
    public float getDDrip(int x, int z) {
        x = x&REGION_MASK;
        z = z&REGION_MASK;
        return subRegions[x>>SUBREGION_SIZE_BITS][z>>SUBREGION_SIZE_BITS].getDDrip(x&SUBREGION_MASK, z&SUBREGION_MASK);
    }

    @Override
    public void setDrip(int x, int z, float value) {
        x = x&REGION_MASK;
        z = z&REGION_MASK;
        subRegions[x>>SUBREGION_SIZE_BITS][z>>SUBREGION_SIZE_BITS].setDrip(x&SUBREGION_MASK, z&SUBREGION_MASK, value);
    }

    @Override
    public void setDDrip(int x, int z, float value) {
        x = x&REGION_MASK;
        z = z&REGION_MASK;
        subRegions[x>>SUBREGION_SIZE_BITS][z>>SUBREGION_SIZE_BITS].setDDrip(x&SUBREGION_MASK, z&SUBREGION_MASK, value);
    }

    @Override
    public void setDamage(int x, int z, float value) {
        x = x&REGION_MASK;
        z = z&REGION_MASK;
        subRegions[x>>SUBREGION_SIZE_BITS][z>>SUBREGION_SIZE_BITS].setDamage(x&SUBREGION_MASK, z&SUBREGION_MASK, value);
    }

    @Override
    public void setDisplacement(int x, int z, float value) {
        x = x&REGION_MASK;
        z = z&REGION_MASK;
        subRegions[x>>SUBREGION_SIZE_BITS][z>>SUBREGION_SIZE_BITS].setDisplacement(x&SUBREGION_MASK, z&SUBREGION_MASK, value);
    }

    @Override
    public void setVelocity(int x, int z, float value) {
        x = x&REGION_MASK;
        z = z&REGION_MASK;
        subRegions[x>>SUBREGION_SIZE_BITS][z>>SUBREGION_SIZE_BITS].setVelocity(x&SUBREGION_MASK, z&SUBREGION_MASK, value);
    }

    @Override
    public void markActive(int x, int z) {
        active = true;
        x = x&REGION_MASK;
        z = z&REGION_MASK;
        subRegions[x>>SUBREGION_SIZE_BITS][z>>SUBREGION_SIZE_BITS].markActive(x&SUBREGION_MASK, z&SUBREGION_MASK);
    }

    @Override
    public void clearActive() {
        for(int i = 0; i < SUBREGIONS; i++) {
            for(int j = 0; j < SUBREGIONS; j++) {
                subRegions[i][j].clearActive();
            }
        }
    }

    @Override
    public void markShouldUpdate(int x, int z) {
        shouldUpdate = true;
        x = x&REGION_MASK;
        z = z&REGION_MASK;
        subRegions[x>>SUBREGION_SIZE_BITS][z>>SUBREGION_SIZE_BITS].markShouldUpdate();
    }

    @Override
    public void clearShouldUpdate() {
        if(this.shouldUpdate) {
            this.shouldUpdate = false;
            for (int i = 0; i < SUBREGIONS; i++) {
                for (int j = 0; j < SUBREGIONS; j++) {
                    subRegions[i][j].clearShouldUpdate();
                }
            }
        }
    }

    @Override
    public void markUpdatesFromActivity() {
        if(active) {
            for (int i = 0; i < SUBREGIONS; i++) {
                for (int j = 0; j < SUBREGIONS; j++) {
                    subRegions[i][j].markUpdatesFromActivity();
                }
            }
        }
    }

    @Override
    public boolean shouldUpdate() {
        return shouldUpdate;
    }
}

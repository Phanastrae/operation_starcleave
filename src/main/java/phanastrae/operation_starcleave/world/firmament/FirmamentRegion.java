package phanastrae.operation_starcleave.world.firmament;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class FirmamentRegion implements FirmamentAccess {

    public static final int REGION_SIZE = 512;
    public static final int REGION_MASK = 0x1FF;
    public static final int REGION_SIZE_BITS = 9;

    public static final int SUBREGIONS = 16;
    public static final int SUBREGION_MASK = 0x1F;
    public static final int SUBREGION_SIZE_BITS = 5;

    public FirmamentSubRegion[][] subRegions;
    boolean shouldUpdate = false;
    boolean active = false;

    // world coords of minimum x-z corner
    public final int x;
    public final int z;

    public final Firmament firmament;

    public FirmamentRegion(Firmament firmament, int x, int z) {
        this.firmament = firmament;
        this.x = x;
        this.z = z;

        this.subRegions = new FirmamentSubRegion[SUBREGIONS][SUBREGIONS];
        for(int i = 0; i < SUBREGIONS; i++) {
            for(int j = 0; j < SUBREGIONS; j++) {
                subRegions[i][j] = new FirmamentSubRegion(this, x + i * FirmamentSubRegion.SUBREGION_SIZE, z + j * FirmamentSubRegion.SUBREGION_SIZE);
            }
        }
    }

    public boolean getActive(int x, int z) {
        x = x&REGION_MASK;
        z = z&REGION_MASK;
        return subRegions[x>>SUBREGION_SIZE_BITS][z>>SUBREGION_SIZE_BITS].active[4];
    }

    public void forEachSubRegion(Consumer<FirmamentSubRegion> method) {
        for(int i = 0; i < SUBREGIONS; i++) {
            for(int j = 0; j < SUBREGIONS; j++) {
                method.accept(subRegions[i][j]);
            }
        }
    }

    @Override
    public void clearActors() {
        forEachSubRegion(FirmamentSubRegion::clearActors);
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
        forEachSubRegion(FirmamentSubRegion::manageActors);
    }

    @Override
    public void tickActors() {
        forEachSubRegion(FirmamentSubRegion::tickActors);
    }

    @Override
    public void forEachPosition(BiConsumer<Integer, Integer> method) {
        forEachSubRegion((firmamentSubRegion -> {
            firmamentSubRegion.forEachActivePosition((x, z) -> method.accept(x + firmamentSubRegion.x - this.x, z + firmamentSubRegion.z - this.z));

        }));
    }

    @Override
    public void forEachActivePosition(BiConsumer<Integer, Integer> method) {
        forEachSubRegion((firmamentSubRegion -> {
            if(firmamentSubRegion.shouldUpdate) {
                firmamentSubRegion.forEachActivePosition((x, z) -> method.accept(x + firmamentSubRegion.x - this.x, z + firmamentSubRegion.z - this.z));
            }

        }));
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
        if(active) {
            this.active = false;
            forEachSubRegion(FirmamentSubRegion::clearActive);
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
            forEachSubRegion(FirmamentSubRegion::clearShouldUpdate);
        }
    }

    @Override
    public void markUpdatesFromActivity() {
        if(active) {
            forEachSubRegion(FirmamentSubRegion::markUpdatesFromActivity);
        }
    }

    @Override
    public boolean shouldUpdate() {
        return shouldUpdate;
    }
}

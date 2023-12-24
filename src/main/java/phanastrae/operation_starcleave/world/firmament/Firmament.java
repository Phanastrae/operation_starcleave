package phanastrae.operation_starcleave.world.firmament;

import net.minecraft.world.World;

import java.util.function.BiConsumer;

public class Firmament implements FirmamentAccess {

    private Firmament() {
    }

    private static final Firmament INSTANCE = new Firmament();
    public static Firmament getInstance() {
        return INSTANCE;
    }

    public FirmamentRegion firmamentRegion = new FirmamentRegion(0, 0);

    public void tick(World world) {
        long t = world.getTime();
        if(t % 2 == 0) {
            manageActors();
            tickActors();

            if(t == 8) {
                clearShouldUpdate();
            }
            for(int i = 0; i < FirmamentRegion.SUBREGIONS; i++) {
                for(int j = 0; j < FirmamentRegion.SUBREGIONS; j++) {
                    FirmamentSubRegion subRegion = firmamentRegion.subRegions[i][j];

                    for(int k = 0; k < 9; k++) {
                        if(subRegion.active[k]) {
                            markShouldUpdate(i * FirmamentSubRegion.GRID_SIZE + subRegion.xOffset[k], j * FirmamentSubRegion.GRID_SIZE + subRegion.zOffset[k]);
                        }
                    }
                }
            }
            if(t == 8) {
                firmamentRegion.clearActive();
            }

            FirmamentUpdater.update(this);
        }
    }

    @Override
    public void clearActors() {
        firmamentRegion.clearActors();
    }

    @Override
    public void addActor(FirmamentActor actor) {
        firmamentRegion.addActor(actor);
    }

    @Override
    public void manageActors() {
        firmamentRegion.manageActors();
    }

    @Override
    public void tickActors() {
        firmamentRegion.tickActors();
    }

    @Override
    public void forEachPosition(BiConsumer<Integer, Integer> method) {
        firmamentRegion.forEachPosition(method);
    }

    @Override
    public void forEachActivePosition(BiConsumer<Integer, Integer> method) {
        firmamentRegion.forEachActivePosition(method);
    }

    @Override
    public float getDrip(int x, int z) {
        return firmamentRegion.getDrip(x, z);
    }

    @Override
    public float getDamage(int x, int z) {
        return firmamentRegion.getDamage(x, z);
    }

    @Override
    public float getDisplacement(int x, int z) {
        return firmamentRegion.getDisplacement(x, z);
    }

    @Override
    public float getVelocity(int x, int z) {
        return firmamentRegion.getVelocity(x, z);
    }

    @Override
    public float getDDrip(int x, int z) {
        return firmamentRegion.getDDrip(x, z);
    }

    @Override
    public void setDrip(int x, int z, float value) {
        firmamentRegion.setDrip(x, z, value);
    }

    @Override
    public void setDamage(int x, int z, float value) {
        firmamentRegion.setDamage(x, z, value);
    }

    @Override
    public void setDisplacement(int x, int z, float value) {
        firmamentRegion.setDisplacement(x, z, value);
    }

    @Override
    public void setVelocity(int x, int z, float value) {
        firmamentRegion.setVelocity(x, z, value);
    }

    @Override
    public void setDDrip(int x, int z, float value) {
        firmamentRegion.setDDrip(x, z, value);
    }

    @Override
    public void markActive(int x, int z) {
        firmamentRegion.markActive(x, z);
    }

    @Override
    public void clearActive() {
        firmamentRegion.clearActive();
    }

    @Override
    public void markShouldUpdate(int x, int z) {
        firmamentRegion.markShouldUpdate(x, z);
    }

    @Override
    public boolean shouldUpdate() {
        return firmamentRegion.shouldUpdate();
    }

    @Override
    public void clearShouldUpdate() {
        firmamentRegion.clearShouldUpdate();
    }

    @Override
    public void markUpdatesFromActivity() {
        firmamentRegion.markUpdatesFromActivity();
    }
}

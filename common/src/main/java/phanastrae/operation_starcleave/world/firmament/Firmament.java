package phanastrae.operation_starcleave.world.firmament;

import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.duck.FirmamentHolder;
import phanastrae.operation_starcleave.world.firmament.actor.FirmamentActor;
import phanastrae.operation_starcleave.world.firmament.pos.RegionPos;
import phanastrae.operation_starcleave.world.firmament.region_manager.FirmamentRegionManager;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Firmament implements FirmamentAccess {

    private final Level level;
    private final FirmamentRegionManager firmamentRegionManager;

    public Firmament(Level level, FirmamentRegionManager firmamentRegionManager) {
        this.level = level;
        this.firmamentRegionManager = firmamentRegionManager;
    }

    public int getY() {
        return this.level.getMaxBuildHeight();
    }

    public void tick(boolean runsNormally) {
        this.firmamentRegionManager.tick();

        if (runsNormally) {
            long t = level.getGameTime();
            if (t % 2 == 0) {
                this.manageActors();
                this.tickActors();

                if (t % 20 == 0) {
                    this.clearShouldUpdate();
                }
                this.markUpdatesFromActivity();

                if (t % 20 == 0) {
                    this.clearActive();
                }

                FirmamentUpdater.update(this);
            }
        }

        this.flushUpdates();
    }

    public void clientTick(boolean runsNormally) {
        this.firmamentRegionManager.tick();

        if (runsNormally) {
            this.manageActors();
            this.tickActors();
        }
    }

    public void forEachRegion(Consumer<FirmamentRegion> method) {
        this.firmamentRegionManager.forEachRegion(method);
    }

    @Nullable
    public FirmamentRegion getFirmamentRegion(int x, int z) {
        return getFirmamentRegion(RegionPos.idFromWorldCoords(x, z));
    }

    @Nullable
    public FirmamentRegion getFirmamentRegion(long id) {
        return this.firmamentRegionManager.getFirmamentRegion(id);
    }

    @Nullable
    public FirmamentRegion getFirmamentRegion(RegionPos regionPos) {
        return getFirmamentRegion(regionPos.id);
    }

    @Override
    public void clearActors() {
        forEachRegion(FirmamentRegion::clearActors);
    }

    @Override
    public void addActor(FirmamentActor actor) {
        FirmamentRegion firmamentRegion = getFirmamentRegion(actor.originX, actor.originZ);
        if (firmamentRegion != null) {
            firmamentRegion.addActor(actor);
        }
    }

    @Override
    public void manageActors() {
        forEachRegion(FirmamentRegion::manageActors);
    }

    @Override
    public void tickActors() {
        forEachRegion(FirmamentRegion::tickActors);
    }

    @Override
    public void forEachActor(Consumer<FirmamentActor> consumer) {
        forEachRegion(firmamentRegion -> firmamentRegion.forEachActor(consumer));
    }

    @Override
    public void forEachActivePosition(BiConsumer<Integer, Integer> method) {
        forEachRegion(firmamentRegion -> {
            if (firmamentRegion.isActive()) {
                firmamentRegion.forEachActivePosition((x, z) -> method.accept(x + firmamentRegion.x, z + firmamentRegion.z));
            }
        });
    }

    @Override
    public int getDrip(int x, int z) {
        FirmamentRegion firmamentRegion = getFirmamentRegion(x, z);
        if (firmamentRegion != null) {
            return firmamentRegion.getDrip(x, z);
        } else {
            return 0;
        }
    }

    @Override
    public int getDamage(int x, int z) {
        FirmamentRegion firmamentRegion = getFirmamentRegion(x, z);
        if (firmamentRegion != null) {
            return firmamentRegion.getDamage(x, z);
        } else {
            return 0;
        }
    }

    @Override
    public int getDisplacement(int x, int z) {
        FirmamentRegion firmamentRegion = getFirmamentRegion(x, z);
        if (firmamentRegion != null) {
            return firmamentRegion.getDisplacement(x, z);
        } else {
            return 0;
        }
    }

    @Override
    public int getVelocity(int x, int z) {
        FirmamentRegion firmamentRegion = getFirmamentRegion(x, z);
        if (firmamentRegion != null) {
            return firmamentRegion.getVelocity(x, z);
        } else {
            return 0;
        }
    }

    @Override
    public float getDDrip(int x, int z) {
        FirmamentRegion firmamentRegion = getFirmamentRegion(x, z);
        if (firmamentRegion != null) {
            return firmamentRegion.getDDrip(x, z);
        } else {
            return 0;
        }
    }

    @Override
    public void setDrip(int x, int z, int value) {
        FirmamentRegion firmamentRegion = getFirmamentRegion(x, z);
        if (firmamentRegion != null) {
            firmamentRegion.setDrip(x & FirmamentRegion.REGION_MASK, z & FirmamentRegion.REGION_MASK, value);
        }
    }

    @Override
    public void setDamage(int x, int z, int value) {
        FirmamentRegion firmamentRegion = getFirmamentRegion(x, z);
        if (firmamentRegion != null) {
            firmamentRegion.setDamage(x & FirmamentRegion.REGION_MASK, z & FirmamentRegion.REGION_MASK, value);
        }
    }

    @Override
    public void setDisplacement(int x, int z, int value) {
        FirmamentRegion firmamentRegion = getFirmamentRegion(x, z);
        if (firmamentRegion != null) {
            firmamentRegion.setDisplacement(x & FirmamentRegion.REGION_MASK, z & FirmamentRegion.REGION_MASK, value);
        }
    }

    @Override
    public void setVelocity(int x, int z, int value) {
        FirmamentRegion firmamentRegion = getFirmamentRegion(x, z);
        if (firmamentRegion != null) {
            firmamentRegion.setVelocity(x & FirmamentRegion.REGION_MASK, z & FirmamentRegion.REGION_MASK, value);
        }
    }

    @Override
    public void setDDrip(int x, int z, float value) {
        FirmamentRegion firmamentRegion = getFirmamentRegion(x, z);
        if (firmamentRegion != null) {
            firmamentRegion.setDDrip(x & FirmamentRegion.REGION_MASK, z & FirmamentRegion.REGION_MASK, value);
        }
    }

    @Override
    public void markActive(int x, int z) {
        FirmamentRegion firmamentRegion = getFirmamentRegion(x, z);
        if (firmamentRegion != null) {
            firmamentRegion.markActive(x & FirmamentRegion.REGION_MASK, z & FirmamentRegion.REGION_MASK);
        }
    }

    @Override
    public void clearActive() {
        forEachRegion(FirmamentRegion::clearActive);
    }

    public void markShouldUpdate(int x, int z) {
        FirmamentRegion firmamentRegion = getFirmamentRegion(x, z);
        if (firmamentRegion != null) {
            firmamentRegion.markShouldUpdate(x & FirmamentRegion.REGION_MASK, z & FirmamentRegion.REGION_MASK);
        }
    }

    @Override
    public void clearShouldUpdate() {
        forEachRegion(FirmamentRegion::clearShouldUpdate);
    }

    @Override
    public void markUpdatesFromActivity() {
        forEachRegion(FirmamentRegion::markUpdatesFromActivity);
    }

    public Level getLevel() {
        return this.level;
    }

    public FirmamentRegionManager getFirmamentRegionManager() {
        return this.firmamentRegionManager;
    }

    public static Firmament fromLevel(Level level) {
        if (level instanceof FirmamentHolder firmamentHolder) {
            return firmamentHolder.operation_starcleave$getFirmament();
        } else {
            OperationStarcleave.LOGGER.info("World " + level.gatherChunkSourceStats() + " has no Firmament!?");
            return null;
        }
    }

    @Nullable
    public FirmamentSubRegion getSubRegionFromId(long id) {
        int srx = (int) (id & 4294967295L);
        int srz = (int) ((id >>> 32) & 4294967295L);
        int x = srx << FirmamentRegion.SUBREGION_SIZE_BITS;
        int z = srz << FirmamentRegion.SUBREGION_SIZE_BITS;
        return this.getSubRegion(x, z);
    }

    @Nullable
    public FirmamentSubRegion getSubRegion(int x, int z) {
        FirmamentRegion firmamentRegion = this.getFirmamentRegion(x, z);
        if (firmamentRegion == null) {
            return null;
        }
        int lx = x & FirmamentRegion.REGION_MASK;
        int lz = z & FirmamentRegion.REGION_MASK;
        int lsrx = lx >> FirmamentRegion.SUBREGION_SIZE_BITS;
        int lsrz = lz >> FirmamentRegion.SUBREGION_SIZE_BITS;
        return firmamentRegion.subRegions[lsrx][lsrz];
    }

    public void flushUpdates() {
        this.firmamentRegionManager.forEachRegion(FirmamentRegion::flushUpdates);
    }
}

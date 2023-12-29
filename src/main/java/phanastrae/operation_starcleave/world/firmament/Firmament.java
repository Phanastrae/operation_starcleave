package phanastrae.operation_starcleave.world.firmament;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.OperationStarcleave;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Firmament implements FirmamentAccess {

    private final World world;
    private final FirmamentRegionManager firmamentRegionManager;

    public Firmament(World world, FirmamentRegionManager firmamentRegionManager) {
        this.world = world;
        this.firmamentRegionManager = firmamentRegionManager;
    }

    public void tick() {
        long t = world.getTime();
        if (t % 2 == 0) {
            manageActors();
            tickActors();

            if (t % 20 == 0) {
                clearShouldUpdate();
            }

            markUpdatesFromActivity();

            if (t % 20 == 0) {
                clearActive();
            }

            this.firmamentRegionManager.tick();

            FirmamentUpdater.update(this);
        }
    }

    public void forEachRegion(Consumer<FirmamentRegion> method) {
        this.firmamentRegionManager.forEachRegion(method);
    }

    public static long getRegionId(int x, int z) {
        int rx = x >> FirmamentRegion.REGION_SIZE_BITS;
        int rz = z >> FirmamentRegion.REGION_SIZE_BITS;
        return ChunkPos.toLong(rx, rz);
    }

    @Nullable
    public FirmamentRegion getFirmamentRegion(int x, int z) {
        return getFirmamentRegion(getRegionId(x, z));
    }

    @Nullable
    public FirmamentRegion getFirmamentRegion(long id) {
        return this.firmamentRegionManager.getFirmamentRegion(id);
    }

    @Override
    public void clearActors() {
        forEachRegion(FirmamentRegion::clearActors);
    }

    @Override
    public void addActor(FirmamentActor actor) {
        FirmamentRegion firmamentRegion = getFirmamentRegion(actor.originX, actor.originZ);
        if(firmamentRegion != null) {
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
    public void forEachPosition(BiConsumer<Integer, Integer> method) {
        forEachRegion(firmamentRegion -> firmamentRegion.forEachPosition(method));
    }

    @Override
    public void forEachActivePosition(BiConsumer<Integer, Integer> method) {
        forEachRegion(firmamentRegion -> {
            if (firmamentRegion.active) {
                firmamentRegion.forEachActivePosition((x, z) -> method.accept(x + firmamentRegion.x, z + firmamentRegion.z));
            }
        });
    }

    @Override
    public float getDrip(int x, int z) {
        FirmamentRegion firmamentRegion = getFirmamentRegion(x, z);
        if(firmamentRegion != null) {
            return firmamentRegion.getDrip(x, z);
        } else {
            return 0;
        }
    }

    @Override
    public float getDamage(int x, int z) {
        FirmamentRegion firmamentRegion = getFirmamentRegion(x, z);
        if(firmamentRegion != null) {
            return firmamentRegion.getDamage(x, z);
        } else {
            return 0;
        }
    }

    @Override
    public float getDisplacement(int x, int z) {
        FirmamentRegion firmamentRegion = getFirmamentRegion(x, z);
        if(firmamentRegion != null) {
            return firmamentRegion.getDisplacement(x, z);
        } else {
            return 0;
        }
    }

    @Override
    public float getVelocity(int x, int z) {
        FirmamentRegion firmamentRegion = getFirmamentRegion(x, z);
        if(firmamentRegion != null) {
            return firmamentRegion.getVelocity(x, z);
        } else {
            return 0;
        }
    }

    @Override
    public float getDDrip(int x, int z) {
        FirmamentRegion firmamentRegion = getFirmamentRegion(x, z);
        if(firmamentRegion != null) {
            return firmamentRegion.getDDrip(x, z);
        } else {
            return 0;
        }
    }

    @Override
    public void setDrip(int x, int z, float value) {
        FirmamentRegion firmamentRegion = getFirmamentRegion(x, z);
        if(firmamentRegion != null) {
            firmamentRegion.setDrip(x & FirmamentRegion.REGION_MASK, z & FirmamentRegion.REGION_MASK, value);
        }
    }

    @Override
    public void setDamage(int x, int z, float value) {
        FirmamentRegion firmamentRegion = getFirmamentRegion(x, z);
        if(firmamentRegion != null) {
            firmamentRegion.setDamage(x & FirmamentRegion.REGION_MASK, z & FirmamentRegion.REGION_MASK, value);
        }
    }

    @Override
    public void setDisplacement(int x, int z, float value) {
        FirmamentRegion firmamentRegion = getFirmamentRegion(x, z);
        if(firmamentRegion != null) {
            firmamentRegion.setDisplacement(x & FirmamentRegion.REGION_MASK, z & FirmamentRegion.REGION_MASK, value);
        }
    }

    @Override
    public void setVelocity(int x, int z, float value) {
        FirmamentRegion firmamentRegion = getFirmamentRegion(x, z);
        if(firmamentRegion != null) {
            firmamentRegion.setVelocity(x & FirmamentRegion.REGION_MASK, z & FirmamentRegion.REGION_MASK, value);
        }
    }

    @Override
    public void setDDrip(int x, int z, float value) {
        FirmamentRegion firmamentRegion = getFirmamentRegion(x, z);
        if(firmamentRegion != null) {
            firmamentRegion.setDDrip(x & FirmamentRegion.REGION_MASK, z & FirmamentRegion.REGION_MASK, value);
        }
    }

    @Override
    public void markActive(int x, int z) {
        FirmamentRegion firmamentRegion = getFirmamentRegion(x, z);
        if(firmamentRegion != null) {
            firmamentRegion.markActive(x & FirmamentRegion.REGION_MASK, z & FirmamentRegion.REGION_MASK);
        }
    }

    @Override
    public void clearActive() {
        forEachRegion(FirmamentRegion::clearActive);
    }

    @Override
    public void markShouldUpdate(int x, int z) {
        FirmamentRegion firmamentRegion = getFirmamentRegion(x, z);
        if(firmamentRegion != null) {
            firmamentRegion.markShouldUpdate(x & FirmamentRegion.REGION_MASK, z & FirmamentRegion.REGION_MASK);
        }
    }

    @Override
    public boolean shouldUpdate() {
        return true;
    }

    @Override
    public void clearShouldUpdate() {
        forEachRegion(FirmamentRegion::clearShouldUpdate);
    }

    @Override
    public void markUpdatesFromActivity() {
        forEachRegion(FirmamentRegion::markUpdatesFromActivity);
    }

    public World getWorld() {
        return this.world;
    }

    public FirmamentRegionManager getFirmamentRegionManager() {
        return this.firmamentRegionManager;
    }

    public static Firmament fromWorld(World world) {
        if(world instanceof FirmamentHolder opscw) {
            return opscw.operation_starcleave$getFirmament();
        } else {
            OperationStarcleave.LOGGER.info("World " + world.asString() + " has no Firmament!?");
            return null;
        }
    }

    @Nullable
    public FirmamentSubRegion getSubRegionFromId(long id) {
        int srx = (int)(id & 4294967295L);
        int srz = (int)((id >>> 32) & 4294967295L);
        int x = srx << FirmamentRegion.SUBREGION_SIZE_BITS;
        int z = srz << FirmamentRegion.SUBREGION_SIZE_BITS;
        return this.getSubRegion(x, z);
    }

    @Nullable
    public FirmamentSubRegion getSubRegion(int x, int z) {
        FirmamentRegion firmamentRegion = this.getFirmamentRegion(x, z);
        if(firmamentRegion == null) {
            return null;
        }
        int lx = x & FirmamentRegion.REGION_MASK;
        int lz = z & FirmamentRegion.REGION_MASK;
        int lsrx = lx >> FirmamentRegion.SUBREGION_SIZE_BITS;
        int lsrz = lz >> FirmamentRegion.SUBREGION_SIZE_BITS;
        return firmamentRegion.subRegions[lsrx][lsrz];
    }
}

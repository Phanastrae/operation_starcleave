package phanastrae.operation_starcleave.world.firmament;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.world.OperationStarcleaveWorld;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Firmament implements FirmamentAccess {

    private final World world;

    public Firmament(World world) {
        this.world = world;
        init();
    }

    public void init() {
        firmamentRegions.clear();
        for(int i = -1; i <= 1; i++) {
            for(int j = -1; j <= 1; j++) {
                int x = i * FirmamentRegion.REGION_SIZE;
                int z = j * FirmamentRegion.REGION_SIZE;
                firmamentRegions.put(getRegionId(x, z), new FirmamentRegion(this, x, z));
            }
        }
    }

    Long2ObjectLinkedOpenHashMap<FirmamentRegion> firmamentRegions = new Long2ObjectLinkedOpenHashMap<>();

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

            FirmamentUpdater.update(this);
        }
    }

    public void forEachRegion(Consumer<FirmamentRegion> method) {
        this.firmamentRegions.forEach((id, firmamentRegion) -> method.accept(firmamentRegion));
    }

    public long getRegionId(int x, int z) {
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
        if(this.firmamentRegions.containsKey(id)) {
            return this.firmamentRegions.get(id);
        } else {
            return null;
        }
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

    public static Firmament fromWorld(World world) {
        if(world instanceof OperationStarcleaveWorld opscw) {
            return opscw.operation_starcleave$getFirmament();
        } else {
            OperationStarcleave.LOGGER.info("World has no Firmament!?");
            return null;
        }
    }
}

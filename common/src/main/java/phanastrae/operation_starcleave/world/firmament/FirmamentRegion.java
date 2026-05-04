package phanastrae.operation_starcleave.world.firmament;

import net.minecraft.nbt.CompoundTag;
import phanastrae.operation_starcleave.world.firmament.actor.FirmamentActor;
import phanastrae.operation_starcleave.world.firmament.data.FirmamentRegionData;
import phanastrae.operation_starcleave.world.firmament.data.FirmamentSubRegionData;
import phanastrae.operation_starcleave.world.firmament.pos.RegionPos;
import phanastrae.operation_starcleave.world.firmament.pos.SubRegionPos;

import java.util.function.Consumer;

public class FirmamentRegion implements FirmamentAccess {
    // getter/setter functions should only be called with x and z in range [0, 511]

    public static final int REGION_SIZE = 512;
    public static final int REGION_MASK = 0x1FF;
    public static final int REGION_SIZE_BITS = 9;

    public static final int SUBREGIONS = 16;
    public static final int SUBREGION_MASK = 0x1F;
    public static final int SUBREGION_SIZE_BITS = 5;

    public FirmamentSubRegion[][] subRegions;
    private boolean pendingClientUpdate = false;

    public final RegionPos regionPos;
    public final Firmament firmament;

    public FirmamentRegion(Firmament firmament, RegionPos regionPos) {
        this.firmament = firmament;
        this.regionPos = regionPos;

        this.subRegions = new FirmamentSubRegion[SUBREGIONS][SUBREGIONS];
        for (int i = 0; i < SUBREGIONS; i++) {
            for (int j = 0; j < SUBREGIONS; j++) {
                this.subRegions[i][j] = new FirmamentSubRegion(
                        this,
                        SubRegionPos.fromWorldCoords(
                                this.minX() + i * FirmamentSubRegion.SUBREGION_SIZE,
                                this.minZ() + j * FirmamentSubRegion.SUBREGION_SIZE
                        )
                );
            }
        }
    }

    public int minX() {
        return this.regionPos.minWorldX;
    }

    public int minZ() {
        return this.regionPos.minWorldZ;
    }

    public void forEachSubRegion(Consumer<FirmamentSubRegion> method) {
        for (int i = 0; i < SUBREGIONS; i++) {
            for (int j = 0; j < SUBREGIONS; j++) {
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

        x = x & REGION_MASK;
        z = z & REGION_MASK;
        subRegions[x >> SUBREGION_SIZE_BITS][z >> SUBREGION_SIZE_BITS].addActor(actor);
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
    public void forEachActor(Consumer<FirmamentActor> consumer) {
        forEachSubRegion(firmamentSubRegion -> firmamentSubRegion.forEachActor(consumer));
    }

    @Override
    public int getDamage(int x, int z) {
        x = x & REGION_MASK;
        z = z & REGION_MASK;
        return subRegions[x >> SUBREGION_SIZE_BITS][z >> SUBREGION_SIZE_BITS].getDamage(x & SUBREGION_MASK, z & SUBREGION_MASK);
    }

    @Override
    public void setDamage(int x, int z, int value) {
        x = x & REGION_MASK;
        z = z & REGION_MASK;
        subRegions[x >> SUBREGION_SIZE_BITS][z >> SUBREGION_SIZE_BITS].setDamage(x & SUBREGION_MASK, z & SUBREGION_MASK, value);
        this.pendingClientUpdate = true;
    }

    public void flushUpdates() {
        if (this.pendingClientUpdate) {
            this.pendingClientUpdate = false;
            forEachSubRegion(FirmamentSubRegion::flushUpdates);
        }
    }

    public void read(CompoundTag nbt) {
        for (int i = 0; i < SUBREGIONS; i++) {
            for (int j = 0; j < SUBREGIONS; j++) {
                CompoundTag subregionNbt = nbt.getCompound("subregion_" + i + "_" + j);
                FirmamentSubRegion subRegion = this.subRegions[i][j];
                FirmamentSubRegion.readFromByteArray(subregionNbt.getByteArray("damage"), subRegion.damage, 0x7);
            }
        }
    }

    public void write(CompoundTag nbt) {
        for (int i = 0; i < SUBREGIONS; i++) {
            for (int j = 0; j < SUBREGIONS; j++) {
                CompoundTag subregionNbt = new CompoundTag();
                FirmamentSubRegion subRegion = this.subRegions[i][j];
                subregionNbt.putByteArray("damage", FirmamentSubRegion.getAsByteArray(subRegion.damage));
                nbt.put("subregion_" + i + "_" + j, subregionNbt);
            }
        }
    }

    public void readFromData(FirmamentRegionData firmamentRegionData) {
        FirmamentSubRegionData[][] data = firmamentRegionData.subRegionData;
        if (data.length != SUBREGIONS) {
            return;
        }
        for (int i = 0; i < SUBREGIONS; i++) {
            if (data[i] == null || data[i].length != SUBREGIONS)
                return;
        }

        for (int i = 0; i < SUBREGIONS; i++) {
            for (int j = 0; j < SUBREGIONS; j++) {
                this.subRegions[i][j].readFromData(data[i][j]);
            }
        }
    }
}

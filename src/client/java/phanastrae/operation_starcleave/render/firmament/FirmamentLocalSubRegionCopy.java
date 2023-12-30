package phanastrae.operation_starcleave.render.firmament;

import phanastrae.operation_starcleave.world.firmament.*;

import java.util.function.BiConsumer;

public class FirmamentLocalSubRegionCopy implements FirmamentView {
    // getter functions should only be called with x and z in range [-16, 31]

    private final FirmamentSubRegion[][] subRegionCopies;
    public FirmamentLocalSubRegionCopy(Firmament firmament, SubRegionPos subRegionPos) {
        // TODO consider not doing this
        this.subRegionCopies = new FirmamentSubRegion[3][3];
        for(int i = -1; i <= 1; i++) {
            for(int j = -1; j <= 1; j++) {
                SubRegionPos subRegionPos2 = new SubRegionPos(subRegionPos.srx+i, subRegionPos.srz+j);

                FirmamentSubRegion oldSubRegion = firmament.getSubRegionFromId(subRegionPos2.id);
                if(oldSubRegion == null) {
                    this.subRegionCopies[i+1][j+1] = new FirmamentSubRegion(null,  i * FirmamentSubRegion.SUBREGION_SIZE, j * FirmamentSubRegion.SUBREGION_SIZE);
                } else {
                    FirmamentSubRegionData data = new FirmamentSubRegionData(oldSubRegion);

                    FirmamentSubRegion newSubRegion = new FirmamentSubRegion(null, i * FirmamentSubRegion.SUBREGION_SIZE, j * FirmamentSubRegion.SUBREGION_SIZE);
                    newSubRegion.readFromData(data);
                    this.subRegionCopies[i+1][j+1] = newSubRegion;
                }
            }
        }
    }

    public FirmamentSubRegion getSubregionAt(int x, int z) {
        int srx = (x >> FirmamentRegion.SUBREGION_SIZE_BITS) + 1;
        int srz = (z >> FirmamentRegion.SUBREGION_SIZE_BITS) + 1;
        return this.subRegionCopies[srx][srz];
    }

    @Override
    public int getDrip(int x, int z) {
        int lx = x & FirmamentRegion.SUBREGION_MASK;
        int lz = z & FirmamentRegion.SUBREGION_MASK;
        return getSubregionAt(x, z).getDrip(lx, lz);
    }

    @Override
    public int getDamage(int x, int z) {
        int lx = x & FirmamentRegion.SUBREGION_MASK;
        int lz = z & FirmamentRegion.SUBREGION_MASK;
        return getSubregionAt(x, z).getDamage(lx, lz);
    }

    @Override
    public int getDisplacement(int x, int z) {
        int lx = x & FirmamentRegion.SUBREGION_MASK;
        int lz = z & FirmamentRegion.SUBREGION_MASK;
        return getSubregionAt(x, z).getDisplacement(lx, lz);
    }

    @Override
    public int getVelocity(int x, int z) {
        int lx = x & FirmamentRegion.SUBREGION_MASK;
        int lz = z & FirmamentRegion.SUBREGION_MASK;
        return getSubregionAt(x, z).getVelocity(lx, lz);
    }

    @Override
    public void forEachPosition(BiConsumer<Integer, Integer> method) {
        // unused
    }

    @Override
    public void forEachActivePosition(BiConsumer<Integer, Integer> method) {
        // unused
    }

    @Override
    public boolean shouldUpdate() {
        // unused
        return false;
    }
}

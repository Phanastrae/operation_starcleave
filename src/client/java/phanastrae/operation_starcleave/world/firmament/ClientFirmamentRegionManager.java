package phanastrae.operation_starcleave.world.firmament;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.render.firmament.FirmamentBuiltSubRegionHolder;
import phanastrae.operation_starcleave.render.firmament.FirmamentBuiltSubRegionStorage;
import phanastrae.operation_starcleave.render.firmament.FirmamentTextureStorage;

import java.util.function.Consumer;

public class ClientFirmamentRegionManager extends FirmamentRegionManager {

    Long2ObjectLinkedOpenHashMap<FirmamentRegionHolder> firmamentRegionHolders = new Long2ObjectLinkedOpenHashMap<>();

    private final ClientWorld clientWorld;
    public ClientFirmamentRegionManager(ClientWorld clientWorld) {
        this.clientWorld = clientWorld;
    }

    @Override
    public void forEachRegion(Consumer<FirmamentRegion> method) {
        this.firmamentRegionHolders.forEach((id, firmamentRegionHolder) -> {
            FirmamentRegion firmamentRegion = firmamentRegionHolder.getFirmamentRegion();
            if(firmamentRegion != null) {
                method.accept(firmamentRegion);
            }
        });
    }

    @Nullable
    @Override
    public FirmamentRegion getFirmamentRegion(long id) {
        if(this.firmamentRegionHolders.containsKey(id)) {
            return this.firmamentRegionHolders.get(id).getFirmamentRegion();
        } else {
            return null;
        }
    }

    @Override
    public void tick() {
    }

    public FirmamentRegionHolder loadRegion(long id) {
        if(this.firmamentRegionHolders.containsKey(id)) {
            return firmamentRegionHolders.get(id);
        }

        int rx = (int)(id & 4294967295L);
        int rz = (int)((id >>> 32) & 4294967295L);
        int x = rx << FirmamentRegion.REGION_SIZE_BITS;
        int z = rz << FirmamentRegion.REGION_SIZE_BITS;

        FirmamentRegion firmamentRegion = new FirmamentRegion(Firmament.fromWorld(this.clientWorld), x, z);

        FirmamentRegionHolder firmamentRegionHolder = new FirmamentRegionHolder(firmamentRegion);
        firmamentRegionHolder.recordAccess();

        this.firmamentRegionHolders.put(id, firmamentRegionHolder);
        firmamentRegionHolder.setState(FirmamentRegionHolder.FirmamentRegionState.STARTED);

        return firmamentRegionHolder;
    }

    public void unloadRegion(long id) {
        if(!this.firmamentRegionHolders.containsKey(id)) {
            return;
        }

        this.firmamentRegionHolders.remove(id);

        RegionPos regionPos = new RegionPos(id);
        for(int i = 0; i < FirmamentRegion.SUBREGIONS; i++) {
            for(int j = 0; j < FirmamentRegion.SUBREGIONS; j++) {
                SubRegionPos subRegionPos = SubRegionPos.fromWorldCoords(regionPos.worldX + i * FirmamentSubRegion.SUBREGION_SIZE, regionPos.worldZ + j * FirmamentSubRegion.SUBREGION_SIZE);
                FirmamentBuiltSubRegionStorage.getInstance().remove(subRegionPos.id);
            }
        }
        FirmamentTextureStorage.getInstance().onRegionRemoved(id);
    }
}
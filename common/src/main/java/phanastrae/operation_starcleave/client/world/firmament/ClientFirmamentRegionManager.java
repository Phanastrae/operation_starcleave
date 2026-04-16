package phanastrae.operation_starcleave.client.world.firmament;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import net.minecraft.client.multiplayer.ClientLevel;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.client.render.firmament.FirmamentTextureStorage;
import phanastrae.operation_starcleave.world.firmament.Firmament;
import phanastrae.operation_starcleave.world.firmament.FirmamentRegion;
import phanastrae.operation_starcleave.world.firmament.FirmamentRegionHolder;
import phanastrae.operation_starcleave.world.firmament.pos.RegionPos;
import phanastrae.operation_starcleave.world.firmament.region_manager.FirmamentRegionManager;

import java.util.function.Consumer;

public class ClientFirmamentRegionManager extends FirmamentRegionManager {

    private final Long2ObjectLinkedOpenHashMap<FirmamentRegionHolder> firmamentRegionHolders = new Long2ObjectLinkedOpenHashMap<>();
    private final ClientLevel level;

    public ClientFirmamentRegionManager(ClientLevel level) {
        this.level = level;
    }

    @Override
    public void forEachRegion(Consumer<FirmamentRegion> method) {
        this.firmamentRegionHolders.forEach((id, firmamentRegionHolder) -> {
            FirmamentRegion firmamentRegion = firmamentRegionHolder.getFirmamentRegion();
            if (firmamentRegion != null) {
                method.accept(firmamentRegion);
            }
        });
    }

    @Nullable
    @Override
    public FirmamentRegion getFirmamentRegion(long id) {
        if (this.firmamentRegionHolders.containsKey(id)) {
            return this.firmamentRegionHolders.get(id).getFirmamentRegion();
        } else {
            return null;
        }
    }

    @Override
    public void tick() {
    }

    public FirmamentRegionHolder loadRegion(long id) {
        if (this.firmamentRegionHolders.containsKey(id)) {
            return firmamentRegionHolders.get(id);
        } else {
            RegionPos regionPos = new RegionPos(id);
            FirmamentRegion firmamentRegion = new FirmamentRegion(Firmament.fromLevel(this.level), regionPos);

            FirmamentRegionHolder firmamentRegionHolder = new FirmamentRegionHolder(firmamentRegion);
            firmamentRegionHolder.recordAccess();

            this.firmamentRegionHolders.put(id, firmamentRegionHolder);
            firmamentRegionHolder.setState(FirmamentRegionHolder.FirmamentRegionState.STARTED);

            return firmamentRegionHolder;
        }
    }

    public void unloadRegion(long id) {
        if (this.firmamentRegionHolders.containsKey(id)) {
            this.firmamentRegionHolders.remove(id);
            FirmamentTextureStorage.getMainInstance().onRegionRemoved(id);
        }
    }
}
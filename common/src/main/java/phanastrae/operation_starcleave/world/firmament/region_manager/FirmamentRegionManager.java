package phanastrae.operation_starcleave.world.firmament.region_manager;

import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.world.firmament.FirmamentRegion;

import java.util.function.Consumer;

public abstract class FirmamentRegionManager {
    public abstract void forEachRegion(Consumer<FirmamentRegion> method);

    @Nullable
    public abstract FirmamentRegion getFirmamentRegion(long id);

    public abstract void tick();
}

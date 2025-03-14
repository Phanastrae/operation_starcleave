package phanastrae.operation_starcleave.world.firmament;

import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public abstract class FirmamentRegionManager {
    public abstract void forEachRegion(Consumer<FirmamentRegion> method);

    @Nullable
    public abstract FirmamentRegion getFirmamentRegion(long id);

    public abstract void tick();
}

package phanastrae.operation_starcleave.world.firmament;

import org.jetbrains.annotations.Nullable;

public class FirmamentRegionHolder {

    private final FirmamentRegion firmamentRegion;
    private long lastAccessTime;
    volatile private FirmamentRegionState state;

    public FirmamentRegionHolder(FirmamentRegion firmamentRegion) {
        this.firmamentRegion = firmamentRegion;
        setState(FirmamentRegionState.NOT_STARTED);
    }

    public enum FirmamentRegionState {
        NOT_STARTED,
        LOADING,
        READY_TO_START,
        STARTED
    }

    public FirmamentRegionState getState() {
        synchronized (this) {
            return this.state;
        }
    }

    public void setState(FirmamentRegionState state) {
        synchronized (this) {
            this.state = state;
        }
    }

    @Nullable
    public FirmamentRegion getFirmamentRegion() {
        if(this.getState() == FirmamentRegionState.STARTED) {
            return this.firmamentRegion;
        } else {
            return null;
        }
    }

    public void recordAccess() {
        this.lastAccessTime = firmamentRegion.firmament.getWorld().getTime();
    }

    public long getTimeSinceLastAccess(long currentTime) {
        return currentTime - this.lastAccessTime;
    }
}

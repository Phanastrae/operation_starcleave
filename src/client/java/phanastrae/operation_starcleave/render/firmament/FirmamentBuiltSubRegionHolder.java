package phanastrae.operation_starcleave.render.firmament;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.world.firmament.Firmament;
import phanastrae.operation_starcleave.world.firmament.FirmamentSubRegion;
import phanastrae.operation_starcleave.world.firmament.SubRegionPos;

public class FirmamentBuiltSubRegionHolder {

    public final long id;
    volatile private State state = State.NOT_STARTED;
    volatile private FirmamentBuiltSubRegion builtSubRegion = null;
    public final Box box;

    public FirmamentBuiltSubRegionHolder(long id, int y) {
        this.id = id;
        SubRegionPos subRegionPos = new SubRegionPos(id);
        this.box = new Box(subRegionPos.worldX, y - 1, subRegionPos.worldZ, subRegionPos.worldX + FirmamentSubRegion.SUBREGION_SIZE, y + 1, subRegionPos.worldZ + FirmamentSubRegion.SUBREGION_SIZE);
    }

    public enum State {
        NOT_STARTED,
        BUILDING,
        READY
    }

    private void setState(State state) {
        synchronized (this) {
            this.state = state;
        }
    }

    private State getState() {
        synchronized (this) {
            return this.state;
        }
    }

    @Nullable
    public FirmamentBuiltSubRegion getBuiltSubRegion() {
        synchronized (this) {
            if (this.state == State.READY) {
                return this.builtSubRegion;
            } else {
                return null;
            }
        }
    }

    private void setBuiltSubRegion(FirmamentBuiltSubRegion builtSubRegion) {
        synchronized (this) {
            this.builtSubRegion = builtSubRegion;
        }
    }

    public void build(Firmament firmament, SubRegionPos subRegionPos) {
        synchronized (this) {
            this.state = State.BUILDING;
            if(this.builtSubRegion != null) {
                this.builtSubRegion.close();
                this.builtSubRegion = null;
            }
        }
        // if subregion is empty, then don't build
        FirmamentSubRegion firmamentSubRegion = firmament.getSubRegion(subRegionPos.worldX, subRegionPos.worldZ);
        if(firmamentSubRegion == null || !firmamentSubRegion.hadDamageLastCheck()) {
            synchronized (this) {
                this.state = State.READY;
            }
            return;
        }

        // TODO check how efficient this copying thing is, consider changing how this works
        FirmamentLocalSubRegionCopy firmamentLocalSubRegionCopy = new FirmamentLocalSubRegionCopy(firmament, subRegionPos);


        // TODO multithread if needed, if so allocate more buffers
        BufferBuilder bufferBuilder = FirmamentBuiltSubRegionStorage.getInstance().bufferBuilder;
        doBuild(firmamentLocalSubRegionCopy, bufferBuilder);
    }

    private void doBuild(FirmamentLocalSubRegionCopy firmamentLocalSubRegionCopy, BufferBuilder bufferBuilder) {
        FirmamentBuiltSubRegion builtSubRegion = new FirmamentBuiltSubRegion(firmamentLocalSubRegionCopy, bufferBuilder);
        synchronized (this) {
            this.state = State.READY;
            this.builtSubRegion = builtSubRegion;
        }
    }

    public void close() {
        synchronized (this) {
            this.state = State.NOT_STARTED;
            if(this.builtSubRegion != null) {
                this.builtSubRegion.close();
                this.builtSubRegion = null;
            }
        }
    }
}

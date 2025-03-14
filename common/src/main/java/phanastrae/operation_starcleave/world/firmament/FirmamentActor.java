package phanastrae.operation_starcleave.world.firmament;

public abstract class FirmamentActor {

    public FirmamentActor(Firmament firmament, int originX, int originZ) {
        this.firmament = firmament;
        this.originX = originX;
        this.originZ = originZ;
    }

    public final Firmament firmament;
    private boolean active = true;

    public int originX;
    public int originZ;

    public abstract void tick();

    public void discard() {
        this.active = false;
    }

    public boolean isActive() {
        return this.active;
    }
}

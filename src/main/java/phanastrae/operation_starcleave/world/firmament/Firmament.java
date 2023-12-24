package phanastrae.operation_starcleave.world.firmament;

public class Firmament {
    private Firmament() {
    }

    private static final Firmament INSTANCE = new Firmament();
    public static Firmament getInstance() {
        return INSTANCE;
    }

    public FirmamentRegion firmamentRegion = new FirmamentRegion();

    public void tick() {
        this.firmamentRegion.tick();
    }
}

package phanastrae.operation_starcleave.world.firmament;

public interface FirmamentAccess extends FirmamentView {
    void clearActors();
    void addActor(FirmamentActor actor);
    void manageActors();
    void tickActors();

    void setDrip(int x, int z, float value);
    void setDamage(int x, int z, float value);
    void setDisplacement(int x, int z, float value);
    void setVelocity(int x, int z, float value);
    void setDDrip(int x, int z, float value);

    float getDDrip(int x, int z);

    void markActive(int x, int z);
    void clearActive();

    void markShouldUpdate(int x, int z);
    void clearShouldUpdate();

    void markUpdatesFromActivity();
}

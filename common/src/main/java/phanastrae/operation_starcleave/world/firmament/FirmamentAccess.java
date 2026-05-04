package phanastrae.operation_starcleave.world.firmament;

import phanastrae.operation_starcleave.world.firmament.actor.FirmamentActor;

import java.util.function.Consumer;

public interface FirmamentAccess extends FirmamentView {
    void clearActors();
    void addActor(FirmamentActor actor);
    void manageActors();
    void tickActors();
    void forEachActor(Consumer<FirmamentActor> consumer);

    void setDamage(int x, int z, int value);
}

package phanastrae.operation_starcleave.world;

import phanastrae.operation_starcleave.world.firmament.Firmament;

public interface OperationStarcleaveWorld {
    Firmament operation_starcleave$getFirmament();

    void operation_starcleave$setCleavingFlashTicksLeft(int ticks);
    int operation_starcleave$getCleavingFlashTicksLeft();
}

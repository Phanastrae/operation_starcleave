package phanastrae.operation_starcleave.world.firmament;

import java.util.function.BiConsumer;

public interface FirmamentView {
    int getDisplacement(int x, int z);
    int getVelocity(int x, int z);
    int getDamage(int x, int z);
    int getDrip(int x, int z);

    void forEachPosition(BiConsumer<Integer, Integer> method);
    void forEachActivePosition(BiConsumer<Integer, Integer> method);

    boolean shouldUpdate();
}

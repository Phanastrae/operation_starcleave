package phanastrae.operation_starcleave.world.firmament;

import java.util.function.BiConsumer;

public interface FirmamentView {
    float getDrip(int x, int z);
    float getDamage(int x, int z);
    float getDisplacement(int x, int z);
    float getVelocity(int x, int z);

    void forEachPosition(BiConsumer<Integer, Integer> method);
    void forEachActivePosition(BiConsumer<Integer, Integer> method);

    boolean shouldUpdate();
}

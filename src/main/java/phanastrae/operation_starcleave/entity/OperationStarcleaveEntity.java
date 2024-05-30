package phanastrae.operation_starcleave.entity;

public interface OperationStarcleaveEntity {
    long operation_starcleave$getLastStellarRepulsorUse();
    void operation_starcleave$setLastStellarRepulsorUse(long time);
    boolean operation_starcleave$isOnPhlogisticFire();
    void operation_starcleave$setOnPhlogisticFire(boolean onPhlogisticFire);
    int operation_starcleave$getPhlogisticFireTicks();
    void operation_starcleave$setPhlogisticFireTicks(int phlogisticFireTicks);
    void operation_starcleave$setOnPhlogisticFireFor(int seconds);
}

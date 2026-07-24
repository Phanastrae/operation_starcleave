package phanastrae.operation_starcleave.world.loot;

import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import phanastrae.operation_starcleave.OperationStarcleave;

public class OperationStarcleaveLootContextParams {
    public static final LootContextParam<Boolean> BROKEN_BY_EXPLOSION = create("broken_by_explosion");

    private static <T> LootContextParam<T> create(String id) {
        return new LootContextParam<>(OperationStarcleave.id(id));
    }
}

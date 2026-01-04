package phanastrae.operation_starcleave.world;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import phanastrae.operation_starcleave.OperationStarcleave;

public class OperationStarcleaveLootTables {
    public static final ResourceKey<LootTable> SINEATER_GOLDEN = create("entities/sineater/golden");
    public static final ResourceKey<LootTable> SINEATER_SPECTRAL = create("entities/sineater/spectral");
    public static final ResourceKey<LootTable> SINEATER_PHANTASMAL = create("entities/sineater/phantasmal");

    private static ResourceKey<LootTable> create(String name) {
        return ResourceKey.create(Registries.LOOT_TABLE, OperationStarcleave.id(name));
    }
}

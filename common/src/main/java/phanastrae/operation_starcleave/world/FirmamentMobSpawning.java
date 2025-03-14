package phanastrae.operation_starcleave.world;

import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.MobSpawnSettings;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;

public class FirmamentMobSpawning {
    public static final WeightedRandomList<MobSpawnSettings.SpawnerData> NOSPAWNS = WeightedRandomList.create(
    );

    public static final WeightedRandomList<MobSpawnSettings.SpawnerData> STARLIGHT_MONSTERS = WeightedRandomList.create(
            new MobSpawnSettings.SpawnerData(OperationStarcleaveEntityTypes.SUBCAELIC_TORPEDO, 35, 3, 7),
            new MobSpawnSettings.SpawnerData(EntityType.ENDERMAN, 10, 1, 4),
            new MobSpawnSettings.SpawnerData(OperationStarcleaveEntityTypes.SUBCAELIC_DUX, 3, 1, 1)
    );
}

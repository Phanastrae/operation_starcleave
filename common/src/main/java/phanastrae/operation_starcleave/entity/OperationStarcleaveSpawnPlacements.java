package phanastrae.operation_starcleave.entity;

import net.minecraft.world.entity.*;
import net.minecraft.world.level.levelgen.Heightmap;
import phanastrae.operation_starcleave.mixin.common.accessor.SpawnPlacementsAccessor;

public class OperationStarcleaveSpawnPlacements {

    public static void init() {
        register(OperationStarcleaveEntityTypes.STARCLEAVER_GOLEM, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        register(OperationStarcleaveEntityTypes.SUBCAELIC_TORPEDO, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        register(OperationStarcleaveEntityTypes.SUBCAELIC_DUX, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
        register(OperationStarcleaveEntityTypes.SINEATER, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules);
    }

    public static <T extends Mob> void register(
            EntityType<T> entityType, SpawnPlacementType spawnPlacementType, Heightmap.Types heightmapType, SpawnPlacements.SpawnPredicate<T> predicate
    ) {
        SpawnPlacementsAccessor.invokeRegister(entityType, spawnPlacementType, heightmapType, predicate);
    }
}

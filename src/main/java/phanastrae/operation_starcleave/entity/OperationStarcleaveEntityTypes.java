package phanastrae.operation_starcleave.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.entity.mob.StarcleaverGolemEntity;
import phanastrae.operation_starcleave.entity.projectile.FirmamentRejuvenatorEntity;
import phanastrae.operation_starcleave.entity.projectile.SplashStarbleachEntity;
import phanastrae.operation_starcleave.entity.projectile.StarbleachedPearlEntity;

public class OperationStarcleaveEntityTypes {

    public static final EntityType<StarcleaverGolemEntity> STARCLEAVER_GOLEM = Registry.register(
            Registries.ENTITY_TYPE,
            OperationStarcleave.id("starcleaver_golem"),
            FabricEntityTypeBuilder.<StarcleaverGolemEntity>create(SpawnGroup.MISC, StarcleaverGolemEntity::new).dimensions(EntityDimensions.fixed(0.6f, 0.6f)).fireImmune().build()
    );

    public static final EntityType<SplashStarbleachEntity> SPLASH_STARBLEACH = Registry.register(
            Registries.ENTITY_TYPE,
            OperationStarcleave.id("splash_starbleach_bottle"),
            FabricEntityTypeBuilder.<SplashStarbleachEntity>create(SpawnGroup.MISC, SplashStarbleachEntity::new).dimensions(new EntityDimensions(0.25F, 0.25F, true)).trackRangeChunks(4).trackedUpdateRate(10).build()
    );

    public static final EntityType<StarbleachedPearlEntity> STARBLEACHED_PEARL = Registry.register(
            Registries.ENTITY_TYPE,
            OperationStarcleave.id("starbleached_pearl"),
            FabricEntityTypeBuilder.<StarbleachedPearlEntity>create(SpawnGroup.MISC, StarbleachedPearlEntity::new).dimensions(new EntityDimensions(0.25F, 0.25F, true)).trackRangeChunks(4).trackedUpdateRate(10).build()
    );

    public static final EntityType<FirmamentRejuvenatorEntity> FIRMAMENT_REJUVENATOR = Registry.register(
            Registries.ENTITY_TYPE,
            OperationStarcleave.id("firmament_rejuvenator"),
            FabricEntityTypeBuilder.<FirmamentRejuvenatorEntity>create(SpawnGroup.MISC, FirmamentRejuvenatorEntity::new).dimensions(new EntityDimensions(0.25F, 0.25F, true)).trackRangeChunks(4).trackedUpdateRate(10).build()
    );

    public static void init() {
        FabricDefaultAttributeRegistry.register(STARCLEAVER_GOLEM, StarcleaverGolemEntity.createStarcleaverGolemAttributes());
    }
}

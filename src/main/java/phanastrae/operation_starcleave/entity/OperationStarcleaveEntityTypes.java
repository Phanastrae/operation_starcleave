package phanastrae.operation_starcleave.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.entity.mob.StarcleaverGolemEntity;
import phanastrae.operation_starcleave.entity.mob.SubcaelicDuxEntity;
import phanastrae.operation_starcleave.entity.mob.SubcaelicTorpedoEntity;
import phanastrae.operation_starcleave.entity.projectile.FirmamentRejuvenatorEntity;
import phanastrae.operation_starcleave.entity.projectile.SplashStarbleachEntity;
import phanastrae.operation_starcleave.entity.projectile.StarbleachedPearlEntity;

public class OperationStarcleaveEntityTypes {

    public static final EntityType<StarcleaverGolemEntity> STARCLEAVER_GOLEM = register(
            "starcleaver_golem",
            EntityType.Builder.create(StarcleaverGolemEntity::new, SpawnGroup.MISC).dimensions(0.6f, 0.6f).maxTrackingRange(10).makeFireImmune().build()
    );

    public static final EntityType<SubcaelicTorpedoEntity> SUBCAELIC_TORPEDO = register(
            "subcaelic_torpedo",
            EntityType.Builder.create(SubcaelicTorpedoEntity::new, SpawnGroup.MISC).dimensions(1f, 1f).maxTrackingRange(10).build()
    );

    public static final EntityType<SubcaelicDuxEntity> SUBCAELIC_DUX = register(
            "subcaelic_dux",
            EntityType.Builder.create(SubcaelicDuxEntity::new, SpawnGroup.MISC).dimensions(7f, 7f).maxTrackingRange(10).build()
    );

    public static final EntityType<SplashStarbleachEntity> SPLASH_STARBLEACH = register(
            "splash_starbleach_bottle",
            EntityType.Builder.<SplashStarbleachEntity>create(SplashStarbleachEntity::new, SpawnGroup.MISC).dimensions(0.25F, 0.25F).maxTrackingRange(4).trackingTickInterval(10).build()
    );

    public static final EntityType<StarbleachedPearlEntity> STARBLEACHED_PEARL = register(
            "starbleached_pearl",
            EntityType.Builder.<StarbleachedPearlEntity>create(StarbleachedPearlEntity::new, SpawnGroup.MISC).dimensions(0.25F, 0.25F).maxTrackingRange(4).trackingTickInterval(10).build()
    );

    public static final EntityType<FirmamentRejuvenatorEntity> FIRMAMENT_REJUVENATOR = register(
            "firmament_rejuvenator",
            EntityType.Builder.<FirmamentRejuvenatorEntity>create(FirmamentRejuvenatorEntity::new, SpawnGroup.MISC).dimensions(0.25F, 0.25F).maxTrackingRange(4).trackingTickInterval(10).build()
    );

    public static void init() {
        FabricDefaultAttributeRegistry.register(STARCLEAVER_GOLEM, StarcleaverGolemEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(SUBCAELIC_TORPEDO, SubcaelicTorpedoEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(SUBCAELIC_DUX, SubcaelicDuxEntity.createAttributes());
    }

    public static <T extends Entity> EntityType<T> register(String id, EntityType<T> entityType) {
        return Registry.register(
                Registries.ENTITY_TYPE,
                OperationStarcleave.id(id),
                entityType
        );
    }
}

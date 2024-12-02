package phanastrae.operation_starcleave.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
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
            EntityType.Builder.of(StarcleaverGolemEntity::new, MobCategory.MISC).sized(0.6f, 0.6f).clientTrackingRange(10).fireImmune().build()
    );

    public static final EntityType<SubcaelicTorpedoEntity> SUBCAELIC_TORPEDO = register(
            "subcaelic_torpedo",
            EntityType.Builder.of(SubcaelicTorpedoEntity::new, MobCategory.MISC).sized(1f, 1f).clientTrackingRange(10).build()
    );

    public static final EntityType<SubcaelicDuxEntity> SUBCAELIC_DUX = register(
            "subcaelic_dux",
            EntityType.Builder.of(SubcaelicDuxEntity::new, MobCategory.MISC).sized(7f, 7f).clientTrackingRange(10).build()
    );

    public static final EntityType<SplashStarbleachEntity> SPLASH_STARBLEACH = register(
            "splash_starbleach_bottle",
            EntityType.Builder.<SplashStarbleachEntity>of(SplashStarbleachEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build()
    );

    public static final EntityType<StarbleachedPearlEntity> STARBLEACHED_PEARL = register(
            "starbleached_pearl",
            EntityType.Builder.<StarbleachedPearlEntity>of(StarbleachedPearlEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build()
    );

    public static final EntityType<FirmamentRejuvenatorEntity> FIRMAMENT_REJUVENATOR = register(
            "firmament_rejuvenator",
            EntityType.Builder.<FirmamentRejuvenatorEntity>of(FirmamentRejuvenatorEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build()
    );

    public static void init() {
        FabricDefaultAttributeRegistry.register(STARCLEAVER_GOLEM, StarcleaverGolemEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(SUBCAELIC_TORPEDO, SubcaelicTorpedoEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(SUBCAELIC_DUX, SubcaelicDuxEntity.createAttributes());
    }

    public static <T extends Entity> EntityType<T> register(String id, EntityType<T> entityType) {
        return Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                OperationStarcleave.id(id),
                entityType
        );
    }
}

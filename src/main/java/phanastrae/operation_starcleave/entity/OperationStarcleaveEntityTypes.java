package phanastrae.operation_starcleave.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
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
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, StarcleaverGolemEntity::new).dimensions(EntityDimensions.fixed(0.6f, 0.6f)).trackRangeChunks(10).fireImmune().build()
    );

    public static final EntityType<SubcaelicTorpedoEntity> SUBCAELIC_TORPEDO = register(
            "subcaelic_torpedo",
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, SubcaelicTorpedoEntity::new).dimensions(EntityDimensions.fixed(1f, 1f)).trackRangeChunks(10).build()
    );

    public static final EntityType<SubcaelicDuxEntity> SUBCAELIC_DUX = register(
            "subcaelic_dux",
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, SubcaelicDuxEntity::new).dimensions(EntityDimensions.fixed(7f, 7f)).trackRangeChunks(10).build()
    );

    public static final EntityType<SplashStarbleachEntity> SPLASH_STARBLEACH = register(
            "splash_starbleach_bottle",
            FabricEntityTypeBuilder.<SplashStarbleachEntity>create(SpawnGroup.MISC, SplashStarbleachEntity::new).dimensions(new EntityDimensions(0.25F, 0.25F, true)).trackRangeChunks(4).trackedUpdateRate(10).build()
    );

    public static final EntityType<StarbleachedPearlEntity> STARBLEACHED_PEARL = register(
            "starbleached_pearl",
            FabricEntityTypeBuilder.<StarbleachedPearlEntity>create(SpawnGroup.MISC, StarbleachedPearlEntity::new).dimensions(new EntityDimensions(0.25F, 0.25F, true)).trackRangeChunks(4).trackedUpdateRate(10).build()
    );

    public static final EntityType<FirmamentRejuvenatorEntity> FIRMAMENT_REJUVENATOR = register(
            "firmament_rejuvenator",
            FabricEntityTypeBuilder.<FirmamentRejuvenatorEntity>create(SpawnGroup.MISC, FirmamentRejuvenatorEntity::new).dimensions(new EntityDimensions(0.25F, 0.25F, true)).trackRangeChunks(4).trackedUpdateRate(10).build()
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

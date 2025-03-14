package phanastrae.operation_starcleave.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.entity.mob.StarcleaverGolemEntity;
import phanastrae.operation_starcleave.entity.mob.SubcaelicDuxEntity;
import phanastrae.operation_starcleave.entity.mob.SubcaelicTorpedoEntity;
import phanastrae.operation_starcleave.entity.projectile.*;
import phanastrae.operation_starcleave.services.XPlatInterface;

import java.util.function.BiConsumer;

public class OperationStarcleaveEntityTypes {

    public static final ResourceLocation STARCLEAVER_GOLEM_KEY = id("starcleaver_golem");
    public static final EntityType<StarcleaverGolemEntity> STARCLEAVER_GOLEM =
            createBuilder(StarcleaverGolemEntity::new, MobCategory.MISC)
                    .sized(0.6f, 0.6f)
                    .clientTrackingRange(10)
                    .fireImmune()
                    .build(getStr(STARCLEAVER_GOLEM_KEY));

    public static final ResourceLocation SUBCAELIC_TORPEDO_KEY = id("subcaelic_torpedo");
    public static final EntityType<SubcaelicTorpedoEntity> SUBCAELIC_TORPEDO =
            createBuilder(SubcaelicTorpedoEntity::new, MobCategory.MONSTER)
                    .sized(1f, 1f)
                    .clientTrackingRange(10)
                    .build(getStr(SUBCAELIC_TORPEDO_KEY));

    public static final ResourceLocation SUBCAELIC_DUX_KEY = id("subcaelic_dux");
    public static final EntityType<SubcaelicDuxEntity> SUBCAELIC_DUX =
        createBuilder(SubcaelicDuxEntity::new, MobCategory.MONSTER)
                .sized(7f, 7f)
                .clientTrackingRange(10)
                .build(getStr(SUBCAELIC_DUX_KEY));

    public static final ResourceLocation SPLASH_STARBLEACH_KEY = id("splash_starbleach_bottle");
    public static final EntityType<SplashStarbleachEntity> SPLASH_STARBLEACH =
        EntityType.Builder.<SplashStarbleachEntity>of(SplashStarbleachEntity::new, MobCategory.MISC)
                .sized(0.25F, 0.25F)
                .clientTrackingRange(4)
                .updateInterval(10)
                .build(getStr(SPLASH_STARBLEACH_KEY));

    public static final ResourceLocation STARBLEACHED_PEARL_KEY = id("starbleached_pearl");
    public static final EntityType<StarbleachedPearlEntity> STARBLEACHED_PEARL =
            EntityType.Builder.<StarbleachedPearlEntity>of(StarbleachedPearlEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build(getStr(STARBLEACHED_PEARL_KEY));

    public static final ResourceLocation FIRMAMENT_REJUVENATOR_KEY = id("firmament_rejuvenator");
    public static final EntityType<FirmamentRejuvenatorEntity> FIRMAMENT_REJUVENATOR =
            EntityType.Builder.<FirmamentRejuvenatorEntity>of(FirmamentRejuvenatorEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(12)
                    .updateInterval(10)
                    .build(getStr(FIRMAMENT_REJUVENATOR_KEY));

    public static final ResourceLocation PHLOGISTIC_SPARK_KEY = id("phlogistic_spark");
    public static final EntityType<PhlogisticSparkEntity> PHLOGISTIC_SPARK =
            EntityType.Builder.<PhlogisticSparkEntity>of(PhlogisticSparkEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build(getStr(PHLOGISTIC_SPARK_KEY));

    public static final ResourceLocation NUCLEAR_STARDROP_KEY = id("nuclear_stardrop");
    public static final EntityType<NuclearStardropEntity> NUCLEAR_STARDROP =
            EntityType.Builder.<NuclearStardropEntity>of(NuclearStardropEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build(getStr(NUCLEAR_STARDROP_KEY));

    public static final ResourceLocation NUCLEAR_STORMCLOUD_KEY = id("nuclear_stormcloud");
    public static final EntityType<NuclearStormcloudEntity> NUCLEAR_STORMCLOUD =
            EntityType.Builder.<NuclearStormcloudEntity>of(NuclearStormcloudEntity::new, MobCategory.MISC)
                    .sized(5F, 3F)
                    .clientTrackingRange(16)
                    .updateInterval(2)
                    .build(getStr(NUCLEAR_STORMCLOUD_KEY));

    public static void init(BiConsumer<ResourceLocation, EntityType<?>> r) {
        // mobs
        r.accept(STARCLEAVER_GOLEM_KEY, STARCLEAVER_GOLEM);

        r.accept(SUBCAELIC_TORPEDO_KEY, SUBCAELIC_TORPEDO);
        r.accept(SUBCAELIC_DUX_KEY, SUBCAELIC_DUX);

        // projectiles
        r.accept(SPLASH_STARBLEACH_KEY, SPLASH_STARBLEACH);
        r.accept(STARBLEACHED_PEARL_KEY, STARBLEACHED_PEARL);
        r.accept(FIRMAMENT_REJUVENATOR_KEY, FIRMAMENT_REJUVENATOR);
        r.accept(PHLOGISTIC_SPARK_KEY, PHLOGISTIC_SPARK);
        r.accept(NUCLEAR_STARDROP_KEY, NUCLEAR_STARDROP);

        // misc
        r.accept(NUCLEAR_STORMCLOUD_KEY, NUCLEAR_STORMCLOUD);
    }

    public static void registerEntityAttributes(org.apache.logging.log4j.util.BiConsumer<EntityType<? extends LivingEntity>, AttributeSupplier.Builder> r) {
        r.accept(STARCLEAVER_GOLEM, StarcleaverGolemEntity.createAttributes());
        r.accept(SUBCAELIC_TORPEDO, SubcaelicTorpedoEntity.createAttributes());
        r.accept(SUBCAELIC_DUX, SubcaelicDuxEntity.createAttributes());
    }

    private static ResourceLocation id(String path) {
        return OperationStarcleave.id(path);
    }

    @Nullable
    private static String getStr(ResourceLocation resourceLocation) {
        // sending null on neoforge crashes, but sending a string on fabric logs an error
        String loader = XPlatInterface.INSTANCE.getLoader();
        if(loader.equals("fabric")) {
            return null;
        } else {
            return resourceLocation.toString();
        }
    }

    private static <T extends Entity> EntityType.Builder<T> createBuilder(EntityType.EntityFactory<T> factory, MobCategory spawnGroup) {
        return EntityType.Builder.of(factory, spawnGroup);
    }
}

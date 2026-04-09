package phanastrae.operation_starcleave.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.entity.mob.*;
import phanastrae.operation_starcleave.entity.projectile.*;
import phanastrae.operation_starcleave.services.XPlatInterface;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class OperationStarcleaveEntityTypes {
    private static final Map<ResourceLocation, EntityType<? extends Entity>> UNREGISTERED_ENTITY_TYPES = new HashMap<>();

    public static final EntityType<StarcleaverGolemEntity> STARCLEAVER_GOLEM = register(id("starcleaver_golem"),
            createBuilder(StarcleaverGolemEntity::new, MobCategory.MISC)
                    .sized(0.6f, 0.6f)
                    .clientTrackingRange(10)
                    .fireImmune()
    );

    public static final EntityType<SubcaelicTorpedoEntity> SUBCAELIC_TORPEDO = register(id("subcaelic_torpedo"),
            createBuilder(SubcaelicTorpedoEntity::new, MobCategory.MONSTER)
                    .sized(1f, 1f)
                    .clientTrackingRange(10)
    );

    public static final EntityType<SubcaelicDuxEntity> SUBCAELIC_DUX = register(id("subcaelic_dux"),
            createBuilder(SubcaelicDuxEntity::new, MobCategory.MONSTER)
                    .sized(7f, 7f)
                    .clientTrackingRange(10)
    );

    public static final EntityType<SineaterEntity> SINEATER = register(id("sineater"),
            createBuilder(SineaterEntity::new, MobCategory.MONSTER)
                    .sized(2.2f, 1.65f)
                    .clientTrackingRange(10)
    );

    public static final EntityType<TractorbloomEntity> TRACTORBLOOM = register(id("tractorbloom"),
            createBuilder(TractorbloomEntity::new, MobCategory.MONSTER)
                    .sized(2.5f, 1.375f)
                    .clientTrackingRange(10)
    );

    public static final EntityType<HammertailGolemEntity> HAMMERTAIL_GOLEM = register(id("hammertail_golem"),
            createBuilder(HammertailGolemEntity::new, MobCategory.MISC)
                    .sized(0.8f, 2.2f)
                    .clientTrackingRange(10)
                    .fireImmune()
    );

    public static final EntityType<SplashStarbleachEntity> SPLASH_STARBLEACH = register(id("splash_starbleach_bottle"),
            EntityType.Builder.<SplashStarbleachEntity>of(SplashStarbleachEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );

    public static final EntityType<StarbleachedPearlEntity> STARBLEACHED_PEARL = register(id("starbleached_pearl"),
            EntityType.Builder.<StarbleachedPearlEntity>of(StarbleachedPearlEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );

    public static final EntityType<FirmamentRejuvenatorEntity> FIRMAMENT_REJUVENATOR = register(id("firmament_rejuvenator"),
            EntityType.Builder.<FirmamentRejuvenatorEntity>of(FirmamentRejuvenatorEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(12)
                    .updateInterval(10)
    );

    public static final EntityType<PhlogisticSparkEntity> PHLOGISTIC_SPARK = register(id("phlogistic_spark"),
            EntityType.Builder.<PhlogisticSparkEntity>of(PhlogisticSparkEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );

    public static final EntityType<NuclearStardropEntity> NUCLEAR_STARDROP = register(id("nuclear_stardrop"),
            EntityType.Builder.<NuclearStardropEntity>of(NuclearStardropEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );

    public static final EntityType<NuclearStormcloudEntity> NUCLEAR_STORMCLOUD = register(id("nuclear_stormcloud"),
            EntityType.Builder.<NuclearStormcloudEntity>of(NuclearStormcloudEntity::new, MobCategory.MISC)
                    .sized(5F, 3F)
                    .clientTrackingRange(16)
                    .updateInterval(2)
    );

    public static final EntityType<NucleosyntheseedEntity> NUCLEOSYNTHESEED = register(id("nucleosyntheseed"),
            EntityType.Builder.<NucleosyntheseedEntity>of(NucleosyntheseedEntity::new, MobCategory.MISC)
                    .sized(0.98F, 0.98F)
                    .eyeHeight(0.15F)
                    .clientTrackingRange(10)
                    .updateInterval(4)
                    .fireImmune()
    );

    public static final EntityType<BismuthBlastEntity> BISMUTH_BLAST = register(id("bismuth_blast"),
            EntityType.Builder.<BismuthBlastEntity>of(BismuthBlastEntity::new, MobCategory.MISC)
                    .sized(0.4F, 0.4F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );

    public static void init(BiConsumer<ResourceLocation, EntityType<?>> r) {
        UNREGISTERED_ENTITY_TYPES.forEach(r);
        UNREGISTERED_ENTITY_TYPES.clear();
    }

    public static void registerEntityAttributes(BiConsumer<EntityType<? extends LivingEntity>, AttributeSupplier.Builder> r) {
        r.accept(STARCLEAVER_GOLEM, StarcleaverGolemEntity.createAttributes());
        r.accept(SUBCAELIC_TORPEDO, SubcaelicTorpedoEntity.createAttributes());
        r.accept(SUBCAELIC_DUX, SubcaelicDuxEntity.createAttributes());
        r.accept(SINEATER, SineaterEntity.createAttributes());
        r.accept(TRACTORBLOOM, TractorbloomEntity.createAttributes());
        r.accept(HAMMERTAIL_GOLEM, HammertailGolemEntity.createAttributes());
    }

    private static ResourceLocation id(String path) {
        return OperationStarcleave.id(path);
    }

    private static <T extends Entity> EntityType<T> register(ResourceLocation location, EntityType.Builder<T> builder) {
        return register(location, builder.build(getStr(location)));
    }

    private static <T extends Entity> EntityType<T> register(ResourceLocation location, EntityType<T> type) {
        UNREGISTERED_ENTITY_TYPES.put(location, type);
        return type;
    }

    @Nullable
    private static String getStr(ResourceLocation resourceLocation) {
        // sending null on neoforge crashes, but sending a string on fabric logs an error
        String loader = XPlatInterface.INSTANCE.getLoader();
        if (loader.equals("fabric")) {
            return null;
        } else {
            return resourceLocation.toString();
        }
    }

    private static <T extends Entity> EntityType.Builder<T> createBuilder(EntityType.EntityFactory<T> factory, MobCategory spawnGroup) {
        return EntityType.Builder.of(factory, spawnGroup);
    }
}

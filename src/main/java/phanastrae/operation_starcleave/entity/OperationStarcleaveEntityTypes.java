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
import phanastrae.operation_starcleave.entity.projectile.FirmamentRejuvenatorEntity;
import phanastrae.operation_starcleave.entity.projectile.SplashStarbleachEntity;
import phanastrae.operation_starcleave.entity.projectile.StarbleachedPearlEntity;
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
            createBuilder(SubcaelicTorpedoEntity::new, MobCategory.MISC)
                    .sized(1f, 1f)
                    .clientTrackingRange(10)
                    .build(getStr(SUBCAELIC_TORPEDO_KEY));

    public static final ResourceLocation SUBCAELIC_DUX_KEY = id("subcaelic_dux");
    public static final EntityType<SubcaelicDuxEntity> SUBCAELIC_DUX =
        createBuilder(SubcaelicDuxEntity::new, MobCategory.MISC)
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
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build(getStr(FIRMAMENT_REJUVENATOR_KEY));

    public static void init(BiConsumer<ResourceLocation, EntityType<?>> r) {
        r.accept(STARCLEAVER_GOLEM_KEY, STARCLEAVER_GOLEM);
        r.accept(SUBCAELIC_TORPEDO_KEY, SUBCAELIC_TORPEDO);
        r.accept(SUBCAELIC_DUX_KEY, SUBCAELIC_DUX);
        r.accept(SPLASH_STARBLEACH_KEY, SPLASH_STARBLEACH);
        r.accept(STARBLEACHED_PEARL_KEY, STARBLEACHED_PEARL);
        r.accept(FIRMAMENT_REJUVENATOR_KEY, FIRMAMENT_REJUVENATOR);
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

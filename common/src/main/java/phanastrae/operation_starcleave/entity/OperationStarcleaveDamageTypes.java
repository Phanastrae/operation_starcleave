package phanastrae.operation_starcleave.entity;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.entity.projectile.BismuthBlastEntity;
import phanastrae.operation_starcleave.entity.projectile.CometChargeEntity;
import phanastrae.operation_starcleave.entity.projectile.PhlogisticSparkEntity;

public class OperationStarcleaveDamageTypes {

    public static ResourceKey<DamageType> INTERNAL_STARBLEACHING = create(id("internal_starbleaching"));
    public static ResourceKey<DamageType> ON_PHLOGISTIC_FIRE = create(id("on_phlogistic_fire"));
    public static ResourceKey<DamageType> IN_PHLOGISTIC_FIRE = create(id("in_phlogistic_fire"));
    public static ResourceKey<DamageType> PHLOGISTIC_SPARK = create(id("phlogistic_spark"));
    public static ResourceKey<DamageType> UNATTRIBUTED_PHLOGISTIC_SPARK = create(id("unattributed_phlogistic_spark"));
    public static ResourceKey<DamageType> BISMUTH_BLAST = create(id("bismuth_blast"));
    public static ResourceKey<DamageType> FALLING_MOB = create(id("falling_mob"));
    public static ResourceKey<DamageType> PLASMA = create(id("plasma"));
    public static ResourceKey<DamageType> COMET_CHARGE = create(id("comet_charge"));

    public static DamageSource phlogisticSpark(Level level, PhlogisticSparkEntity phlogisticSpark, @Nullable Entity thrower) {
        return thrower == null ? source(level, UNATTRIBUTED_PHLOGISTIC_SPARK, phlogisticSpark) : source(level, PHLOGISTIC_SPARK, phlogisticSpark, thrower);
    }

    public static DamageSource bismuthBlast(Level level, BismuthBlastEntity bismuthBlast, @Nullable Entity thrower) {
        return source(level, BISMUTH_BLAST, bismuthBlast, thrower);
    }

    public static DamageSource fallingMob(Level level, @Nullable Entity entity) {
        return source(level, FALLING_MOB, entity);
    }

    public static DamageSource cometCharge(Level level, CometChargeEntity entity) {
        return source(level, COMET_CHARGE, entity.position());
    }

    public static DamageSource plasma(Level level) {
        return source(level, PLASMA);
    }

    public static DamageSource source(Level level, ResourceKey<DamageType> damageTypeKey) {
        return new DamageSource(getHolderOrThrow(level, damageTypeKey));
    }

    public static DamageSource source(Level level, ResourceKey<DamageType> damageTypeKey, Vec3 damageSourcePosition) {
        return new DamageSource(getHolderOrThrow(level, damageTypeKey), damageSourcePosition);
    }

    private static DamageSource source(Level level, ResourceKey<DamageType> damageTypeKey, @Nullable Entity entity) {
        return new DamageSource(getHolderOrThrow(level, damageTypeKey), entity);
    }

    private static DamageSource source(Level level, ResourceKey<DamageType> damageTypeKey, @Nullable Entity causingEntity, @Nullable Entity directEntity) {
        return new DamageSource(getHolderOrThrow(level, damageTypeKey), causingEntity, directEntity);
    }

    public static Holder<DamageType> getHolderOrThrow(Level level, ResourceKey<DamageType> damageTypeKey) {
        return level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(damageTypeKey);
    }

    public static ResourceLocation id(String key) {
        return OperationStarcleave.id(key);
    }

    private static ResourceKey<DamageType> create(ResourceLocation location) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, location);
    }
}

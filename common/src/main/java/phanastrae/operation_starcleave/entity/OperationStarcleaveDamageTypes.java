package phanastrae.operation_starcleave.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;
import phanastrae.operation_starcleave.OperationStarcleave;

public class OperationStarcleaveDamageTypes {

    public static ResourceKey<DamageType> INTERNAL_STARBLEACHING = ResourceKey.create(Registries.DAMAGE_TYPE, OperationStarcleave.id("internal_starbleaching"));
    public static ResourceKey<DamageType> ON_PHLOGISTIC_FIRE = ResourceKey.create(Registries.DAMAGE_TYPE, OperationStarcleave.id("on_phlogistic_fire"));
    public static ResourceKey<DamageType> IN_PHLOGISTIC_FIRE = ResourceKey.create(Registries.DAMAGE_TYPE, OperationStarcleave.id("in_phlogistic_fire"));

    public static DamageSource of(Level world, ResourceKey<DamageType> key) {
        return new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(key));
    }
}

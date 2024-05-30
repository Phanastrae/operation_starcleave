package phanastrae.operation_starcleave.entity;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;
import phanastrae.operation_starcleave.OperationStarcleave;

public class OperationStarcleaveDamageTypes {

    public static RegistryKey<DamageType> INTERNAL_STARBLEACHING = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, OperationStarcleave.id("internal_starbleaching"));
    public static RegistryKey<DamageType> ON_PHLOGISTIC_FIRE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, OperationStarcleave.id("on_phlogistic_fire"));
    public static RegistryKey<DamageType> IN_PHLOGISTIC_FIRE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, OperationStarcleave.id("in_phlogistic_fire"));

    public static DamageSource of(World world, RegistryKey<DamageType> key) {
        return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key));
    }
}

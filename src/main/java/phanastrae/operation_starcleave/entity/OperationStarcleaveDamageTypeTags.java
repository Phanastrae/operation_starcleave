package phanastrae.operation_starcleave.entity;

import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import phanastrae.operation_starcleave.OperationStarcleave;

public interface OperationStarcleaveDamageTypeTags {

    TagKey<DamageType> IS_PHLOGISTIC_FIRE = of("is_phlogistic_fire");

    private static TagKey<DamageType> of(String id) {
        return TagKey.of(RegistryKeys.DAMAGE_TYPE, OperationStarcleave.id(id));
    }
}

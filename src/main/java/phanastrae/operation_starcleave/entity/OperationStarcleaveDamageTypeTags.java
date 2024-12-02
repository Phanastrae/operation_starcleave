package phanastrae.operation_starcleave.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import phanastrae.operation_starcleave.OperationStarcleave;

public interface OperationStarcleaveDamageTypeTags {

    TagKey<DamageType> IS_PHLOGISTIC_FIRE = of("is_phlogistic_fire");

    private static TagKey<DamageType> of(String id) {
        return TagKey.create(Registries.DAMAGE_TYPE, OperationStarcleave.id(id));
    }
}

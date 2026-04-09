package phanastrae.operation_starcleave.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import phanastrae.operation_starcleave.OperationStarcleave;

public interface OperationStarcleaveDamageTypeTags {

    TagKey<DamageType> IS_PHLOGISTIC_FIRE = of("is_phlogistic_fire");

    // reduce damage cooldown from 10(+10) ticks to 5(+10) ticks
    TagKey<DamageType> REDUCED_COOLDOWN = of("reduced_cooldown");

    // reduce armor effectiveness (x0.5)
    TagKey<DamageType> PARTIALLY_BYPASSES_ARMOR = of("partially_bypasses_armor");

    private static TagKey<DamageType> of(String id) {
        return TagKey.create(Registries.DAMAGE_TYPE, OperationStarcleave.id(id));
    }
}

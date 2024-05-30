package phanastrae.operation_starcleave.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import phanastrae.operation_starcleave.OperationStarcleave;

public interface OperationStarcleaveEntityTypeTags {

    TagKey<EntityType<?>> PHLOGISTIC_FIRE_IMMUNE = of("phlogistic_fire_immune");

    private static TagKey<EntityType<?>> of(String id) {
        return TagKey.of(RegistryKeys.ENTITY_TYPE, OperationStarcleave.id(id));
    }
}

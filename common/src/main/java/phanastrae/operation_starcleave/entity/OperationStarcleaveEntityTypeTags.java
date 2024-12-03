package phanastrae.operation_starcleave.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import phanastrae.operation_starcleave.OperationStarcleave;

public interface OperationStarcleaveEntityTypeTags {

    TagKey<EntityType<?>> PHLOGISTIC_FIRE_IMMUNE = of("phlogistic_fire_immune");

    private static TagKey<EntityType<?>> of(String id) {
        return TagKey.create(Registries.ENTITY_TYPE, OperationStarcleave.id(id));
    }
}

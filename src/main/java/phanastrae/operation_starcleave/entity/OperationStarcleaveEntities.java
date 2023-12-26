package phanastrae.operation_starcleave.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import phanastrae.operation_starcleave.OperationStarcleave;

public class OperationStarcleaveEntities {

    public static final EntityType<StarcleaverGolemEntity> STARCLEAVER_GOLEM = Registry.register(
            Registries.ENTITY_TYPE,
            OperationStarcleave.id("starcleaver_golem"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, StarcleaverGolemEntity::new).dimensions(EntityDimensions.fixed(0.6f, 0.6f)).fireImmune().build()
    );

    public static void init() {
        FabricDefaultAttributeRegistry.register(STARCLEAVER_GOLEM, StarcleaverGolemEntity.createStarcleaverGolemAttributes());
    }
}

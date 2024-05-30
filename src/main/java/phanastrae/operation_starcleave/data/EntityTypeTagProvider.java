package phanastrae.operation_starcleave.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.EntityTypeTags;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypeTags;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;

import java.util.concurrent.CompletableFuture;

public class EntityTypeTagProvider extends FabricTagProvider.EntityTypeTagProvider {
    public EntityTypeTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getOrCreateTagBuilder(EntityTypeTags.FALL_DAMAGE_IMMUNE)
                .add(OperationStarcleaveEntityTypes.STARCLEAVER_GOLEM)
                .add(OperationStarcleaveEntityTypes.SUBCAELIC_TORPEDO)
                .add(OperationStarcleaveEntityTypes.SUBCAELIC_DUX);

        getOrCreateTagBuilder(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES)
                .add(OperationStarcleaveEntityTypes.STARCLEAVER_GOLEM);

        getOrCreateTagBuilder(EntityTypeTags.FROG_FOOD)
                .add(OperationStarcleaveEntityTypes.SUBCAELIC_TORPEDO);

        getOrCreateTagBuilder(OperationStarcleaveEntityTypeTags.PHLOGISTIC_FIRE_IMMUNE)
                .add(OperationStarcleaveEntityTypes.STARCLEAVER_GOLEM)
                .add(OperationStarcleaveEntityTypes.SUBCAELIC_TORPEDO)
                .add(OperationStarcleaveEntityTypes.SUBCAELIC_DUX);

    }
}

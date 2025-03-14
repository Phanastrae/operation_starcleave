package phanastrae.operation_starcleave.fabric.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.tags.EntityTypeTags.*;
import static phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypeTags.PHLOGISTIC_FIRE_IMMUNE;

public class EntityTypeTagProvider extends FabricTagProvider.EntityTypeTagProvider {
    public EntityTypeTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        // vanilla tags
        getOrCreateTagBuilder(IMPACT_PROJECTILES)
                .add(OperationStarcleaveEntityTypes.PHLOGISTIC_SPARK);

        getOrCreateTagBuilder(FREEZE_IMMUNE_ENTITY_TYPES)
                .add(OperationStarcleaveEntityTypes.STARCLEAVER_GOLEM);

        getOrCreateTagBuilder(FROG_FOOD)
                .add(OperationStarcleaveEntityTypes.SUBCAELIC_TORPEDO);

        getOrCreateTagBuilder(FALL_DAMAGE_IMMUNE)
                .add(OperationStarcleaveEntityTypes.STARCLEAVER_GOLEM)
                .add(OperationStarcleaveEntityTypes.SUBCAELIC_TORPEDO)
                .add(OperationStarcleaveEntityTypes.SUBCAELIC_DUX);

        getOrCreateTagBuilder(AQUATIC)
                .add(OperationStarcleaveEntityTypes.SUBCAELIC_DUX)
                .add(OperationStarcleaveEntityTypes.SUBCAELIC_TORPEDO);

        getOrCreateTagBuilder(REDIRECTABLE_PROJECTILE)
                .add(OperationStarcleaveEntityTypes.PHLOGISTIC_SPARK);

        // starcleave tags
        getOrCreateTagBuilder(PHLOGISTIC_FIRE_IMMUNE)
                .add(OperationStarcleaveEntityTypes.STARCLEAVER_GOLEM)
                .add(OperationStarcleaveEntityTypes.SUBCAELIC_TORPEDO)
                .add(OperationStarcleaveEntityTypes.SUBCAELIC_DUX);
    }
}

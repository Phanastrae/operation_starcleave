package phanastrae.operation_starcleave.fabric.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.tags.EntityTypeTags.*;
import static phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypeTags.PHLOGISTIC_FIRE_IMMUNE;
import static phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes.*;

public class EntityTypeTagProvider extends FabricTagProvider.EntityTypeTagProvider {
    public EntityTypeTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        // vanilla tags
        getOrCreateTagBuilder(IMPACT_PROJECTILES)
                .add(PHLOGISTIC_SPARK);

        getOrCreateTagBuilder(FREEZE_IMMUNE_ENTITY_TYPES)
                .add(
                        STARCLEAVER_GOLEM,
                        SINEATER
                );

        getOrCreateTagBuilder(FROG_FOOD)
                .add(SUBCAELIC_TORPEDO);

        getOrCreateTagBuilder(FALL_DAMAGE_IMMUNE)
                .add(
                        STARCLEAVER_GOLEM,
                        SUBCAELIC_TORPEDO,
                        SUBCAELIC_DUX
                );

        getOrCreateTagBuilder(AQUATIC)
                .add(
                        SUBCAELIC_DUX,
                        SUBCAELIC_TORPEDO
                );

        getOrCreateTagBuilder(ARTHROPOD)
                .add(
                        SINEATER
                );

        getOrCreateTagBuilder(REDIRECTABLE_PROJECTILE)
                .add(PHLOGISTIC_SPARK);

        // starcleave tags
        getOrCreateTagBuilder(PHLOGISTIC_FIRE_IMMUNE)
                .add(
                        STARCLEAVER_GOLEM,
                        SUBCAELIC_TORPEDO,
                        SUBCAELIC_DUX
                );
    }
}

package phanastrae.operation_starcleave.fabric.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageType;
import phanastrae.operation_starcleave.entity.OperationStarcleaveDamageTypeTags;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.tags.DamageTypeTags.*;
import static phanastrae.operation_starcleave.entity.OperationStarcleaveDamageTypes.*;

public class DamageTypeTagProvider extends FabricTagProvider<DamageType> {

    public DamageTypeTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, Registries.DAMAGE_TYPE, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        // vanilla tags
        getOrCreateTagBuilder(DAMAGES_HELMET)
                .addOptional(FALLING_MOB);

        getOrCreateTagBuilder(BYPASSES_ARMOR)
                .addOptional(ON_PHLOGISTIC_FIRE)
                .addOptional(INTERNAL_STARBLEACHING);

        getOrCreateTagBuilder(BYPASSES_EFFECTS)
                .addOptional(INTERNAL_STARBLEACHING);

        getOrCreateTagBuilder(BYPASSES_SHIELD)
                .addOptional(FALLING_MOB);

        getOrCreateTagBuilder(IS_PROJECTILE)
                .addOptional(UNATTRIBUTED_PHLOGISTIC_SPARK)
                .addOptional(PHLOGISTIC_SPARK)
                .addOptional(BISMUTH_BLAST)
                .addOptional(COMET_CHARGE);

        getOrCreateTagBuilder(IGNITES_ARMOR_STANDS)
                .addOptional(IN_PHLOGISTIC_FIRE);

        getOrCreateTagBuilder(BURNS_ARMOR_STANDS)
                .addOptional(ON_PHLOGISTIC_FIRE);

        getOrCreateTagBuilder(NO_KNOCKBACK)
                .addOptional(INTERNAL_STARBLEACHING)
                .addOptional(IN_PHLOGISTIC_FIRE)
                .addOptional(ON_PHLOGISTIC_FIRE)
                .addOptional(BISMUTH_BLAST)
                .addOptional(PLASMA);

        getOrCreateTagBuilder(ALWAYS_KILLS_ARMOR_STANDS)
                .addOptional(PHLOGISTIC_SPARK)
                .addOptional(BISMUTH_BLAST)
                .addOptional(COMET_CHARGE);

        getOrCreateTagBuilder(PANIC_CAUSES)
                .addOptional(UNATTRIBUTED_PHLOGISTIC_SPARK)
                .addOptional(PHLOGISTIC_SPARK)
                .addOptional(BISMUTH_BLAST)
                .addOptional(COMET_CHARGE);

        getOrCreateTagBuilder(PANIC_ENVIRONMENTAL_CAUSES)
                .addOptional(PLASMA);

        // starcleave tags
        getOrCreateTagBuilder(OperationStarcleaveDamageTypeTags.IS_PHLOGISTIC_FIRE)
                .addOptional(IN_PHLOGISTIC_FIRE)
                .addOptional(ON_PHLOGISTIC_FIRE)
                .addOptional(UNATTRIBUTED_PHLOGISTIC_SPARK)
                .addOptional(PHLOGISTIC_SPARK)
                .addOptional(PLASMA);

        getOrCreateTagBuilder(OperationStarcleaveDamageTypeTags.REDUCED_COOLDOWN)
                .addOptional(BISMUTH_BLAST);

        getOrCreateTagBuilder(OperationStarcleaveDamageTypeTags.PARTIALLY_BYPASSES_ARMOR)
                .addOptional(BISMUTH_BLAST);
    }
}

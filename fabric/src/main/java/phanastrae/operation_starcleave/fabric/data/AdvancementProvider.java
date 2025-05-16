package phanastrae.operation_starcleave.fabric.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.advancements.critereon.SummonedEntityTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static phanastrae.operation_starcleave.advancement.criterion.OperationStarcleaveAdvancementCriteria.*;
import static phanastrae.operation_starcleave.item.OperationStarcleaveItems.*;

public class AdvancementProvider extends FabricAdvancementProvider {
    protected AdvancementProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(HolderLookup.Provider registryLookup, Consumer<AdvancementHolder> consumer) {
        // vanilla advancements
        AdvancementHolder obtain_debris = new AdvancementHolder(ResourceLocation.parse("nether/obtain_ancient_debris"), null);

        // nether tab
        AdvancementHolder summonStarcleaverGolem = save(consumer, "operation_starcleave/summon_starcleaver_golem", builder()
                .parent(obtain_debris)
                .display(
                        NETHERITE_PUMPKIN,
                        Component.translatable("advancements.operation_starcleave.summon_starcleaver_golem.title"),
                        Component.translatable("advancements.operation_starcleave.summon_starcleaver_golem.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("summoned_golem", summonedEntity(
                        entity().of(OperationStarcleaveEntityTypes.STARCLEAVER_GOLEM))
                )
        );

        // starcleave tab
        AdvancementHolder root = save(consumer, "operation_starcleave/root", builder()
                .display(
                        NETHERITE_PUMPKIN,
                        Component.translatable("advancements.operation_starcleave.root.title"),
                        Component.translatable("advancements.operation_starcleave.root.description"),
                        OperationStarcleave.id("textures/gui/advancements/backgrounds/operation_starcleave.png"),
                        AdvancementType.TASK,
                        false,
                        false,
                        false
                )
                .addCriterion("summoned_golem", summonedEntity(
                        entity().of(OperationStarcleaveEntityTypes.STARCLEAVER_GOLEM))
                )
        );

        AdvancementHolder launchStarcleaverGolem = save(consumer, "operation_starcleave/launch_starcleaver_golem", builder()
                .parent(root)
                .display(
                        Items.FLINT_AND_STEEL,
                        Component.translatable("advancements.operation_starcleave.launch_starcleaver_golem.title"),
                        Component.translatable("advancements.operation_starcleave.launch_starcleaver_golem.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("launched_golem", LAUNCH_STARCLEAVER_GOLEM.createCriterion(
                        new PlayerTrigger.TriggerInstance(Optional.empty()))
                )
        );

        AdvancementHolder cleave_firmament = save(consumer, "operation_starcleave/cleave_firmament", builder()
                .parent(launchStarcleaverGolem)
                .display(
                        FIRMAMENT_MANIPULATOR,
                        Component.translatable("advancements.operation_starcleave.cleave_firmament.title"),
                        Component.translatable("advancements.operation_starcleave.cleave_firmament.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        true
                )
                .addCriterion("cleaved_firmament", CLEAVE_FIRMAMENT.createCriterion(
                        new PlayerTrigger.TriggerInstance(Optional.empty()))
                )
        );

        AdvancementHolder obtain_starbleach = save(consumer, "operation_starcleave/obtain_starbleach", builder()
                .parent(cleave_firmament)
                .display(
                        STARBLEACH_BOTTLE,
                        Component.translatable("advancements.operation_starcleave.obtain_starbleach.title"),
                        Component.translatable("advancements.operation_starcleave.obtain_starbleach.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("obtain_item", hasItems(STARBLEACH_BOTTLE))
        );

        AdvancementHolder obtain_blessed_bed = save(consumer, "operation_starcleave/obtain_blessed_bed", builder()
                .parent(cleave_firmament)
                .display(
                        BLESSED_BED,
                        Component.translatable("advancements.operation_starcleave.obtain_blessed_bed.title"),
                        Component.translatable("advancements.operation_starcleave.obtain_blessed_bed.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("obtain_item", hasItems(BLESSED_BED))
        );

        AdvancementHolder obtain_bismuth = save(consumer, "operation_starcleave/obtain_bismuth", builder()
                .parent(cleave_firmament)
                .display(
                        STARFLAKED_BISMUTH,
                        Component.translatable("advancements.operation_starcleave.obtain_bismuth.title"),
                        Component.translatable("advancements.operation_starcleave.obtain_bismuth.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("obtain_bismuth", hasItems(STARFLAKED_BISMUTH))
                .addCriterion("obtain_bismuth_flake", hasItems(BISMUTH_FLAKE))
                .requirements(AdvancementRequirements.Strategy.OR)
        );

        AdvancementHolder fly_pegasus = save(consumer, "operation_starcleave/fly_pegasus", builder()
                .parent(obtain_bismuth)
                .display(
                        BISMUTH_PEGASUS_ARMOR,
                        Component.translatable("advancements.operation_starcleave.fly_pegasus.title"),
                        Component.translatable("advancements.operation_starcleave.fly_pegasus.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("fly_pegasus", FLY_PEGASUS.createCriterion(
                        new PlayerTrigger.TriggerInstance(Optional.empty()))
                )
        );

        AdvancementHolder kill_dux = save(consumer, "operation_starcleave/kill_dux", builder()
                .parent(cleave_firmament)
                .display(
                        Items.TRIDENT,
                        Component.translatable("advancements.operation_starcleave.kill_dux.title"),
                        Component.translatable("advancements.operation_starcleave.kill_dux.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("kill_dux", KILL_DUX.createCriterion(
                        new PlayerTrigger.TriggerInstance(Optional.empty()))
                )
        );

        AdvancementHolder obtain_phlogiston_sac = save(consumer, "operation_starcleave/obtain_phlogiston_sac", builder()
                .parent(kill_dux)
                .display(
                        PHLOGISTON_SAC,
                        Component.translatable("advancements.operation_starcleave.obtain_phlogiston_sac.title"),
                        Component.translatable("advancements.operation_starcleave.obtain_phlogiston_sac.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("obtain_item", hasItems(PHLOGISTON_SAC))
        );
    }

    private static AdvancementHolder save(Consumer<AdvancementHolder> consumer, String id, Advancement.Builder builder) {
        return builder.save(consumer, id(id).toString());
    }

    private static Advancement.Builder builder() {
        return Advancement.Builder.recipeAdvancement();
    }

    private static ResourceLocation id(String path) {
        return OperationStarcleave.id(path);
    }

    private static Criterion<InventoryChangeTrigger.TriggerInstance> hasItems(ItemLike... items) {
        return InventoryChangeTrigger.TriggerInstance.hasItems(items);
    }

    private static Criterion<SummonedEntityTrigger.TriggerInstance> summonedEntity(EntityPredicate.Builder entity) {
        return SummonedEntityTrigger.TriggerInstance.summonedEntity(entity);
    }

    private static EntityPredicate.Builder entity() {
        return EntityPredicate.Builder.entity();
    }
}

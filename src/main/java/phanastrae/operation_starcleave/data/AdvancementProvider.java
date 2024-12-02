package phanastrae.operation_starcleave.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.advancements.critereon.SummonedEntityTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.advancement.criterion.OperationStarcleaveAdvancementCriteria;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class AdvancementProvider extends FabricAdvancementProvider {
    protected AdvancementProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(HolderLookup.Provider registryLookup, Consumer<AdvancementHolder> consumer) {
        AdvancementHolder summonStarcleaverGolem = Advancement.Builder.recipeAdvancement()
                .display(
                        OperationStarcleaveItems.NETHERITE_PUMPKIN,
                        Component.translatable("advancements.operation_starcleave.summon_starcleaver_golem.title"),
                        Component.translatable("advancements.operation_starcleave.summon_starcleaver_golem.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .parent(new AdvancementHolder(ResourceLocation.parse("nether/obtain_ancient_debris"), null))
                .addCriterion("summoned_golem", SummonedEntityTrigger.TriggerInstance
                        .summonedEntity(EntityPredicate.Builder
                                .entity().of(OperationStarcleaveEntityTypes.STARCLEAVER_GOLEM)))
                .save(consumer, OperationStarcleave.id("operation_starcleave/summon_starcleaver_golem").toString());

        AdvancementHolder root = Advancement.Builder.recipeAdvancement()
                .display(
                        OperationStarcleaveItems.NETHERITE_PUMPKIN,
                        Component.translatable("advancements.operation_starcleave.root.title"),
                        Component.translatable("advancements.operation_starcleave.root.description"),
                        OperationStarcleave.id("textures/gui/advancements/backgrounds/operation_starcleave.png"),
                        AdvancementType.TASK,
                        false,
                        false,
                        false
                )
                .addCriterion("summoned_golem", SummonedEntityTrigger.TriggerInstance
                        .summonedEntity(EntityPredicate.Builder
                                .entity().of(OperationStarcleaveEntityTypes.STARCLEAVER_GOLEM)))
                .save(consumer, OperationStarcleave.id("operation_starcleave/root").toString());

        AdvancementHolder launchStarcleaverGolem = Advancement.Builder.recipeAdvancement()
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
                .parent(root)
                .addCriterion("launched_golem", OperationStarcleaveAdvancementCriteria.LAUNCH_STARCLEAVER_GOLEM.createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty())))
                .save(consumer, OperationStarcleave.id("operation_starcleave/launch_starcleaver_golem").toString());

        AdvancementHolder cleave_firmament = Advancement.Builder.recipeAdvancement()
                .display(
                        OperationStarcleaveItems.FIRMAMENT_MANIPULATOR,
                        Component.translatable("advancements.operation_starcleave.cleave_firmament.title"),
                        Component.translatable("advancements.operation_starcleave.cleave_firmament.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        true
                )
                .parent(launchStarcleaverGolem)
                .addCriterion("cleaved_firmament", OperationStarcleaveAdvancementCriteria.CLEAVE_FIRMAMENT.createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty())))
                .save(consumer, OperationStarcleave.id("operation_starcleave/cleave_firmament").toString());

        AdvancementHolder obtain_starbleach = Advancement.Builder.recipeAdvancement()
                .display(
                        OperationStarcleaveItems.STARBLEACH_BOTTLE,
                        Component.translatable("advancements.operation_starcleave.obtain_starbleach.title"),
                        Component.translatable("advancements.operation_starcleave.obtain_starbleach.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .parent(cleave_firmament)
                .addCriterion("obtain_item", InventoryChangeTrigger.TriggerInstance.hasItems(OperationStarcleaveItems.STARBLEACH_BOTTLE))
                .save(consumer, OperationStarcleave.id("operation_starcleave/obtain_starbleach").toString());

        AdvancementHolder obtain_blessed_bed = Advancement.Builder.recipeAdvancement()
                .display(
                        OperationStarcleaveItems.BLESSED_BED,
                        Component.translatable("advancements.operation_starcleave.obtain_blessed_bed.title"),
                        Component.translatable("advancements.operation_starcleave.obtain_blessed_bed.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .parent(cleave_firmament)
                .addCriterion("obtain_item", InventoryChangeTrigger.TriggerInstance.hasItems(OperationStarcleaveItems.BLESSED_BED))
                .save(consumer, OperationStarcleave.id("operation_starcleave/obtain_blessed_bed").toString());
    }
}

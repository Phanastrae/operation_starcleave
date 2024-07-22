package phanastrae.operation_starcleave.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.ItemCriterion;
import net.minecraft.advancement.criterion.SummonedEntityCriterion;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.item.Items;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.advancement.criterion.OperationStarcleaveAdvancementCriteria;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class AdvancementProvider extends FabricAdvancementProvider {
    protected AdvancementProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(RegistryWrapper.WrapperLookup registryLookup, Consumer<AdvancementEntry> consumer) {
        AdvancementEntry summonStarcleaverGolem = Advancement.Builder.createUntelemetered()
                .display(
                        OperationStarcleaveItems.NETHERITE_PUMPKIN,
                        Text.translatable("advancements.operation_starcleave.summon_starcleaver_golem.title"),
                        Text.translatable("advancements.operation_starcleave.summon_starcleaver_golem.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .parent(new AdvancementEntry(Identifier.of("nether/obtain_ancient_debris"), null))
                .criterion("summoned_golem", SummonedEntityCriterion.Conditions
                        .create(EntityPredicate.Builder
                                .create().type(OperationStarcleaveEntityTypes.STARCLEAVER_GOLEM)))
                .build(consumer, OperationStarcleave.id("operation_starcleave/summon_starcleaver_golem").toString());

        AdvancementEntry root = Advancement.Builder.createUntelemetered()
                .display(
                        OperationStarcleaveItems.NETHERITE_PUMPKIN,
                        Text.translatable("advancements.operation_starcleave.root.title"),
                        Text.translatable("advancements.operation_starcleave.root.description"),
                        OperationStarcleave.id("textures/gui/advancements/backgrounds/operation_starcleave.png"),
                        AdvancementFrame.TASK,
                        false,
                        false,
                        false
                )
                .criterion("summoned_golem", SummonedEntityCriterion.Conditions
                        .create(EntityPredicate.Builder
                                .create().type(OperationStarcleaveEntityTypes.STARCLEAVER_GOLEM)))
                .build(consumer, OperationStarcleave.id("operation_starcleave/root").toString());

        AdvancementEntry launchStarcleaverGolem = Advancement.Builder.createUntelemetered()
                .display(
                        Items.FLINT_AND_STEEL,
                        Text.translatable("advancements.operation_starcleave.launch_starcleaver_golem.title"),
                        Text.translatable("advancements.operation_starcleave.launch_starcleaver_golem.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .parent(root)
                .criterion("launched_golem", OperationStarcleaveAdvancementCriteria.LAUNCH_STARCLEAVER_GOLEM.create(new TickCriterion.Conditions(Optional.empty())))
                .build(consumer, OperationStarcleave.id("operation_starcleave/launch_starcleaver_golem").toString());

        AdvancementEntry cleave_firmament = Advancement.Builder.createUntelemetered()
                .display(
                        OperationStarcleaveItems.FIRMAMENT_MANIPULATOR,
                        Text.translatable("advancements.operation_starcleave.cleave_firmament.title"),
                        Text.translatable("advancements.operation_starcleave.cleave_firmament.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        true
                )
                .parent(launchStarcleaverGolem)
                .criterion("cleaved_firmament", OperationStarcleaveAdvancementCriteria.CLEAVE_FIRMAMENT.create(new TickCriterion.Conditions(Optional.empty())))
                .build(consumer, OperationStarcleave.id("operation_starcleave/cleave_firmament").toString());

        AdvancementEntry obtain_starbleach = Advancement.Builder.createUntelemetered()
                .display(
                        OperationStarcleaveItems.STARBLEACH_BOTTLE,
                        Text.translatable("advancements.operation_starcleave.obtain_starbleach.title"),
                        Text.translatable("advancements.operation_starcleave.obtain_starbleach.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .parent(cleave_firmament)
                .criterion("obtain_item", InventoryChangedCriterion.Conditions.items(OperationStarcleaveItems.STARBLEACH_BOTTLE))
                .build(consumer, OperationStarcleave.id("operation_starcleave/obtain_starbleach").toString());

        AdvancementEntry obtain_blessed_bed = Advancement.Builder.createUntelemetered()
                .display(
                        OperationStarcleaveItems.BLESSED_BED,
                        Text.translatable("advancements.operation_starcleave.obtain_blessed_bed.title"),
                        Text.translatable("advancements.operation_starcleave.obtain_blessed_bed.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .parent(cleave_firmament)
                .criterion("obtain_item", InventoryChangedCriterion.Conditions.items(OperationStarcleaveItems.BLESSED_BED))
                .build(consumer, OperationStarcleave.id("operation_starcleave/obtain_blessed_bed").toString());
    }
}

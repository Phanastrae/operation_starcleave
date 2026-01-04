package phanastrae.operation_starcleave.fabric.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.DamageSourceCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import phanastrae.operation_starcleave.world.OperationStarcleaveLootTables;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import static net.minecraft.world.item.Items.GOLD_NUGGET;
import static phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes.*;
import static phanastrae.operation_starcleave.item.OperationStarcleaveItems.*;

public class EntityLootTableProvider extends SimpleFabricLootTableProvider {

    private final CompletableFuture<HolderLookup.Provider> registryLookup;

    public EntityLootTableProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup, LootContextParamSets.ENTITY);
        this.registryLookup = registryLookup;
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> lootTableBiConsumer) {
        // generate() should be getting run inside of run() which should have got the registryLookup by the time this gets run
        HolderLookup.Provider registries = this.registryLookup.getNow(null);
        Objects.requireNonNull(registries);

        lootTableBiConsumer.accept(STARCLEAVER_GOLEM.getDefaultLootTable(), LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(GOLD_NUGGET)))
        );

        lootTableBiConsumer.accept(SUBCAELIC_TORPEDO.getDefaultLootTable(), LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(HOLLOWED_SAC)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0.0F, 1.0F)))
                                .when(this.killedByFrog().invert())
                        )
                        .add(LootItem.lootTableItem(SUBCAELIC_PHLOGLIGHT)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                                .when(this.killedByFrog())
                        )
                )
        );

        lootTableBiConsumer.accept(
                SINEATER.getDefaultLootTable(),
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(
                                                LootItem.lootTableItem(MUCKY_SINGUTS)
                                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 3.0F)))
                                                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0.0F, 1.0F)))
                                        )
                        )
        );
        lootTableBiConsumer.accept(OperationStarcleaveLootTables.SINEATER_GOLDEN, createSineaterTable(BLESSED_CLOTH, 1, 2, registries));
        lootTableBiConsumer.accept(OperationStarcleaveLootTables.SINEATER_SPECTRAL, createSineaterTable(BLESSED_CLOTH, 1, 4, registries));
        lootTableBiConsumer.accept(OperationStarcleaveLootTables.SINEATER_PHANTASMAL, createSineaterTable(Items.PHANTOM_MEMBRANE, 1, 1, registries));
    }

    protected LootItemCondition.Builder killedByFrog() {
        return DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType().source(EntityPredicate.Builder.entity().of(EntityType.FROG)));
    }

    protected static LootTable.Builder createSineaterTable(ItemLike clothItem, int minRoll, int maxRoll, HolderLookup.Provider registries) {
        return LootTable.lootTable()
                .withPool(
                        LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1.0F))
                                .add(
                                        LootItem.lootTableItem(clothItem)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(minRoll, maxRoll)))
                                                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0.0F, 1.0F)))
                                )
                )
                .withPool(
                        LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1.0F))
                                .add(NestedLootTable.lootTableReference(SINEATER.getDefaultLootTable()))
                );
    }
}

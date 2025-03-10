package phanastrae.operation_starcleave.fabric.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.*;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import phanastrae.operation_starcleave.block.BisreedBlock;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static phanastrae.operation_starcleave.block.OperationStarcleaveBlocks.BISREEDS;

public class BlockLootTableProvider extends FabricBlockLootTableProvider {
    protected BlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        HolderLookup.RegistryLookup<Enchantment> impl = this.registries.lookupOrThrow(Registries.ENCHANTMENT);

        dropSelf(OperationStarcleaveBlocks.NETHERITE_PUMPKIN);
        dropSelf(OperationStarcleaveBlocks.STELLAR_SEDIMENT);
        dropSelf(OperationStarcleaveBlocks.STARBLEACHED_LOG);
        dropSelf(OperationStarcleaveBlocks.STARBLEACHED_WOOD);
        dropSelf(OperationStarcleaveBlocks.STARBLEACHED_LEAVES);
        dropSelf(OperationStarcleaveBlocks.STARBLEACHED_TILES);
        dropSelf(OperationStarcleaveBlocks.STARBLEACHED_TILE_STAIRS);
        dropSelf(OperationStarcleaveBlocks.STARBLEACHED_TILE_SLAB);
        dropSelf(OperationStarcleaveBlocks.STARBLEACHED_TILE_WALL);
        dropSelf(OperationStarcleaveBlocks.CHISELED_STARBLEACHED_TILES);
        dropSelf(OperationStarcleaveBlocks.IMBUED_STARBLEACHED_TILES);
        dropSelf(OperationStarcleaveBlocks.STARBLEACHED_PEARL_BLOCK);
        dropSelf(OperationStarcleaveBlocks.STELLAR_TILES);
        dropSelf(OperationStarcleaveBlocks.STELLAR_TILE_SLAB);
        dropSelf(OperationStarcleaveBlocks.STELLAR_REPULSOR);
        dropSelf(OperationStarcleaveBlocks.BLESSED_CLOTH_BLOCK);
        dropSelf(OperationStarcleaveBlocks.BLESSED_CLOTH_CARPET);
        dropSelf(OperationStarcleaveBlocks.BLESSED_CLOTH_CURTAIN);

        add(OperationStarcleaveBlocks.PHLOGISTIC_FIRE, noDrop());

        dropOther(OperationStarcleaveBlocks.STARBLEACH_CAULDRON, Items.CAULDRON);

        addRandomDrop(OperationStarcleaveBlocks.HOLY_MOSS, silkTouchDrop(OperationStarcleaveItems.HOLY_MOSS, OperationStarcleaveItems.STELLAR_SEDIMENT));
        addRandomDrop(OperationStarcleaveBlocks.STELLAR_MULCH, silkTouchDrop(OperationStarcleaveItems.STELLAR_MULCH, OperationStarcleaveItems.STELLAR_SEDIMENT));
        addRandomDrop(OperationStarcleaveBlocks.STELLAR_FARMLAND, silkTouchDrop(OperationStarcleaveItems.STELLAR_MULCH, OperationStarcleaveItems.STELLAR_SEDIMENT));

        addRandomDrop(OperationStarcleaveBlocks.STARDUST_BLOCK,
                silkTouchDrop(
                        item(OperationStarcleaveItems.STARDUST_BLOCK),
                        item(OperationStarcleaveItems.STARDUST_CLUSTER, 1, 4, false).apply(ApplyExplosionDecay.explosionDecay())));

        addRandomDrop(OperationStarcleaveBlocks.SHORT_HOLY_MOSS,
                conditionalDrop(
                        item(OperationStarcleaveItems.SHORT_HOLY_MOSS),
                        item(OperationStarcleaveItems.HOLY_STRANDS).apply(ApplyBonusCount.addUniformBonusCount(impl.getOrThrow(Enchantments.FORTUNE), 4)).apply(ApplyExplosionDecay.explosionDecay()).when(LootItemRandomChanceCondition.randomChance(0.3F)),
                        HAS_SHEARS
                )
        );

        addRandomDrop(OperationStarcleaveBlocks.MULCHBORNE_TUFT,
                conditionalDrop(
                        item(OperationStarcleaveItems.MULCHBORNE_TUFT),
                        HAS_SHEARS
                )
        );

        add(OperationStarcleaveBlocks.BLESSED_BED, LootTable.lootTable().withPool(
                LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(item(OperationStarcleaveItems.BLESSED_BED)
                                .when(LootItemBlockStatePropertyCondition
                                        .hasBlockStateProperties(OperationStarcleaveBlocks.BLESSED_BED)
                                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(BedBlock.PART, BedPart.HEAD)))
                        )
                        .when(ExplosionCondition.survivesExplosion())
                )
        );

        LootItemCondition.Builder fullyGrownBisreed = LootItemBlockStatePropertyCondition.hasBlockStateProperties(BISREEDS)
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(BisreedBlock.AGE, 3));
        this.add(BISREEDS, this.applyExplosionDecay(
                BISREEDS,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .when(fullyGrownBisreed.invert().and(LootItemRandomChanceCondition.randomChance(0.3F))) // when not fully grown, low chance to recover root
                                        .add(LootItem.lootTableItem(OperationStarcleaveItems.BISREED_ROOT))
                        )
                        .withPool(
                                LootPool.lootPool()
                                        .when(fullyGrownBisreed.and(LootItemRandomChanceCondition.randomChance(0.8F))) // when fully grown, decent chance to recover root
                                        .add(
                                                LootItem.lootTableItem(OperationStarcleaveItems.BISREED_ROOT) // small chance for bonus root
                                                        .apply(ApplyBonusCount.addBonusBinomialDistributionCount(impl.getOrThrow(Enchantments.FORTUNE), 0.1F, 1))
                                        )
                        )
                        .withPool(
                                LootPool.lootPool()
                                        .when(fullyGrownBisreed) // when fully grown, drop flakes
                                        .add(
                                                LootItem.lootTableItem(OperationStarcleaveItems.BISMUTH_FLAKE)
                                                        .apply(ApplyBonusCount.addBonusBinomialDistributionCount(impl.getOrThrow(Enchantments.FORTUNE), 0.25F, 5))
                                        )
                        )
                )
        );
    }

    public void addRandomDrop(Block block, LootTable.Builder builder) {
        add(block, builder.setRandomSequence(block.getLootTable().location()));
    }

    public static LootPoolSingletonContainer.Builder<?> item(ItemLike itemConvertible) {
        return LootItem.lootTableItem(itemConvertible);
    }

    public static LootPoolSingletonContainer.Builder<?> item(ItemLike itemConvertible, float min, float max, boolean add) {
        return item(itemConvertible).apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max), add));
    }

    public static LootTable.Builder conditionalDrop(LootPoolEntryContainer.Builder<?> withCondition, LootItemCondition.Builder condition) {
        LootPoolEntryContainer.Builder<?> withConditionEntry = withCondition.when(condition);

        return LootTable.lootTable().withPool(
                LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(withConditionEntry)
        );
    }

    public static LootTable.Builder conditionalDrop(LootPoolEntryContainer.Builder<?> withCondition, LootPoolEntryContainer.Builder<?> noCondition, LootItemCondition.Builder condition) {
        LootPoolEntryContainer.Builder<?> noConditionEntry = noCondition.when(ExplosionCondition.survivesExplosion());
        LootPoolEntryContainer.Builder<?> withConditionEntry = withCondition.when(condition);

        return LootTable.lootTable().withPool(
                LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(withConditionEntry.otherwise(noConditionEntry))
        );
    }

    public LootTable.Builder silkTouchDrop(LootPoolEntryContainer.Builder<?> withSilkTouch, LootPoolEntryContainer.Builder<?> noSilkTouch) {
        return conditionalDrop(withSilkTouch, noSilkTouch, hasSilkTouch());
    }

    public LootTable.Builder silkTouchDrop(ItemLike withSilkTouch, ItemLike noSilkTouch) {
        return silkTouchDrop(item(withSilkTouch), item(noSilkTouch));
    }

    public LootItemCondition.Builder hasSilkTouch() {
        HolderLookup.RegistryLookup<Enchantment> impl = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return MatchTool.toolMatches(
                ItemPredicate.Builder.item()
                        .withSubPredicate(
                                ItemSubPredicates.ENCHANTMENTS,
                                ItemEnchantmentsPredicate.enchantments(List.of(new EnchantmentPredicate(impl.getOrThrow(Enchantments.SILK_TOUCH), MinMaxBounds.Ints.atLeast(1))))
                        )
        );
    }
}

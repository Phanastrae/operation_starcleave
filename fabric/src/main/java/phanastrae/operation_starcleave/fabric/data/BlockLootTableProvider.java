package phanastrae.operation_starcleave.fabric.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.*;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import phanastrae.operation_starcleave.block.BisreedBlock;
import phanastrae.operation_starcleave.block.StarbleachedLeafLitterBlock;
import phanastrae.operation_starcleave.data.OperationStarcleaveBlockFamilies;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static phanastrae.operation_starcleave.block.OperationStarcleaveBlocks.*;

public class BlockLootTableProvider extends FabricBlockLootTableProvider {
    protected BlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        addLootForFamilies(
                OperationStarcleaveBlockFamilies.STARDUST_BRICKS,

                OperationStarcleaveBlockFamilies.FELLCRUST,
                OperationStarcleaveBlockFamilies.CUT_FELLCRUST,
                OperationStarcleaveBlockFamilies.SMOOTH_FELLCRUST,
                OperationStarcleaveBlockFamilies.SMOOTH_FELLCRUST_BRICKS,
                OperationStarcleaveBlockFamilies.COBBLED_FELLCRUST,
                OperationStarcleaveBlockFamilies.POLISHED_FELLCRUST,
                OperationStarcleaveBlockFamilies.POLISHED_FELLCRUST_BRICKS,
                OperationStarcleaveBlockFamilies.CUT_POLISHED_FELLCRUST,

                OperationStarcleaveBlockFamilies.STARBLEACHED_TILES,
                OperationStarcleaveBlockFamilies.STELLAR_TILES,

                OperationStarcleaveBlockFamilies.BLESSED_CLOTH,
                OperationStarcleaveBlockFamilies.BLESSED_CLOTH_PADDING,

                OperationStarcleaveBlockFamilies.STARFLAKED_BISMUTH_BLOCK,
                OperationStarcleaveBlockFamilies.STARFLAKED_BISMUTH_BRICKS,
                OperationStarcleaveBlockFamilies.STARFLAKED_BISMUTH_TILES,
                OperationStarcleaveBlockFamilies.STARFLAKED_BISMUTH_MOSAIC,

                OperationStarcleaveBlockFamilies.CELESTIAL_OPAL_BLOCK,
                OperationStarcleaveBlockFamilies.POLISHED_CELESTIAL_OPAL_BLOCK,
                OperationStarcleaveBlockFamilies.POLISHED_CELESTIAL_OPAL_BRICKS,

                OperationStarcleaveBlockFamilies.OURANIC_CHIP_BLOCK,
                OperationStarcleaveBlockFamilies.OURANIC_BRICKS
        );

        HolderLookup.RegistryLookup<Enchantment> registryLookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        Holder<Enchantment> fortune = registryLookup.getOrThrow(Enchantments.FORTUNE);

        forEach(this::dropSelf,
                NETHERITE_PUMPKIN,

                STELLAR_SEDIMENT,

                ASTERUBBLE,

                GREAT_TREES_CARE,
                RED_MOURNER,
                ANGELCLAW,
                ELDROSE,
                WITCHGLARE,
                BLUE_DREAMER,
                DRAGONS_MAW,

                STARDUST_CLUSTER,

                SMOOTH_FELLCRUST_PILLAR,

                STARBLEACHED_LOG,
                STARBLEACHED_WOOD,

                STARBLEACHED_LEAF_BUNCH_BLOCK,

                IMBUED_STARBLEACHED_TILES,

                STARBLEACHED_SAPLING,

                STARBLEACHED_PEARL_BLOCK,

                STELLAR_REPULSOR,

                BLESSED_CLOTH_CARPET,
                BLESSED_CLOTH_CARPET_PADDING,
                BLESSED_CLOTH_CURTAIN,

                SUBCAELIC_PHLOGLIGHT,

                NUCLEIC_FISSUREROOT,
                NUCLEIC_FISSURERIND,
                STRIPED_NUCLEIC_FISSUREROOT,
                STRIPED_NUCLEIC_FISSURERIND,

                COAGULATED_PLASMA,

                STARFLAKED_BISMUTH_PILLAR,
                STARFLAKED_BISMUTH_TRAPDOOR,

                POLISHED_CELESTIAL_OPAL_PILLAR,

                MUCKY_SINGUT_COIL,
                MUCKY_SINGUT_BLOCK,
                CLEANSED_SINGUT_COIL,
                CLEANSED_SINGUT_BLOCK,

                OURANIC_PILLAR
        );

        forEach(this::dropPottedContents,
                POTTED_MULCHBORNE_TUFT,
                POTTED_SHORT_HOLY_MOSS,
                POTTED_STARBLEACHED_SAPLING,

                POTTED_GREAT_TREES_CARE,
                POTTED_RED_MOURNER,
                POTTED_ANGELCLAW,
                POTTED_ELDROSE,
                POTTED_WITCHGLARE,
                POTTED_BLUE_DREAMER,
                POTTED_DRAGONS_MAW
        );

        forEach(this::dropWhenSilkTouch,
                PLASMA_ICE,

                LARGE_CELESTIAL_OPAL_BUD,
                MEDIUM_CELESTIAL_OPAL_BUD,
                SMALL_CELESTIAL_OPAL_BUD
        );

        forEach(this::dropNothing,
                PHLOGISTIC_FIRE,
                BUDDING_CELESTIAL_OPAL
        );

        dropOther(STARBLEACH_CAULDRON, Items.CAULDRON);
        dropOther(STELLAR_PATH, STELLAR_SEDIMENT);

        dropWhenSilkTouch(HOLY_MOSS, STELLAR_SEDIMENT);
        dropWhenSilkTouch(STELLAR_MULCH, STELLAR_SEDIMENT);

        add(STELLAR_FARMLAND, createSingleItemTableWithSilkTouch(STELLAR_MULCH, STELLAR_SEDIMENT));
        add(STARDUST_BLOCK, block -> createSingleItemTableWithSilkTouch(block, OperationStarcleaveItems.STARDUST_CLUSTER, UniformGenerator.between(1.0F, 4.0F)));

        this.add(STARBLEACHED_LEAVES, block -> this.createStarbleachedLeavesDrops(block, STARBLEACHED_SAPLING, NORMAL_LEAVES_SAPLING_CHANCES));
        dropWithSilkTouchOrShears(NUCLEIC_FISSURELEAVES);

        dropWithShears(MULCHBORNE_TUFT);

        this.add(STARFLAKED_BISMUTH_DOOR, this::createDoorTable);

        this.add(SHORT_HOLY_MOSS, block -> this.createShortHolyMossDrops(block, fortune));
        this.add(TALL_HOLY_MOSS, block -> this.createTallHolyMossDrops(block, SHORT_HOLY_MOSS, fortune));

        add(BLESSED_BED, block -> createSinglePropConditionTable(block, BedBlock.PART, BedPart.HEAD));

        LootItemCondition.Builder fullyGrownBisreed = LootItemBlockStatePropertyCondition.hasBlockStateProperties(BISREEDS)
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(BisreedBlock.AGE, 3));
        add(BISREEDS, block -> applyExplosionDecay(block,
                lootTable()
                        .withPool(lootPool()
                                .when(fullyGrownBisreed.invert().and(LootItemRandomChanceCondition.randomChance(0.3F))) // when not fully grown, low chance to recover root
                                .add(item(OperationStarcleaveItems.BISREED_ROOT))
                        )
                        .withPool(lootPool()
                                .when(fullyGrownBisreed.and(LootItemRandomChanceCondition.randomChance(0.8F))) // when fully grown, decent chance to recover root
                                .add(
                                        item(OperationStarcleaveItems.BISREED_ROOT) // small chance for bonus root
                                                .apply(ApplyBonusCount.addBonusBinomialDistributionCount(fortune, 0.1F, 1))
                                )
                        )
                        .withPool(lootPool()
                                .when(fullyGrownBisreed) // when fully grown, drop flakes
                                .add(
                                        item(OperationStarcleaveItems.BISMUTH_FLAKE)
                                                .apply(ApplyBonusCount.addBonusBinomialDistributionCount(fortune, 0.25F, 5))
                                )
                        )
        ));

        this.add(
                NUCLEOSYNTHESEED,
                block -> this.createSilkTouchDispatchTable(
                        block,
                        this.applyExplosionDecay(
                                block,
                                LootItem.lootTableItem(OperationStarcleaveItems.OURANIC_CHIP)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 4.0F)))
                                        .apply(ApplyBonusCount.addUniformBonusCount(fortune, 2))
                        )
                )
        );

        addClusterDrops(registryLookup, CELESTIAL_OPAL_CLUSTER, OperationStarcleaveItems.CELESTIAL_OPAL_SHARD);
        addClusterDrops(registryLookup, CELESTIAL_OPAL_SPIRE, OperationStarcleaveItems.CELESTIAL_OPAL_SHARD);

        this.add(STARBLEACHED_LEAF_LITTER, this.createSegmentedDrops(STARBLEACHED_LEAF_LITTER));

        this.add(HOLY_LEAF_PLATFORM, createSlabItemTable(HOLY_LEAF_PLATFORM));
    }

    private void forEach(Consumer<Block> consumer, Block... list) {
        for (Block t : list) {
            consumer.accept(t);
        }
    }

    private void dropNothing(Block block) {
        this.add(block, noDrop());
    }

    private void dropWhenSilkTouch(Block block, Block noSilkTouch) {
        this.add(block, b -> this.createSingleItemTableWithSilkTouch(b, noSilkTouch));
    }

    private void dropWithShears(Block block) {
        add(block, BlockLootSubProvider::createShearsOnlyDrop);
    }

    private void dropWithSilkTouchOrShears(Block block) {
        add(block, this::createSilkTouchOrShearsDrop);
    }

    private LootTable.Builder createSilkTouchOrShearsDrop(ItemLike item) {
        return lootTable().withPool(lootPool().setRolls(ConstantValue.exactly(1.0F)).when(hasShearsOrSilkTouch()).add(item(item)));
    }

    public LootTable.Builder createStarbleachedLeavesDrops(Block leavesBlock, Block saplingBlock, float... chances) {
        HolderLookup.RegistryLookup<Enchantment> registryLookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        Holder<Enchantment> fortune = registryLookup.getOrThrow(Enchantments.FORTUNE);


        return this.createSilkTouchOrShearsDispatchTable(
                leavesBlock,
                this.applyExplosionCondition(leavesBlock, LootItem.lootTableItem(saplingBlock))
                        .when(BonusLevelTableCondition.bonusLevelFlatChance(registryLookup.getOrThrow(Enchantments.FORTUNE), chances))
        ).withPool(
                LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .when(this.doesNotHaveShearsOrSilkTouch())
                        .add(
                                this.applyExplosionDecay(
                                        leavesBlock,
                                        LootItem.lootTableItem(OperationStarcleaveItems.STARBLEACHED_LEAF_BUNCH)
                                                .apply(SetItemCountFunction.setCount(BinomialDistributionGenerator.binomial(1, 0.3333F)))
                                                .apply(ApplyBonusCount.addBonusBinomialDistributionCount(fortune, 0.333F, 0))
                                )
                        )
        );
    }

    private void addClusterDrops(HolderLookup.RegistryLookup<Enchantment> registryLookup, Block cluster, Item item) {
        this.add(
                cluster,
                block -> this.createSilkTouchDispatchTable(
                        block,
                        LootItem.lootTableItem(item)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(4.0F)))
                                .apply(ApplyBonusCount.addOreBonusCount(registryLookup.getOrThrow(Enchantments.FORTUNE)))
                                .when(MatchTool.toolMatches(ItemPredicate.Builder.item().of(ItemTags.CLUSTER_MAX_HARVESTABLES)))
                                .otherwise(
                                        this.applyExplosionDecay(
                                                block, LootItem.lootTableItem(item).apply(SetItemCountFunction.setCount(ConstantValue.exactly(2.0F)))
                                        )
                                )
                )
        );
    }

    public LootTable.Builder createSegmentedDrops(Block segmentedBlock) {
        return LootTable.lootTable()
                .withPool(
                        LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1.0F))
                                .add(
                                        this.applyExplosionDecay(
                                                segmentedBlock,
                                                LootItem.lootTableItem(segmentedBlock)
                                                        .apply(
                                                                IntStream.rangeClosed(1, 4).boxed().toList(),
                                                                integer -> SetItemCountFunction.setCount(ConstantValue.exactly(integer))
                                                                        .when(
                                                                                LootItemBlockStatePropertyCondition.hasBlockStateProperties(segmentedBlock)
                                                                                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(StarbleachedLeafLitterBlock.SEGMENT_AMOUNT, integer))
                                                                        )
                                                        )
                                        )
                                )
                );
    }

    public LootTable.Builder createShortHolyMossDrops(Block block, Holder<Enchantment> fortune) {
        return this.createShearsDispatchTable(block,
                this.applyExplosionDecay(block,
                        item(OperationStarcleaveItems.HOLY_STRANDS)
                                .when(LootItemRandomChanceCondition.randomChance(0.3F))
                                .apply(ApplyBonusCount.addUniformBonusCount(fortune, 4))
                )
        );
    }

    public LootTable.Builder createTallHolyMossDrops(Block block, Block sheared, Holder<Enchantment> fortune) {
        LootPoolEntryContainer.Builder<?> builder = item(sheared)
                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(2.0F)))
                .when(HAS_SHEARS)
                .otherwise(
                        this.applyExplosionCondition(block, item(OperationStarcleaveItems.HOLY_STRANDS))
                                .when(LootItemRandomChanceCondition.randomChance(0.6F))
                                .apply(ApplyBonusCount.addUniformBonusCount(fortune, 4))
                );

        return LootTable.lootTable()
                .withPool(
                        LootPool.lootPool()
                                .add(builder)
                                .when(
                                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER))
                                )
                                .when(
                                        LocationCheck.checkLocation(
                                                LocationPredicate.Builder.location()
                                                        .setBlock(
                                                                BlockPredicate.Builder.block()
                                                                        .of(block)
                                                                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER))
                                                        ),
                                                new BlockPos(0, 1, 0)
                                        )
                                )
                )
                .withPool(
                        LootPool.lootPool()
                                .add(builder)
                                .when(
                                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER))
                                )
                                .when(
                                        LocationCheck.checkLocation(
                                                LocationPredicate.Builder.location()
                                                        .setBlock(
                                                                BlockPredicate.Builder.block()
                                                                        .of(block)
                                                                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER))
                                                        ),
                                                new BlockPos(0, -1, 0)
                                        )
                                )
                );
    }

    private void addLootForFamilies(BlockFamily... families) {
        for (BlockFamily family : families) {
            addLootForFamily(family);
        }
    }

    private void addLootForFamily(BlockFamily family) {
        this.dropSelf(family.getBaseBlock());
        for (BlockFamily.Variant variant : BlockFamily.Variant.values()) {
            Block block = family.get(variant);
            if (block != null) {
                if (variant == BlockFamily.Variant.DOOR) {
                    add(block, createDoorTable(block));
                } else if (variant == BlockFamily.Variant.SLAB) {
                    add(block, createSlabItemTable(block));
                } else {
                    this.dropSelf(block);
                }
            }
        }
    }

    private static LootPoolSingletonContainer.Builder<?> item(ItemLike itemConvertible) {
        return LootItem.lootTableItem(itemConvertible);
    }

    private static LootPool.Builder lootPool() {
        return LootPool.lootPool();
    }

    private static LootTable.Builder lootTable() {
        return LootTable.lootTable();
    }
}

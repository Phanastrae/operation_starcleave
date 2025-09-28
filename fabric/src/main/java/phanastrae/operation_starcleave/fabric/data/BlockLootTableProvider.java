package phanastrae.operation_starcleave.fabric.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.loot.BlockLootSubProvider;
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
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import phanastrae.operation_starcleave.block.BisreedBlock;
import phanastrae.operation_starcleave.data.OperationStarcleaveBlockFamilies;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static phanastrae.operation_starcleave.block.OperationStarcleaveBlocks.*;

public class BlockLootTableProvider extends FabricBlockLootTableProvider {
    protected BlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        addLootForFamilies(
                OperationStarcleaveBlockFamilies.STARDUST_BRICKS,
                OperationStarcleaveBlockFamilies.STARBLEACHED_TILES,
                OperationStarcleaveBlockFamilies.STELLAR_TILES
        );

        HolderLookup.RegistryLookup<Enchantment> impl = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        Holder<Enchantment> fortune = impl.getOrThrow(Enchantments.FORTUNE);

        forEach(this::dropSelf,
                NETHERITE_PUMPKIN,

                STELLAR_SEDIMENT,

                STARBLEACHED_LOG,
                STARBLEACHED_WOOD,
                STARBLEACHED_LEAVES,

                IMBUED_STARBLEACHED_TILES,
                STARBLEACHED_PEARL_BLOCK,

                STELLAR_REPULSOR,

                BLESSED_CLOTH_BLOCK,
                BLESSED_CLOTH_CARPET,
                BLESSED_CLOTH_CURTAIN,

                SUBCAELIC_PHLOGLIGHT,

                NUCLEIC_FISSUREROOT,
                NUCLEIC_FISSURERIND,
                STRIPED_NUCLEIC_FISSUREROOT,
                STRIPED_NUCLEIC_FISSURERIND,

                COAGULATED_PLASMA
        );

        forEach(this::dropPottedContents,
                POTTED_MULCHBORNE_TUFT,
                POTTED_SHORT_HOLY_MOSS
        );

        dropNothing(PHLOGISTIC_FIRE);

        dropOther(STARBLEACH_CAULDRON, Items.CAULDRON);

        dropWhenSilkTouch(PLASMA_ICE);

        dropWhenSilkTouch(HOLY_MOSS, STELLAR_SEDIMENT);
        dropWhenSilkTouch(STELLAR_MULCH, STELLAR_SEDIMENT);
        dropWhenSilkTouch(NUCLEOSYNTHESEED, NUCLEIC_FISSUREROOT);

        add(STELLAR_FARMLAND, createSingleItemTableWithSilkTouch(STELLAR_MULCH, STELLAR_SEDIMENT));
        add(STARDUST_BLOCK, block -> createSingleItemTableWithSilkTouch(block, OperationStarcleaveItems.STARDUST_CLUSTER, UniformGenerator.between(1.0F, 4.0F)));

        dropWithSilkTouchOrShears(NUCLEIC_FISSURELEAVES);

        dropWithShears(MULCHBORNE_TUFT);

        add(SHORT_HOLY_MOSS, block -> createShearsDispatchTable(block,
                        applyExplosionDecay(block,
                                item(OperationStarcleaveItems.HOLY_STRANDS)
                                        .apply(ApplyBonusCount.addUniformBonusCount(fortune, 4))
                                        .when(LootItemRandomChanceCondition.randomChance(0.3F))
                        )
                )
        );

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
    }

    private void forEach(Consumer<Block> consumer, Block... list) {
        for(Block t : list) {
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

    private void addLootForFamilies(BlockFamily... families) {
        for (BlockFamily family : families) {
            addLootForFamily(family);
        }
    }

    private void addLootForFamily(BlockFamily family) {
        this.dropSelf(family.getBaseBlock());
        for(BlockFamily.Variant variant : BlockFamily.Variant.values()) {
            Block block = family.get(variant);
            if(block != null) {
                if(variant == BlockFamily.Variant.DOOR) {
                    add(block, createDoorTable(block));
                } else
                if(variant == BlockFamily.Variant.SLAB) {
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

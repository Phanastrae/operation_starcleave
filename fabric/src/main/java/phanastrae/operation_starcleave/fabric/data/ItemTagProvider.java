package phanastrae.operation_starcleave.fabric.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;

import java.util.concurrent.CompletableFuture;

public class ItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture, @Nullable FabricTagProvider.BlockTagProvider blockTagProvider) {
        super(output, completableFuture, blockTagProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        // copy block tags
        //this.copy(BlockTags.WOOL, ItemTags.WOOL);
        //this.copy(BlockTags.PLANKS, ItemTags.PLANKS);
        //this.copy(BlockTags.STONE_BRICKS, ItemTags.STONE_BRICKS);
        //this.copy(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
        //this.copy(BlockTags.STONE_BUTTONS, ItemTags.STONE_BUTTONS);
        //this.copy(BlockTags.BUTTONS, ItemTags.BUTTONS);
        //this.copy(BlockTags.WOOL_CARPETS, ItemTags.WOOL_CARPETS);
        //this.copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
        //this.copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
        //this.copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
        //this.copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
        //this.copy(BlockTags.FENCE_GATES, ItemTags.FENCE_GATES);
        //this.copy(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);
        //this.copy(BlockTags.DOORS, ItemTags.DOORS);
        //this.copy(BlockTags.SAPLINGS, ItemTags.SAPLINGS);
        //this.copy(BlockTags.BAMBOO_BLOCKS, ItemTags.BAMBOO_BLOCKS);
        //this.copy(BlockTags.OAK_LOGS, ItemTags.OAK_LOGS);
        //this.copy(BlockTags.DARK_OAK_LOGS, ItemTags.DARK_OAK_LOGS);
        //this.copy(BlockTags.BIRCH_LOGS, ItemTags.BIRCH_LOGS);
        //this.copy(BlockTags.ACACIA_LOGS, ItemTags.ACACIA_LOGS);
        //this.copy(BlockTags.SPRUCE_LOGS, ItemTags.SPRUCE_LOGS);
        //this.copy(BlockTags.MANGROVE_LOGS, ItemTags.MANGROVE_LOGS);
        //this.copy(BlockTags.JUNGLE_LOGS, ItemTags.JUNGLE_LOGS);
        //this.copy(BlockTags.CHERRY_LOGS, ItemTags.CHERRY_LOGS);
        //this.copy(BlockTags.CRIMSON_STEMS, ItemTags.CRIMSON_STEMS);
        //this.copy(BlockTags.WARPED_STEMS, ItemTags.WARPED_STEMS);
        //this.copy(BlockTags.WART_BLOCKS, ItemTags.WART_BLOCKS);
        //this.copy(BlockTags.LOGS_THAT_BURN, ItemTags.LOGS_THAT_BURN);
        //this.copy(BlockTags.LOGS, ItemTags.LOGS);
        //this.copy(BlockTags.SAND, ItemTags.SAND);
        //this.copy(BlockTags.SMELTS_TO_GLASS, ItemTags.SMELTS_TO_GLASS);
        //this.copy(BlockTags.SLABS, ItemTags.SLABS);
        //this.copy(BlockTags.WALLS, ItemTags.WALLS);
        //this.copy(BlockTags.STAIRS, ItemTags.STAIRS);
        //this.copy(BlockTags.ANVIL, ItemTags.ANVIL);
        //this.copy(BlockTags.RAILS, ItemTags.RAILS);
        //this.copy(BlockTags.LEAVES, ItemTags.LEAVES);
        //this.copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
        //this.copy(BlockTags.TRAPDOORS, ItemTags.TRAPDOORS);
        //this.copy(BlockTags.SMALL_FLOWERS, ItemTags.SMALL_FLOWERS);
        //this.copy(BlockTags.BEDS, ItemTags.BEDS);
        //this.copy(BlockTags.FENCES, ItemTags.FENCES);
        //this.copy(BlockTags.TALL_FLOWERS, ItemTags.TALL_FLOWERS);
        //this.copy(BlockTags.FLOWERS, ItemTags.FLOWERS);
        //this.copy(BlockTags.SOUL_FIRE_BASE_BLOCKS, ItemTags.SOUL_FIRE_BASE_BLOCKS);
        //this.copy(BlockTags.CANDLES, ItemTags.CANDLES);
        this.copy(BlockTags.DAMPENS_VIBRATIONS, ItemTags.DAMPENS_VIBRATIONS);
        //this.copy(BlockTags.GOLD_ORES, ItemTags.GOLD_ORES);
        //this.copy(BlockTags.IRON_ORES, ItemTags.IRON_ORES);
        //this.copy(BlockTags.DIAMOND_ORES, ItemTags.DIAMOND_ORES);
        //this.copy(BlockTags.REDSTONE_ORES, ItemTags.REDSTONE_ORES);
        //this.copy(BlockTags.LAPIS_ORES, ItemTags.LAPIS_ORES);
        //this.copy(BlockTags.COAL_ORES, ItemTags.COAL_ORES);
        //this.copy(BlockTags.EMERALD_ORES, ItemTags.EMERALD_ORES);
        //this.copy(BlockTags.COPPER_ORES, ItemTags.COPPER_ORES);
        //this.copy(BlockTags.DIRT, ItemTags.DIRT);
        //this.copy(BlockTags.TERRACOTTA, ItemTags.TERRACOTTA);
        //this.copy(BlockTags.COMPLETES_FIND_TREE_TUTORIAL, ItemTags.COMPLETES_FIND_TREE_TUTORIAL);
        //this.copy(BlockTags.STANDING_SIGNS, ItemTags.SIGNS);
        //this.copy(BlockTags.CEILING_HANGING_SIGNS, ItemTags.HANGING_SIGNS);

        // vanilla
        getOrCreateTagBuilder(ItemTags.VILLAGER_PLANTABLE_SEEDS).add(
                OperationStarcleaveItems.BISREED_ROOT
        );
    }
}

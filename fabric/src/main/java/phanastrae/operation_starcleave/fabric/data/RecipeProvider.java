package phanastrae.operation_starcleave.fabric.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;

import java.util.concurrent.CompletableFuture;

public class RecipeProvider extends FabricRecipeProvider {
    public RecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void buildRecipes(RecipeOutput exporter) {
        slab(exporter, RecipeCategory.BUILDING_BLOCKS, OperationStarcleaveItems.STARBLEACHED_TILE_SLAB, OperationStarcleaveItems.STARBLEACHED_TILES);
        wall(exporter, RecipeCategory.BUILDING_BLOCKS, OperationStarcleaveItems.STARBLEACHED_TILE_WALL, OperationStarcleaveItems.STARBLEACHED_TILES);
        slab(exporter, RecipeCategory.BUILDING_BLOCKS, OperationStarcleaveItems.STELLAR_TILE_SLAB, OperationStarcleaveItems.STELLAR_TILES);
        woodFromLogs(exporter, OperationStarcleaveBlocks.STARBLEACHED_WOOD, OperationStarcleaveBlocks.STARBLEACHED_LOG);

        // shapeless
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, OperationStarcleaveItems.STARBLEACHED_TILES, 4)
                .requires(OperationStarcleaveItems.STARBLEACHED_LOG)
                .group("planks")
                .unlockedBy(
                        getHasName(OperationStarcleaveItems.STARBLEACHED_LOG),
                        has(OperationStarcleaveItems.STARBLEACHED_LOG))
                .save(exporter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, OperationStarcleaveItems.SPLASH_STARBLEACH_BOTTLE, 5)
                .requires(Items.GUNPOWDER)
                .requires(OperationStarcleaveItems.STARBLEACH_BOTTLE, 5)
                .unlockedBy(
                        getHasName(OperationStarcleaveItems.STARBLEACH_BOTTLE),
                        has(OperationStarcleaveItems.STARBLEACH_BOTTLE))
                .save(exporter);

        // shaped
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, OperationStarcleaveItems.BLESSED_CLOTH)
                .define('#', OperationStarcleaveItems.HOLY_STRANDS)
                .pattern("##").pattern("##")
                .unlockedBy(
                        getHasName(OperationStarcleaveItems.HOLY_STRANDS),
                        has(OperationStarcleaveItems.HOLY_STRANDS))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, OperationStarcleaveItems.BLESSED_CLOTH_BLOCK, 2)
                .define('#', OperationStarcleaveItems.BLESSED_CLOTH)
                .pattern("##").pattern("##")
                .unlockedBy(
                        getHasName(OperationStarcleaveItems.BLESSED_CLOTH),
                        has(OperationStarcleaveItems.BLESSED_CLOTH))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, OperationStarcleaveItems.STARDUST_BLOCK)
                .define('#', OperationStarcleaveItems.STARDUST_CLUSTER)
                .pattern("##").pattern("##")
                .unlockedBy(
                        getHasName(OperationStarcleaveItems.STARDUST_CLUSTER),
                        has(OperationStarcleaveItems.STARDUST_CLUSTER))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, OperationStarcleaveItems.STELLAR_TILES, 4)
                .define('#', OperationStarcleaveItems.STELLAR_SEDIMENT)
                .pattern("##").pattern("##")
                .unlockedBy(
                        getHasName(OperationStarcleaveItems.STELLAR_SEDIMENT),
                        has(OperationStarcleaveItems.STELLAR_SEDIMENT))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, OperationStarcleaveItems.CHISELED_STARBLEACHED_TILES)
                .define('#', OperationStarcleaveItems.STARBLEACHED_TILE_SLAB)
                .pattern("#")
                .pattern("#")
                .unlockedBy(
                        getHasName(OperationStarcleaveItems.STARBLEACHED_TILES),
                        has(OperationStarcleaveItems.STARBLEACHED_TILES))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, OperationStarcleaveItems.BLESSED_CLOTH_CARPET, 3)
                .define('#', OperationStarcleaveItems.BLESSED_CLOTH_BLOCK)
                .pattern("##")
                .unlockedBy(
                        getHasName(OperationStarcleaveItems.BLESSED_CLOTH_BLOCK),
                        has(OperationStarcleaveItems.BLESSED_CLOTH_BLOCK))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, OperationStarcleaveItems.BLESSED_CLOTH_CURTAIN, 16)
                .define('#', OperationStarcleaveItems.BLESSED_CLOTH)
                .pattern("###")
                .pattern("###")
                .unlockedBy(
                        getHasName(OperationStarcleaveItems.BLESSED_CLOTH),
                        has(OperationStarcleaveItems.BLESSED_CLOTH))
                .save(exporter);

        stairBuilder(OperationStarcleaveItems.STARBLEACHED_TILE_STAIRS, Ingredient.of(OperationStarcleaveItems.STARBLEACHED_TILES))
                .unlockedBy(
                        getHasName(OperationStarcleaveItems.STARBLEACHED_TILES),
                        has(OperationStarcleaveItems.STARBLEACHED_TILES))
                .save(exporter);

        // complex shaped
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, OperationStarcleaveItems.BLESSED_BED)
                .define('B', OperationStarcleaveItems.BLESSED_CLOTH)
                .define('P', ItemTags.PLANKS)
                .pattern("BBB")
                .pattern("PPP")
                .group("bed")
                .unlockedBy(
                        getHasName(OperationStarcleaveItems.BLESSED_CLOTH),
                        has(OperationStarcleaveItems.BLESSED_CLOTH))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, OperationStarcleaveItems.IMBUED_STARBLEACHED_TILES, 4)
                .define('T', OperationStarcleaveItems.STARBLEACHED_TILES)
                .define('B', OperationStarcleaveItems.STARBLEACH_BOTTLE)
                .pattern(" T ")
                .pattern("TBT")
                .pattern(" T ")
                .unlockedBy(
                        getHasName(OperationStarcleaveItems.STARBLEACH_BOTTLE),
                        has(OperationStarcleaveItems.STARBLEACH_BOTTLE))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, OperationStarcleaveItems.STARBLEACHED_PEARL_BLOCK)
                .define('T', OperationStarcleaveItems.STARBLEACHED_TILES)
                .define('P', OperationStarcleaveItems.STARBLEACHED_PEARL)
                .pattern("TPT")
                .pattern("P P")
                .pattern("TPT")
                .unlockedBy(
                        getHasName(OperationStarcleaveItems.STARBLEACHED_PEARL),
                        has(OperationStarcleaveItems.STARBLEACHED_PEARL)
                )
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, OperationStarcleaveItems.STELLAR_REPULSOR, 3)
                .define('T', OperationStarcleaveItems.STELLAR_TILES)
                .define('P', OperationStarcleaveItems.STARBLEACHED_PEARL)
                .define('C', OperationStarcleaveItems.BLESSED_CLOTH)
                .pattern("CCC")
                .pattern("CPC")
                .pattern("TTT")
                .unlockedBy(
                        getHasName(OperationStarcleaveItems.STARBLEACHED_PEARL),
                        has(OperationStarcleaveItems.STARBLEACHED_PEARL)
                )
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, OperationStarcleaveItems.FIRMAMENT_REJUVENATOR, 4)
                .define('G', Items.GHAST_TEAR)
                .define('O', Items.CRYING_OBSIDIAN)
                .define('T', Items.TNT)
                .define('E', Items.ENDER_PEARL)
                .define('P', OperationStarcleaveItems.STARBLEACHED_PEARL)
                .define('H', OperationStarcleaveItems.HOLY_STRANDS)
                .pattern("OTP")
                .pattern("EOH")
                .pattern("G H")
                .unlockedBy(
                        getHasName(OperationStarcleaveItems.STARBLEACHED_PEARL),
                        has(OperationStarcleaveItems.STARBLEACHED_PEARL)
                )
                .save(exporter);

        SmithingTransformRecipeBuilder.smithing(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), Ingredient.of(Items.CARVED_PUMPKIN), Ingredient.of(Items.NETHERITE_INGOT), RecipeCategory.TOOLS, OperationStarcleaveItems.NETHERITE_PUMPKIN)
                .unlocks(
                        getHasName(Items.NETHERITE_INGOT),
                        has(Items.NETHERITE_INGOT)
                )
                .save(exporter, OperationStarcleave.id("netherite_pumpkin_smithing"));
    }
}

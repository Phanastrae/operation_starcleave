package phanastrae.operation_starcleave.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.data.server.recipe.SmithingTransformRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;

public class RecipeProvider extends FabricRecipeProvider {
    public RecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        offerSlabRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, OperationStarcleaveItems.STARBLEACHED_TILE_SLAB, OperationStarcleaveItems.STARBLEACHED_TILES);
        offerWallRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, OperationStarcleaveItems.STARBLEACHED_TILE_WALL, OperationStarcleaveItems.STARBLEACHED_TILES);
        offerSlabRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, OperationStarcleaveItems.STELLAR_TILE_SLAB, OperationStarcleaveItems.STELLAR_TILES);
        offerBarkBlockRecipe(exporter, OperationStarcleaveBlocks.STARBLEACHED_WOOD, OperationStarcleaveBlocks.STARBLEACHED_LOG);

        // shapeless
        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, OperationStarcleaveItems.STARBLEACHED_TILES, 4)
                .input(OperationStarcleaveItems.STARBLEACHED_LOG)
                .group("planks")
                .criterion(
                        hasItem(OperationStarcleaveItems.STARBLEACHED_LOG),
                        conditionsFromItem(OperationStarcleaveItems.STARBLEACHED_LOG))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, OperationStarcleaveItems.CHISELED_STARBLEACHED_TILES)
                .input(OperationStarcleaveItems.STARBLEACHED_TILES)
                .criterion(
                        hasItem(OperationStarcleaveItems.STARBLEACHED_TILES),
                        conditionsFromItem(OperationStarcleaveItems.STARBLEACHED_TILES))
                .offerTo(exporter);

        // shaped
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, OperationStarcleaveItems.BLESSED_CLOTH)
                .input('#', OperationStarcleaveItems.HOLY_STRANDS)
                .pattern("##").pattern("##")
                .criterion(
                        hasItem(OperationStarcleaveItems.HOLY_STRANDS),
                        conditionsFromItem(OperationStarcleaveItems.HOLY_STRANDS))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, OperationStarcleaveItems.BLESSED_CLOTH_BLOCK, 2)
                .input('#', OperationStarcleaveItems.BLESSED_CLOTH)
                .pattern("##").pattern("##")
                .criterion(
                        hasItem(OperationStarcleaveItems.BLESSED_CLOTH),
                        conditionsFromItem(OperationStarcleaveItems.BLESSED_CLOTH))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, OperationStarcleaveItems.STARDUST_BLOCK)
                .input('#', OperationStarcleaveItems.STARDUST_CLUSTER)
                .pattern("##").pattern("##")
                .criterion(
                        hasItem(OperationStarcleaveItems.STARDUST_CLUSTER),
                        conditionsFromItem(OperationStarcleaveItems.STARDUST_CLUSTER))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, OperationStarcleaveItems.STELLAR_TILES, 4)
                .input('#', OperationStarcleaveItems.STELLAR_SEDIMENT)
                .pattern("##").pattern("##")
                .criterion(
                        hasItem(OperationStarcleaveItems.STELLAR_SEDIMENT),
                        conditionsFromItem(OperationStarcleaveItems.STELLAR_SEDIMENT))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, OperationStarcleaveItems.BLESSED_CLOTH_CARPET, 3)
                .input('#', OperationStarcleaveItems.BLESSED_CLOTH)
                .pattern("##")
                .criterion(
                        hasItem(OperationStarcleaveItems.BLESSED_CLOTH),
                        conditionsFromItem(OperationStarcleaveItems.BLESSED_CLOTH))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, OperationStarcleaveItems.BLESSED_CLOTH_CURTAIN, 16)
                .input('#', OperationStarcleaveItems.BLESSED_CLOTH)
                .pattern("###")
                .pattern("###")
                .criterion(
                        hasItem(OperationStarcleaveItems.BLESSED_CLOTH),
                        conditionsFromItem(OperationStarcleaveItems.BLESSED_CLOTH))
                .offerTo(exporter);

        createStairsRecipe(OperationStarcleaveItems.STARBLEACHED_TILE_STAIRS, Ingredient.ofItems(OperationStarcleaveItems.STARBLEACHED_TILES))
                .criterion(
                        hasItem(OperationStarcleaveItems.STARBLEACHED_TILES),
                        conditionsFromItem(OperationStarcleaveItems.STARBLEACHED_TILES))
                .offerTo(exporter);

        // complex shaped
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, OperationStarcleaveItems.BLESSED_BED)
                .input('B', OperationStarcleaveItems.BLESSED_CLOTH)
                .input('P', ItemTags.PLANKS)
                .pattern("BBB")
                .pattern("PPP")
                .group("bed")
                .criterion(
                        hasItem(OperationStarcleaveItems.BLESSED_CLOTH),
                        conditionsFromItem(OperationStarcleaveItems.BLESSED_CLOTH))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, OperationStarcleaveItems.IMBUED_STARBLEACHED_TILES, 8)
                .input('T', OperationStarcleaveItems.STARBLEACHED_TILES)
                .input('B', OperationStarcleaveItems.STARBLEACH_BOTTLE)
                .pattern("TTT")
                .pattern("TBT")
                .pattern("TTT")
                .criterion(
                        hasItem(OperationStarcleaveItems.STARBLEACH_BOTTLE),
                        conditionsFromItem(OperationStarcleaveItems.STARBLEACH_BOTTLE))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, OperationStarcleaveItems.SPLASH_STARBLEACH_BOTTLE, 8)
                .input('B', OperationStarcleaveItems.STARBLEACH_BOTTLE)
                .input('G', Items.GUNPOWDER)
                .pattern("BBB")
                .pattern("BGB")
                .pattern("BBB")
                .criterion(
                        hasItem(OperationStarcleaveItems.STARBLEACH_BOTTLE),
                        conditionsFromItem(OperationStarcleaveItems.STARBLEACH_BOTTLE))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, OperationStarcleaveItems.STARBLEACHED_PEARL_BLOCK)
                .input('T', OperationStarcleaveItems.STARBLEACHED_TILES)
                .input('P', OperationStarcleaveItems.STARBLEACHED_PEARL)
                .pattern("TPT")
                .pattern("P P")
                .pattern("TPT")
                .criterion(
                        hasItem(OperationStarcleaveItems.STARBLEACHED_PEARL),
                        conditionsFromItem(OperationStarcleaveItems.STARBLEACHED_PEARL)
                )
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, OperationStarcleaveItems.STELLAR_REPULSOR, 3)
                .input('T', OperationStarcleaveItems.STELLAR_TILES)
                .input('P', OperationStarcleaveItems.STARBLEACHED_PEARL)
                .input('C', OperationStarcleaveItems.BLESSED_CLOTH)
                .pattern("CCC")
                .pattern("CPC")
                .pattern("TTT")
                .criterion(
                        hasItem(OperationStarcleaveItems.STARBLEACHED_PEARL),
                        conditionsFromItem(OperationStarcleaveItems.STARBLEACHED_PEARL)
                )
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, OperationStarcleaveItems.FIRMAMENT_REJUVENATOR, 4)
                .input('G', Items.GHAST_TEAR)
                .input('O', Items.CRYING_OBSIDIAN)
                .input('T', Items.TNT)
                .input('P', OperationStarcleaveItems.STARBLEACHED_PEARL)
                .input('H', OperationStarcleaveItems.HOLY_STRANDS)
                .pattern("PGP")
                .pattern("HTH")
                .pattern("OHO")
                .criterion(
                        hasItem(OperationStarcleaveItems.STARBLEACHED_PEARL),
                        conditionsFromItem(OperationStarcleaveItems.STARBLEACHED_PEARL)
                )
                .offerTo(exporter);

        SmithingTransformRecipeJsonBuilder.create(Ingredient.ofItems(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), Ingredient.ofItems(Items.CARVED_PUMPKIN), Ingredient.ofItems(Items.NETHERITE_INGOT), RecipeCategory.TOOLS, OperationStarcleaveItems.NETHERITE_PUMPKIN)
                .criterion(
                        hasItem(Items.NETHERITE_INGOT),
                        conditionsFromItem(Items.NETHERITE_INGOT)
                )
                .offerTo(exporter, OperationStarcleave.id("netherite_pumpkin_smithing"));
    }
}

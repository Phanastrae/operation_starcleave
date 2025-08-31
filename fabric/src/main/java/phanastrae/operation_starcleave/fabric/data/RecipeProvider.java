package phanastrae.operation_starcleave.fabric.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.data.OperationStarcleaveBlockFamilies;
import phanastrae.operation_starcleave.item.tag.OperationStarcleaveItemTags;

import java.util.concurrent.CompletableFuture;

import static phanastrae.operation_starcleave.item.OperationStarcleaveItems.*;


public class RecipeProvider extends FabricRecipeProvider {
    public RecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void buildRecipes(RecipeOutput exporter) {
        OperationStarcleaveBlockFamilies
                .getAllOperationStarcleaveFamilies()
                .filter(BlockFamily::shouldGenerateRecipe)
                .forEach(blockFamily -> generateRecipes(exporter, blockFamily, FeatureFlags.VANILLA_SET));

        woodFromLogs(exporter, STARBLEACHED_WOOD, STARBLEACHED_LOG);

        // shapeless
        planksFromLog(exporter, STARBLEACHED_TILES, OperationStarcleaveItemTags.STARBLEACHED_LOGS, 4);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, SPLASH_STARBLEACH_BOTTLE, 5)
                .requires(Items.GUNPOWDER)
                .requires(STARBLEACH_BOTTLE, 5)
                .unlockedBy(
                        getHasName(STARBLEACH_BOTTLE),
                        has(STARBLEACH_BOTTLE))
                .save(exporter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BISMUTH_FLAKE, 6)
                .requires(STARFLAKED_BISMUTH)
                .unlockedBy(
                        getHasName(STARFLAKED_BISMUTH),
                        has(STARFLAKED_BISMUTH))
                .save(exporter);

        // shaped
        twoByTwoPacker(exporter, RecipeCategory.MISC, BLESSED_CLOTH, HOLY_STRANDS);
        twoByTwoPacker(exporter, RecipeCategory.BUILDING_BLOCKS, BLESSED_CLOTH_BLOCK, BLESSED_CLOTH, 2);
        twoByTwoPacker(exporter, RecipeCategory.BUILDING_BLOCKS, STARDUST_BLOCK, STARDUST_CLUSTER);

        savePolished(STARDUST_BRICKS, STARDUST_BLOCK, exporter);
        savePolished(STELLAR_TILES, STELLAR_SEDIMENT, exporter);

        carpet(exporter, BLESSED_CLOTH_CARPET, BLESSED_CLOTH_BLOCK);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BLESSED_CLOTH_CURTAIN, 16)
                .define('#', BLESSED_CLOTH)
                .pattern("###")
                .pattern("###")
                .unlockedBy(
                        getHasName(BLESSED_CLOTH),
                        has(BLESSED_CLOTH))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, STARFLAKED_BISMUTH)
                .define('#', BISMUTH_FLAKE)
                .pattern("# #")
                .pattern("###")
                .pattern(" # ")
                .unlockedBy(
                        getHasName(BISMUTH_FLAKE),
                        has(BISMUTH_FLAKE))
                .save(exporter);

        // complex shaped
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BLESSED_BED)
                .define('B', BLESSED_CLOTH)
                .define('P', ItemTags.PLANKS)
                .pattern("BBB")
                .pattern("PPP")
                .group("bed")
                .unlockedBy(
                        getHasName(BLESSED_CLOTH),
                        has(BLESSED_CLOTH))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, IMBUED_STARBLEACHED_TILES, 4)
                .define('T', STARBLEACHED_TILES)
                .define('B', STARBLEACH_BOTTLE)
                .pattern(" T ")
                .pattern("TBT")
                .pattern(" T ")
                .unlockedBy(
                        getHasName(STARBLEACH_BOTTLE),
                        has(STARBLEACH_BOTTLE))
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, STARBLEACHED_PEARL_BLOCK)
                .define('T', STARBLEACHED_TILES)
                .define('P', STARBLEACHED_PEARL)
                .pattern("TPT")
                .pattern("P P")
                .pattern("TPT")
                .unlockedBy(
                        getHasName(STARBLEACHED_PEARL),
                        has(STARBLEACHED_PEARL)
                )
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, STELLAR_REPULSOR, 3)
                .define('T', STELLAR_TILES)
                .define('P', STARBLEACHED_PEARL)
                .define('C', BLESSED_CLOTH)
                .pattern("CCC")
                .pattern("CPC")
                .pattern("TTT")
                .unlockedBy(
                        getHasName(STARBLEACHED_PEARL),
                        has(STARBLEACHED_PEARL)
                )
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, FIRMAMENT_REJUVENATOR, 4)
                .define('G', Items.GHAST_TEAR)
                .define('O', Items.CRYING_OBSIDIAN)
                .define('T', Items.TNT)
                .define('E', Items.ENDER_PEARL)
                .define('P', STARBLEACHED_PEARL)
                .define('H', HOLY_STRANDS)
                .pattern("OTP")
                .pattern("EOH")
                .pattern("G H")
                .unlockedBy(
                        getHasName(STARBLEACHED_PEARL),
                        has(STARBLEACHED_PEARL)
                )
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BISMUTH_PEGASUS_ARMOR)
                .define('F', BISMUTH_FLAKE)
                .define('B', STARFLAKED_BISMUTH)
                .define('P', STARBLEACHED_PEARL)
                .pattern("F B")
                .pattern("BPB")
                .pattern("F F")
                .unlockedBy(
                        getHasName(STARFLAKED_BISMUTH),
                        has(STARFLAKED_BISMUTH)
                )
                .save(exporter);

        // smithing
        SmithingTransformRecipeBuilder.smithing(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), Ingredient.of(Items.CARVED_PUMPKIN), Ingredient.of(Items.NETHERITE_INGOT), RecipeCategory.TOOLS, NETHERITE_PUMPKIN)
                .unlocks(
                        getHasName(Items.NETHERITE_INGOT),
                        has(Items.NETHERITE_INGOT)
                )
                .save(exporter, OperationStarcleave.id("netherite_pumpkin_smithing"));
    }

    private static void savePolished(ItemLike polished, ItemLike material, RecipeOutput exporter) {
        polished(exporter, RecipeCategory.BUILDING_BLOCKS, polished, material);
    }

    private static void twoByTwoPacker(RecipeOutput recipeOutput, RecipeCategory category, ItemLike packed, ItemLike unpacked, int count) {
        ShapedRecipeBuilder.shaped(category, packed, count)
                .define('#', unpacked)
                .pattern("##")
                .pattern("##")
                .unlockedBy(getHasName(unpacked), has(unpacked))
                .save(recipeOutput);
    }
}

package phanastrae.operation_starcleave.fabric.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.data.OperationStarcleaveBlockFamilies;
import phanastrae.operation_starcleave.item.tag.OperationStarcleaveItemTags;
import phanastrae.operation_starcleave.recipe.ItemStarbleachingRecipe;

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

        // region shapeless crafting
        planksFromLog(exporter, STARBLEACHED_TILES, OperationStarcleaveItemTags.STARBLEACHED_LOGS, 4);
        planksFromLog(exporter, STARTOUCHED_PLANKS, OperationStarcleaveItemTags.STARTOUCHED_LOGS, 4);

        simpleShapelessWithSuffix(exporter, RecipeCategory.MISC, MUCKY_SINGUT_COIL, MUCKY_SINGUTS, 2, "_from_coil");
        simpleShapelessWithSuffix(exporter, RecipeCategory.MISC, MUCKY_SINGUT_BLOCK, MUCKY_SINGUTS, 2, "_from_block");
        simpleShapelessWithSuffix(exporter, RecipeCategory.MISC, CLEANSED_SINGUT_COIL, CLEANSED_SINGUTS, 2, "_from_coil");
        simpleShapelessWithSuffix(exporter, RecipeCategory.MISC, CLEANSED_SINGUT_BLOCK, CLEANSED_SINGUTS, 2, "_from_block");

        simpleShapeless(exporter, RecipeCategory.MISC, STARBLEACHED_LEAVES, STARBLEACHED_LEAF_LITTER, 4);

        oneToOneConversionRecipe(exporter, Items.PINK_DYE, GREAT_TREES_CARE, "pink_dye");
        oneToOneConversionRecipe(exporter, Items.RED_DYE, RED_MOURNER, "red_dye");
        oneToOneConversionRecipe(exporter, Items.ORANGE_DYE, ANGELCLAW, "orange_dye");
        oneToOneConversionRecipe(exporter, Items.LIGHT_GRAY_DYE, ELDROSE, "light_gray_dye");
        oneToOneConversionRecipe(exporter, Items.LIME_DYE, WITCHGLARE, "lime_dye");
        oneToOneConversionRecipe(exporter, Items.LIGHT_BLUE_DYE, BLUE_DREAMER, "light_blue_dye");
        oneToOneConversionRecipe(exporter, Items.PURPLE_DYE, DRAGONS_MAW, "purple_dye");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BISMUTH_FLAKE, 6)
                .requires(STARFLAKED_BISMUTH)
                .unlockedBy(
                        getHasName(STARFLAKED_BISMUTH),
                        has(STARFLAKED_BISMUTH))
                .save(exporter);
        // endregion

        nineBlockStorageRecipes(exporter, RecipeCategory.MISC, STARBLEACHED_LEAF_BUNCH, RecipeCategory.BUILDING_BLOCKS, STARBLEACHED_LEAF_BUNCH_BLOCK);

        // region shaped crafting
        woodFromLogs(exporter, STARBLEACHED_WOOD, STARBLEACHED_LOG);
        woodFromLogs(exporter, STARTOUCHED_WOOD, STARTOUCHED_LOG);
        woodFromLogs(exporter, NUCLEIC_FISSURERIND, NUCLEIC_FISSUREROOT);
        woodFromLogs(exporter, STRIPED_NUCLEIC_FISSURERIND, STRIPED_NUCLEIC_FISSUREROOT);

        hangingSign(exporter, STARTOUCHED_HANGING_SIGN, STARTOUCHED_LOG);

        twoByTwoPacker(exporter, RecipeCategory.MISC, BLESSED_CLOTH, HOLY_STRANDS);
        twoByTwoPacker(exporter, RecipeCategory.BUILDING_BLOCKS, BLESSED_CLOTH_BLOCK, BLESSED_CLOTH, 2);
        twoByTwoPacker(exporter, RecipeCategory.BUILDING_BLOCKS, BLESSED_CLOTH_PADDING, BLESSED_CLOTH_BLOCK, 4);
        twoByTwoPacker(exporter, RecipeCategory.BUILDING_BLOCKS, STARDUST_BLOCK, STARDUST_CLUSTER);

        twoByTwoPacker(exporter, RecipeCategory.BUILDING_BLOCKS, STARFLAKED_BISMUTH_BLOCK, STARFLAKED_BISMUTH);
        mosaicBuilder(exporter, RecipeCategory.DECORATIONS, STARFLAKED_BISMUTH_MOSAIC, STARFLAKED_BISMUTH_TILE_SLAB);

        doorBuilder(STARFLAKED_BISMUTH_DOOR, Ingredient.of(STARFLAKED_BISMUTH))
                .unlockedBy(getHasName(STARFLAKED_BISMUTH), has(STARFLAKED_BISMUTH))
                .save(exporter);
        trapdoorBuilder(STARFLAKED_BISMUTH_TRAPDOOR, Ingredient.of(STARFLAKED_BISMUTH))
                .unlockedBy(getHasName(STARFLAKED_BISMUTH), has(STARFLAKED_BISMUTH))
                .save(exporter);

        savePolished(STARDUST_BRICKS, STARDUST_BLOCK, exporter);
        savePolished(STELLAR_TILES, STELLAR_SEDIMENT, exporter);

        carpet(exporter, BLESSED_CLOTH_CARPET, BLESSED_CLOTH_BLOCK);
        carpet(exporter, BLESSED_CLOTH_CARPET_PADDING, BLESSED_CLOTH_PADDING);

        twoByTwoPacker(exporter, RecipeCategory.BUILDING_BLOCKS, CELESTIAL_OPAL_BLOCK, CELESTIAL_OPAL_SHARD);

        savePillar(exporter, SMOOTH_FELLCRUST_PILLAR, SMOOTH_FELLCRUST);
        savePillar(exporter, STARFLAKED_BISMUTH_PILLAR, STARFLAKED_BISMUTH_BLOCK);
        savePillar(exporter, POLISHED_CELESTIAL_OPAL_PILLAR, POLISHED_CELESTIAL_OPAL_BLOCK);
        savePillar(exporter, OURANIC_PILLAR, OURANIC_CHIP_BLOCK);

        twoByTwoPacker(exporter, RecipeCategory.BUILDING_BLOCKS, OURANIC_CHIP_BLOCK, OURANIC_CHIP);

        twoByTwoPacker(exporter, RecipeCategory.BUILDING_BLOCKS, SKYSHELL_BLOCK, SKYSHELL);

        eightCircle(exporter, MUCKY_SINGUT_COIL, MUCKY_SINGUTS, 4);
        twoByTwoPacker(exporter, RecipeCategory.BUILDING_BLOCKS, MUCKY_SINGUT_BLOCK, MUCKY_SINGUT_COIL, 4);
        eightCircle(exporter, CLEANSED_SINGUT_COIL, CLEANSED_SINGUTS, 4);
        twoByTwoPacker(exporter, RecipeCategory.BUILDING_BLOCKS, CLEANSED_SINGUT_BLOCK, CLEANSED_SINGUT_COIL, 4);

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

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, BISMUTH_BLASTER)
                .define('F', BISMUTH_FLAKE)
                .define('B', STARFLAKED_BISMUTH)
                .define('O', OURANIC_CHIP)
                .pattern("  O")
                .pattern("OBB")
                .pattern("  F")
                .unlockedBy(
                        getHasName(OURANIC_CHIP),
                        has(OURANIC_CHIP)
                )
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, BISBLAST_CANISTER, 3)
                .define('F', BISMUTH_FLAKE)
                .define('O', OURANIC_CHIP)
                .pattern("F")
                .pattern("O")
                .pattern("F")
                .unlockedBy(
                        getHasName(OURANIC_CHIP),
                        has(OURANIC_CHIP)
                )
                .save(exporter);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, HOLY_LEAF_PLATFORM, 6)
                .define('L', STARBLEACHED_LEAF_BUNCH)
                .define('H', HOLY_STRANDS)
                .define('S', STARBLEACHED_TILES)
                .pattern("LLL")
                .pattern("HHH")
                .pattern("S S")
                .unlockedBy(
                        getHasName(STARBLEACHED_LEAF_BUNCH),
                        has(STARBLEACHED_LEAF_BUNCH)
                )
                .save(exporter);
        // endregion

        // region smelting
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(FELLCRUST), RecipeCategory.BUILDING_BLOCKS, SMOOTH_FELLCRUST.asItem(), 0.1F, 200)
                .unlockedBy(getHasName(FELLCRUST), has(FELLCRUST))
                .save(exporter);

        SimpleCookingRecipeBuilder.smelting(Ingredient.of(COBBLED_FELLCRUST), RecipeCategory.BUILDING_BLOCKS, FELLCRUST.asItem(), 0.1F, 200)
                .unlockedBy(getHasName(COBBLED_FELLCRUST), has(COBBLED_FELLCRUST))
                .save(exporter);
        // endregion

        // region stonecutting
        // stardust bricks
        scBlocks(exporter, STARDUST_BRICK_STAIRS, 1, STARDUST_BRICKS);
        scBlocks(exporter, STARDUST_BRICK_SLAB, 2, STARDUST_BRICKS);
        scWalls(exporter, STARDUST_BRICK_WALL, 1, STARDUST_BRICKS);

        // fellcrust
        scBlocks(exporter, FELLCRUST_STAIRS, 1, FELLCRUST);
        scBlocks(exporter, FELLCRUST_SLAB, 2, FELLCRUST);
        scWalls(exporter, FELLCRUST_WALL, 1, FELLCRUST);
        scBlocks(exporter, CHISELED_FELLCRUST, 1, FELLCRUST);

        // cut fellcrust
        scBlocks(exporter, CUT_FELLCRUST, 1, FELLCRUST);
        scBlocks(exporter, CUT_FELLCRUST_STAIRS, 1, CUT_FELLCRUST, FELLCRUST);
        scBlocks(exporter, CUT_FELLCRUST_SLAB, 2, CUT_FELLCRUST, FELLCRUST);
        scWalls(exporter, CUT_FELLCRUST_WALL, 1, CUT_FELLCRUST, FELLCRUST);

        // smooth fellcrust
        scBlocks(exporter, SMOOTH_FELLCRUST_STAIRS, 1, SMOOTH_FELLCRUST);
        scBlocks(exporter, SMOOTH_FELLCRUST_SLAB, 2, SMOOTH_FELLCRUST);
        scWalls(exporter, SMOOTH_FELLCRUST_WALL, 1, SMOOTH_FELLCRUST);
        scBlocks(exporter, CHISELED_SMOOTH_FELLCRUST, 1, SMOOTH_FELLCRUST);
        scBlocks(exporter, SMOOTH_FELLCRUST_PILLAR, 1, SMOOTH_FELLCRUST);

        // smooth fellcrust bricks
        scBlocks(exporter, SMOOTH_FELLCRUST_BRICKS, 1, SMOOTH_FELLCRUST);
        scBlocks(exporter, SMOOTH_FELLCRUST_BRICK_STAIRS, 1, SMOOTH_FELLCRUST_BRICKS, SMOOTH_FELLCRUST);
        scBlocks(exporter, SMOOTH_FELLCRUST_BRICK_SLAB, 2, SMOOTH_FELLCRUST_BRICKS, SMOOTH_FELLCRUST);
        scWalls(exporter, SMOOTH_FELLCRUST_BRICK_WALL, 1, SMOOTH_FELLCRUST_BRICKS, SMOOTH_FELLCRUST);

        // cobbled fellcrust
        scBlocks(exporter, COBBLED_FELLCRUST, 1, FELLCRUST);
        scBlocks(exporter, COBBLED_FELLCRUST_STAIRS, 1, COBBLED_FELLCRUST, FELLCRUST);
        scBlocks(exporter, COBBLED_FELLCRUST_SLAB, 2, COBBLED_FELLCRUST, FELLCRUST);
        scWalls(exporter, COBBLED_FELLCRUST_WALL, 1, COBBLED_FELLCRUST, FELLCRUST);

        // polished fellcrust
        scBlocks(exporter, POLISHED_FELLCRUST, 1, COBBLED_FELLCRUST, FELLCRUST);
        scBlocks(exporter, POLISHED_FELLCRUST_STAIRS, 1, POLISHED_FELLCRUST, COBBLED_FELLCRUST, FELLCRUST);
        scBlocks(exporter, POLISHED_FELLCRUST_SLAB, 2, POLISHED_FELLCRUST, COBBLED_FELLCRUST, FELLCRUST);

        // polished fellcrust bricks
        scBlocks(exporter, POLISHED_FELLCRUST_BRICKS, 1, POLISHED_FELLCRUST, COBBLED_FELLCRUST, FELLCRUST);
        scBlocks(exporter, POLISHED_FELLCRUST_BRICK_STAIRS, 1, POLISHED_FELLCRUST_BRICKS, POLISHED_FELLCRUST, COBBLED_FELLCRUST, FELLCRUST);
        scBlocks(exporter, POLISHED_FELLCRUST_BRICK_SLAB, 2, POLISHED_FELLCRUST_BRICKS, POLISHED_FELLCRUST, COBBLED_FELLCRUST, FELLCRUST);
        scWalls(exporter, POLISHED_FELLCRUST_BRICK_WALL, 1, POLISHED_FELLCRUST_BRICKS, POLISHED_FELLCRUST, COBBLED_FELLCRUST, FELLCRUST);

        // cut polished fellcrust
        scBlocks(exporter, CUT_POLISHED_FELLCRUST, 1, POLISHED_FELLCRUST_BRICKS, POLISHED_FELLCRUST, COBBLED_FELLCRUST, FELLCRUST);
        scBlocks(exporter, CUT_POLISHED_FELLCRUST_STAIRS, 1, CUT_POLISHED_FELLCRUST, POLISHED_FELLCRUST_BRICKS, POLISHED_FELLCRUST, COBBLED_FELLCRUST, FELLCRUST);
        scBlocks(exporter, CUT_POLISHED_FELLCRUST_SLAB, 2, CUT_POLISHED_FELLCRUST, POLISHED_FELLCRUST_BRICKS, POLISHED_FELLCRUST, COBBLED_FELLCRUST, FELLCRUST);

        // skyshell
        scBlocks(exporter, SKYSHELL_STAIRS, 1, SKYSHELL_BLOCK);
        scBlocks(exporter, SKYSHELL_SLAB, 2, SKYSHELL_BLOCK);
        scWalls(exporter, SKYSHELL_WALL, 1, SKYSHELL_BLOCK);

        // 1:1 wood:log recipe, slightly better than the normal 3:4 wood:log crafting recipe
        scBlocks(exporter, STARBLEACHED_WOOD, 1, STARBLEACHED_LOG);
        // starbleached tiles (from log/wood)
        scBlocks(exporter, STARBLEACHED_TILES, 4, STARBLEACHED_LOG, STARBLEACHED_WOOD);
        scBlocks(exporter, STARBLEACHED_TILE_STAIRS, 4, STARBLEACHED_LOG, STARBLEACHED_WOOD);
        scBlocks(exporter, STARBLEACHED_TILE_SLAB, 8, STARBLEACHED_LOG, STARBLEACHED_WOOD);
        scWalls(exporter, STARBLEACHED_TILE_WALL, 4, STARBLEACHED_LOG, STARBLEACHED_WOOD);
        scBlocks(exporter, CHISELED_STARBLEACHED_TILES, 4, STARBLEACHED_LOG, STARBLEACHED_WOOD);
        // starbleached tiles
        scBlocks(exporter, STARBLEACHED_TILE_STAIRS, 1, STARBLEACHED_TILES);
        scBlocks(exporter, STARBLEACHED_TILE_SLAB, 2, STARBLEACHED_TILES);
        scWalls(exporter, STARBLEACHED_TILE_WALL, 1, STARBLEACHED_TILES);
        scBlocks(exporter, CHISELED_STARBLEACHED_TILES, 1, STARBLEACHED_TILES);

        // stellar tiles
        scBlocks(exporter, STELLAR_TILE_SLAB, 2, STELLAR_TILES);

        // ouranic chip blocks
        scBlocks(exporter, OURANIC_CHIP_STAIRS, 1, OURANIC_CHIP_BLOCK);
        scBlocks(exporter, OURANIC_CHIP_SLAB, 2, OURANIC_CHIP_BLOCK);
        scWalls(exporter, OURANIC_CHIP_WALL, 1, OURANIC_CHIP_BLOCK);

        scBlocks(exporter, CHISELED_OURANIC_CHIP_BLOCK, 1, OURANIC_CHIP_BLOCK);
        scBlocks(exporter, OURANIC_PILLAR, 1, OURANIC_CHIP_BLOCK);

        // ouranic bricks
        scBlocks(exporter, OURANIC_BRICKS, 1, OURANIC_CHIP_BLOCK);

        scBlocks(exporter, OURANIC_BRICK_STAIRS, 1, OURANIC_BRICKS, OURANIC_CHIP_BLOCK);
        scBlocks(exporter, OURANIC_BRICK_SLAB, 2, OURANIC_BRICKS, OURANIC_CHIP_BLOCK);
        scWalls(exporter, OURANIC_BRICK_WALL, 1, OURANIC_BRICKS, OURANIC_CHIP_BLOCK);
        // endregion

        // region smithing
        SmithingTransformRecipeBuilder.smithing(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), Ingredient.of(Items.CARVED_PUMPKIN), Ingredient.of(Items.NETHERITE_INGOT), RecipeCategory.TOOLS, NETHERITE_PUMPKIN)
                .unlocks(
                        getHasName(Items.NETHERITE_INGOT),
                        has(Items.NETHERITE_INGOT)
                )
                .save(exporter, OperationStarcleave.id("netherite_pumpkin_smithing"));
        // endregion

        // region starbleaching
        saveStarbleachRecipe(exporter,
                Ingredient.of(Items.GLASS_BOTTLE),
                1,
                new ItemStack(STARBLEACH_BOTTLE),
                true
        );
        saveStarbleachRecipe(exporter,
                Ingredient.of(Items.BUCKET),
                4,
                new ItemStack(STARBLEACH_BUCKET),
                true
        );

        saveStarbleachRecipe(exporter,
                Items.ENDER_PEARL,
                1,
                STARBLEACHED_PEARL
        );
        saveStarbleachRecipe(exporter,
                Items.CHORUS_FRUIT,
                1,
                STARFRUIT
        );
        saveStarbleachRecipe(exporter,
                Items.INK_SAC,
                1,
                HOLLOWED_SAC
        );
        saveStarbleachRecipe(exporter,
                MUCKY_SINGUTS,
                1,
                CLEANSED_SINGUTS
        );
        saveStarbleachRecipe(exporter,
                MUCKY_SINGUT_COIL,
                2,
                CLEANSED_SINGUT_COIL
        );
        saveStarbleachRecipe(exporter,
                MUCKY_SINGUT_BLOCK,
                2,
                CLEANSED_SINGUT_BLOCK
        );

        saveStarbleachRecipe(exporter,
                Items.GRASS_BLOCK,
                0.125F,
                HOLY_MOSS
        );
        saveStarbleachRecipe(exporter,
                Items.PODZOL,
                0.125F,
                STELLAR_MULCH
        );

        saveStarbleachRecipe(exporter,
                Items.SHORT_GRASS,
                0.1F,
                SHORT_HOLY_MOSS
        );
        saveStarbleachRecipe(exporter,
                Items.TALL_GRASS,
                0.2F,
                TALL_HOLY_MOSS
        );

        saveStarbleachRecipe(exporter,
                Items.DIRT,
                0.04F,
                STELLAR_SEDIMENT
        );
        saveStarbleachRecipe(exporter,
                Items.FARMLAND,
                0.04F,
                STELLAR_FARMLAND
        );
        saveStarbleachRecipe(exporter,
                Items.DIRT_PATH,
                0.04F,
                STELLAR_PATH
        );
        saveStarbleachRecipe(exporter,
                Items.GRAVEL,
                0.04F,
                ASTERUBBLE
        );

        saveStarbleachRecipe(exporter,
                Items.BUDDING_AMETHYST,
                3,
                BUDDING_CELESTIAL_OPAL
        );
        saveStarbleachRecipe(exporter,
                Items.AMETHYST_BLOCK,
                1,
                CELESTIAL_OPAL_BLOCK
        );
        saveStarbleachRecipe(exporter,
                Items.SMALL_AMETHYST_BUD,
                0.1F,
                SMALL_CELESTIAL_OPAL_BUD
        );
        saveStarbleachRecipe(exporter,
                Items.MEDIUM_AMETHYST_BUD,
                0.2F,
                MEDIUM_CELESTIAL_OPAL_BUD
        );
        saveStarbleachRecipe(exporter,
                Items.LARGE_AMETHYST_BUD,
                0.3F,
                LARGE_CELESTIAL_OPAL_BUD
        );
        saveStarbleachRecipe(exporter,
                Items.AMETHYST_CLUSTER,
                0.4F,
                CELESTIAL_OPAL_CLUSTER
        );
        saveStarbleachRecipe(exporter,
                Items.AMETHYST_SHARD,
                0.7F,
                CELESTIAL_OPAL_SHARD
        );

        saveStarbleachRecipe(exporter,
                OperationStarcleaveItemTags.SB_I_FELLCRUST,
                0.04F,
                FELLCRUST
        );
        saveStarbleachRecipe(exporter,
                OperationStarcleaveItemTags.SB_I_FELLCRUST_STAIRS,
                0.04F,
                FELLCRUST_STAIRS
        );
        saveStarbleachRecipe(exporter,
                OperationStarcleaveItemTags.SB_I_FELLCRUST_SLAB,
                0.02F,
                FELLCRUST_SLAB
        );
        saveStarbleachRecipe(exporter,
                OperationStarcleaveItemTags.SB_I_FELLCRUST_WALL,
                0.04F,
                FELLCRUST_WALL
        );

        saveStarbleachRecipe(exporter,
                OperationStarcleaveItemTags.SB_I_CHISELED_FELLCRUST,
                0.04F,
                CHISELED_FELLCRUST
        );

        saveStarbleachRecipe(exporter,
                OperationStarcleaveItemTags.SB_I_SMOOTH_FELLCRUST,
                0.04F,
                SMOOTH_FELLCRUST
        );
        saveStarbleachRecipe(exporter,
                OperationStarcleaveItemTags.SB_I_SMOOTH_FELLCRUST_STAIRS,
                0.04F,
                SMOOTH_FELLCRUST_STAIRS
        );
        saveStarbleachRecipe(exporter,
                OperationStarcleaveItemTags.SB_I_SMOOTH_FELLCRUST_SLAB,
                0.04F,
                SMOOTH_FELLCRUST_SLAB
        );

        saveStarbleachRecipe(exporter,
                OperationStarcleaveItemTags.SB_I_CUT_FELLCRUST,
                0.04F,
                CUT_FELLCRUST
        );
        saveStarbleachRecipe(exporter,
                OperationStarcleaveItemTags.SB_I_CUT_FELLCRUST_SLAB,
                0.04F,
                CUT_FELLCRUST_SLAB
        );

        saveStarbleachRecipe(exporter,
                Items.COBBLESTONE,
                0.04F,
                SKYSHELL_BLOCK
        );
        saveStarbleachRecipe(exporter,
                Items.COBBLESTONE_STAIRS,
                0.04F,
                SKYSHELL_STAIRS
        );
        saveStarbleachRecipe(exporter,
                Items.COBBLESTONE_SLAB,
                0.02F,
                SKYSHELL_SLAB
        );
        saveStarbleachRecipe(exporter,
                Items.COBBLESTONE_WALL,
                0.04F,
                SKYSHELL_WALL
        );

        saveStarbleachRecipe(exporter,
                OperationStarcleaveItemTags.SB_I_STARTOUCHED_TORCH,
                0.02F,
                STARTOUCHED_TORCH
        );
        // endregion
    }

    private static void savePolished(ItemLike polished, ItemLike material, RecipeOutput exporter) {
        polished(exporter, RecipeCategory.BUILDING_BLOCKS, polished, material);
    }

    private static void simpleShapeless(RecipeOutput recipeOutput, RecipeCategory category, ItemLike input, ItemLike output, int count) {
        simpleShapeless(recipeOutput, category, input, output, count, RecipeBuilder.getDefaultRecipeId(output));
    }

    private static void simpleShapelessWithSuffix(RecipeOutput recipeOutput, RecipeCategory category, ItemLike input, ItemLike output, int count, String suffix) {
        simpleShapeless(recipeOutput, category, input, output, count, RecipeBuilder.getDefaultRecipeId(output).withSuffix(suffix));
    }

    private static void simpleShapeless(RecipeOutput recipeOutput, RecipeCategory category, ItemLike input, ItemLike output, int count, ResourceLocation recipeId) {
        ShapelessRecipeBuilder.shapeless(category, output, count)
                .requires(input)
                .unlockedBy(getHasName(input), has(input))
                .save(recipeOutput, recipeId);
    }

    private static void twoByTwoPacker(RecipeOutput recipeOutput, RecipeCategory category, ItemLike packed, ItemLike unpacked, int count) {
        ShapedRecipeBuilder.shaped(category, packed, count)
                .define('#', unpacked)
                .pattern("##")
                .pattern("##")
                .unlockedBy(getHasName(unpacked), has(unpacked))
                .save(recipeOutput);
    }

    private static void eightCircle(RecipeOutput recipeOutput, ItemLike grateBlock, ItemLike material, int count) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, grateBlock, count)
                .define('#', material)
                .pattern("###")
                .pattern("# #")
                .pattern("###")
                .unlockedBy(getHasName(material), has(material))
                .save(recipeOutput);
    }

    protected static void savePillar(RecipeOutput exporter, ItemLike pillarBlock, ItemLike baseBlock) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, pillarBlock, 2)
                .define('#', baseBlock)
                .pattern("#")
                .pattern("#")
                .unlockedBy(
                        getHasName(baseBlock),
                        has(baseBlock))
                .save(exporter);
    }

    private static void scBlocks(RecipeOutput recipeOutput, ItemLike result, int amount, ItemLike... materials) {
        for (ItemLike material : materials) {
            scBuildingBlock(recipeOutput, result, material, amount);
        }
    }

    private static void scWalls(RecipeOutput recipeOutput, ItemLike result, int amount, ItemLike... materials) {
        for (ItemLike material : materials) {
            scDecoration(recipeOutput, result, material, amount);
        }
    }

    private static void scBuildingBlock(RecipeOutput recipeOutput, ItemLike result, ItemLike material, int amount) {
        // use this for non-walls
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, result, material, amount);
    }

    private static void scDecoration(RecipeOutput recipeOutput, ItemLike result, ItemLike material, int amount) {
        // use this for walls
        stonecutterResultFromBase(recipeOutput, RecipeCategory.DECORATIONS, result, material, amount);
    }

    public static void saveStarbleachRecipe(RecipeOutput recipeOutput, TagKey<Item> input, float starbleachCost, Item output) {
        saveStarbleachRecipe(recipeOutput, Ingredient.of(input), starbleachCost, new ItemStack(output));
    }

    public static void saveStarbleachRecipe(RecipeOutput recipeOutput, Item input, float starbleachCost, Item output) {
        saveStarbleachRecipe(recipeOutput, Ingredient.of(input), starbleachCost, new ItemStack(output));
    }

    public static void saveStarbleachRecipe(RecipeOutput recipeOutput, Ingredient input, float starbleachCost, ItemStack output) {
        saveStarbleachRecipe(recipeOutput, input, starbleachCost, output, false);
    }

    public static void saveStarbleachRecipe(RecipeOutput recipeOutput, Ingredient input, float starbleachCost, ItemStack output, boolean isFillingRecipe) {
        Item outputItem = output.getItem();
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(outputItem);

        saveStarbleachRecipe(recipeOutput, OperationStarcleave.id(key.getPath()).withPrefix("item_starbleaching/"), input, starbleachCost, output, isFillingRecipe);
    }

    public static void saveStarbleachRecipe(RecipeOutput recipeOutput, ResourceLocation location, Ingredient input, float starbleachCost, ItemStack output, boolean isFillingRecipe) {
        recipeOutput.accept(
                location,
                new ItemStarbleachingRecipe(
                        input,
                        starbleachCost,
                        output,
                        isFillingRecipe
                ),
                null);
    }
}

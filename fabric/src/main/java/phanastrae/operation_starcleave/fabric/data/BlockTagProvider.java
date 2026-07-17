package phanastrae.operation_starcleave.fabric.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamily;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.block.tag.OperationStarcleaveBlockTags;
import phanastrae.operation_starcleave.data.OperationStarcleaveBlockFamilies;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static phanastrae.operation_starcleave.block.OperationStarcleaveBlocks.*;


public class BlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public BlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        addTagsForFamilies(false, true,
                OperationStarcleaveBlockFamilies.STARDUST_BRICKS,

                OperationStarcleaveBlockFamilies.FELLCRUST,
                OperationStarcleaveBlockFamilies.CUT_FELLCRUST,
                OperationStarcleaveBlockFamilies.SMOOTH_FELLCRUST,
                OperationStarcleaveBlockFamilies.SMOOTH_FELLCRUST_BRICKS,
                OperationStarcleaveBlockFamilies.COBBLED_FELLCRUST,
                OperationStarcleaveBlockFamilies.POLISHED_FELLCRUST,
                OperationStarcleaveBlockFamilies.POLISHED_FELLCRUST_BRICKS,
                OperationStarcleaveBlockFamilies.CUT_POLISHED_FELLCRUST,

                OperationStarcleaveBlockFamilies.ASTERUBBLE,

                OperationStarcleaveBlockFamilies.SKYSHELL,

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
        addTagsForFamilies(true, false,
                OperationStarcleaveBlockFamilies.STARTOUCHED_PLANKS
        );

        addFamiliesToTag(BlockTags.MINEABLE_WITH_PICKAXE,
                OperationStarcleaveBlockFamilies.STARDUST_BRICKS,

                OperationStarcleaveBlockFamilies.FELLCRUST,
                OperationStarcleaveBlockFamilies.CUT_FELLCRUST,
                OperationStarcleaveBlockFamilies.SMOOTH_FELLCRUST,
                OperationStarcleaveBlockFamilies.SMOOTH_FELLCRUST_BRICKS,
                OperationStarcleaveBlockFamilies.COBBLED_FELLCRUST,
                OperationStarcleaveBlockFamilies.POLISHED_FELLCRUST,
                OperationStarcleaveBlockFamilies.POLISHED_FELLCRUST_BRICKS,
                OperationStarcleaveBlockFamilies.CUT_POLISHED_FELLCRUST,

                OperationStarcleaveBlockFamilies.ASTERUBBLE,

                OperationStarcleaveBlockFamilies.SKYSHELL,

                OperationStarcleaveBlockFamilies.STARBLEACHED_TILES,
                OperationStarcleaveBlockFamilies.STELLAR_TILES,

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
        addFamiliesToTag(BlockTags.MINEABLE_WITH_SHOVEL,
                OperationStarcleaveBlockFamilies.STARDUST_BRICKS,
                OperationStarcleaveBlockFamilies.STELLAR_TILES
        );
        addFamiliesToTag(BlockTags.MINEABLE_WITH_AXE,
                OperationStarcleaveBlockFamilies.STARTOUCHED_PLANKS,
                OperationStarcleaveBlockFamilies.OURANIC_CHIP_BLOCK,
                OperationStarcleaveBlockFamilies.OURANIC_BRICKS
        );
        addFamiliesToTag(BlockTags.NEEDS_STONE_TOOL,
                OperationStarcleaveBlockFamilies.STARFLAKED_BISMUTH_BLOCK,
                OperationStarcleaveBlockFamilies.STARFLAKED_BISMUTH_BRICKS,
                OperationStarcleaveBlockFamilies.STARFLAKED_BISMUTH_TILES,
                OperationStarcleaveBlockFamilies.STARFLAKED_BISMUTH_MOSAIC
        );
        addFamiliesToTag(BlockTags.NEEDS_IRON_TOOL,
                OperationStarcleaveBlockFamilies.ASTERUBBLE,

                OperationStarcleaveBlockFamilies.OURANIC_CHIP_BLOCK,
                OperationStarcleaveBlockFamilies.OURANIC_BRICKS
        );
        addFamiliesToTag(BlockTags.CRYSTAL_SOUND_BLOCKS,
                OperationStarcleaveBlockFamilies.CELESTIAL_OPAL_BLOCK,
                OperationStarcleaveBlockFamilies.POLISHED_CELESTIAL_OPAL_BLOCK,
                OperationStarcleaveBlockFamilies.POLISHED_CELESTIAL_OPAL_BRICKS
        );

        // vanilla tags
        getOrCreateTagBuilder(BlockTags.WOOL)
                .add(
                        BLESSED_CLOTH_BLOCK,
                        BLESSED_CLOTH_PADDING
                );

        getOrCreateTagBuilder(BlockTags.WOOL_CARPETS)
                .add(
                        BLESSED_CLOTH_CARPET,
                        BLESSED_CLOTH_CARPET_PADDING
                );

        getOrCreateTagBuilder(BlockTags.LOGS)
                .addOptionalTag(OperationStarcleaveBlockTags.STARTOUCHED_LOGS)
                .addOptionalTag(OperationStarcleaveBlockTags.NUCLEIC_FISSUREROOTS);

        getOrCreateTagBuilder(BlockTags.LEAVES)
                .add(
                        STARBLEACHED_LEAVES,
                        NUCLEIC_FISSURELEAVES
                );

        getOrCreateTagBuilder(BlockTags.BEDS)
                .add(
                        BLESSED_BED
                );

        getOrCreateTagBuilder(BlockTags.CROPS)
                .add(
                        BISREEDS
                );

        getOrCreateTagBuilder(BlockTags.FIRE)
                .add(
                        PHLOGISTIC_FIRE
                );

        getOrCreateTagBuilder(BlockTags.CAULDRONS)
                .add(
                        STARBLEACH_CAULDRON
                );

        getOrCreateTagBuilder(BlockTags.INSIDE_STEP_SOUND_BLOCKS)
                .add(
                        STARBLEACHED_LEAF_LITTER
                );

        getOrCreateTagBuilder(BlockTags.COMBINATION_STEP_SOUND_BLOCKS)
                .add(
                        BLESSED_CLOTH_CARPET,
                        BLESSED_CLOTH_CARPET_PADDING
                );

        getOrCreateTagBuilder(BlockTags.OCCLUDES_VIBRATION_SIGNALS)
                .add(
                        BLESSED_CLOTH_BLOCK,
                        BLESSED_CLOTH_STAIRS,
                        BLESSED_CLOTH_SLAB,

                        BLESSED_CLOTH_PADDING,
                        BLESSED_CLOTH_PADDING_STAIRS,
                        BLESSED_CLOTH_PADDING_SLAB
                );

        getOrCreateTagBuilder(BlockTags.DAMPENS_VIBRATIONS)
                .add(
                        BLESSED_CLOTH_BLOCK,
                        BLESSED_CLOTH_STAIRS,
                        BLESSED_CLOTH_SLAB,
                        BLESSED_CLOTH_CARPET,

                        BLESSED_CLOTH_PADDING,
                        BLESSED_CLOTH_PADDING,
                        BLESSED_CLOTH_PADDING_STAIRS,
                        BLESSED_CLOTH_CARPET_PADDING,

                        BLESSED_CLOTH_CURTAIN
                );

        getOrCreateTagBuilder(BlockTags.BIG_DRIPLEAF_PLACEABLE)
                .add(
                        STELLAR_FARMLAND
                );

        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_AXE)
                .add(
                        NETHERITE_PUMPKIN,

                        MULCHBORNE_TUFT,
                        SHORT_HOLY_MOSS,
                        TALL_HOLY_MOSS,

                        BISREEDS,
                        NUCLEOSYNTHESEED,

                        OURANIC_PILLAR
                );

        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_HOE)
                .add(
                        STARBLEACHED_LEAVES,
                        NUCLEIC_FISSURELEAVES,

                        STARBLEACHED_LEAF_BUNCH_BLOCK,

                        MUCKY_SINGUT_COIL,
                        MUCKY_SINGUT_BLOCK,
                        CLEANSED_SINGUT_COIL,
                        CLEANSED_SINGUT_BLOCK
                );

        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_PICKAXE)
                .addOptionalTag(OperationStarcleaveBlockTags.STARBLEACHED_LOGS)
                .add(
                        NETHERITE_PUMPKIN,

                        SMOOTH_FELLCRUST_PILLAR,

                        IMBUED_STARBLEACHED_TILES,

                        STARBLEACH_CAULDRON,
                        STARBLEACHED_PEARL_BLOCK,

                        STELLAR_REPULSOR,

                        HOLY_LEAF_PLATFORM,

                        COAGULATED_PLASMA,
                        PLASMA_ICE,

                        STARFLAKED_BISMUTH_PILLAR,
                        STARFLAKED_BISMUTH_DOOR,
                        STARFLAKED_BISMUTH_TRAPDOOR,

                        BUDDING_CELESTIAL_OPAL,

                        CELESTIAL_OPAL_SPIRE,
                        CELESTIAL_OPAL_CLUSTER,
                        LARGE_CELESTIAL_OPAL_BUD,
                        MEDIUM_CELESTIAL_OPAL_BUD,
                        SMALL_CELESTIAL_OPAL_BUD,

                        POLISHED_CELESTIAL_OPAL_PILLAR,

                        OURANIC_PILLAR
                );

        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(
                        STELLAR_SEDIMENT,
                        STELLARUBBLE_MIX,
                        STELLAR_PATH,
                        STELLAR_MULCH,
                        STELLAR_FARMLAND,

                        HOLY_MOSS,

                        STELLAR_REPULSOR,

                        STARDUST_BLOCK,

                        COAGULATED_PLASMA,

                        MUCKY_SINGUT_COIL,
                        MUCKY_SINGUT_BLOCK,
                        CLEANSED_SINGUT_COIL,
                        CLEANSED_SINGUT_BLOCK
                );

        getOrCreateTagBuilder(BlockTags.SWORD_EFFICIENT)
                .add(
                        NETHERITE_PUMPKIN,
                        MULCHBORNE_TUFT,
                        SHORT_HOLY_MOSS,
                        TALL_HOLY_MOSS,

                        MUCKY_SINGUT_COIL,
                        MUCKY_SINGUT_BLOCK,
                        CLEANSED_SINGUT_COIL,
                        CLEANSED_SINGUT_BLOCK
                );

        getOrCreateTagBuilder(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(
                        NETHERITE_PUMPKIN
                );

        getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL)
                .addOptionalTag(OperationStarcleaveBlockTags.NUCLEIC_FISSUREROOTS)
                .add(
                        NUCLEOSYNTHESEED,

                        OURANIC_BRICKS,
                        OURANIC_PILLAR
                );

        getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL)
                .add(
                        COAGULATED_PLASMA,

                        STARFLAKED_BISMUTH_PILLAR,
                        STARFLAKED_BISMUTH_DOOR,
                        STARFLAKED_BISMUTH_TRAPDOOR
                );

        getOrCreateTagBuilder(BlockTags.REPLACEABLE_BY_TREES)
                .add(
                        MULCHBORNE_TUFT,
                        SHORT_HOLY_MOSS,
                        TALL_HOLY_MOSS,

                        STARBLEACHED_LEAF_LITTER
                );

        getOrCreateTagBuilder(BlockTags.REPLACEABLE)
                .add(
                        STARDUST_CLUSTER,
                        MULCHBORNE_TUFT,
                        SHORT_HOLY_MOSS,
                        TALL_HOLY_MOSS,

                        STARBLEACHED_LEAF_LITTER,

                        PHLOGISTIC_FIRE
                );

        getOrCreateTagBuilder(BlockTags.ENCHANTMENT_POWER_TRANSMITTER)
                .add(
                        STARDUST_CLUSTER,
                        MULCHBORNE_TUFT,
                        SHORT_HOLY_MOSS,
                        PHLOGISTIC_FIRE
                );

        getOrCreateTagBuilder(BlockTags.MAINTAINS_FARMLAND)
                .add(
                        BISREEDS
                );

        getOrCreateTagBuilder(BlockTags.SMALL_FLOWERS)
                .add(
                        GREAT_TREES_CARE,
                        RED_MOURNER,
                        ANGELCLAW,
                        ELDROSE,
                        WITCHGLARE,
                        BLUE_DREAMER,
                        DRAGONS_MAW
                );

        getOrCreateTagBuilder(BlockTags.FLOWER_POTS)
                .add(
                        POTTED_STARBLEACHED_SAPLING,

                        POTTED_MULCHBORNE_TUFT,
                        POTTED_SHORT_HOLY_MOSS,

                        POTTED_GREAT_TREES_CARE,
                        POTTED_RED_MOURNER,
                        POTTED_ANGELCLAW,
                        POTTED_ELDROSE,
                        POTTED_WITCHGLARE,
                        POTTED_BLUE_DREAMER,
                        POTTED_DRAGONS_MAW
                );

        getOrCreateTagBuilder(BlockTags.DOORS)
                .add(
                        STARFLAKED_BISMUTH_DOOR
                );

        getOrCreateTagBuilder(BlockTags.TRAPDOORS)
                .add(
                        STARFLAKED_BISMUTH_TRAPDOOR
                );

        getOrCreateTagBuilder(BlockTags.CRYSTAL_SOUND_BLOCKS)
                .add(
                        BUDDING_CELESTIAL_OPAL,
                        POLISHED_CELESTIAL_OPAL_PILLAR
                );

        getOrCreateTagBuilder(BlockTags.SAPLINGS)
                .add(
                        STARBLEACHED_SAPLING
                );

        getOrCreateTagBuilder(BlockTags.PLANKS)
                .add(
                        STARTOUCHED_PLANKS
                );

        getOrCreateTagBuilder(BlockTags.STANDING_SIGNS)
                .add(
                        STARTOUCHED_SIGN
                );

        getOrCreateTagBuilder(BlockTags.WALL_SIGNS)
                .add(
                        STARTOUCHED_WALL_SIGN
                );

        getOrCreateTagBuilder(BlockTags.CEILING_HANGING_SIGNS)
                .add(
                        STARTOUCHED_HANGING_SIGN
                );

        getOrCreateTagBuilder(BlockTags.WALL_HANGING_SIGNS)
                .add(
                        STARTOUCHED_WALL_HANGING_SIGN
                );

        getOrCreateTagBuilder(BlockTags.WALL_POST_OVERRIDE)
                .add(
                        STARTOUCHED_TORCH
                );

        // conventional
        getOrCreateTagBuilder(ConventionalBlockTags.STRIPPED_LOGS)
                .add(
                        STARTOUCHED_LOG,
                        STRIPED_NUCLEIC_FISSUREROOT
                );
        getOrCreateTagBuilder(ConventionalBlockTags.STRIPPED_WOODS)
                .add(
                        STARTOUCHED_WOOD,
                        STRIPED_NUCLEIC_FISSURERIND
                );
        getOrCreateTagBuilder(ConventionalBlockTags.BUDDING_BLOCKS)
                .add(
                        BUDDING_CELESTIAL_OPAL
                );
        getOrCreateTagBuilder(ConventionalBlockTags.CLUSTERS)
                .add(
                        CELESTIAL_OPAL_CLUSTER,
                        CELESTIAL_OPAL_SPIRE
                );
        getOrCreateTagBuilder(ConventionalBlockTags.BUDS)
                .add(
                        LARGE_CELESTIAL_OPAL_BUD,
                        MEDIUM_CELESTIAL_OPAL_BUD,
                        SMALL_CELESTIAL_OPAL_BUD
                );


        getOrCreateTagBuilder(ConventionalBlockTags.VILLAGER_JOB_SITES)
                .add(
                        STARBLEACH_CAULDRON
                );

        // starcleave tags
        // block-only tags
        addFamiliesToTag(OperationStarcleaveBlockTags.STARBLEACHED,
                OperationStarcleaveBlockFamilies.STARTOUCHED_PLANKS,
                OperationStarcleaveBlockFamilies.ASTERUBBLE
        );
        addFamiliesToTag(OperationStarcleaveBlockTags.STARBLEACH_IMMUNE,
                OperationStarcleaveBlockFamilies.STARTOUCHED_PLANKS
        );

        getOrCreateTagBuilder(OperationStarcleaveBlockTags.STARBLEACHED)
                .addOptionalTag(OperationStarcleaveBlockTags.STARBLEACHED_LOGS)
                .addOptionalTag(OperationStarcleaveBlockTags.STARTOUCHED_LOGS)
                .add(
                        STELLAR_SEDIMENT,
                        STELLARUBBLE_MIX,
                        STELLAR_PATH,
                        STELLAR_MULCH,
                        STELLAR_FARMLAND,
                        HOLY_MOSS,
                        STARBLEACHED_LEAVES,
                        STARDUST_BLOCK,
                        STARBLEACHED_SAPLING,
                        GREAT_TREES_CARE,
                        RED_MOURNER,
                        ANGELCLAW,
                        ELDROSE,
                        WITCHGLARE,
                        BLUE_DREAMER,
                        DRAGONS_MAW
                );

        getOrCreateTagBuilder(OperationStarcleaveBlockTags.HOLY_MOSS_REPLACEABLE)
                .add(
                        STELLAR_SEDIMENT
                );

        getOrCreateTagBuilder(OperationStarcleaveBlockTags.STELLAR_MULCH_REPLACEABLE)
                .add(
                        STELLAR_SEDIMENT
                );

        getOrCreateTagBuilder(OperationStarcleaveBlockTags.ALLOWS_BISREED_PLANTING)
                .addOptionalTag(OperationStarcleaveBlockTags.STARBLEACHED_LOGS)
                .add(
                        Blocks.FARMLAND,
                        Blocks.MOSS_BLOCK,
                        Blocks.SOUL_SAND,
                        Blocks.END_STONE,
                        Blocks.PRISMARINE,
                        STELLAR_SEDIMENT,
                        STELLARUBBLE_MIX,
                        STELLAR_FARMLAND,
                        STELLAR_MULCH,
                        HOLY_MOSS,
                        ASTERUBBLE,
                        STARDUST_BLOCK
                );

        getOrCreateTagBuilder(OperationStarcleaveBlockTags.STARBLEACHED_SAPLING_PLANTABLE_ON)
                .addOptionalTag(BlockTags.DIRT)
                .add(
                        Blocks.FARMLAND,

                        STELLAR_SEDIMENT,
                        STELLAR_FARMLAND,
                        STELLAR_MULCH,
                        HOLY_MOSS,
                        STARDUST_BLOCK
                );

        getOrCreateTagBuilder(OperationStarcleaveBlockTags.STARBLEACH_IMMUNE)
                .addOptionalTag(OperationStarcleaveBlockTags.STARBLEACHED_LOGS)
                .addOptionalTag(OperationStarcleaveBlockTags.STARTOUCHED_LOGS)
                .addOptionalTag(OperationStarcleaveBlockTags.NUCLEIC_FISSUREROOTS)
                .add(
                        STARBLEACHED_LEAVES,
                        NUCLEIC_FISSURELEAVES,
                        STARBLEACHED_SAPLING
                );

        getOrCreateTagBuilder(OperationStarcleaveBlockTags.PHLOGISTIC_HYPERFLAMMABLES)
                .addOptionalTag(OperationStarcleaveBlockTags.NUCLEIC_FISSUREROOTS)
                .add(
                        NUCLEOSYNTHESEED,
                        NUCLEIC_FISSURELEAVES
                );

        getOrCreateTagBuilder(OperationStarcleaveBlockTags.NUCLEOSYNTHESEED_BLAST_IMMUNE)
                .addOptionalTag(OperationStarcleaveBlockTags.NUCLEIC_FISSUREROOTS)
                .add(
                        NUCLEOSYNTHESEED,
                        PHLOGISTIC_FIRE
                );

        getOrCreateTagBuilder(OperationStarcleaveBlockTags.ALSO_PRESERVES_LEAVES)
                .add(
                        NUCLEOSYNTHESEED
                );

        getOrCreateTagBuilder(OperationStarcleaveBlockTags.PREVENTS_ELYTRA_WALL_DAMAGE)
                .add(
                        BLESSED_CLOTH_BLOCK,
                        BLESSED_CLOTH_STAIRS,
                        BLESSED_CLOTH_SLAB,
                        BLESSED_CLOTH_CARPET,

                        BLESSED_CLOTH_PADDING,
                        BLESSED_CLOTH_PADDING,
                        BLESSED_CLOTH_PADDING_STAIRS,
                        BLESSED_CLOTH_CARPET_PADDING,

                        BLESSED_CLOTH_CURTAIN
                );

        getOrCreateTagBuilder(OperationStarcleaveBlockTags.MINED_QUICKLY_BY_SHEARS)
                .add(
                        STARBLEACHED_LEAVES
                );

        getOrCreateTagBuilder(OperationStarcleaveBlockTags.ASTERUBBLE_BOULDER_REPLACEABLE)
                .addOptionalTag(BlockTags.DIRT)
                .addOptionalTag(BlockTags.BASE_STONE_OVERWORLD)
                .addOptionalTag(BlockTags.SAND)
                .addOptionalTag(BlockTags.LEAVES)
                .add(
                        Blocks.GRAVEL,
                        STELLAR_SEDIMENT,
                        STELLARUBBLE_MIX,
                        STELLAR_MULCH,
                        HOLY_MOSS,
                        STARDUST_BLOCK,
                        STARBLEACHED_LEAVES
                );

        getOrCreateTagBuilder(OperationStarcleaveBlockTags.SB_I_STARTOUCHED_WALL_TORCH)
                .add(
                        Blocks.WALL_TORCH,
                        Blocks.SOUL_WALL_TORCH
                );

        // block tags that are also item tags
        getOrCreateTagBuilder(OperationStarcleaveBlockTags.STARBLEACHED_LOGS)
                .add(
                        STARBLEACHED_LOG,
                        STARBLEACHED_WOOD
                );

        getOrCreateTagBuilder(OperationStarcleaveBlockTags.STARTOUCHED_LOGS)
                .add(
                        STARTOUCHED_LOG,
                        STARTOUCHED_WOOD
                );

        getOrCreateTagBuilder(OperationStarcleaveBlockTags.NUCLEIC_FISSUREROOTS)
                .add(
                        NUCLEIC_FISSUREROOT,
                        NUCLEIC_FISSURERIND,
                        STRIPED_NUCLEIC_FISSUREROOT,
                        STRIPED_NUCLEIC_FISSURERIND
                );

        getOrCreateTagBuilder(OperationStarcleaveBlockTags.SB_I_FELLCRUST)
                .add(
                        Blocks.SANDSTONE,
                        Blocks.RED_SANDSTONE
                );

        getOrCreateTagBuilder(OperationStarcleaveBlockTags.SB_I_FELLCRUST_STAIRS)
                .add(
                        Blocks.SANDSTONE_STAIRS,
                        Blocks.RED_SANDSTONE_STAIRS
                );

        getOrCreateTagBuilder(OperationStarcleaveBlockTags.SB_I_FELLCRUST_SLAB)
                .add(
                        Blocks.SANDSTONE_SLAB,
                        Blocks.RED_SANDSTONE_SLAB
                );

        getOrCreateTagBuilder(OperationStarcleaveBlockTags.SB_I_FELLCRUST_WALL)
                .add(
                        Blocks.SANDSTONE_WALL,
                        Blocks.RED_SANDSTONE_WALL
                );

        getOrCreateTagBuilder(OperationStarcleaveBlockTags.SB_I_CHISELED_FELLCRUST)
                .add(
                        Blocks.CHISELED_SANDSTONE,
                        Blocks.CHISELED_RED_SANDSTONE
                );

        getOrCreateTagBuilder(OperationStarcleaveBlockTags.SB_I_SMOOTH_FELLCRUST)
                .add(
                        Blocks.SMOOTH_SANDSTONE,
                        Blocks.SMOOTH_RED_SANDSTONE
                );

        getOrCreateTagBuilder(OperationStarcleaveBlockTags.SB_I_SMOOTH_FELLCRUST_STAIRS)
                .add(
                        Blocks.SMOOTH_SANDSTONE_STAIRS,
                        Blocks.SMOOTH_RED_SANDSTONE_STAIRS
                );

        getOrCreateTagBuilder(OperationStarcleaveBlockTags.SB_I_SMOOTH_FELLCRUST_SLAB)
                .add(
                        Blocks.SMOOTH_SANDSTONE_SLAB,
                        Blocks.SMOOTH_RED_SANDSTONE_SLAB
                );

        getOrCreateTagBuilder(OperationStarcleaveBlockTags.SB_I_CUT_FELLCRUST)
                .add(
                        Blocks.CUT_SANDSTONE,
                        Blocks.CUT_RED_SANDSTONE
                );

        getOrCreateTagBuilder(OperationStarcleaveBlockTags.SB_I_CUT_FELLCRUST_SLAB)
                .add(
                        Blocks.CUT_SANDSTONE_SLAB,
                        Blocks.CUT_RED_SANDSTONE_SLAB
                );

        getOrCreateTagBuilder(OperationStarcleaveBlockTags.SB_I_STARTOUCHED_TORCH)
                .add(
                        Blocks.TORCH,
                        Blocks.SOUL_TORCH
                );
    }

    private void addTagsForFamilies(boolean isWooden, boolean isStone, BlockFamily... families) {
        for (BlockFamily family : families) {
            addTagsForFamily(family, isWooden, isStone);
        }
    }

    private void addTagsForFamily(BlockFamily family, boolean isWooden, boolean isStone) {
        addBlockToTags(family, BlockFamily.Variant.BUTTON, isWooden, isStone, BlockTags.BUTTONS, BlockTags.WOODEN_BUTTONS, BlockTags.STONE_BUTTONS);
        addBlockToTags(family, BlockFamily.Variant.PRESSURE_PLATE, isWooden, isStone, BlockTags.PRESSURE_PLATES, BlockTags.WOODEN_PRESSURE_PLATES, BlockTags.STONE_PRESSURE_PLATES);

        addBlockToTags(family, BlockFamily.Variant.STAIRS, isWooden, BlockTags.STAIRS, BlockTags.WOODEN_STAIRS);
        addBlockToTags(family, BlockFamily.Variant.SLAB, isWooden, BlockTags.SLABS, BlockTags.WOODEN_SLABS);
        addBlockToTags(family, BlockFamily.Variant.FENCE, isWooden, BlockTags.FENCES, BlockTags.WOODEN_FENCES);
        addBlockToTags(family, BlockFamily.Variant.FENCE, isWooden, ConventionalBlockTags.FENCES, ConventionalBlockTags.WOODEN_FENCES);
        addBlockToTags(family, BlockFamily.Variant.DOOR, isWooden, BlockTags.DOORS, BlockTags.WOODEN_DOORS);
        addBlockToTags(family, BlockFamily.Variant.TRAPDOOR, isWooden, BlockTags.TRAPDOORS, BlockTags.WOODEN_TRAPDOORS);

        addBlockToTags(family, BlockFamily.Variant.FENCE_GATE, BlockTags.FENCE_GATES);
        addBlockToTags(family, BlockFamily.Variant.FENCE_GATE, isWooden, ConventionalBlockTags.FENCE_GATES, ConventionalBlockTags.WOODEN_FENCE_GATES);
        addBlockToTags(family, BlockFamily.Variant.WALL, BlockTags.WALLS);
    }

    private void addBlockToTags(BlockFamily family, BlockFamily.Variant variant, TagKey<Block> tag) {
        Block block = family.get(variant);
        if (block != null) {
            getOrCreateTagBuilder(tag).add(block);
        }
    }

    private void addBlockToTags(BlockFamily family, BlockFamily.Variant variant, boolean isWooden, TagKey<Block> baseTag, TagKey<Block> woodTag) {
        addBlockToTags(family.get(variant), isWooden, baseTag, woodTag);
    }

    private void addBlockToTags(@Nullable Block block, boolean isWooden, TagKey<Block> baseTag, TagKey<Block> woodTag) {
        if (block != null) {
            getOrCreateTagBuilder(baseTag).add(block);
            if (isWooden) {
                getOrCreateTagBuilder(woodTag).add(block);
            }
        }
    }

    private void addBlockToTags(BlockFamily family, BlockFamily.Variant variant, boolean isWooden, boolean isStone, TagKey<Block> baseTag, TagKey<Block> woodTag, TagKey<Block> stoneTag) {
        addBlockToTags(family.get(variant), isWooden, isStone, baseTag, woodTag, stoneTag);
    }

    private void addBlockToTags(@Nullable Block block, boolean isWooden, boolean isStone, TagKey<Block> baseTag, TagKey<Block> woodTag, TagKey<Block> stoneTag) {
        if (block != null) {
            getOrCreateTagBuilder(baseTag).add(block);
            if (isWooden) {
                getOrCreateTagBuilder(woodTag).add(block);
            }
            if (isStone) {
                getOrCreateTagBuilder(stoneTag).add(block);
            }
        }
    }

    private void addFamiliesToTag(TagKey<Block> tag, BlockFamily... families) {
        for (BlockFamily family : families) {
            addFamilyToTag(family, tag);
        }
    }

    private void addFamilyToTag(BlockFamily family, TagKey<Block> tag) {
        FabricTagBuilder builder = getOrCreateTagBuilder(tag);

        // family.getVariants() gives a HashMap, so sort it for consistent ordering
        Collection<Block> blocks = family.getVariants().values();
        List<Block> blocksSorted = blocks.stream().sorted(Comparator.comparing(BuiltInRegistries.BLOCK::getKey)).toList();

        builder.add(family.getBaseBlock());
        for (Block block : blocksSorted) {
            builder.add(block);
        }
    }

    public TagBuilder getOrCreateRawBuilderPublic(TagKey<Block> tag) {
        return this.getOrCreateRawBuilder(tag);
    }
}

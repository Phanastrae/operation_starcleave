package phanastrae.operation_starcleave.fabric.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.BlockFamily;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.block.tag.OperationStarcleaveBlockTags;
import phanastrae.operation_starcleave.data.OperationStarcleaveBlockFamilies;

import java.util.Arrays;
import java.util.Collection;
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
                OperationStarcleaveBlockFamilies.STARBLEACHED_TILES,
                OperationStarcleaveBlockFamilies.STELLAR_TILES,

                OperationStarcleaveBlockFamilies.STARFLAKED_BISMUTH_BLOCK,
                OperationStarcleaveBlockFamilies.STARFLAKED_BISMUTH_BRICKS,
                OperationStarcleaveBlockFamilies.STARFLAKED_BISMUTH_TILES,
                OperationStarcleaveBlockFamilies.STARFLAKED_BISMUTH_MOSAIC,

                OperationStarcleaveBlockFamilies.CELESTIAL_OPAL_BLOCK,
                OperationStarcleaveBlockFamilies.POLISHED_CELESTIAL_OPAL_BLOCK,
                OperationStarcleaveBlockFamilies.POLISHED_CELESTIAL_OPAL_BRICKS
        );

        addFamiliesToTag(BlockTags.MINEABLE_WITH_PICKAXE,
                OperationStarcleaveBlockFamilies.STARDUST_BRICKS,
                OperationStarcleaveBlockFamilies.STARBLEACHED_TILES,
                OperationStarcleaveBlockFamilies.STELLAR_TILES,

                OperationStarcleaveBlockFamilies.STARFLAKED_BISMUTH_BLOCK,
                OperationStarcleaveBlockFamilies.STARFLAKED_BISMUTH_BRICKS,
                OperationStarcleaveBlockFamilies.STARFLAKED_BISMUTH_TILES,
                OperationStarcleaveBlockFamilies.STARFLAKED_BISMUTH_MOSAIC,

                OperationStarcleaveBlockFamilies.CELESTIAL_OPAL_BLOCK,
                OperationStarcleaveBlockFamilies.POLISHED_CELESTIAL_OPAL_BLOCK,
                OperationStarcleaveBlockFamilies.POLISHED_CELESTIAL_OPAL_BRICKS
        );
        addFamiliesToTag(BlockTags.MINEABLE_WITH_SHOVEL,
                OperationStarcleaveBlockFamilies.STARDUST_BRICKS,
                OperationStarcleaveBlockFamilies.STELLAR_TILES
        );
        addFamiliesToTag(BlockTags.NEEDS_STONE_TOOL,
                OperationStarcleaveBlockFamilies.STARFLAKED_BISMUTH_BLOCK,
                OperationStarcleaveBlockFamilies.STARFLAKED_BISMUTH_BRICKS,
                OperationStarcleaveBlockFamilies.STARFLAKED_BISMUTH_TILES,
                OperationStarcleaveBlockFamilies.STARFLAKED_BISMUTH_MOSAIC
        );
        addFamiliesToTag(BlockTags.CRYSTAL_SOUND_BLOCKS,
                OperationStarcleaveBlockFamilies.CELESTIAL_OPAL_BLOCK,
                OperationStarcleaveBlockFamilies.POLISHED_CELESTIAL_OPAL_BLOCK,
                OperationStarcleaveBlockFamilies.POLISHED_CELESTIAL_OPAL_BRICKS
        );

        // vanilla tags
        getOrCreateTagBuilder(BlockTags.WOOL)
                .add(
                        BLESSED_CLOTH_BLOCK
                );

        getOrCreateTagBuilder(BlockTags.WOOL_CARPETS)
                .add(
                        BLESSED_CLOTH_CARPET
                );

        getOrCreateTagBuilder(BlockTags.LOGS)
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

        getOrCreateTagBuilder(BlockTags.COMBINATION_STEP_SOUND_BLOCKS)
                .add(
                        BLESSED_CLOTH_CARPET
                );

        getOrCreateTagBuilder(BlockTags.OCCLUDES_VIBRATION_SIGNALS)
                .add(
                        BLESSED_CLOTH_BLOCK
                );

        getOrCreateTagBuilder(BlockTags.DAMPENS_VIBRATIONS)
                .add(
                        BLESSED_CLOTH_BLOCK,
                        BLESSED_CLOTH_CARPET,
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
                        BISREEDS,
                        NUCLEOSYNTHESEED
                );

        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_HOE)
                .add(
                        STARBLEACHED_LEAVES,
                        NUCLEIC_FISSURELEAVES,

                        MUCKY_SINGUT_COIL,
                        MUCKY_SINGUT_BLOCK,
                        CLEANSED_SINGUT_COIL,
                        CLEANSED_SINGUT_BLOCK
                );

        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_PICKAXE)
                .addOptionalTag(OperationStarcleaveBlockTags.STARBLEACHED_LOGS)
                .add(
                        NETHERITE_PUMPKIN,

                        STARBLEACHED_LEAVES,
                        IMBUED_STARBLEACHED_TILES,

                        STARBLEACH_CAULDRON,
                        STARBLEACHED_PEARL_BLOCK,

                        STELLAR_REPULSOR,

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

                        POLISHED_CELESTIAL_OPAL_PILLAR
                );

        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(
                        STELLAR_SEDIMENT,
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
                        NUCLEOSYNTHESEED
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
                        SHORT_HOLY_MOSS
                );

        getOrCreateTagBuilder(BlockTags.REPLACEABLE)
                .add(
                        STARDUST_CLUSTER,
                        MULCHBORNE_TUFT,
                        SHORT_HOLY_MOSS,
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

        getOrCreateTagBuilder(BlockTags.FLOWER_POTS)
                .add(
                        POTTED_MULCHBORNE_TUFT,
                        POTTED_SHORT_HOLY_MOSS
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

        // conventional
        getOrCreateTagBuilder(ConventionalBlockTags.STRIPPED_LOGS)
                .add(
                        STRIPED_NUCLEIC_FISSUREROOT
                );
        getOrCreateTagBuilder(ConventionalBlockTags.STRIPPED_WOODS)
                .add(
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
        getOrCreateTagBuilder(OperationStarcleaveBlockTags.STARBLEACHED)
                .addOptionalTag(OperationStarcleaveBlockTags.STARBLEACHED_LOGS)
                .add(
                        STELLAR_SEDIMENT,
                        STELLAR_PATH,
                        STELLAR_MULCH,
                        STELLAR_FARMLAND,
                        HOLY_MOSS,
                        STARBLEACHED_LEAVES,
                        STARDUST_BLOCK
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
                        STELLAR_FARMLAND,
                        STELLAR_MULCH,
                        HOLY_MOSS,
                        STARDUST_BLOCK
                );

        getOrCreateTagBuilder(OperationStarcleaveBlockTags.STARBLEACH_IMMUNE)
                .addOptionalTag(OperationStarcleaveBlockTags.STARBLEACHED_LOGS)
                .addOptionalTag(OperationStarcleaveBlockTags.NUCLEIC_FISSUREROOTS)
                .add(
                        STARBLEACHED_LEAVES,
                        NUCLEIC_FISSURELEAVES
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


        getOrCreateTagBuilder(OperationStarcleaveBlockTags.STARBLEACHED_LOGS)
                .add(
                        STARBLEACHED_LOG,
                        STARBLEACHED_WOOD
                );

        getOrCreateTagBuilder(OperationStarcleaveBlockTags.NUCLEIC_FISSUREROOTS)
                .add(
                        NUCLEIC_FISSUREROOT,
                        NUCLEIC_FISSURERIND,
                        STRIPED_NUCLEIC_FISSUREROOT,
                        STRIPED_NUCLEIC_FISSURERIND
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
        List<Block> blocksSorted = blocks.stream().sorted((b1, b2) -> {
            char[] c1 = b1.getDescriptionId().toCharArray();
            char[] c2 = b2.getDescriptionId().toCharArray();
            return Arrays.compare(c1, c2);
        }).toList();

        builder.add(family.getBaseBlock());
        for (Block block : blocksSorted) {
            builder.add(block);
        }
    }

}

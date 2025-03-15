package phanastrae.operation_starcleave.fabric.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import phanastrae.operation_starcleave.block.tag.OperationStarcleaveBlockTags;

import java.util.concurrent.CompletableFuture;

import static phanastrae.operation_starcleave.block.OperationStarcleaveBlocks.*;


public class BlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public BlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        // vanilla tags
        getOrCreateTagBuilder(BlockTags.WOOL).add(
                BLESSED_CLOTH_BLOCK
        );

        getOrCreateTagBuilder(BlockTags.WOOL_CARPETS).add(
                BLESSED_CLOTH_CARPET
        );

        getOrCreateTagBuilder(BlockTags.LOGS).add(
                NUCLEIC_FISSUREROOT
        );

        getOrCreateTagBuilder(BlockTags.WALLS).add(
                STARBLEACHED_TILE_WALL
        );

        getOrCreateTagBuilder(BlockTags.STAIRS).add(
                STARBLEACHED_TILE_STAIRS
        );

        getOrCreateTagBuilder(BlockTags.SLABS).add(
                STARBLEACHED_TILE_SLAB,
                STELLAR_TILE_SLAB
        );

        getOrCreateTagBuilder(BlockTags.LEAVES).add(
                STARBLEACHED_LEAVES,
                NUCLEIC_FISSURELEAVES
        );

        getOrCreateTagBuilder(BlockTags.BEDS).add(
                BLESSED_BED
        );

        getOrCreateTagBuilder(BlockTags.CROPS).add(
                BISREEDS
        );

        getOrCreateTagBuilder(BlockTags.FIRE).add(
                PHLOGISTIC_FIRE
        );

        getOrCreateTagBuilder(BlockTags.CAULDRONS).add(
                STARBLEACH_CAULDRON
        );

        getOrCreateTagBuilder(BlockTags.COMBINATION_STEP_SOUND_BLOCKS).add(
                BLESSED_CLOTH_CARPET
        );

        getOrCreateTagBuilder(BlockTags.OCCLUDES_VIBRATION_SIGNALS).add(
                BLESSED_CLOTH_BLOCK
        );

        getOrCreateTagBuilder(BlockTags.DAMPENS_VIBRATIONS).add(
                BLESSED_CLOTH_BLOCK,
                BLESSED_CLOTH_CARPET,
                BLESSED_CLOTH_CURTAIN
        );

        getOrCreateTagBuilder(BlockTags.BIG_DRIPLEAF_PLACEABLE).add(
                STELLAR_FARMLAND
        );

        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_AXE).add(
                NETHERITE_PUMPKIN,
                MULCHBORNE_TUFT,
                SHORT_HOLY_MOSS,
                BISREEDS,
                NUCLEOSYNTHESEED
        );

        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_HOE).add(
                STARBLEACHED_LEAVES,
                NUCLEIC_FISSURELEAVES
        );

        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_PICKAXE).add(
                NETHERITE_PUMPKIN,

                STARBLEACHED_LOG,
                STARBLEACHED_WOOD,
                STARBLEACHED_LEAVES,
                STARBLEACHED_TILES,
                STARBLEACHED_TILE_SLAB,
                STARBLEACHED_TILE_STAIRS,
                STARBLEACHED_TILE_WALL,
                CHISELED_STARBLEACHED_TILES,
                IMBUED_STARBLEACHED_TILES,

                STARBLEACH_CAULDRON,
                STARBLEACHED_PEARL_BLOCK,

                STELLAR_TILES,
                STELLAR_TILE_SLAB,
                STELLAR_REPULSOR,

                COAGULATED_PLASMA,
                PLASMA_ICE
        );

        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_SHOVEL).add(
                STELLAR_SEDIMENT,
                STELLAR_MULCH,
                STELLAR_FARMLAND,

                HOLY_MOSS,

                STELLAR_TILES,
                STELLAR_TILE_SLAB,

                STELLAR_REPULSOR,

                STARDUST_BLOCK,
                COAGULATED_PLASMA
        );

        getOrCreateTagBuilder(BlockTags.SWORD_EFFICIENT).add(
                NETHERITE_PUMPKIN,
                MULCHBORNE_TUFT,
                SHORT_HOLY_MOSS
        );

        getOrCreateTagBuilder(BlockTags.NEEDS_DIAMOND_TOOL).add(
                NETHERITE_PUMPKIN
        );

        getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL).add(
                NUCLEIC_FISSUREROOT,
                NUCLEOSYNTHESEED
        );

        getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL).add(
                COAGULATED_PLASMA
        );

        getOrCreateTagBuilder(BlockTags.REPLACEABLE_BY_TREES).add(
                MULCHBORNE_TUFT,
                SHORT_HOLY_MOSS
        );

        getOrCreateTagBuilder(BlockTags.REPLACEABLE).add(
                STARDUST_CLUSTER,
                MULCHBORNE_TUFT,
                SHORT_HOLY_MOSS,
                PHLOGISTIC_FIRE
        );

        getOrCreateTagBuilder(BlockTags.ENCHANTMENT_POWER_TRANSMITTER).add(
                STARDUST_CLUSTER,
                MULCHBORNE_TUFT,
                SHORT_HOLY_MOSS,
                PHLOGISTIC_FIRE
        );

        getOrCreateTagBuilder(BlockTags.MAINTAINS_FARMLAND).add(
                BISREEDS
        );

        // common
        getOrCreateTagBuilder(ConventionalBlockTags.VILLAGER_JOB_SITES).add(
                STARBLEACH_CAULDRON
        );

        // starcleave tags
        getOrCreateTagBuilder(OperationStarcleaveBlockTags.STARBLEACHED).add(
                STELLAR_SEDIMENT,
                STELLAR_MULCH,
                STELLAR_FARMLAND,
                HOLY_MOSS,
                STARBLEACHED_LOG,
                STARBLEACHED_WOOD,
                STARBLEACHED_LEAVES,
                STARDUST_BLOCK
        );

        getOrCreateTagBuilder(OperationStarcleaveBlockTags.ALLOWS_BISREED_PLANTING).add(
                Blocks.FARMLAND,
                Blocks.MOSS_BLOCK,
                Blocks.SOUL_SAND,
                Blocks.END_STONE,
                Blocks.PRISMARINE,
                STELLAR_SEDIMENT,
                STELLAR_FARMLAND,
                STELLAR_MULCH,
                HOLY_MOSS,
                STARDUST_BLOCK,
                STARBLEACHED_LOG,
                STARBLEACHED_WOOD
        );

        getOrCreateTagBuilder(OperationStarcleaveBlockTags.STARBLEACH_IMMUNE).add(
                STARBLEACHED_LOG,
                STARBLEACHED_WOOD,
                STARBLEACHED_LEAVES,
                NUCLEIC_FISSUREROOT,
                NUCLEIC_FISSURELEAVES
        );

        getOrCreateTagBuilder(OperationStarcleaveBlockTags.PHLOGISTIC_HYPERFLAMMABLES).add(
                NUCLEOSYNTHESEED,
                NUCLEIC_FISSUREROOT,
                NUCLEIC_FISSURELEAVES
        );

        getOrCreateTagBuilder(OperationStarcleaveBlockTags.NUCLEOSYNTHESEED_BLAST_IMMUNE).add(
                NUCLEOSYNTHESEED,
                NUCLEIC_FISSUREROOT,
                PHLOGISTIC_FIRE
        );
    }
}

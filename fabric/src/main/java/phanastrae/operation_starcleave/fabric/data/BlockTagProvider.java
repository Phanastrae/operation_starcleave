package phanastrae.operation_starcleave.fabric.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
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
        getOrCreateTagBuilder(BlockTags.LOGS).add(
                NUCLEIC_FISSUREROOT
        );

        getOrCreateTagBuilder(BlockTags.WALLS).add(
                STARBLEACHED_TILE_WALL
        );

        getOrCreateTagBuilder(BlockTags.LEAVES).add(
                NUCLEIC_FISSURELEAVES
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

        getOrCreateTagBuilder(BlockTags.OCCLUDES_VIBRATION_SIGNALS).add(
                BLESSED_CLOTH_BLOCK
        );

        getOrCreateTagBuilder(BlockTags.DAMPENS_VIBRATIONS).add(
                BLESSED_CLOTH_BLOCK,
                BLESSED_CLOTH_CARPET,
                BLESSED_CLOTH_CURTAIN
        );

        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_AXE).add(
                MULCHBORNE_TUFT,
                SHORT_HOLY_MOSS,
                BISREEDS
        );

        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_HOE).add(
                NETHERITE_PUMPKIN,
                STARBLEACHED_LEAVES
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
                STELLAR_REPULSOR
        );

        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_SHOVEL).add(
                STELLAR_SEDIMENT,
                STELLAR_MULCH,
                STELLAR_FARMLAND,
                HOLY_MOSS,
                STELLAR_TILES,
                STELLAR_REPULSOR,
                STARDUST_BLOCK
        );

        getOrCreateTagBuilder(BlockTags.SWORD_EFFICIENT).add(
                MULCHBORNE_TUFT,
                SHORT_HOLY_MOSS
        );

        getOrCreateTagBuilder(BlockTags.NEEDS_DIAMOND_TOOL).add(
                NETHERITE_PUMPKIN
        );

        getOrCreateTagBuilder(BlockTags.REPLACEABLE_BY_TREES).add(
                MULCHBORNE_TUFT,
                SHORT_HOLY_MOSS
        );

        getOrCreateTagBuilder(BlockTags.REPLACEABLE).add(
                MULCHBORNE_TUFT,
                SHORT_HOLY_MOSS
        );

        getOrCreateTagBuilder(BlockTags.ENCHANTMENT_POWER_TRANSMITTER).add(
                MULCHBORNE_TUFT,
                SHORT_HOLY_MOSS
        );

        getOrCreateTagBuilder(BlockTags.MAINTAINS_FARMLAND).add(
                BISREEDS
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
    }
}

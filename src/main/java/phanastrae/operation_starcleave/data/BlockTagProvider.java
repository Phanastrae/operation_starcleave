package phanastrae.operation_starcleave.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;
import phanastrae.operation_starcleave.block.tag.OperationStarcleaveBlockTags;

import java.util.concurrent.CompletableFuture;

public class BlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public BlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(OperationStarcleaveBlocks.NETHERITE_PUMPKIN)
                .add(OperationStarcleaveBlocks.STARBLEACHED_LOG)
                .add(OperationStarcleaveBlocks.STARBLEACHED_WOOD)
                .add(OperationStarcleaveBlocks.STARBLEACHED_LEAVES)
                .add(OperationStarcleaveBlocks.STARBLEACHED_TILES)
                .add(OperationStarcleaveBlocks.STARBLEACHED_TILE_SLAB)
                .add(OperationStarcleaveBlocks.STARBLEACHED_TILE_STAIRS)
                .add(OperationStarcleaveBlocks.STARBLEACHED_TILE_WALL)
                .add(OperationStarcleaveBlocks.CHISELED_STARBLEACHED_TILES)
                .add(OperationStarcleaveBlocks.IMBUED_STARBLEACHED_TILES)
                .add(OperationStarcleaveBlocks.STARBLEACH_CAULDRON)
                .add(OperationStarcleaveBlocks.STARBLEACHED_PEARL_BLOCK)
                .add(OperationStarcleaveBlocks.STELLAR_TILES)
                .add(OperationStarcleaveBlocks.STELLAR_TILE_SLAB)
                .add(OperationStarcleaveBlocks.STELLAR_REPULSOR);

        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE)
                .add(OperationStarcleaveBlocks.MULCHBORNE_TUFT)
                .add(OperationStarcleaveBlocks.SHORT_HOLY_MOSS);

        getOrCreateTagBuilder(BlockTags.HOE_MINEABLE)
                .add(OperationStarcleaveBlocks.NETHERITE_PUMPKIN)
                .add(OperationStarcleaveBlocks.STARBLEACHED_LEAVES);

        getOrCreateTagBuilder(BlockTags.SHOVEL_MINEABLE)
                .add(OperationStarcleaveBlocks.STELLAR_SEDIMENT)
                .add(OperationStarcleaveBlocks.STELLAR_MULCH)
                .add(OperationStarcleaveBlocks.STELLAR_FARMLAND)
                .add(OperationStarcleaveBlocks.HOLY_MOSS)
                .add(OperationStarcleaveBlocks.STELLAR_TILES)
                .add(OperationStarcleaveBlocks.STELLAR_REPULSOR)
                .add(OperationStarcleaveBlocks.STARDUST_BLOCK);

        getOrCreateTagBuilder(BlockTags.CAULDRONS)
                .add(OperationStarcleaveBlocks.STARBLEACH_CAULDRON);

        getOrCreateTagBuilder(BlockTags.DAMPENS_VIBRATIONS)
                .add(OperationStarcleaveBlocks.BLESSED_CLOTH_BLOCK)
                .add(OperationStarcleaveBlocks.BLESSED_CLOTH_CARPET)
                .add(OperationStarcleaveBlocks.BLESSED_CLOTH_CURTAIN);

        getOrCreateTagBuilder(BlockTags.ENCHANTMENT_POWER_TRANSMITTER)
                .add(OperationStarcleaveBlocks.MULCHBORNE_TUFT)
                .add(OperationStarcleaveBlocks.SHORT_HOLY_MOSS);

        getOrCreateTagBuilder(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(OperationStarcleaveBlocks.NETHERITE_PUMPKIN);

        getOrCreateTagBuilder(BlockTags.OCCLUDES_VIBRATION_SIGNALS)
                .add(OperationStarcleaveBlocks.BLESSED_CLOTH_BLOCK);

        getOrCreateTagBuilder(BlockTags.REPLACEABLE)
                .add(OperationStarcleaveBlocks.MULCHBORNE_TUFT)
                .add(OperationStarcleaveBlocks.SHORT_HOLY_MOSS);

        getOrCreateTagBuilder(BlockTags.REPLACEABLE_BY_TREES)
                .add(OperationStarcleaveBlocks.MULCHBORNE_TUFT)
                .add(OperationStarcleaveBlocks.SHORT_HOLY_MOSS);

        getOrCreateTagBuilder(BlockTags.SWORD_EFFICIENT)
                .add(OperationStarcleaveBlocks.MULCHBORNE_TUFT)
                .add(OperationStarcleaveBlocks.SHORT_HOLY_MOSS);

        getOrCreateTagBuilder(BlockTags.WALLS)
                .add(OperationStarcleaveBlocks.STARBLEACHED_TILE_WALL);

        getOrCreateTagBuilder(BlockTags.FIRE)
                .add(OperationStarcleaveBlocks.PHLOGISTIC_FIRE);

        getOrCreateTagBuilder(OperationStarcleaveBlockTags.STARBLEACHED)
                .add(OperationStarcleaveBlocks.STELLAR_SEDIMENT)
                .add(OperationStarcleaveBlocks.STELLAR_MULCH)
                .add(OperationStarcleaveBlocks.STELLAR_FARMLAND)
                .add(OperationStarcleaveBlocks.HOLY_MOSS)
                .add(OperationStarcleaveBlocks.STARBLEACHED_LOG)
                .add(OperationStarcleaveBlocks.STARBLEACHED_WOOD)
                .add(OperationStarcleaveBlocks.STARBLEACHED_LEAVES)
                .add(OperationStarcleaveBlocks.STARDUST_BLOCK);
    }
}

package phanastrae.operation_starcleave.data;

import com.google.common.collect.Maps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamily;
import net.minecraft.world.level.block.Block;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;

import java.util.Map;
import java.util.stream.Stream;

public class OperationStarcleaveBlockFamilies {
    private static final Map<Block, BlockFamily> STARCLEAVE_MAP = Maps.newHashMap();
    private static final String RECIPE_GROUP_PREFIX_WOODEN = "wooden";
    private static final String RECIPE_UNLOCKED_BY_HAS_PLANKS = "has_planks";

    public static final BlockFamily STARDUST_BRICKS = familyBuilder(OperationStarcleaveBlocks.STARDUST_BRICKS)
            .stairs(OperationStarcleaveBlocks.STARDUST_BRICK_STAIRS)
            .slab(OperationStarcleaveBlocks.STARDUST_BRICK_SLAB)
            .wall(OperationStarcleaveBlocks.STARDUST_BRICK_WALL)
            .getFamily();

    public static final BlockFamily FELLCRUST = familyBuilder(OperationStarcleaveBlocks.FELLCRUST)
            .stairs(OperationStarcleaveBlocks.FELLCRUST_STAIRS)
            .slab(OperationStarcleaveBlocks.FELLCRUST_SLAB)
            .wall(OperationStarcleaveBlocks.FELLCRUST_WALL)
            .chiseled(OperationStarcleaveBlocks.CHISELED_FELLCRUST)
            .cut(OperationStarcleaveBlocks.CUT_FELLCRUST)
            .getFamily();

    public static final BlockFamily CUT_FELLCRUST = familyBuilder(OperationStarcleaveBlocks.CUT_FELLCRUST)
            .stairs(OperationStarcleaveBlocks.CUT_FELLCRUST_STAIRS)
            .slab(OperationStarcleaveBlocks.CUT_FELLCRUST_SLAB)
            .wall(OperationStarcleaveBlocks.CUT_FELLCRUST_WALL)
            .getFamily();

    public static final BlockFamily SMOOTH_FELLCRUST = familyBuilder(OperationStarcleaveBlocks.SMOOTH_FELLCRUST)
            .stairs(OperationStarcleaveBlocks.SMOOTH_FELLCRUST_STAIRS)
            .slab(OperationStarcleaveBlocks.SMOOTH_FELLCRUST_SLAB)
            .wall(OperationStarcleaveBlocks.SMOOTH_FELLCRUST_WALL)
            .chiseled(OperationStarcleaveBlocks.CHISELED_SMOOTH_FELLCRUST)
            .polished(OperationStarcleaveBlocks.SMOOTH_FELLCRUST_BRICKS)
            .getFamily();

    public static final BlockFamily SMOOTH_FELLCRUST_BRICKS = familyBuilder(OperationStarcleaveBlocks.SMOOTH_FELLCRUST_BRICKS)
            .stairs(OperationStarcleaveBlocks.SMOOTH_FELLCRUST_BRICK_STAIRS)
            .slab(OperationStarcleaveBlocks.SMOOTH_FELLCRUST_BRICK_SLAB)
            .wall(OperationStarcleaveBlocks.SMOOTH_FELLCRUST_BRICK_WALL)
            .getFamily();

    public static final BlockFamily COBBLED_FELLCRUST = familyBuilder(OperationStarcleaveBlocks.COBBLED_FELLCRUST)
            .stairs(OperationStarcleaveBlocks.COBBLED_FELLCRUST_STAIRS)
            .slab(OperationStarcleaveBlocks.COBBLED_FELLCRUST_SLAB)
            .wall(OperationStarcleaveBlocks.COBBLED_FELLCRUST_WALL)
            .polished(OperationStarcleaveBlocks.POLISHED_FELLCRUST)
            .getFamily();

    public static final BlockFamily POLISHED_FELLCRUST = familyBuilder(OperationStarcleaveBlocks.POLISHED_FELLCRUST)
            .stairs(OperationStarcleaveBlocks.POLISHED_FELLCRUST_STAIRS)
            .slab(OperationStarcleaveBlocks.POLISHED_FELLCRUST_SLAB)
            .polished(OperationStarcleaveBlocks.POLISHED_FELLCRUST_BRICKS)
            .getFamily();

    public static final BlockFamily POLISHED_FELLCRUST_BRICKS = familyBuilder(OperationStarcleaveBlocks.POLISHED_FELLCRUST_BRICKS)
            .stairs(OperationStarcleaveBlocks.POLISHED_FELLCRUST_BRICK_STAIRS)
            .slab(OperationStarcleaveBlocks.POLISHED_FELLCRUST_BRICK_SLAB)
            .wall(OperationStarcleaveBlocks.POLISHED_FELLCRUST_BRICK_WALL)
            .polished(OperationStarcleaveBlocks.CUT_POLISHED_FELLCRUST)
            .getFamily();

    public static final BlockFamily CUT_POLISHED_FELLCRUST = familyBuilder(OperationStarcleaveBlocks.CUT_POLISHED_FELLCRUST)
            .stairs(OperationStarcleaveBlocks.CUT_POLISHED_FELLCRUST_STAIRS)
            .slab(OperationStarcleaveBlocks.CUT_POLISHED_FELLCRUST_SLAB)
            .getFamily();

    public static final BlockFamily STARBLEACHED_TILES = familyBuilder(OperationStarcleaveBlocks.STARBLEACHED_TILES)
            .stairs(OperationStarcleaveBlocks.STARBLEACHED_TILE_STAIRS)
            .slab(OperationStarcleaveBlocks.STARBLEACHED_TILE_SLAB)
            .wall(OperationStarcleaveBlocks.STARBLEACHED_TILE_WALL)
            .chiseled(OperationStarcleaveBlocks.CHISELED_STARBLEACHED_TILES)
            .pressurePlate(OperationStarcleaveBlocks.STARBLEACHED_PRESSURE_PLATE)
            .button(OperationStarcleaveBlocks.STARBLEACHED_BUTTON)
            .getFamily();

    public static final BlockFamily STELLAR_TILES = familyBuilder(OperationStarcleaveBlocks.STELLAR_TILES)
            .slab(OperationStarcleaveBlocks.STELLAR_TILE_SLAB)
            .getFamily();

    public static final BlockFamily BLESSED_CLOTH = familyBuilder(OperationStarcleaveBlocks.BLESSED_CLOTH_BLOCK)
            .stairs(OperationStarcleaveBlocks.BLESSED_CLOTH_STAIRS)
            .slab(OperationStarcleaveBlocks.BLESSED_CLOTH_SLAB)
            .getFamily();

    public static final BlockFamily BLESSED_CLOTH_PADDING = familyBuilder(OperationStarcleaveBlocks.BLESSED_CLOTH_PADDING)
            .stairs(OperationStarcleaveBlocks.BLESSED_CLOTH_PADDING_STAIRS)
            .slab(OperationStarcleaveBlocks.BLESSED_CLOTH_PADDING_SLAB)
            .getFamily();

    public static final BlockFamily STARFLAKED_BISMUTH_BLOCK = familyBuilder(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_BLOCK)
            .slab(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_SLAB)
            .polished(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_BRICKS)
            .chiseled(OperationStarcleaveBlocks.CHISELED_STARFLAKED_BISMUTH_BLOCK)
            .dontGenerateModel()
            .getFamily();

    public static final BlockFamily STARFLAKED_BISMUTH_BRICKS = familyBuilder(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_BRICKS)
            .stairs(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_BRICK_STAIRS)
            .slab(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_BRICK_SLAB)
            .wall(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_BRICK_WALL)
            .polished(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_TILES)
            .chiseled(OperationStarcleaveBlocks.CHISELED_STARFLAKED_BISMUTH_BRICKS)
            .getFamily();

    public static final BlockFamily STARFLAKED_BISMUTH_TILES = familyBuilder(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_TILES)
            .stairs(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_TILE_STAIRS)
            .slab(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_TILE_SLAB)
            .wall(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_TILE_WALL)
            .mosaic(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_MOSAIC)
            .getFamily();

    public static final BlockFamily STARFLAKED_BISMUTH_MOSAIC = familyBuilder(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_MOSAIC)
            .stairs(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_MOSAIC_STAIRS)
            .slab(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_MOSAIC_SLAB)
            .wall(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_MOSAIC_WALL)
            .getFamily();

    public static final BlockFamily CELESTIAL_OPAL_BLOCK = familyBuilder(OperationStarcleaveBlocks.CELESTIAL_OPAL_BLOCK)
            .stairs(OperationStarcleaveBlocks.CELESTIAL_OPAL_STAIRS)
            .slab(OperationStarcleaveBlocks.CELESTIAL_OPAL_SLAB)
            .wall(OperationStarcleaveBlocks.CELESTIAL_OPAL_WALL)
            .polished(OperationStarcleaveBlocks.POLISHED_CELESTIAL_OPAL_BLOCK)
            .getFamily();

    public static final BlockFamily POLISHED_CELESTIAL_OPAL_BLOCK = familyBuilder(OperationStarcleaveBlocks.POLISHED_CELESTIAL_OPAL_BLOCK)
            .stairs(OperationStarcleaveBlocks.POLISHED_CELESTIAL_OPAL_STAIRS)
            .slab(OperationStarcleaveBlocks.POLISHED_CELESTIAL_OPAL_SLAB)
            .polished(OperationStarcleaveBlocks.POLISHED_CELESTIAL_OPAL_BRICKS)
            .getFamily();

    public static final BlockFamily POLISHED_CELESTIAL_OPAL_BRICKS = familyBuilder(OperationStarcleaveBlocks.POLISHED_CELESTIAL_OPAL_BRICKS)
            .stairs(OperationStarcleaveBlocks.POLISHED_CELESTIAL_OPAL_BRICK_STAIRS)
            .slab(OperationStarcleaveBlocks.POLISHED_CELESTIAL_OPAL_BRICK_SLAB)
            .wall(OperationStarcleaveBlocks.POLISHED_CELESTIAL_OPAL_BRICK_WALL)
            .getFamily();

    public static final BlockFamily OURANIC_CHIP_BLOCK = familyBuilder(OperationStarcleaveBlocks.OURANIC_CHIP_BLOCK)
            .stairs(OperationStarcleaveBlocks.OURANIC_CHIP_STAIRS)
            .slab(OperationStarcleaveBlocks.OURANIC_CHIP_SLAB)
            .wall(OperationStarcleaveBlocks.OURANIC_CHIP_WALL)
            .chiseled(OperationStarcleaveBlocks.CHISELED_OURANIC_CHIP_BLOCK)
            .polished(OperationStarcleaveBlocks.OURANIC_BRICKS)
            .getFamily();

    public static final BlockFamily OURANIC_BRICKS = familyBuilder(OperationStarcleaveBlocks.OURANIC_BRICKS)
            .stairs(OperationStarcleaveBlocks.OURANIC_BRICK_STAIRS)
            .slab(OperationStarcleaveBlocks.OURANIC_BRICK_SLAB)
            .wall(OperationStarcleaveBlocks.OURANIC_BRICK_WALL)
            .getFamily();

    public static BlockFamily.Builder familyBuilder(Block baseBlock) {
        BlockFamily.Builder builder = new BlockFamily.Builder(baseBlock);
        BlockFamily blockFamily = STARCLEAVE_MAP.put(baseBlock, builder.getFamily());
        if (blockFamily != null) {
            throw new IllegalStateException("Duplicate family definition for " + BuiltInRegistries.BLOCK.getKey(baseBlock));
        } else {
            return builder;
        }
    }

    public static Stream<BlockFamily> getAllOperationStarcleaveFamilies() {
        return STARCLEAVE_MAP.values().stream();
    }
}

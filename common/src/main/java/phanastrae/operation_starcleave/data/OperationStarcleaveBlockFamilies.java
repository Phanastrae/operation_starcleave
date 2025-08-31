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

    public static final BlockFamily STARBLEACHED_TILES = familyBuilder(OperationStarcleaveBlocks.STARBLEACHED_TILES)
            .stairs(OperationStarcleaveBlocks.STARBLEACHED_TILE_STAIRS)
            .slab(OperationStarcleaveBlocks.STARBLEACHED_TILE_SLAB)
            .wall(OperationStarcleaveBlocks.STARBLEACHED_TILE_WALL)
            .chiseled(OperationStarcleaveBlocks.CHISELED_STARBLEACHED_TILES)
            .getFamily();

    public static final BlockFamily STELLAR_TILES = familyBuilder(OperationStarcleaveBlocks.STELLAR_TILES)
            .slab(OperationStarcleaveBlocks.STELLAR_TILE_SLAB)
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

package phanastrae.operation_starcleave.client.render.extras_baking;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Iridescence {

    static {
        // list iridescent block items
        Set<Block> iridescentBismuthBlockItems = new ObjectOpenHashSet<>();
        Set<Block> iridescentOpalBlockItems = new ObjectOpenHashSet<>();

        iridescentBismuthBlockItems.addAll(List.of(
                OperationStarcleaveBlocks.STARFLAKED_BISMUTH_BLOCK,
                OperationStarcleaveBlocks.STARFLAKED_BISMUTH_SLAB,
                OperationStarcleaveBlocks.CHISELED_STARFLAKED_BISMUTH_BLOCK,

                OperationStarcleaveBlocks.STARFLAKED_BISMUTH_PILLAR,

                OperationStarcleaveBlocks.STARFLAKED_BISMUTH_BRICKS,
                OperationStarcleaveBlocks.STARFLAKED_BISMUTH_BRICK_STAIRS,
                OperationStarcleaveBlocks.STARFLAKED_BISMUTH_BRICK_SLAB,
                OperationStarcleaveBlocks.STARFLAKED_BISMUTH_BRICK_WALL,
                OperationStarcleaveBlocks.CHISELED_STARFLAKED_BISMUTH_BRICKS,

                OperationStarcleaveBlocks.STARFLAKED_BISMUTH_TILES,
                OperationStarcleaveBlocks.STARFLAKED_BISMUTH_TILE_STAIRS,
                OperationStarcleaveBlocks.STARFLAKED_BISMUTH_TILE_SLAB,
                OperationStarcleaveBlocks.STARFLAKED_BISMUTH_TILE_WALL,

                OperationStarcleaveBlocks.STARFLAKED_BISMUTH_MOSAIC,
                OperationStarcleaveBlocks.STARFLAKED_BISMUTH_MOSAIC_STAIRS,
                OperationStarcleaveBlocks.STARFLAKED_BISMUTH_MOSAIC_SLAB,
                OperationStarcleaveBlocks.STARFLAKED_BISMUTH_MOSAIC_WALL,

                OperationStarcleaveBlocks.STARFLAKED_BISMUTH_DOOR,
                OperationStarcleaveBlocks.STARFLAKED_BISMUTH_TRAPDOOR
        ));
        iridescentOpalBlockItems.addAll(List.of(
                OperationStarcleaveBlocks.CELESTIAL_OPAL_BLOCK,
                OperationStarcleaveBlocks.BUDDING_CELESTIAL_OPAL,

                OperationStarcleaveBlocks.CELESTIAL_OPAL_SPIRE,
                OperationStarcleaveBlocks.CELESTIAL_OPAL_CLUSTER,
                OperationStarcleaveBlocks.LARGE_CELESTIAL_OPAL_BUD,
                OperationStarcleaveBlocks.MEDIUM_CELESTIAL_OPAL_BUD,
                OperationStarcleaveBlocks.SMALL_CELESTIAL_OPAL_BUD
        ));

        // list iridescent blocks
        Set<Block> iridescentBismuthBlocks = new ObjectOpenHashSet<>(iridescentBismuthBlockItems);
        Set<Block> iridescentOpalBlocks = new ObjectOpenHashSet<>(iridescentOpalBlockItems);

        iridescentBismuthBlocks.addAll(List.of(
                OperationStarcleaveBlocks.BISREEDS
        ));

        // list iridescent items
        Set<Item> iridescentBismuthItems = new ObjectOpenHashSet<>(iridescentBismuthBlockItems.stream().map(Block::asItem).toList());
        Set<Item> iridescentOpalItems = new ObjectOpenHashSet<>(iridescentOpalBlockItems.stream().map(Block::asItem).toList());

        iridescentBismuthItems.addAll(List.of(
                OperationStarcleaveItems.BISMUTH_FLAKE,
                OperationStarcleaveItems.STARFLAKED_BISMUTH,
                OperationStarcleaveItems.BISMUTH_PEGASUS_ARMOR
        ));

        iridescentOpalItems.addAll(List.of(
                OperationStarcleaveItems.CELESTIAL_OPAL_SHARD
        ));

        // store in maps
        ImmutableMap.Builder<Block, Integer> iridescenceBlockMapBuilder = ImmutableMap.builder();
        for (Block block : iridescentBismuthBlocks) {
            iridescenceBlockMapBuilder.put(block, getBismuthIridescenceId());
        }
        for (Block block : iridescentOpalBlocks) {
            iridescenceBlockMapBuilder.put(block, getOpalIridescenceId());
        }
        IRIDESCENCE_BLOCK_ID_MAP = iridescenceBlockMapBuilder.build();

        ImmutableMap.Builder<Item, Integer> iridescenceItemMapBuilder = ImmutableMap.builder();
        for (Item item : iridescentBismuthItems) {
            iridescenceItemMapBuilder.put(item, getBismuthIridescenceId());
        }
        for (Item item : iridescentOpalItems) {
            iridescenceItemMapBuilder.put(item, getOpalIridescenceId());
        }
        IRIDESCENCE_ITEM_ID_MAP = iridescenceItemMapBuilder.build();

    }

    public static final Map<Block, Integer> IRIDESCENCE_BLOCK_ID_MAP;
    public static final Map<Item, Integer> IRIDESCENCE_ITEM_ID_MAP;

    public static boolean isStateIridescent(BlockState state) {
        return IRIDESCENCE_BLOCK_ID_MAP.containsKey(state.getBlock());
    }

    public static boolean isItemIridescent(Item item) {
        return IRIDESCENCE_ITEM_ID_MAP.containsKey(item);
    }

    public static int getIridescenceId(BlockState state) {
        return IRIDESCENCE_BLOCK_ID_MAP.getOrDefault(state.getBlock(), 0);
    }

    public static int getIridescenceId(Item item) {
        return IRIDESCENCE_ITEM_ID_MAP.getOrDefault(item, 0);
    }

    public static int getBismuthIridescenceId() {
        return 1;
    }

    public static int getOpalIridescenceId() {
        return 2;
    }

    public static List<Integer> getUsedIridescenceIds() {
        return List.of(
                getBismuthIridescenceId(),
                getOpalIridescenceId()
        );
    }
}

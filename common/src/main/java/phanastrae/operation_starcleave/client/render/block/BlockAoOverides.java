package phanastrae.operation_starcleave.client.render.block;

import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import static phanastrae.operation_starcleave.block.OperationStarcleaveBlocks.*;

public class BlockAoOverides {

    public static Set<ModelResourceLocation> MODELS_TO_OVERRIDE = new HashSet<>();

    public static void init() {
        forEach(BlockAoOverides::addOverride,
                STELLAR_SEDIMENT,
                STELLARUBBLE_MIX,
                STELLAR_PATH,
                STELLAR_FARMLAND,

                STELLAR_BRICKS,
                STELLAR_BRICK_STAIRS,
                STELLAR_BRICK_SLAB,
                STELLAR_BRICK_WALL,

                STELLAR_MULCH,
                HOLY_MOSS,

                STARDUST_BLOCK,

                STARDUST_BRICKS,
                STARDUST_BRICK_STAIRS,
                STARDUST_BRICK_SLAB,
                STARDUST_BRICK_WALL,

                STARBLEACHED_LOG,
                STARBLEACHED_WOOD,
                STARBLEACHED_TILES,
                STARBLEACHED_TILE_STAIRS,
                STARBLEACHED_TILE_SLAB,
                STARBLEACHED_TILE_WALL,
                CHISELED_STARBLEACHED_TILES,

                STELLAR_TILES,
                STELLAR_TILE_SLAB,

                COAGULATED_PLASMA
        );
    }

    private static void forEach(Consumer<Block> consumer, Block... blocks) {
        for (Block block : blocks) {
            consumer.accept(block);
        }
    }

    private static void addOverride(Block block) {
        for (BlockState state : block.getStateDefinition().getPossibleStates()) {
            addOverride(BlockModelShaper.stateToModelLocation(state));
        }
    }

    private static void addOverride(ModelResourceLocation location) {
        MODELS_TO_OVERRIDE.add(location);
    }
}

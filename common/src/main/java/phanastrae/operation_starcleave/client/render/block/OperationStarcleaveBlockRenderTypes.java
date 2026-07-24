package phanastrae.operation_starcleave.client.render.block;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import phanastrae.operation_starcleave.client.services.XPlatClientInterface;

import static phanastrae.operation_starcleave.block.OperationStarcleaveBlocks.*;

public class OperationStarcleaveBlockRenderTypes {

    public static void init() {
        putBlocks(RenderType.cutoutMipped(),
                STARBLEACHED_LEAVES,
                NUCLEIC_FISSURELEAVES
        );
        putBlocks(RenderType.cutout(),
                STARBLEACHED_LEAF_LITTER,

                STARTOUCHED_DOOR,
                STARTOUCHED_TRAPDOOR,

                STARTOUCHED_TORCH,
                STARTOUCHED_WALL_TORCH,

                STARBLEACHED_SAPLING,
                POTTED_STARBLEACHED_SAPLING,

                BISREEDS,

                MULCHBORNE_TUFT,
                POTTED_MULCHBORNE_TUFT,
                SHORT_HOLY_MOSS,
                TALL_HOLY_MOSS,
                POTTED_SHORT_HOLY_MOSS,

                GREAT_TREES_CARE,
                POTTED_GREAT_TREES_CARE,
                RED_MOURNER,
                POTTED_RED_MOURNER,
                ANGELCLAW,
                POTTED_ANGELCLAW,
                ELDROSE,
                POTTED_ELDROSE,
                WITCHGLARE,
                POTTED_WITCHGLARE,
                BLUE_DREAMER,
                POTTED_BLUE_DREAMER,
                DRAGONS_MAW,
                POTTED_DRAGONS_MAW,

                STARCLOVERS,

                HOLY_LEAF_PLATFORM,

                BLESSED_BED,
                PHLOGISTIC_FIRE,

                STARFLAKED_BISMUTH_DOOR,
                STARFLAKED_BISMUTH_TRAPDOOR,

                CELESTIAL_OPAL_SPIRE,
                CELESTIAL_OPAL_CLUSTER,
                LARGE_CELESTIAL_OPAL_BUD,
                MEDIUM_CELESTIAL_OPAL_BUD,
                SMALL_CELESTIAL_OPAL_BUD
        );
        putBlocks(RenderType.translucent(),
                PETRICHORIC_VAPOR
        );
    }

    private static void putBlocks(RenderType renderLayer, Block... blocks) {
        XPlatClientInterface.INSTANCE.registerBlockRenderLayers(renderLayer, blocks);
    }
}

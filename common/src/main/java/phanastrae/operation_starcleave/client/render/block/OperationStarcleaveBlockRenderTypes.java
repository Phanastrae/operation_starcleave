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
                BISREEDS,

                MULCHBORNE_TUFT,
                POTTED_MULCHBORNE_TUFT,
                SHORT_HOLY_MOSS,
                POTTED_SHORT_HOLY_MOSS,

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

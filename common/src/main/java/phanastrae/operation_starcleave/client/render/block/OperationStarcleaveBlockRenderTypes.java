package phanastrae.operation_starcleave.client.render.block;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;
import phanastrae.operation_starcleave.client.services.XPlatClientInterface;

public class OperationStarcleaveBlockRenderTypes {

    public static void init() {
        putBlocks(RenderType.cutoutMipped(),
                OperationStarcleaveBlocks.STARBLEACHED_LEAVES);
        putBlocks(RenderType.cutout(),
                OperationStarcleaveBlocks.BISREEDS,
                OperationStarcleaveBlocks.MULCHBORNE_TUFT,
                OperationStarcleaveBlocks.SHORT_HOLY_MOSS,
                OperationStarcleaveBlocks.BLESSED_BED,
                OperationStarcleaveBlocks.PHLOGISTIC_FIRE);
        putBlocks(RenderType.translucent(),
                OperationStarcleaveBlocks.PETRICHORIC_VAPOR);
    }

    private static void putBlocks(RenderType renderLayer, Block... blocks) {
        XPlatClientInterface.INSTANCE.registerBlockRenderLayers(renderLayer, blocks);
    }
}

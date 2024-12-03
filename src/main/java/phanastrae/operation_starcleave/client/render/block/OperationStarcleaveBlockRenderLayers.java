package phanastrae.operation_starcleave.client.render.block;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;

public class OperationStarcleaveBlockRenderLayers {

    public static void init() {
        putBlocks(RenderType.cutoutMipped(),
                OperationStarcleaveBlocks.STARBLEACHED_LEAVES);
        putBlocks(RenderType.cutout(),
                OperationStarcleaveBlocks.MULCHBORNE_TUFT,
                OperationStarcleaveBlocks.SHORT_HOLY_MOSS,
                OperationStarcleaveBlocks.BLESSED_BED,
                OperationStarcleaveBlocks.PHLOGISTIC_FIRE);
        putBlocks(RenderType.translucent(),
                OperationStarcleaveBlocks.PETRICHORIC_VAPOR);
    }

    private static void putBlocks(RenderType renderLayer, Block... blocks) {
        // TODO Xplat
        BlockRenderLayerMap.INSTANCE.putBlocks(renderLayer, blocks);
    }
}

package phanastrae.operation_starcleave.client.render.entity.model;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.client.render.block.entity.BlessedBedBlockEntityRenderer;

public class OperationStarcleaveEntityModelLayers {

    public static final EntityModelLayer STARCLEAVER_GOLEM = new EntityModelLayer(OperationStarcleave.id("starcleaver_golem"), "main");
    public static final EntityModelLayer SUBCAELIC_TORPEDO = new EntityModelLayer(OperationStarcleave.id("subcaelic_torpedo"), "main");
    public static final EntityModelLayer SUBCAELIC_TORPEDO_OVERLAY = new EntityModelLayer(OperationStarcleave.id("subcaelic_torpedo"), "overlay");
    public static final EntityModelLayer SUBCAELIC_DUX = new EntityModelLayer(OperationStarcleave.id("subcaelic_dux"), "main");
    public static final EntityModelLayer BLESSED_BED_HEAD = new EntityModelLayer(OperationStarcleave.id("blessed_bed_head"), "main");
    public static final EntityModelLayer BLESSED_BED_FOOT = new EntityModelLayer(OperationStarcleave.id("blessed_bed_foot"), "main");

    public static void init() {
        EntityModelLayerRegistry.registerModelLayer(STARCLEAVER_GOLEM, StarcleaverGolemEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(SUBCAELIC_TORPEDO, SubcaelicTorpedoEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(SUBCAELIC_TORPEDO_OVERLAY, SubcaelicTorpedoEntityModel::getOverlayTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(SUBCAELIC_DUX, SubcaelicDuxEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(BLESSED_BED_HEAD, BlessedBedBlockEntityRenderer::getHeadTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(BLESSED_BED_FOOT, BlessedBedBlockEntityRenderer::getFootTexturedModelData);
    }
}

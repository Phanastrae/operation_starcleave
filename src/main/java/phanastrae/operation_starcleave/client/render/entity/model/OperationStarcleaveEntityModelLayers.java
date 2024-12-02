package phanastrae.operation_starcleave.client.render.entity.model;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.client.render.block.entity.BlessedBedBlockEntityRenderer;

public class OperationStarcleaveEntityModelLayers {

    public static final ModelLayerLocation STARCLEAVER_GOLEM = new ModelLayerLocation(OperationStarcleave.id("starcleaver_golem"), "main");
    public static final ModelLayerLocation SUBCAELIC_TORPEDO = new ModelLayerLocation(OperationStarcleave.id("subcaelic_torpedo"), "main");
    public static final ModelLayerLocation SUBCAELIC_TORPEDO_OVERLAY = new ModelLayerLocation(OperationStarcleave.id("subcaelic_torpedo"), "overlay");
    public static final ModelLayerLocation SUBCAELIC_DUX = new ModelLayerLocation(OperationStarcleave.id("subcaelic_dux"), "main");
    public static final ModelLayerLocation BLESSED_BED_HEAD = new ModelLayerLocation(OperationStarcleave.id("blessed_bed_head"), "main");
    public static final ModelLayerLocation BLESSED_BED_FOOT = new ModelLayerLocation(OperationStarcleave.id("blessed_bed_foot"), "main");

    public static void init() {
        EntityModelLayerRegistry.registerModelLayer(STARCLEAVER_GOLEM, StarcleaverGolemEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(SUBCAELIC_TORPEDO, SubcaelicTorpedoEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(SUBCAELIC_TORPEDO_OVERLAY, SubcaelicTorpedoEntityModel::getOverlayTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(SUBCAELIC_DUX, SubcaelicDuxEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(BLESSED_BED_HEAD, BlessedBedBlockEntityRenderer::getHeadTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(BLESSED_BED_FOOT, BlessedBedBlockEntityRenderer::getFootTexturedModelData);
    }
}

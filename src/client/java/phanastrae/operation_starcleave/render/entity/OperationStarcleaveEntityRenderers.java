package phanastrae.operation_starcleave.render.entity;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.block.entity.BedBlockEntityRenderer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;
import phanastrae.operation_starcleave.render.entity.model.StarcleaverGolemEntityModel;

public class OperationStarcleaveEntityRenderers {

    public static final EntityModelLayer MODEL_STARCLEAVER_GOLEM_LAYER = new EntityModelLayer(OperationStarcleave.id("starcleaver_golem"), "main");
    public static final EntityModelLayer MODEL_BLESSED_BED_HEAD = new EntityModelLayer(OperationStarcleave.id("blessed_bed_head"), "main");
    public static final EntityModelLayer MODEL_BLESSED_BED_FOOT = new EntityModelLayer(OperationStarcleave.id("blessed_bed_foot"), "main");

    public static void init() {
        EntityRendererRegistry.register(OperationStarcleaveEntityTypes.STARCLEAVER_GOLEM, StarcleaverGolemEntityRenderer::new);

        EntityRendererRegistry.register(OperationStarcleaveEntityTypes.SPLASH_STARBLEACH, FlyingItemEntityRenderer::new);
        EntityRendererRegistry.register(OperationStarcleaveEntityTypes.STARBLEACHED_PEARL, FlyingItemEntityRenderer::new);
        EntityRendererRegistry.register(OperationStarcleaveEntityTypes.FIRMAMENT_REJUVENATOR, FlyingItemEntityRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(MODEL_STARCLEAVER_GOLEM_LAYER, StarcleaverGolemEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(MODEL_BLESSED_BED_HEAD, BlessedBedBlockEntityRenderer::getHeadTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(MODEL_BLESSED_BED_FOOT, BlessedBedBlockEntityRenderer::getFootTexturedModelData);
    }
}

package phanastrae.operation_starcleave.render.entity;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;
import phanastrae.operation_starcleave.render.entity.model.StarcleaverGolemEntityModel;

public class OperationStarcleaveEntityRenderers {

    public static final EntityModelLayer MODEL_STARCLEAVER_GOLEM_LAYER = new EntityModelLayer(OperationStarcleave.id("starcleaver_golem"), "main");

    public static void init() {
        EntityRendererRegistry.register(OperationStarcleaveEntityTypes.STARCLEAVER_GOLEM, StarcleaverGolemEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_STARCLEAVER_GOLEM_LAYER, StarcleaverGolemEntityModel::getTexturedModelData);
    }
}

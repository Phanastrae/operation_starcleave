package phanastrae.operation_starcleave.client.render.entity.model;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.client.render.block.entity.BlessedBedBlockEntityRenderer;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class OperationStarcleaveEntityModelLayers {

    public static final ModelLayerLocation STARCLEAVER_GOLEM = createMainLayer("starcleaver_golem");
    public static final ModelLayerLocation SUBCAELIC_TORPEDO = createMainLayer("subcaelic_torpedo");
    public static final ModelLayerLocation SUBCAELIC_TORPEDO_OVERLAY = createLayer("subcaelic_torpedo", "overlay");
    public static final ModelLayerLocation SUBCAELIC_DUX = createMainLayer("subcaelic_dux");
    public static final ModelLayerLocation BLESSED_BED_HEAD = createMainLayer("blessed_bed_head");
    public static final ModelLayerLocation BLESSED_BED_FOOT = createMainLayer("blessed_bed_foot");

    public static void init(BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> r) {
        r.accept(STARCLEAVER_GOLEM, StarcleaverGolemEntityModel::getTexturedModelData);
        r.accept(SUBCAELIC_TORPEDO, SubcaelicTorpedoEntityModel::getTexturedModelData);
        r.accept(SUBCAELIC_TORPEDO_OVERLAY, SubcaelicTorpedoEntityModel::getOverlayTexturedModelData);
        r.accept(SUBCAELIC_DUX, SubcaelicDuxEntityModel::getTexturedModelData);
        r.accept(BLESSED_BED_HEAD, BlessedBedBlockEntityRenderer::getHeadTexturedModelData);
        r.accept(BLESSED_BED_FOOT, BlessedBedBlockEntityRenderer::getFootTexturedModelData);
    }

    private static ModelLayerLocation createMainLayer(String id) {
        return createLayer(id, "main");
    }

    private static ModelLayerLocation createLayer(String id, String layer) {
        return new ModelLayerLocation(OperationStarcleave.id(id), layer);
    }
}

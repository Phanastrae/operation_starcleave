package phanastrae.operation_starcleave.render.entity;

import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.entity.StarcleaverGolemEntity;
import phanastrae.operation_starcleave.render.entity.model.StarcleaverGolemEntityModel;

public class StarcleaverGolemEntityRenderer extends MobEntityRenderer<StarcleaverGolemEntity, StarcleaverGolemEntityModel> {

    public StarcleaverGolemEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new StarcleaverGolemEntityModel(context.getPart(OperationStarcleaveEntityRenderers.MODEL_STARCLEAVER_GOLEM_LAYER)), 0.3f);
    }

    @Override
    public Identifier getTexture(StarcleaverGolemEntity entity) {
        return OperationStarcleave.id("textures/entity/starcleaver_golem.png");
    }

    @Override
    public boolean shouldRender(StarcleaverGolemEntity mobEntity, Frustum frustum, double d, double e, double f) {
        if(mobEntity.isIgnited()) return true;

        return super.shouldRender(mobEntity, frustum, d, e, f);
    }
}

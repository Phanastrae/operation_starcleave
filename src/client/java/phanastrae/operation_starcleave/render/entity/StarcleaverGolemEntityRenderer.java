package phanastrae.operation_starcleave.render.entity;

import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.entity.StarcleaverGolemEntity;
import phanastrae.operation_starcleave.render.entity.model.StarcleaverGolemEntityModel;

public class StarcleaverGolemEntityRenderer extends MobEntityRenderer<StarcleaverGolemEntity, StarcleaverGolemEntityModel> {

    public StarcleaverGolemEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new StarcleaverGolemEntityModel(context.getPart(OperationStarcleaveEntityRenderers.MODEL_STARCLEAVER_GOLEM_LAYER)), 0.3f);
    }

    @Override
    public void render(StarcleaverGolemEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        if(mobEntity.isPlummeting()) {
            matrixStack.multiply(new Quaternionf().rotateZ((float) Math.PI));
            matrixStack.translate(0, -1, 0);
        }
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
        matrixStack.pop();
    }

    @Override
    public Identifier getTexture(StarcleaverGolemEntity entity) {
        return OperationStarcleave.id("textures/entity/starcleaver_golem.png");
    }

    @Override
    public boolean shouldRender(StarcleaverGolemEntity mobEntity, Frustum frustum, double d, double e, double f) {
        if(mobEntity.isIgnited() || mobEntity.isPlummeting()) {
            if(frustum.isVisible(mobEntity.getVisibilityBoundingBox())) {
                return true;
            }
        }

        return super.shouldRender(mobEntity, frustum, d, e, f);
    }
}

package phanastrae.operation_starcleave.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaternionf;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.client.render.entity.model.OperationStarcleaveEntityModelLayers;
import phanastrae.operation_starcleave.client.render.entity.model.StarcleaverGolemEntityModel;
import phanastrae.operation_starcleave.entity.mob.StarcleaverGolemEntity;

public class StarcleaverGolemEntityRenderer extends MobRenderer<StarcleaverGolemEntity, StarcleaverGolemEntityModel<StarcleaverGolemEntity>> {

    public StarcleaverGolemEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new StarcleaverGolemEntityModel<>(context.bakeLayer(OperationStarcleaveEntityModelLayers.STARCLEAVER_GOLEM)), 0.3f);
    }

    @Override
    public void render(StarcleaverGolemEntity mobEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i) {
        matrixStack.pushPose();
        if(mobEntity.isPlummeting()) {
            matrixStack.mulPose(new Quaternionf().rotateZ((float) Math.PI));
            matrixStack.translate(0, -1, 0);
        }
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
        matrixStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(StarcleaverGolemEntity entity) {
        return OperationStarcleave.id("textures/entity/starcleaver_golem.png");
    }

    @Override
    public boolean shouldRender(StarcleaverGolemEntity mobEntity, Frustum frustum, double d, double e, double f) {
        if(mobEntity.isIgnited() || mobEntity.isPlummeting()) {
            if(frustum.isVisible(mobEntity.getBoundingBoxForCulling())) {
                return true;
            }
        }

        return super.shouldRender(mobEntity, frustum, d, e, f);
    }
}

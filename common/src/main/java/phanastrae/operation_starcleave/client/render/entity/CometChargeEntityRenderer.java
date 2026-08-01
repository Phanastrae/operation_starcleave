package phanastrae.operation_starcleave.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.client.render.entity.model.CometChargeEntityModel;
import phanastrae.operation_starcleave.client.render.entity.model.OperationStarcleaveEntityModelLayers;
import phanastrae.operation_starcleave.entity.projectile.CometChargeEntity;

public class CometChargeEntityRenderer extends EntityRenderer<CometChargeEntity> {
    private static final float MIN_CAMERA_DISTANCE_SQUARED = Mth.square(2.5F);
    public static final ResourceLocation TEXTURE_LOCATION = OperationStarcleave.id("textures/entity/projectiles/comet_charge.png");
    private final CometChargeEntityModel<CometChargeEntity> model;

    public CometChargeEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new CometChargeEntityModel<>(context.bakeLayer(OperationStarcleaveEntityModelLayers.COMET_CHARGE));
    }

    public void render(CometChargeEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.tickCount >= 4 || this.entityRenderDispatcher.camera.getEntity().distanceToSqr(entity) >= MIN_CAMERA_DISTANCE_SQUARED) {
            VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity)));
            int light = LightTexture.pack(Math.max(9, LightTexture.block(packedLight)), LightTexture.sky(packedLight));

            poseStack.pushPose();

            poseStack.translate(0, entity.getBbHeight() / 2, 0);

            poseStack.mulPose(Axis.YP.rotationDegrees(Mth.rotLerp(partialTicks, entity.yRotO, entity.getYRot()) + 180.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(Mth.rotLerp(partialTicks, entity.xRotO, entity.getXRot())));

            poseStack.translate(0, -0.1875, 0);

            poseStack.scale(-1.0F, -1.0F, 1.0F);
            poseStack.translate(0.0F, -1.501F, 0.0F);

            this.model.renderToBuffer(poseStack, consumer, light, OverlayTexture.NO_OVERLAY);

            poseStack.popPose();
            super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(CometChargeEntity entity) {
        return TEXTURE_LOCATION;
    }
}

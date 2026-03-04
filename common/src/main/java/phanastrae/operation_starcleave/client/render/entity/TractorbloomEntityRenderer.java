package phanastrae.operation_starcleave.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.client.render.entity.model.OperationStarcleaveEntityModelLayers;
import phanastrae.operation_starcleave.client.render.entity.model.TractorbloomEntityModel;
import phanastrae.operation_starcleave.entity.mob.TractorbloomEntity;

public class TractorbloomEntityRenderer extends MobRenderer<TractorbloomEntity, TractorbloomEntityModel<TractorbloomEntity>> {
    private static final ResourceLocation TEXTURE = OperationStarcleave.id("textures/entity/tractorbloom.png");

    public TractorbloomEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new TractorbloomEntityModel<>(context.bakeLayer(OperationStarcleaveEntityModelLayers.TRACTORBLOOM)), 0.9f);
    }

    @Override
    public void render(TractorbloomEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        // lerp x,z velocities
        float deltaX = Mth.lerp(partialTicks, entity.prevDeltaX, entity.deltaX);
        float deltaZ = Mth.lerp(partialTicks, entity.prevDeltaZ, entity.deltaZ);
        // calculate and check length
        double len = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        if (len > 1E-5) {
            // calc rotation axis + angle
            float rotationAxisX = (float) (deltaZ / len);
            float rotationAxisZ = (float) (-deltaX / len);
            double blocksPerSecond = len * 20;
            float angle = (float) Math.toRadians(Mth.clamp(Math.sqrt(blocksPerSecond) * 20, 0, 40));

            // rotate to tilt in direction of movement
            poseStack.mulPose(new Quaternionf().rotateAxis(angle, rotationAxisX, 0, rotationAxisZ));
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(TractorbloomEntity entity) {
        return TEXTURE;
    }
}

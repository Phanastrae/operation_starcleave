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
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.client.render.entity.model.BismuthBlastEntityModel;
import phanastrae.operation_starcleave.client.render.entity.model.OperationStarcleaveEntityModelLayers;
import phanastrae.operation_starcleave.entity.projectile.BismuthBlastEntity;

public class BismuthBlastEntityRenderer extends EntityRenderer<BismuthBlastEntity> {
    public static final ResourceLocation TEXTURE_LOCATION = OperationStarcleave.id("textures/entity/projectiles/bismuth_blast.png");
    private final BismuthBlastEntityModel<BismuthBlastEntity> model;

    public BismuthBlastEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new BismuthBlastEntityModel<>(context.bakeLayer(OperationStarcleaveEntityModelLayers.BISMUTH_BLAST));
    }

    public void render(BismuthBlastEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity)));
        int light = LightTexture.pack(15, LightTexture.sky(packedLight));
        float initialAgeFraction = (Math.min(entity.getAge() + partialTicks, 7F) / 7F);
        float scale = 1.5F * initialAgeFraction;
        float delayedInitialAgeFraction = (Math.clamp(entity.getAge() + partialTicks - 6F, 0F, 4F) / 4F);
        int outerColor = getColor(entity.getAge(), partialTicks, 1F - 0.35F * delayedInitialAgeFraction);
        int innerColor = getColor(entity.getAge() + 2, partialTicks, 1F - 0.45F * delayedInitialAgeFraction);

        poseStack.pushPose();

        poseStack.translate(0, entity.getBbHeight() / 2, 0);

        poseStack.scale(scale, scale, scale);

        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.rotLerp(partialTicks, entity.yRotO, entity.getYRot()) + 180.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(Mth.rotLerp(partialTicks, entity.xRotO, entity.getXRot())));

        poseStack.translate(0, -0.125, 0);

        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0F, -1.501F, 0.0F);

        this.model.getOuter().render(poseStack, consumer, light, OverlayTexture.NO_OVERLAY, outerColor);
        this.model.getInner().render(poseStack, consumer, light, OverlayTexture.NO_OVERLAY, innerColor);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    public void vertex(
            PoseStack.Pose pose,
            VertexConsumer consumer,
            int x,
            int y,
            int z,
            int color,
            float u,
            float v,
            int normalX,
            int normalY,
            int normalZ,
            int packedLight
    ) {
        consumer.addVertex(pose, x, y, z)
                .setColor(color)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(pose, normalX, normalZ, normalY);
    }

    public int getColor(int age, float partialTicks, float whiteness) {
        float twopi = 2 * Mth.PI;

        float angle = twopi * (age % 30 + partialTicks) / 30F;
        float base = 0.5F * (1 + whiteness);
        float fluctuation = 0.5F * (1 - whiteness);

        float red = Mth.sin(angle) * fluctuation + base;
        float green = Mth.sin((angle + twopi / 3f)) * fluctuation + base;
        float blue = Mth.sin((angle - twopi / 3f)) * fluctuation + base;
        return FastColor.ARGB32.colorFromFloat(1.0F, red, green, blue);
    }

    @Override
    public ResourceLocation getTextureLocation(BismuthBlastEntity entity) {
        return TEXTURE_LOCATION;
    }
}

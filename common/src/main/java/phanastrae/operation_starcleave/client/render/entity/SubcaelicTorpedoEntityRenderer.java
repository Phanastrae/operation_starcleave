package phanastrae.operation_starcleave.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.joml.Matrix4f;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.client.render.OperationStarcleaveRenderLayers;
import phanastrae.operation_starcleave.client.render.entity.layers.SubcaelicTorpedoOverlayLayer;
import phanastrae.operation_starcleave.client.render.entity.model.OperationStarcleaveEntityModelLayers;
import phanastrae.operation_starcleave.client.render.entity.model.SubcaelicTorpedoEntityModel;
import phanastrae.operation_starcleave.entity.mob.SubcaelicTorpedoEntity;

public class SubcaelicTorpedoEntityRenderer extends MobRenderer<SubcaelicTorpedoEntity, SubcaelicTorpedoEntityModel<SubcaelicTorpedoEntity>> {
    private static final ResourceLocation TEXTURE = OperationStarcleave.id("textures/entity/subcaelic_torpedo/subcaelic_torpedo.png");

    private static final float HALF_SQRT_3 = (float)(Math.sqrt(3.0) / 2.0);

    public SubcaelicTorpedoEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new SubcaelicTorpedoEntityModel<>(context.bakeLayer(OperationStarcleaveEntityModelLayers.SUBCAELIC_TORPEDO)), 0.7f);
        this.addLayer(new SubcaelicTorpedoOverlayLayer<>(this, context.getModelSet()));
    }

    @Override
    public void render(SubcaelicTorpedoEntity entity, float yaw, float tickDelta, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light) {
        super.render(entity, yaw, tickDelta, matrixStack, vertexConsumerProvider, light);

        if(entity.isPrimed()) {
            matrixStack.pushPose();
            matrixStack.translate(0, 0.5, 0);
            float l = entity.getClientFuseTime(tickDelta);
            float l2 = l * l * l * l;
            RandomSource random = RandomSource.create(432L);

            VertexConsumer vertexConsumer4 = vertexConsumerProvider.getBuffer(OperationStarcleaveRenderLayers.getSkyRay());

            float rayCount = (l + l2) / 2.0F * 25.0F;
            for (int n = 0; (float) n < rayCount; ++n) {
                matrixStack.pushPose();
                matrixStack.mulPose(Axis.XP.rotationDegrees(random.nextFloat() * 360.0F));
                matrixStack.mulPose(Axis.YP.rotationDegrees(random.nextFloat() * 360.0F));
                matrixStack.mulPose(Axis.ZP.rotationDegrees(random.nextFloat() * 360.0F));
                matrixStack.mulPose(Axis.XP.rotationDegrees(random.nextFloat() * 360.0F));
                matrixStack.mulPose(Axis.YP.rotationDegrees(random.nextFloat() * 360.0F));
                matrixStack.mulPose(Axis.ZP.rotationDegrees(random.nextFloat() * 360.0F + l * 90.0F));

                float radius = 0.5F + 0.2F * random.nextFloat() + 6.0F * l2;
                float width = 0.4F + 0.2F * random.nextFloat() + 1.2F * l2;
                Matrix4f matrix4f = matrixStack.last().pose();
                int alpha = (int) (255.0F * (1.0F - l2));

                float twopi = 2 * Mth.PI;
                float red = Mth.sin(n) * 0.2f + 0.8f;
                float green = Mth.sin(n + 1 / 3f * twopi) * 0.2f + 0.8f;
                float blue = Mth.sin(n + 2 / 3f * twopi) * 0.2f + 0.8f;

                int r = (int) (red * alpha) & 0xFF;
                int g = (int) (green * alpha) & 0xFF;
                int b = (int) (blue * alpha) & 0xFF;

                putDeathLightSourceVertex(vertexConsumer4, matrix4f, r, g, b);
                putDeathLightNegativeXTerminalVertex(vertexConsumer4, matrix4f, radius, width);
                putDeathLightPositiveXTerminalVertex(vertexConsumer4, matrix4f, radius, width);
                putDeathLightNegativeXTerminalVertex(vertexConsumer4, matrix4f, radius, width);

                putDeathLightSourceVertex(vertexConsumer4, matrix4f, r, g, b);
                putDeathLightNegativeXTerminalVertex(vertexConsumer4, matrix4f, radius, width);
                putDeathLightPositiveZTerminalVertex(vertexConsumer4, matrix4f, radius, width);
                putDeathLightNegativeXTerminalVertex(vertexConsumer4, matrix4f, radius, width);

                putDeathLightSourceVertex(vertexConsumer4, matrix4f, r, g, b);
                putDeathLightPositiveXTerminalVertex(vertexConsumer4, matrix4f, radius, width);
                putDeathLightPositiveZTerminalVertex(vertexConsumer4, matrix4f, radius, width);
                putDeathLightPositiveXTerminalVertex(vertexConsumer4, matrix4f, radius, width);

                matrixStack.popPose();
            }
            matrixStack.popPose();
        }
    }

    private static void putDeathLightSourceVertex(VertexConsumer buffer, Matrix4f matrix, int r, int g, int b) {
        buffer.addVertex(matrix, 0.0F, 0.0F, 0.0F).setColor(r, g, b, 255);
    }

    private static void putDeathLightNegativeXTerminalVertex(VertexConsumer buffer, Matrix4f matrix, float radius, float width) {
        buffer.addVertex(matrix, -HALF_SQRT_3 * width, radius, -0.5F * width).setColor(0, 0, 0, 0);
    }

    private static void putDeathLightPositiveXTerminalVertex(VertexConsumer buffer, Matrix4f matrix, float radius, float width) {
        buffer.addVertex(matrix, HALF_SQRT_3 * width, radius, -0.5F * width).setColor(0, 0, 0, 0);
    }

    private static void putDeathLightPositiveZTerminalVertex(VertexConsumer buffer, Matrix4f matrix, float radius, float width) {
        buffer.addVertex(matrix, 0.0F, radius, 1.0F * width).setColor(0, 0, 0, 0);
    }

    @Override
    protected void setupRotations(SubcaelicTorpedoEntity entity, PoseStack matrixStack, float animationProgress, float bodyYaw, float tickDelta, float scale) {
        float i = Mth.rotLerp(tickDelta, entity.prevTiltAngle, entity.tiltAngle);
        float j = Mth.rotLerp(tickDelta, entity.prevRollAngle, entity.rollAngle);
        matrixStack.translate(0.0F, 0.5F, 0.0F);
        matrixStack.mulPose(Axis.YP.rotationDegrees(180.0F -  bodyYaw));
        matrixStack.mulPose(Axis.XP.rotationDegrees(i));
        matrixStack.mulPose(Axis.YP.rotationDegrees(j));
        matrixStack.translate(0.0F, -1.0F, 0.0F);
    }

    @Override
    protected void scale(SubcaelicTorpedoEntity entity, PoseStack matrixStack, float f) {
        float g = entity.getClientFuseTime(f);
        float h = 1.0F + Mth.sin(g * 100.0F) * g * 0.01F;
        g = Mth.clamp(g, 0.0F, 1.0F);
        g *= g;
        g *= g;
        float i = (1.0F + g * 0.4F) * h;
        float j = (1.0F + g * 0.1F) / h;
        matrixStack.scale(i, j, i);
    }

    @Override
    protected float getAttackAnim(SubcaelicTorpedoEntity entity, float f) {
        float g = entity.getClientFuseTime(f);
        return (int)(g * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(g, 0.5F, 1.0F);
    }

    @Override
    protected float getBob(SubcaelicTorpedoEntity entity, float tickDelta) {
        float a = Mth.rotLerp(tickDelta, entity.prevTentacleRollAngle, entity.tentacleRollAngle);
        return 0.5f + 0.5f * (float)Math.sin(0.1f * a);
    }

    @Override
    public ResourceLocation getTextureLocation(SubcaelicTorpedoEntity entity) {
        return TEXTURE;
    }
}

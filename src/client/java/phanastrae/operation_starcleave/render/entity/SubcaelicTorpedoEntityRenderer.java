package phanastrae.operation_starcleave.render.entity;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix4f;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.entity.mob.SubcaelicTorpedoEntity;
import phanastrae.operation_starcleave.render.OperationStarcleaveRenderLayers;
import phanastrae.operation_starcleave.render.entity.feature.SubcaelicTorpedoOverlayFeatureRenderer;
import phanastrae.operation_starcleave.render.entity.model.OperationStarcleaveEntityModelLayers;
import phanastrae.operation_starcleave.render.entity.model.SubcaelicTorpedoEntityModel;

public class SubcaelicTorpedoEntityRenderer extends MobEntityRenderer<SubcaelicTorpedoEntity, SubcaelicTorpedoEntityModel<SubcaelicTorpedoEntity>> {
    private static final Identifier TEXTURE = OperationStarcleave.id("textures/entity/subcaelic_torpedo/subcaelic_torpedo.png");

    private static final float HALF_SQRT_3 = (float)(Math.sqrt(3.0) / 2.0);

    public SubcaelicTorpedoEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new SubcaelicTorpedoEntityModel<>(context.getPart(OperationStarcleaveEntityModelLayers.SUBCAELIC_TORPEDO)), 0.7f);
        this.addFeature(new SubcaelicTorpedoOverlayFeatureRenderer<>(this, context.getModelLoader()));
    }

    @Override
    public void render(SubcaelicTorpedoEntity entity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
        super.render(entity, yaw, tickDelta, matrixStack, vertexConsumerProvider, light);

        if(entity.isPrimed()) {
            matrixStack.push();
            matrixStack.translate(0, 0.5, 0);
            float l = entity.getClientFuseTime(tickDelta);
            float l2 = l * l * l * l;
            Random random = Random.create(432L);

            VertexConsumer vertexConsumer4 = vertexConsumerProvider.getBuffer(OperationStarcleaveRenderLayers.getSkyRay());

            float rayCount = (l + l2) / 2.0F * 25.0F;
            for (int n = 0; (float) n < rayCount; ++n) {
                matrixStack.push();
                matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(random.nextFloat() * 360.0F));
                matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(random.nextFloat() * 360.0F));
                matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(random.nextFloat() * 360.0F));
                matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(random.nextFloat() * 360.0F));
                matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(random.nextFloat() * 360.0F));
                matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(random.nextFloat() * 360.0F + l * 90.0F));

                float radius = 0.5F + 0.2F * random.nextFloat() + 6.0F * l2;
                float width = 0.4F + 0.2F * random.nextFloat() + 1.2F * l2;
                Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
                int alpha = (int) (255.0F * (1.0F - l2));

                float twopi = 2 * MathHelper.PI;
                float red = MathHelper.sin(n) * 0.2f + 0.8f;
                float green = MathHelper.sin(n + 1 / 3f * twopi) * 0.2f + 0.8f;
                float blue = MathHelper.sin(n + 2 / 3f * twopi) * 0.2f + 0.8f;

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

                matrixStack.pop();
            }
            matrixStack.pop();
        }
    }

    private static void putDeathLightSourceVertex(VertexConsumer buffer, Matrix4f matrix, int r, int g, int b) {
        buffer.vertex(matrix, 0.0F, 0.0F, 0.0F).color(r, g, b, 255).next();
    }

    private static void putDeathLightNegativeXTerminalVertex(VertexConsumer buffer, Matrix4f matrix, float radius, float width) {
        buffer.vertex(matrix, -HALF_SQRT_3 * width, radius, -0.5F * width).color(0, 0, 0, 0).next();
    }

    private static void putDeathLightPositiveXTerminalVertex(VertexConsumer buffer, Matrix4f matrix, float radius, float width) {
        buffer.vertex(matrix, HALF_SQRT_3 * width, radius, -0.5F * width).color(0, 0, 0, 0).next();
    }

    private static void putDeathLightPositiveZTerminalVertex(VertexConsumer buffer, Matrix4f matrix, float radius, float width) {
        buffer.vertex(matrix, 0.0F, radius, 1.0F * width).color(0, 0, 0, 0).next();
    }

    @Override
    protected void setupTransforms(SubcaelicTorpedoEntity entity, MatrixStack matrixStack, float animationProgress, float bodyYaw, float tickDelta) {
        float i = MathHelper.lerpAngleDegrees(tickDelta, entity.prevTiltAngle, entity.tiltAngle);
        float j = MathHelper.lerpAngleDegrees(tickDelta, entity.prevRollAngle, entity.rollAngle);
        matrixStack.translate(0.0F, 0.5F, 0.0F);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F -  bodyYaw));
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(i));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));
        matrixStack.translate(0.0F, -1.0F, 0.0F);
    }

    @Override
    protected void scale(SubcaelicTorpedoEntity entity, MatrixStack matrixStack, float f) {
        float g = entity.getClientFuseTime(f);
        float h = 1.0F + MathHelper.sin(g * 100.0F) * g * 0.01F;
        g = MathHelper.clamp(g, 0.0F, 1.0F);
        g *= g;
        g *= g;
        float i = (1.0F + g * 0.4F) * h;
        float j = (1.0F + g * 0.1F) / h;
        matrixStack.scale(i, j, i);
    }

    @Override
    protected float getAnimationCounter(SubcaelicTorpedoEntity entity, float f) {
        float g = entity.getClientFuseTime(f);
        return (int)(g * 10.0F) % 2 == 0 ? 0.0F : MathHelper.clamp(g, 0.5F, 1.0F);
    }

    @Override
    protected float getAnimationProgress(SubcaelicTorpedoEntity entity, float tickDelta) {
        float a = MathHelper.lerpAngleDegrees(tickDelta, entity.prevTentacleRollAngle, entity.tentacleRollAngle);
        return 0.5f + 0.5f * (float)Math.sin(0.1f * a);
    }

    @Override
    public Identifier getTexture(SubcaelicTorpedoEntity entity) {
        return TEXTURE;
    }
}

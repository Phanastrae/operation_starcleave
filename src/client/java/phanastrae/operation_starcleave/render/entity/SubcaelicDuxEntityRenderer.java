package phanastrae.operation_starcleave.render.entity;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix4f;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.entity.mob.SubcaelicDuxEntity;
import phanastrae.operation_starcleave.render.OperationStarcleaveRenderLayers;
import phanastrae.operation_starcleave.render.entity.feature.SubcaelicDuxFeatureRenderer;
import phanastrae.operation_starcleave.render.entity.model.OperationStarcleaveEntityModelLayers;
import phanastrae.operation_starcleave.render.entity.model.SubcaelicDuxEntityModel;

public class SubcaelicDuxEntityRenderer extends MobEntityRenderer<SubcaelicDuxEntity, SubcaelicDuxEntityModel<SubcaelicDuxEntity>> {
    private static final Identifier TEXTURE = OperationStarcleave.id("textures/entity/subcaelic_dux/subcaelic_dux.png");
    private static final Identifier GLOW_1_TEXTURE = OperationStarcleave.id("textures/entity/subcaelic_dux/subcaelic_dux_glow_1.png");
    private static final Identifier GLOW_2_TEXTURE = OperationStarcleave.id("textures/entity/subcaelic_dux/subcaelic_dux_glow_2.png");
    private static final Identifier GLOW_3_TEXTURE = OperationStarcleave.id("textures/entity/subcaelic_dux/subcaelic_dux_glow_3.png");
    private static final float HALF_SQRT_3 = (float)(Math.sqrt(3.0) / 2.0);

    private final Random random = Random.create();

    public SubcaelicDuxEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new SubcaelicDuxEntityModel<>(context.getPart(OperationStarcleaveEntityModelLayers.SUBCAELIC_DUX)), 3f);
        this.addFeature(
                new SubcaelicDuxFeatureRenderer<>(this, GLOW_1_TEXTURE, (dux, tickDelta, animationProgress) -> MathHelper.sin((0.1f * (float)Math.toRadians(MathHelper.lerpAngleDegrees(tickDelta, dux.prevTentacleRollAngle, dux.tentacleRollAngle))) * 2f * (float)Math.PI) * 0.5f + 0.5f, SubcaelicDuxEntityModel::getGlowingParts)
        );
        this.addFeature(
                new SubcaelicDuxFeatureRenderer<>(this, GLOW_2_TEXTURE, (dux, tickDelta, animationProgress) -> MathHelper.sin((0.1f * (float)Math.toRadians(MathHelper.lerpAngleDegrees(tickDelta, dux.prevTentacleRollAngle, dux.tentacleRollAngle)) + 1/3f) * 2f * (float)Math.PI) * 0.5f + 0.5f, SubcaelicDuxEntityModel::getGlowingParts)
        );
        this.addFeature(
                new SubcaelicDuxFeatureRenderer<>(this, GLOW_3_TEXTURE, (dux, tickDelta, animationProgress) -> MathHelper.sin((0.1f * (float)Math.toRadians(MathHelper.lerpAngleDegrees(tickDelta, dux.prevTentacleRollAngle, dux.tentacleRollAngle)) - 1/3f) * 2f * (float)Math.PI) * 0.5f + 0.5f, SubcaelicDuxEntityModel::getGlowingParts)
        );
    }

    @Override
    public void render(SubcaelicDuxEntity entity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
        super.render(entity, yaw, tickDelta, matrixStack, vertexConsumerProvider, light);

        if(entity.isDead()) {
            matrixStack.push();
            matrixStack.scale(7, 7, 7);
            matrixStack.translate(0, 0.5, 0);
            float l = entity.getExplosionGlowProgress();
            float l2 = l * l * l * l;
            Random random = Random.create(432L);

            VertexConsumer vertexConsumer4 = vertexConsumerProvider.getBuffer(OperationStarcleaveRenderLayers.getSkyRay());

            float rayCount = (l + l2) / 2.0F * 90.0F;
            for (int n = 0; (float) n < rayCount; ++n) {
                matrixStack.push();
                matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(random.nextFloat() * 360.0F));
                matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(random.nextFloat() * 360.0F));
                matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(random.nextFloat() * 360.0F));
                matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(random.nextFloat() * 360.0F));
                matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(random.nextFloat() * 360.0F));
                matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(random.nextFloat() * 360.0F + l * 90.0F));

                float radius = 0.8F + 0.7F * random.nextFloat() + 6.0F * l2;
                float width = 0.3F + 0.2F * random.nextFloat() + 0.3F * l2;
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
        buffer.vertex(matrix, 0.0F, 0.0F, 0.0F).color(r, g, b, 255);
    }

    private static void putDeathLightNegativeXTerminalVertex(VertexConsumer buffer, Matrix4f matrix, float radius, float width) {
        buffer.vertex(matrix, -HALF_SQRT_3 * width, radius, -0.5F * width).color(0, 0, 0, 0);
    }

    private static void putDeathLightPositiveXTerminalVertex(VertexConsumer buffer, Matrix4f matrix, float radius, float width) {
        buffer.vertex(matrix, HALF_SQRT_3 * width, radius, -0.5F * width).color(0, 0, 0, 0);
    }

    private static void putDeathLightPositiveZTerminalVertex(VertexConsumer buffer, Matrix4f matrix, float radius, float width) {
        buffer.vertex(matrix, 0.0F, radius, 1.0F * width).color(0, 0, 0, 0);
    }

    @Override
    protected void setupTransforms(SubcaelicDuxEntity duxEntity, MatrixStack matrixStack, float animationProgress, float bodyYaw, float tickDelta, float scale) {
        float i = MathHelper.lerpAngleDegrees(tickDelta, duxEntity.prevTiltAngle, duxEntity.tiltAngle);
        float j = MathHelper.lerpAngleDegrees(tickDelta, duxEntity.prevRollAngle, duxEntity.rollAngle);
        matrixStack.translate(0.0F, 2.5F, 0.0F);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F -  bodyYaw));
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(i));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));
        matrixStack.translate(0.0F, -4.0F, 0.0F);
    }

    @Override
    protected void scale(SubcaelicDuxEntity entity, MatrixStack matrices, float amount) {
        matrices.scale(5, 5, 5);
    }

    @Override
    protected float getAnimationCounter(SubcaelicDuxEntity entity, float f) {
        if(entity.isDead()) {
            float d = entity.ticksSinceDeath / 200f;
            return (float) MathHelper.clamp(d * 1.5, 0.0, 1.0);
        } else {
            return 0;
        }
    }

    @Override
    public Vec3d getPositionOffset(SubcaelicDuxEntity entity, float tickDelta) {
        if (entity.isDead()) {
            double d = (entity.ticksSinceDeath / 200f);
            d = d * d * 0.9;
            return new Vec3d(this.random.nextGaussian() * d, this.random.nextGaussian() * d, this.random.nextGaussian() * d);
        } else {
            return super.getPositionOffset(entity, tickDelta);
        }
    }

    @Override
    protected float getAnimationProgress(SubcaelicDuxEntity entity, float tickDelta) {
        float a = MathHelper.lerpAngleDegrees(tickDelta, entity.prevTentacleRollAngle, entity.tentacleRollAngle);
        return 0.5f + 0.5f * (float)Math.sin(0.02f * a);
    }

    @Override
    public Identifier getTexture(SubcaelicDuxEntity duxEntity) {
        return TEXTURE;
    }
}

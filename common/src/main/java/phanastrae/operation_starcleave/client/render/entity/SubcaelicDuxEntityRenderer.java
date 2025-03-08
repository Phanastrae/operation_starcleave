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
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.client.render.OperationStarcleaveRenderLayers;
import phanastrae.operation_starcleave.client.render.entity.layers.SubcaelicDuxLayer;
import phanastrae.operation_starcleave.client.render.entity.model.OperationStarcleaveEntityModelLayers;
import phanastrae.operation_starcleave.client.render.entity.model.SubcaelicDuxEntityModel;
import phanastrae.operation_starcleave.entity.mob.SubcaelicDuxEntity;

public class SubcaelicDuxEntityRenderer extends MobRenderer<SubcaelicDuxEntity, SubcaelicDuxEntityModel<SubcaelicDuxEntity>> {
    private static final ResourceLocation TEXTURE = OperationStarcleave.id("textures/entity/subcaelic_dux/subcaelic_dux.png");
    private static final ResourceLocation GLOW_1_TEXTURE = OperationStarcleave.id("textures/entity/subcaelic_dux/subcaelic_dux_glow_1.png");
    private static final ResourceLocation GLOW_2_TEXTURE = OperationStarcleave.id("textures/entity/subcaelic_dux/subcaelic_dux_glow_2.png");
    private static final ResourceLocation GLOW_3_TEXTURE = OperationStarcleave.id("textures/entity/subcaelic_dux/subcaelic_dux_glow_3.png");
    private static final float HALF_SQRT_3 = (float)(Math.sqrt(3.0) / 2.0);

    private final RandomSource random = RandomSource.create();

    public SubcaelicDuxEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new SubcaelicDuxEntityModel<>(context.bakeLayer(OperationStarcleaveEntityModelLayers.SUBCAELIC_DUX)), 3f);
        this.addLayer(
                new SubcaelicDuxLayer<>(this, GLOW_1_TEXTURE, (dux, tickDelta, animationProgress) -> Mth.sin((0.1f * (float)Math.toRadians(Mth.rotLerp(tickDelta, dux.prevTentacleRollAngle, dux.tentacleRollAngle))) * 2f * (float)Math.PI) * 0.5f + 0.5f, SubcaelicDuxEntityModel::getGlowingParts)
        );
        this.addLayer(
                new SubcaelicDuxLayer<>(this, GLOW_2_TEXTURE, (dux, tickDelta, animationProgress) -> Mth.sin((0.1f * (float)Math.toRadians(Mth.rotLerp(tickDelta, dux.prevTentacleRollAngle, dux.tentacleRollAngle)) + 1/3f) * 2f * (float)Math.PI) * 0.5f + 0.5f, SubcaelicDuxEntityModel::getGlowingParts)
        );
        this.addLayer(
                new SubcaelicDuxLayer<>(this, GLOW_3_TEXTURE, (dux, tickDelta, animationProgress) -> Mth.sin((0.1f * (float)Math.toRadians(Mth.rotLerp(tickDelta, dux.prevTentacleRollAngle, dux.tentacleRollAngle)) - 1/3f) * 2f * (float)Math.PI) * 0.5f + 0.5f, SubcaelicDuxEntityModel::getGlowingParts)
        );
    }

    @Override
    public void render(SubcaelicDuxEntity entity, float yaw, float tickDelta, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light) {
        super.render(entity, yaw, tickDelta, matrixStack, vertexConsumerProvider, light);

        if(entity.isDeadOrDying()) {
            matrixStack.pushPose();
            matrixStack.scale(7, 7, 7);
            matrixStack.translate(0, 0.5, 0);
            float l = entity.getExplosionGlowProgress();
            float l2 = l * l * l * l;
            RandomSource random = RandomSource.create(432L);

            VertexConsumer vertexConsumer4 = vertexConsumerProvider.getBuffer(OperationStarcleaveRenderLayers.getSkyRay());

            float rayCount = (l + l2) / 2.0F * 90.0F;
            for (int n = 0; (float) n < rayCount; ++n) {
                matrixStack.pushPose();
                matrixStack.mulPose(Axis.XP.rotationDegrees(random.nextFloat() * 360.0F));
                matrixStack.mulPose(Axis.YP.rotationDegrees(random.nextFloat() * 360.0F));
                matrixStack.mulPose(Axis.ZP.rotationDegrees(random.nextFloat() * 360.0F));
                matrixStack.mulPose(Axis.XP.rotationDegrees(random.nextFloat() * 360.0F));
                matrixStack.mulPose(Axis.YP.rotationDegrees(random.nextFloat() * 360.0F));
                matrixStack.mulPose(Axis.ZP.rotationDegrees(random.nextFloat() * 360.0F + l * 90.0F));

                float radius = 0.8F + 0.7F * random.nextFloat() + 6.0F * l2;
                float width = 0.3F + 0.2F * random.nextFloat() + 0.3F * l2;
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
    protected void setupRotations(SubcaelicDuxEntity duxEntity, PoseStack matrixStack, float animationProgress, float bodyYaw, float tickDelta, float scale) {
        float i = Mth.rotLerp(tickDelta, duxEntity.prevTiltAngle, duxEntity.tiltAngle);
        float j = Mth.rotLerp(tickDelta, duxEntity.prevRollAngle, duxEntity.rollAngle);
        matrixStack.translate(0.0F, 2.5F, 0.0F);
        matrixStack.mulPose(Axis.YP.rotationDegrees(180.0F -  bodyYaw));
        matrixStack.mulPose(Axis.XP.rotationDegrees(i));
        matrixStack.mulPose(Axis.YP.rotationDegrees(j));
        matrixStack.translate(0.0F, -4.0F, 0.0F);
    }

    @Override
    protected void scale(SubcaelicDuxEntity entity, PoseStack matrices, float amount) {
        matrices.scale(5, 5, 5);
    }

    @Override
    protected float getAttackAnim(SubcaelicDuxEntity entity, float f) {
        if(entity.isDeadOrDying()) {
            float d = entity.ticksSinceDeath / 200f;
            return (float) Mth.clamp(d * 1.5, 0.0, 1.0);
        } else {
            return 0;
        }
    }

    @Override
    public Vec3 getRenderOffset(SubcaelicDuxEntity entity, float tickDelta) {
        if (entity.isDeadOrDying()) {
            double d = (entity.ticksSinceDeath / 200f);
            d = d * d * 0.9;
            return new Vec3(this.random.nextGaussian() * d, this.random.nextGaussian() * d, this.random.nextGaussian() * d);
        } else {
            return super.getRenderOffset(entity, tickDelta);
        }
    }

    @Override
    protected float getBob(SubcaelicDuxEntity entity, float tickDelta) {
        float a = Mth.rotLerp(tickDelta, entity.prevTentacleRollAngle, entity.tentacleRollAngle);
        return 0.5f + 0.5f * (float)Math.sin(0.02f * a);
    }

    @Override
    public ResourceLocation getTextureLocation(SubcaelicDuxEntity duxEntity) {
        return TEXTURE;
    }
}

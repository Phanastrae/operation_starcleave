package phanastrae.operation_starcleave.client.render.entity.feature;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.client.render.entity.model.OperationStarcleaveEntityModelLayers;
import phanastrae.operation_starcleave.client.render.entity.model.SubcaelicTorpedoEntityModel;
import phanastrae.operation_starcleave.entity.mob.SubcaelicTorpedoEntity;

public class SubcaelicTorpedoOverlayFeatureRenderer<T extends SubcaelicTorpedoEntity, M extends SubcaelicTorpedoEntityModel<T>> extends FeatureRenderer<T, M> {
    private static final Identifier TEXTURE = OperationStarcleave.id("textures/entity/subcaelic_torpedo/subcaelic_torpedo_overlay.png");
    private final EntityModel<T> model;

    public SubcaelicTorpedoOverlayFeatureRenderer(FeatureRendererContext<T, M> context, EntityModelLoader loader) {
        super(context);
        this.model = new SubcaelicTorpedoEntityModel<>(loader.getModelPart(OperationStarcleaveEntityModelLayers.SUBCAELIC_TORPEDO_OVERLAY));
    }

    public void render(
            MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, T torpedo, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch
    ) {
        if(!torpedo.isPrimed()) return;

        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        boolean bl = minecraftClient.hasOutline(torpedo) && torpedo.isInvisible();
        if (!torpedo.isInvisible() || bl) {
            VertexConsumer vertexConsumer;
            if (bl) {
                vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getOutline(this.getTexture(torpedo)));
            } else {
                vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(this.getTexture(torpedo)));
            }

            this.getContextModel().copyStateTo(this.model);
            this.model.animateModel(torpedo, limbAngle, limbDistance, tickDelta);
            this.model.setAngles(torpedo, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
            matrixStack.push();
            float f = 0.02f;
            float fl = torpedo.getClientFuseTime(tickDelta) * 2f;
            float twopi = 2 * MathHelper.PI;
            float red = MathHelper.sin(fl * twopi) * 0.4f + 0.6f;
            float green = MathHelper.sin((fl + 1/3f) * twopi) * 0.4f + 0.6f;
            float blue = MathHelper.sin((fl + 2/3f) * twopi) * 0.4f + 0.6f;

            torpedo.getRandom().setSeed((long)(fl * 0x10000));
            matrixStack.translate(f * (torpedo.getRandom().nextDouble() * 2.0 - 1.0), f * (torpedo.getRandom().nextDouble() * 2.0 - 1.0), f * (torpedo.getRandom().nextDouble() * 2.0 - 1.0));

            int color = ColorHelper.Argb.fromFloats(red, green, blue, 1.0F);
            this.model.render(matrixStack, vertexConsumer, LightmapTextureManager.MAX_LIGHT_COORDINATE, LivingEntityRenderer.getOverlay(torpedo, 0.0F), color);
            matrixStack.pop();
        }
    }

    @Override
    protected Identifier getTexture(SubcaelicTorpedoEntity entity) {
        return TEXTURE;
    }
}

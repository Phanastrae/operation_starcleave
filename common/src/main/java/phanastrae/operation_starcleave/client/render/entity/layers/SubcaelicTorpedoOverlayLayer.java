package phanastrae.operation_starcleave.client.render.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.client.render.entity.model.OperationStarcleaveEntityModelLayers;
import phanastrae.operation_starcleave.client.render.entity.model.SubcaelicTorpedoEntityModel;
import phanastrae.operation_starcleave.entity.mob.SubcaelicTorpedoEntity;

public class SubcaelicTorpedoOverlayLayer<T extends SubcaelicTorpedoEntity, M extends SubcaelicTorpedoEntityModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation TEXTURE = OperationStarcleave.id("textures/entity/subcaelic_torpedo/subcaelic_torpedo_overlay.png");
    private final EntityModel<T> model;

    public SubcaelicTorpedoOverlayLayer(RenderLayerParent<T, M> context, EntityModelSet loader) {
        super(context);
        this.model = new SubcaelicTorpedoEntityModel<>(loader.bakeLayer(OperationStarcleaveEntityModelLayers.SUBCAELIC_TORPEDO_OVERLAY));
    }

    public void render(
            PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, T torpedo, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch
    ) {
        if(!torpedo.isPrimed()) return;

        Minecraft minecraftClient = Minecraft.getInstance();
        boolean bl = minecraftClient.shouldEntityAppearGlowing(torpedo) && torpedo.isInvisible();
        if (!torpedo.isInvisible() || bl) {
            VertexConsumer vertexConsumer;
            if (bl) {
                vertexConsumer = vertexConsumerProvider.getBuffer(RenderType.outline(this.getTextureLocation(torpedo)));
            } else {
                vertexConsumer = vertexConsumerProvider.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(torpedo)));
            }

            this.getParentModel().copyPropertiesTo(this.model);
            this.model.prepareMobModel(torpedo, limbAngle, limbDistance, tickDelta);
            this.model.setupAnim(torpedo, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
            matrixStack.pushPose();
            float f = 0.02f;
            float fl = torpedo.getClientFuseTime(tickDelta) * 2f;
            float twopi = 2 * Mth.PI;
            float red = Mth.sin(fl * twopi) * 0.4f + 0.6f;
            float green = Mth.sin((fl + 1/3f) * twopi) * 0.4f + 0.6f;
            float blue = Mth.sin((fl + 2/3f) * twopi) * 0.4f + 0.6f;

            torpedo.getRandom().setSeed((long)(fl * 0x10000));
            matrixStack.translate(f * (torpedo.getRandom().nextDouble() * 2.0 - 1.0), f * (torpedo.getRandom().nextDouble() * 2.0 - 1.0), f * (torpedo.getRandom().nextDouble() * 2.0 - 1.0));

            int color = FastColor.ARGB32.colorFromFloat(red, green, blue, 1.0F);
            this.model.renderToBuffer(matrixStack, vertexConsumer, LightTexture.FULL_BRIGHT, LivingEntityRenderer.getOverlayCoords(torpedo, 0.0F), color);
            matrixStack.popPose();
        }
    }

    @Override
    protected ResourceLocation getTextureLocation(SubcaelicTorpedoEntity entity) {
        return TEXTURE;
    }
}

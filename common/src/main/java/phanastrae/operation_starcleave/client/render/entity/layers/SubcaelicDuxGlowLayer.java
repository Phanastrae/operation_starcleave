package phanastrae.operation_starcleave.client.render.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import phanastrae.operation_starcleave.client.render.entity.model.SubcaelicDuxEntityModel;
import phanastrae.operation_starcleave.entity.mob.SubcaelicDuxEntity;

public class SubcaelicDuxGlowLayer<T extends SubcaelicDuxEntity, M extends SubcaelicDuxEntityModel<T>> extends GlowLayer<T, M> {

    private final AlphaProvider<T> alphaProvider;

    public SubcaelicDuxGlowLayer(
            RenderLayerParent<T, M> context,
            ResourceLocation texture,
            AlphaProvider<T> alphaProvider,
            SubcaelicDuxGlowLayer.ModelPartVisibility<T, M> modelPartVisibility
    ) {
        super(context, texture, modelPartVisibility);
        this.alphaProvider = alphaProvider;
    }

    public int getColor(T dux, float tickDelta, float animationProgress) {
        float twopi = 2 * Mth.PI;
        float f = 0.1f * (float) Math.toRadians(Mth.rotLerp(tickDelta, dux.prevTentacleRollAngle, dux.tentacleRollAngle));
        float red = Mth.sin(f * twopi) * 0.3f + 0.6f;
        float green = Mth.sin((f + 1 / 3f) * twopi) * 0.3f + 0.6f;
        float blue = Mth.sin((f + 2 / 3f) * twopi) * 0.3f + 0.6f;

        float alpha = this.alphaProvider.apply(dux, tickDelta, animationProgress);

        // TODO these colors are not correct...
        return FastColor.ARGB32.colorFromFloat(red * alpha, green * alpha, blue * alpha, alpha * alpha * alpha * 0.85f);
    }

    public void render(
            PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, T dux, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch
    ) {
        if (!dux.isInvisible() && !dux.isHollow()) {
            this.updateModelPartVisibility();
            this.getParentModel()
                    .renderToBuffer(
                            matrixStack,
                            this.getVertexConsumer(vertexConsumerProvider),
                            this.getLight(light),
                            LivingEntityRenderer.getOverlayCoords(dux, 0.0F),
                            this.getColor(dux, tickDelta, animationProgress)
                    );
            this.unhideAllModelParts();
        }
    }

    @FunctionalInterface
    public interface AlphaProvider<T extends SubcaelicDuxEntity> {
        float apply(T dux, float tickDelta, float animationProgress);
    }
}

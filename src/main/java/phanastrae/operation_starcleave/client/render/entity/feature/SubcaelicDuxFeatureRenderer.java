package phanastrae.operation_starcleave.client.render.entity.feature;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import phanastrae.operation_starcleave.entity.mob.SubcaelicDuxEntity;
import phanastrae.operation_starcleave.client.render.entity.model.SubcaelicDuxEntityModel;

import java.util.List;

public class SubcaelicDuxFeatureRenderer<T extends SubcaelicDuxEntity, M extends SubcaelicDuxEntityModel<T>> extends FeatureRenderer<T, M> {
    private final Identifier texture;
    private final SubcaelicDuxFeatureRenderer.AnimationAngleAdjuster<T> animationAngleAdjuster;
    private final SubcaelicDuxFeatureRenderer.ModelPartVisibility<T, M> modelPartVisibility;

    public SubcaelicDuxFeatureRenderer(
            FeatureRendererContext<T, M> context,
            Identifier texture,
            SubcaelicDuxFeatureRenderer.AnimationAngleAdjuster<T> animationAngleAdjuster,
            SubcaelicDuxFeatureRenderer.ModelPartVisibility<T, M> modelPartVisibility
    ) {
        super(context);
        this.texture = texture;
        this.animationAngleAdjuster = animationAngleAdjuster;
        this.modelPartVisibility = modelPartVisibility;
    }

    public void render(
            MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, T dux, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch
    ) {
        if(dux.isHollow()) return;

        if (!dux.isInvisible()) {
            this.updateModelPartVisibility();
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucentEmissive(this.texture));

            float twopi = 2 * MathHelper.PI;
            float f = 0.1f * (float)Math.toRadians(MathHelper.lerpAngleDegrees(tickDelta, dux.prevTentacleRollAngle, dux.tentacleRollAngle));
            float red = MathHelper.sin(f * twopi) * 0.3f + 0.6f;
            float green = MathHelper.sin((f + 1/3f) * twopi) * 0.3f + 0.6f;
            float blue = MathHelper.sin((f + 2/3f) * twopi) * 0.3f + 0.6f;

            float alpha = this.animationAngleAdjuster.apply(dux, tickDelta, animationProgress);

            int color = ColorHelper.Argb.fromFloats(red * alpha, green * alpha, blue * alpha, alpha * alpha * alpha * 0.85f);
            this.getContextModel()
                    .render(
                            matrixStack,
                            vertexConsumer,
                            LightmapTextureManager.MAX_LIGHT_COORDINATE,
                            LivingEntityRenderer.getOverlay(dux, 0.0F),
                            color
                    );
            this.unhideAllModelParts();
        }
    }

    private void updateModelPartVisibility() {
        List<ModelPart> list = this.modelPartVisibility.getPartsToDraw(this.getContextModel());
        this.getContextModel().getPart().traverse().forEach(part -> part.hidden = true);
        list.forEach(part -> part.hidden = false);
    }

    private void unhideAllModelParts() {
        this.getContextModel().getPart().traverse().forEach(part -> part.hidden = false);
    }

    public interface AnimationAngleAdjuster<T extends SubcaelicDuxEntity> {
        float apply(T dux, float tickDelta, float animationProgress);
    }

    public interface ModelPartVisibility<T extends SubcaelicDuxEntity, M extends EntityModel<T>> {
        List<ModelPart> getPartsToDraw(M model);
    }
}

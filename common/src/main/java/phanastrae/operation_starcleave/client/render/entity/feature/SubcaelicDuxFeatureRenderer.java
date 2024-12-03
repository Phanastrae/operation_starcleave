package phanastrae.operation_starcleave.client.render.entity.feature;

import phanastrae.operation_starcleave.entity.mob.SubcaelicDuxEntity;
import phanastrae.operation_starcleave.client.render.entity.model.SubcaelicDuxEntityModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

public class SubcaelicDuxFeatureRenderer<T extends SubcaelicDuxEntity, M extends SubcaelicDuxEntityModel<T>> extends RenderLayer<T, M> {
    private final ResourceLocation texture;
    private final SubcaelicDuxFeatureRenderer.AnimationAngleAdjuster<T> animationAngleAdjuster;
    private final SubcaelicDuxFeatureRenderer.ModelPartVisibility<T, M> modelPartVisibility;

    public SubcaelicDuxFeatureRenderer(
            RenderLayerParent<T, M> context,
            ResourceLocation texture,
            SubcaelicDuxFeatureRenderer.AnimationAngleAdjuster<T> animationAngleAdjuster,
            SubcaelicDuxFeatureRenderer.ModelPartVisibility<T, M> modelPartVisibility
    ) {
        super(context);
        this.texture = texture;
        this.animationAngleAdjuster = animationAngleAdjuster;
        this.modelPartVisibility = modelPartVisibility;
    }

    public void render(
            PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, T dux, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch
    ) {
        if(dux.isHollow()) return;

        if (!dux.isInvisible()) {
            this.updateModelPartVisibility();
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderType.entityTranslucentEmissive(this.texture));

            float twopi = 2 * Mth.PI;
            float f = 0.1f * (float)Math.toRadians(Mth.rotLerp(tickDelta, dux.prevTentacleRollAngle, dux.tentacleRollAngle));
            float red = Mth.sin(f * twopi) * 0.3f + 0.6f;
            float green = Mth.sin((f + 1/3f) * twopi) * 0.3f + 0.6f;
            float blue = Mth.sin((f + 2/3f) * twopi) * 0.3f + 0.6f;

            float alpha = this.animationAngleAdjuster.apply(dux, tickDelta, animationProgress);

            int color = FastColor.ARGB32.colorFromFloat(red * alpha, green * alpha, blue * alpha, alpha * alpha * alpha * 0.85f);
            this.getParentModel()
                    .renderToBuffer(
                            matrixStack,
                            vertexConsumer,
                            LightTexture.FULL_BRIGHT,
                            LivingEntityRenderer.getOverlayCoords(dux, 0.0F),
                            color
                    );
            this.unhideAllModelParts();
        }
    }

    private void updateModelPartVisibility() {
        List<ModelPart> list = this.modelPartVisibility.getPartsToDraw(this.getParentModel());
        this.getParentModel().root().getAllParts().forEach(part -> part.skipDraw = true);
        list.forEach(part -> part.skipDraw = false);
    }

    private void unhideAllModelParts() {
        this.getParentModel().root().getAllParts().forEach(part -> part.skipDraw = false);
    }

    public interface AnimationAngleAdjuster<T extends SubcaelicDuxEntity> {
        float apply(T dux, float tickDelta, float animationProgress);
    }

    public interface ModelPartVisibility<T extends SubcaelicDuxEntity, M extends EntityModel<T>> {
        List<ModelPart> getPartsToDraw(M model);
    }
}

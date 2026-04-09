package phanastrae.operation_starcleave.client.render.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public abstract class AbstractBonusLayer<T extends LivingEntity, M extends HierarchicalModel<T>> extends RenderLayer<T, M> {

    private final ResourceLocation texture;
    private final ModelPartVisibility<T, M> modelPartVisibility;

    public AbstractBonusLayer(
            RenderLayerParent<T, M> context,
            ResourceLocation texture,
            ModelPartVisibility<T, M> modelPartVisibility
    ) {
        super(context);
        this.texture = texture;
        this.modelPartVisibility = modelPartVisibility;
    }

    public abstract int getLight(int light);

    public abstract VertexConsumer getVertexConsumer(MultiBufferSource vertexConsumerProvider);

    public void render(
            PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch
    ) {
        if (!entity.isInvisible()) {
            this.updateModelPartVisibility();
            this.getParentModel()
                    .renderToBuffer(
                            matrixStack,
                            this.getVertexConsumer(vertexConsumerProvider),
                            this.getLight(light),
                            LivingEntityRenderer.getOverlayCoords(entity, 0.0F),
                            -1
                    );
            this.unhideAllModelParts();
        }
    }

    protected ResourceLocation getTexture() {
        return this.texture;
    }

    protected void updateModelPartVisibility() {
        List<ModelPart> list = this.modelPartVisibility.getPartsToDraw(this.getParentModel());
        this.setAllVisible(false);
        list.forEach(part -> part.skipDraw = false);
    }

    protected void unhideAllModelParts() {
        this.setAllVisible(true);
    }

    protected void setAllVisible(boolean visible) {
        this.getParentModel().root().getAllParts().forEach(part -> part.skipDraw = !visible);
    }

    @FunctionalInterface
    public interface ModelPartVisibility<T extends LivingEntity, M extends EntityModel<T>> {
        List<ModelPart> getPartsToDraw(M model);
    }
}

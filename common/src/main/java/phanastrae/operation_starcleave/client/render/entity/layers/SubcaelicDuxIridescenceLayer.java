package phanastrae.operation_starcleave.client.render.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import phanastrae.operation_starcleave.client.render.OperationStarcleaveRenderTypes;
import phanastrae.operation_starcleave.client.render.entity.model.SubcaelicDuxEntityModel;
import phanastrae.operation_starcleave.client.render.extras_baking.Iridescence;
import phanastrae.operation_starcleave.entity.mob.SubcaelicDuxEntity;

import java.util.List;

public class SubcaelicDuxIridescenceLayer<T extends SubcaelicDuxEntity, M extends SubcaelicDuxEntityModel<T>> extends RenderLayer<T, M> {

    private final ResourceLocation texture;
    private final ModelPartVisibility<T, M> modelPartVisibility;

    public SubcaelicDuxIridescenceLayer(
            RenderLayerParent<T, M> context,
            ResourceLocation texture,
            ModelPartVisibility<T, M> modelPartVisibility
    ) {
        super(context);
        this.texture = texture;
        this.modelPartVisibility = modelPartVisibility;
    }

    public void render(
            PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch
    ) {
        if (!entity.isInvisible()) {
            this.updateModelPartVisibility();
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(OperationStarcleaveRenderTypes.entityIridescence(this.texture, Iridescence.getBismuthIridescenceId()));

            this.getParentModel()
                    .renderToBuffer(
                            matrixStack,
                            vertexConsumer,
                            light,
                            LivingEntityRenderer.getOverlayCoords(entity, 0.0F),
                            -1
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

    @FunctionalInterface
    public interface ModelPartVisibility<T extends Entity, M extends EntityModel<T>> {
        List<ModelPart> getPartsToDraw(M model);
    }
}

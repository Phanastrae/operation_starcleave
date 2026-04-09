package phanastrae.operation_starcleave.client.render.entity.layers;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import phanastrae.operation_starcleave.client.render.OperationStarcleaveRenderTypes;
import phanastrae.operation_starcleave.client.render.extras_baking.Iridescence;

public class BismuthIridescenceLayer<T extends LivingEntity, M extends HierarchicalModel<T>> extends AbstractBonusLayer<T, M> {

    public BismuthIridescenceLayer(
            RenderLayerParent<T, M> context,
            ResourceLocation texture,
            ModelPartVisibility<T, M> modelPartVisibility
    ) {
        super(context, texture, modelPartVisibility);
    }

    @Override
    public int getLight(int light) {
        return light;
    }

    @Override
    public VertexConsumer getVertexConsumer(MultiBufferSource vertexConsumerProvider) {
        return vertexConsumerProvider.getBuffer(OperationStarcleaveRenderTypes.entityIridescence(this.getTexture(), Iridescence.getBismuthIridescenceId()));
    }
}

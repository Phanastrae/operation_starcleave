package phanastrae.operation_starcleave.client.render.entity.layers;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class GlowLayer<T extends LivingEntity, M extends HierarchicalModel<T>> extends AbstractBonusLayer<T, M> {

    public GlowLayer(
            RenderLayerParent<T, M> context,
            ResourceLocation texture,
            ModelPartVisibility<T, M> modelPartVisibility
    ) {
        super(context, texture, modelPartVisibility);
    }

    @Override
    public int getLight(int light) {
        return LightTexture.FULL_BRIGHT;
    }

    @Override
    public VertexConsumer getVertexConsumer(MultiBufferSource vertexConsumerProvider) {
        return vertexConsumerProvider.getBuffer(RenderType.entityTranslucentEmissive(this.getTexture()));
    }
}

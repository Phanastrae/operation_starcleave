package phanastrae.operation_starcleave.client.render.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.client.render.entity.model.OperationStarcleaveEntityModelLayers;
import phanastrae.operation_starcleave.client.render.entity.model.PegasusWingsModel;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityAttachment;

public class PegasusWingsLayer<T extends AbstractHorse, M extends EntityModel<T>> extends RenderLayer<T, M> {
    public static final ResourceLocation WINGS_LOCATION = OperationStarcleave.id("pegasus_wings").withPath(st -> "textures/entity/horse/armor/" + st).withSuffix(".png");

    private final PegasusWingsModel<T> model;

    public PegasusWingsLayer(RenderLayerParent<T, M> renderer, EntityModelSet modelSet) {
        super(renderer);
        this.model = new PegasusWingsModel<>(modelSet.bakeLayer(OperationStarcleaveEntityModelLayers.PEGASUS_WINGS));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T livingEntity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (OperationStarcleaveEntityAttachment.isPegasus(livingEntity)) {
            this.getParentModel().copyPropertiesTo(this.model);
            this.model.prepareMobModel(livingEntity, limbSwing, limbSwingAmount, partialTick);
            this.model.setupAnim(livingEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

            VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(WINGS_LOCATION));
            this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, -1);
        }
    }
}

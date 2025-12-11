package phanastrae.operation_starcleave.client.render.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.client.render.OperationStarcleaveRenderTypes;
import phanastrae.operation_starcleave.client.render.entity.model.OperationStarcleaveEntityModelLayers;
import phanastrae.operation_starcleave.client.render.entity.model.PegasusWingsModel;
import phanastrae.operation_starcleave.client.render.extras_baking.Iridescence;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityAttachment;

public class PegasusWingsLayer<T extends AbstractHorse, M extends EntityModel<T>> extends RenderLayer<T, M> {
    public static final ResourceLocation WINGS_LOCATION = OperationStarcleave.id("pegasus_wings").withPath(st -> "textures/entity/horse/armor/" + st).withSuffix(".png");
    public static final ResourceLocation WINGS_GLOW_LOCATION = OperationStarcleave.id("pegasus_wings_glow").withPath(st -> "textures/entity/horse/armor/" + st).withSuffix(".png");
    public static final ResourceLocation WINGS_IRIDESCENCE_LOCATION = OperationStarcleave.id("pegasus_wings_iridescence").withPath(st -> "textures/entity/horse/armor/" + st).withSuffix(".png");

    private final PegasusWingsModel<T> model;

    public PegasusWingsLayer(RenderLayerParent<T, M> renderer, EntityModelSet modelSet) {
        super(renderer);
        this.model = new PegasusWingsModel<>(modelSet.bakeLayer(OperationStarcleaveEntityModelLayers.PEGASUS_WINGS));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T horse, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (OperationStarcleaveEntityAttachment.isPegasus(horse)) {
            this.getParentModel().copyPropertiesTo(this.model);
            this.model.prepareMobModel(horse, limbSwing, limbSwingAmount, partialTick);
            this.model.setupAnim(horse, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

            VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(WINGS_LOCATION));
            this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, -1);

            VertexConsumer iridescenceVertexConsumer = buffer.getBuffer(OperationStarcleaveRenderTypes.entityIridescence(WINGS_IRIDESCENCE_LOCATION, Iridescence.getBismuthIridescenceId()));
            this.model.renderToBuffer(poseStack, iridescenceVertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, -1);

            float twopi = 2 * Mth.PI;
            float angle = twopi * (ageInTicks % 50 + partialTick) / 50f;
            float red = Mth.sin(angle) * 0.25f + 0.75f;
            float green = Mth.sin((angle + twopi / 3f)) * 0.25f + 0.75f;
            float blue = Mth.sin((angle - twopi / 3f)) * 0.25f + 0.75f;
            int color = FastColor.ARGB32.colorFromFloat(0.75f, red, green, blue);

            VertexConsumer glowVertexConsumer = buffer.getBuffer(RenderType.entityTranslucentEmissive(WINGS_GLOW_LOCATION));
            this.model.renderToBuffer(poseStack, glowVertexConsumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, color);
        }
    }
}

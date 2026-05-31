package phanastrae.operation_starcleave.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.client.render.entity.model.OperationStarcleaveEntityModelLayers;
import phanastrae.operation_starcleave.client.render.entity.model.PreecherEntityModel;
import phanastrae.operation_starcleave.entity.mob.PreecherEntity;

public class PreecherEntityRenderer extends MobRenderer<PreecherEntity, PreecherEntityModel<PreecherEntity>> {
    private static final ResourceLocation TEXTURE = OperationStarcleave.id("textures/entity/preecher.png");

    public PreecherEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new PreecherEntityModel<>(context.bakeLayer(OperationStarcleaveEntityModelLayers.PREECHER)), 0.5F);
    }

    @Override
    protected void scale(PreecherEntity livingEntity, PoseStack poseStack, float partialTickTime) {
        float swelling = livingEntity.getSwelling(partialTickTime);
        float f = 1.0F + Mth.sin(swelling * 100.0F) * swelling * 0.01F;
        swelling = Mth.clamp(swelling, 0.0F, 1.0F);
        swelling *= swelling;
        swelling *= swelling;
        float xzScale = (1.0F + swelling * 0.4F) * f;
        float yScale = (1.0F + swelling * 0.1F) / f;
        poseStack.scale(xzScale, yScale, xzScale);
    }

    @Override
    protected float getWhiteOverlayProgress(PreecherEntity livingEntity, float partialTicks) {
        float swelling = livingEntity.getSwelling(partialTicks);
        return (int) (swelling * 10.0F) % 2 == 0 ? 0.0F : Mth.clamp(swelling, 0.5F, 1.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(PreecherEntity entity) {
        return TEXTURE;
    }
}

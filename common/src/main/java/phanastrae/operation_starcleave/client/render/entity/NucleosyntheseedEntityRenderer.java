package phanastrae.operation_starcleave.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.TntMinecartRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import phanastrae.operation_starcleave.entity.NucleosyntheseedEntity;

public class NucleosyntheseedEntityRenderer extends EntityRenderer<NucleosyntheseedEntity> {
    // this is all mostly the same as TntRenderer
    private final BlockRenderDispatcher blockRenderer;

    public NucleosyntheseedEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.5F;
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    public void render(NucleosyntheseedEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.5F, 0.0F);

        int fuse = entity.getFuse();
        float partialFuse = fuse - partialTicks + 1.0F;
        if (partialFuse < 120.0F) {
            float growthFactor = 1.0F - partialFuse / 120.0F;
            growthFactor = Mth.clamp(growthFactor, 0.0F, 1.0F);
            growthFactor *= growthFactor;
            float scale = 1.0F + growthFactor * 1.25F;
            poseStack.scale(scale, scale, scale);
        }

        poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
        poseStack.translate(-0.5F, -0.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));

        int repackedLight = LightTexture.pack(15, LightTexture.sky(packedLight));
        TntMinecartRenderer.renderWhiteSolidBlock(this.blockRenderer, entity.getBlockState(), poseStack, buffer, repackedLight, fuse / 5 % 2 == 0);

        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(NucleosyntheseedEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}

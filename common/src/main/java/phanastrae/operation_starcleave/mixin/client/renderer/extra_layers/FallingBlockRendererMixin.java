package phanastrae.operation_starcleave.mixin.client.renderer.extra_layers;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.client.render.OperationStarcleaveRenderTypes;
import phanastrae.operation_starcleave.client.render.extras_baking.ExtrasSectionCompiler;

@Mixin(FallingBlockRenderer.class)
public class FallingBlockRendererMixin {

    @Shadow
    @Final
    private BlockRenderDispatcher dispatcher;

    @Inject(method = "render(Lnet/minecraft/world/entity/item/FallingBlockEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V"))
    private void operation_starcleave$renderExtras(
            FallingBlockEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci,
            @Local Level level, @Local BlockState state, @Local BlockPos pos
    ) {
        if (ExtrasSectionCompiler.isStateIridescent(state)) {
            RenderType renderType = OperationStarcleaveRenderTypes.getIridescence();
            VertexConsumer vertexConsumer = buffer.getBuffer(renderType);

            BakedModel model = ExtrasSectionCompiler.getModel(state, this.dispatcher.getBlockModelShaper().getModelManager());

            this.dispatcher
                    .getModelRenderer()
                    .tesselateBlock(
                            level,
                            model,
                            state,
                            pos,
                            poseStack,
                            vertexConsumer,
                            false,
                            RandomSource.create(),
                            state.getSeed(entity.getStartPos()),
                            OverlayTexture.NO_OVERLAY
                    );
        }
    }
}

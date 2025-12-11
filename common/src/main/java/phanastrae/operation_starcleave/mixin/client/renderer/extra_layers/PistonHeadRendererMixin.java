package phanastrae.operation_starcleave.mixin.client.renderer.extra_layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.PistonHeadRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
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
import phanastrae.operation_starcleave.client.render.extras_baking.Iridescence;

@Mixin(PistonHeadRenderer.class)
public class PistonHeadRendererMixin {

    @Shadow
    @Final
    private BlockRenderDispatcher blockRenderer;

    @Inject(method = "renderBlock", at = @At("RETURN"))
    private void operation_starcleave$renderExtras(BlockPos pos, BlockState state, PoseStack poseStack, MultiBufferSource bufferSource, Level level, boolean extended, int packedOverlay, CallbackInfo ci) {
        if (Iridescence.isStateIridescent(state)) {
            RenderType renderType = OperationStarcleaveRenderTypes.getIridescence();
            VertexConsumer vertexConsumer = bufferSource.getBuffer(renderType);

            BakedModel model = ExtrasSectionCompiler.getModel(state, this.blockRenderer.getBlockModelShaper().getModelManager());

            int iridescenceId = Iridescence.getIridescenceId(state);
            this.blockRenderer
                    .getModelRenderer()
                    .tesselateBlock(
                            level,
                            model,
                            state,
                            pos,
                            poseStack,
                            vertexConsumer,
                            extended,
                            RandomSource.create(),
                            state.getSeed(pos),
                            OverlayTexture.pack(0, iridescenceId)
                    );
        }
    }
}

package phanastrae.operation_starcleave.mixin.client.renderer.extra_layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.client.render.OperationStarcleaveSheets;
import phanastrae.operation_starcleave.client.render.extras_baking.ExtrasSectionCompiler;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    @Shadow
    protected abstract void renderModelLists(BakedModel model, ItemStack stack, int combinedLight, int combinedOverlay, PoseStack poseStack, VertexConsumer buffer);

    @Shadow
    public abstract ItemModelShaper getItemModelShaper();

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderModelLists(Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/item/ItemStack;IILcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V", shift = At.Shift.AFTER))
    private void operation_starcleave$renderExtras(ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, BakedModel model, CallbackInfo ci) {
        Item item = itemStack.getItem();
        // TODO implement functionality for non-block item models
        if (item instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            BlockState state = block.defaultBlockState();
            if (ExtrasSectionCompiler.isStateIridescent(state)) {
                BakedModel iridescenceModel = ExtrasSectionCompiler.getModel(state, this.getItemModelShaper().getModelManager());

                RenderType renderType = OperationStarcleaveSheets.iridescenceBlockSheet();
                VertexConsumer vertexconsumer = bufferSource.getBuffer(renderType);

                this.renderModelLists(iridescenceModel, itemStack, combinedLight, combinedOverlay, poseStack, vertexconsumer);
            }
        }
    }
}

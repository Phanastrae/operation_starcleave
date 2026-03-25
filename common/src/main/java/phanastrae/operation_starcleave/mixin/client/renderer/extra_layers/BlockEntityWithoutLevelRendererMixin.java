package phanastrae.operation_starcleave.mixin.client.renderer.extra_layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.client.render.OperationStarcleaveSheets;
import phanastrae.operation_starcleave.client.render.extras_baking.Iridescence;
import phanastrae.operation_starcleave.mixin.client.accessor.ItemRendererAccessor;

import static net.minecraft.client.renderer.entity.ItemRenderer.getFoilBufferDirect;

@Mixin(BlockEntityWithoutLevelRenderer.class)
public class BlockEntityWithoutLevelRendererMixin {

    @Inject(method = "renderByItem", at = @At("HEAD"))
    private void operation_starcleave$render(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        Item item = stack.getItem();
        if (Iridescence.isItemIridescent(item)) {
            ResourceLocation itemLocation = BuiltInRegistries.ITEM.getKey(item);
            ModelResourceLocation mainLocation = ModelResourceLocation.inventory(itemLocation);
            ModelResourceLocation iridescenceLocation = ModelResourceLocation.inventory(itemLocation.withSuffix("_iridescence"));

            ModelManager modelManager = minecraft.getModelManager();
            BakedModel mainModel = modelManager.getModel(mainLocation);
            BakedModel iridescenceModel = modelManager.getModel(iridescenceLocation);

            RenderType mainRenderType = ItemBlockRenderTypes.getRenderType(stack, true);
            RenderType iridescenceRenderType = OperationStarcleaveSheets.iridescenceBlockSheet(Iridescence.getIridescenceId(item));

            VertexConsumer mainVC = getFoilBufferDirect(buffer, mainRenderType, true, stack.hasFoil());
            VertexConsumer iridescenceVC = buffer.getBuffer(iridescenceRenderType);

            ItemRendererAccessor ira = ((ItemRendererAccessor) minecraft.getItemRenderer());
            ira.invokeRenderModelLists(mainModel, stack, packedLight, packedOverlay, poseStack, mainVC);
            ira.invokeRenderModelLists(iridescenceModel, stack, packedLight, packedOverlay, poseStack, iridescenceVC);
        }
    }
}

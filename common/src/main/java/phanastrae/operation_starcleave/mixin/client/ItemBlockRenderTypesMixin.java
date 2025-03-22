package phanastrae.operation_starcleave.mixin.client;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;

@Mixin(ItemBlockRenderTypes.class)
public class ItemBlockRenderTypesMixin {

    @Shadow private static boolean renderCutout;

    @Inject(method = "getChunkRenderType", at = @At("HEAD"), cancellable = true)
    private static void operation_starcleave$starbleachedLeavesAdjustChunkRenderType(BlockState state, CallbackInfoReturnable<RenderType> cir) {
        // manually tell starbleached leaves to do the leaves behaviour because they're technically not leaves
        if(state.is(OperationStarcleaveBlocks.STARBLEACHED_LEAVES)) {
            cir.setReturnValue(renderCutout ? RenderType.cutoutMipped() : RenderType.solid());
        }
    }

    @Inject(method = "getMovingBlockRenderType", at = @At("HEAD"), cancellable = true)
    private static void operation_starcleave$starbleachedLeavesAdjustMovingBlockRenderType(BlockState state, CallbackInfoReturnable<RenderType> cir) {
        // manually tell starbleached leaves to do the leaves behaviour because they're technically not leaves
        if(state.is(OperationStarcleaveBlocks.STARBLEACHED_LEAVES)) {
            cir.setReturnValue(renderCutout ? RenderType.cutoutMipped() : RenderType.solid());
        }
    }
}

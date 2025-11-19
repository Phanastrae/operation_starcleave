package phanastrae.operation_starcleave.mixin.client.renderer.extra_layers;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.client.render.extras_baking.ExtrasSectionCompiler;
import phanastrae.operation_starcleave.client.render.extras_baking.RenderExtras;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {

    @Inject(method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;IIII)V", at = @At("HEAD"))
    private void operation_starcleave$setupRenderItem(LivingEntity entity, Level level, ItemStack stack, int x, int y, int seed, int guiOffset, CallbackInfo ci) {
        if (stack.getItem() instanceof BlockItem blockItem && ExtrasSectionCompiler.isStateIridescent(blockItem.getBlock().defaultBlockState())) {
            float progress = (System.currentTimeMillis() % 2000) / 2000F;
            double angle = progress * Math.TAU;
            float dx = (float) Math.cos(angle) * 1600F;
            float dy = (float) Math.sin(angle) * 1600F;

            RenderExtras.setPosOffset(dx, dy, -(5000 + guiOffset));
        }
    }

    @Inject(method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;IIII)V", at = @At("RETURN"))
    private void operation_starcleave$cleanupRenderItem(LivingEntity entity, Level level, ItemStack stack, int x, int y, int seed, int guiOffset, CallbackInfo ci) {
        if (RenderExtras.isInScreen()) {
            RenderExtras.setPosOffset(0F, 0F, -5000F);
        } else {
            RenderExtras.resetPosOffset();
        }
    }
}

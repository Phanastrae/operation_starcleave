package phanastrae.operation_starcleave.mixin.client.renderer.extra_layers;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.client.render.extras_baking.RenderExtras;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {

    @Shadow
    protected abstract void applyItemArmTransform(PoseStack poseStack, HumanoidArm hand, float equippedProg);

    @Inject(method = "renderHandsWithItems", at = @At("HEAD"))
    private void operation_starcleave$setupIridescence(float partialTicks, PoseStack poseStack, MultiBufferSource.BufferSource buffer, LocalPlayer playerEntity, int combinedLight, CallbackInfo ci) {
        float progress = (System.currentTimeMillis() % 3000) / 3000F;
        double angle = progress * Math.TAU;
        float dx = (float) Math.cos(angle) * 0.25F;
        float dz = (float) Math.sin(angle) * 0.25F;

        RenderExtras.setPosOffset(dx, 0.125F, dz);
    }

    @Inject(method = "renderHandsWithItems", at = @At("RETURN"))
    private void operation_starcleave$cleanupIridescence(float partialTicks, PoseStack poseStack, MultiBufferSource.BufferSource buffer, LocalPlayer playerEntity, int combinedLight, CallbackInfo ci) {
        RenderExtras.resetPosOffset();
    }

    @Inject(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getUseAnimation()Lnet/minecraft/world/item/UseAnim;"))
    private void operation_starcleave$blasterPose(AbstractClientPlayer player, float partialTicks, float pitch, InteractionHand hand, float swingProgress, ItemStack stack, float equippedProgress, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, CallbackInfo ci, @Local HumanoidArm humanoidArm) {
        // need to position manually as crossbow anim would otherwise do nothing here, leading to a weird pose
        if (stack.is(OperationStarcleaveItems.BISMUTH_BLASTER)) {
            this.applyItemArmTransform(poseStack, humanoidArm, equippedProgress);
            poseStack.translate(0, -0.2F, 0);
        }
    }
}

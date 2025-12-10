package phanastrae.operation_starcleave.mixin.client.renderer;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HorseArmorLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.item.AnimalArmorItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.client.render.OperationStarcleaveRenderTypes;
import phanastrae.operation_starcleave.client.render.extras_baking.RenderExtras;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;

@Mixin(HorseArmorLayer.class)
public class HorseArmorLayerMixin {

    @Shadow
    @Final
    private HorseModel<Horse> model;

    @Unique
    private static final ResourceLocation operation_starcleave$PEGASUS_ARMOR_IRIDESCENCE = OperationStarcleave.id("textures/entity/horse/armor/horse_armor_bismuth_iridescence.png");

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/animal/horse/Horse;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/HorseModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V", shift = At.Shift.AFTER))
    private void operation_starcleave$renderIridescence(
            PoseStack poseStack, MultiBufferSource buffer, int packedLight, Horse livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci,
            @Local AnimalArmorItem animalArmorItem
    ) {
        if (animalArmorItem.equals(OperationStarcleaveItems.BISMUTH_PEGASUS_ARMOR)) {
            VertexConsumer vertexConsumer = buffer.getBuffer(OperationStarcleaveRenderTypes.entityIridescence(operation_starcleave$PEGASUS_ARMOR_IRIDESCENCE, RenderExtras.getBismuthIridescenceId()));
            this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, -1);
        }
    }
}

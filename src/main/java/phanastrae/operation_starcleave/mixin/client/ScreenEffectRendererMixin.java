package phanastrae.operation_starcleave.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.duck.EntityDuck;

@Mixin(ScreenEffectRenderer.class)
public abstract class ScreenEffectRendererMixin {

    @Shadow protected static void renderFire(Minecraft minecraft, PoseStack poseStack) {
        throw new AssertionError();
    };

    private static final Material PHLOGISTIC_FIRE_1 = new Material(TextureAtlas.LOCATION_BLOCKS, OperationStarcleave.id("block/phlogistic_fire_1"));

    // Render phlogistic fire overlay if player is not on normal fire (and thus not already rendering it)
    @Inject(method = "renderScreenEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z", shift = At.Shift.AFTER))
    private static void operation_starcleave$renderPhlogisticFireOverlay(Minecraft client, PoseStack matrices, CallbackInfo ci) {
        if(client.player instanceof EntityDuck opsce) {
            if(!client.player.isOnFire() && opsce.operation_starcleave$isOnPhlogisticFire()) {
                renderFire(client, matrices);
            }
        }
    }

    // Swap the textures in the fire overlay if the player is on phlogistic fire
    @Inject(method = "renderFire", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V", ordinal = 0, shift = At.Shift.BEFORE))
    private static void operation_starcleave$renderPhlogisticFireOverlay(Minecraft client, PoseStack matrices, CallbackInfo ci, @Local(ordinal = 0) LocalRef<TextureAtlasSprite> spriteRefFire1) {
        Player player = client.player;
        if(player instanceof EntityDuck operationStarcleaveEntity) {
            if(operationStarcleaveEntity.operation_starcleave$isOnPhlogisticFire()) {
                spriteRefFire1.set(PHLOGISTIC_FIRE_1.sprite());
            }
        }
    }
}

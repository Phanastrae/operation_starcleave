package phanastrae.operation_starcleave.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.duck.EntityDuck;

@Mixin(InGameOverlayRenderer.class)
public abstract class InGameOverlayRendererMixin {
    @Shadow
    private static void renderFireOverlay(MinecraftClient client, MatrixStack matrices){};

    private static final SpriteIdentifier PHLOGISTIC_FIRE_1 = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, OperationStarcleave.id("block/phlogistic_fire_1"));

    // Render phlogistic fire overlay if player is not on normal fire (and thus not already rendering it)
    @Inject(method = "renderOverlays", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSpectator()Z", shift = At.Shift.AFTER))
    private static void operation_starcleave$renderPhlogisticFireOverlay(MinecraftClient client, MatrixStack matrices, CallbackInfo ci) {
        if(client.player instanceof EntityDuck opsce) {
            if(!client.player.isOnFire() && opsce.operation_starcleave$isOnPhlogisticFire()) {
                renderFireOverlay(client, matrices);
            }
        }
    }

    // Swap the textures in the fire overlay if the player is on phlogistic fire
    @Inject(method = "renderFireOverlay", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/util/Identifier;)V", ordinal = 0, shift = At.Shift.BEFORE))
    private static void operation_starcleave$renderPhlogisticFireOverlay(MinecraftClient client, MatrixStack matrices, CallbackInfo ci, @Local(ordinal = 0) LocalRef<Sprite> spriteRefFire1) {
        PlayerEntity player = client.player;
        if(player instanceof EntityDuck operationStarcleaveEntity) {
            if(operationStarcleaveEntity.operation_starcleave$isOnPhlogisticFire()) {
                spriteRefFire1.set(PHLOGISTIC_FIRE_1.getSprite());
            }
        }
    }
}

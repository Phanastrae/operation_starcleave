package phanastrae.operation_starcleave.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.entity.Entity;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.duck.EntityDuck;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    private static final Material PHLOGISTIC_FIRE_0 = new Material(TextureAtlas.LOCATION_BLOCKS, OperationStarcleave.id("block/phlogistic_fire_0"));
    private static final Material PHLOGISTIC_FIRE_1 = new Material(TextureAtlas.LOCATION_BLOCKS, OperationStarcleave.id("block/phlogistic_fire_1"));

    @Inject(method = "renderFlame", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V", ordinal = 0, shift = At.Shift.BEFORE))
    private void operation_starcleave$renderPhlogisticFire(PoseStack matrices, MultiBufferSource vertexConsumers, Entity entity, Quaternionf rotation, CallbackInfo ci, @Local(ordinal = 0) LocalRef<TextureAtlasSprite> spriteRefFire0, @Local(ordinal = 1) LocalRef<TextureAtlasSprite> spriteRefFire1) {
        if(((EntityDuck)entity).operation_starcleave$isOnPhlogisticFire()) {
            spriteRefFire0.set(PHLOGISTIC_FIRE_0.sprite());
            spriteRefFire1.set(PHLOGISTIC_FIRE_1.sprite());
        }
    }
}

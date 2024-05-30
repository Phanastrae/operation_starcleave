package phanastrae.operation_starcleave.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntity;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    private static final SpriteIdentifier PHLOGISTIC_FIRE_0 = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, OperationStarcleave.id("block/phlogistic_fire_0"));
    private static final SpriteIdentifier PHLOGISTIC_FIRE_1 = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, OperationStarcleave.id("block/phlogistic_fire_1"));

    @Inject(method = "renderFire", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;push()V", ordinal = 0, shift = At.Shift.BEFORE))
    private void operation_starcleave$renderPhlogisticFire(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Entity entity, Quaternionf rotation, CallbackInfo ci, @Local(ordinal = 0) LocalRef<Sprite> spriteRefFire0, @Local(ordinal = 1) LocalRef<Sprite> spriteRefFire1) {
        if(((OperationStarcleaveEntity)entity).operation_starcleave$isOnPhlogisticFire()) {
            spriteRefFire0.set(PHLOGISTIC_FIRE_0.getSprite());
            spriteRefFire1.set(PHLOGISTIC_FIRE_1.getSprite());
        }
    }
}

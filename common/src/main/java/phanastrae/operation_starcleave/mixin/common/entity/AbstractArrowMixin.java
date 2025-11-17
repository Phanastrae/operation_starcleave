package phanastrae.operation_starcleave.mixin.common.entity;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityAttachment;

@Mixin(AbstractArrow.class)
public class AbstractArrowMixin {

    @Inject(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private void operation_starcleave$setOnPhlogisticFire(EntityHitResult result, CallbackInfo ci, @Local(ordinal = 0) Entity entity, @Local(ordinal = 0) boolean targetIsEnderman) {
        if (OperationStarcleaveEntityAttachment.fromEntity((AbstractArrow) (Object) this).isOnPhlogisticFire() && !targetIsEnderman) {
            OperationStarcleaveEntityAttachment.fromEntity(entity).setOnPhlogisticFireFor(5.0F);
        }
    }
}

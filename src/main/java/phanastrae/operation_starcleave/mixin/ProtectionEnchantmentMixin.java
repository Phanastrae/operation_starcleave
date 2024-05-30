package phanastrae.operation_starcleave.mixin;

import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.tag.DamageTypeTags;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.operation_starcleave.entity.OperationStarcleaveDamageTypeTags;

@Mixin(ProtectionEnchantment.class)
public class ProtectionEnchantmentMixin {

    @Shadow @Final public ProtectionEnchantment.Type protectionType;

    @Inject(method = "getProtectionAmount", at = @At("HEAD"), cancellable = true)
    private void operation_starcleave$getPhlogisticFireProtectionAmount(int level, DamageSource source, CallbackInfoReturnable<Integer> cir) {
        if (!source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY) && this.protectionType == ProtectionEnchantment.Type.FIRE && source.isIn(OperationStarcleaveDamageTypeTags.IS_PHLOGISTIC_FIRE)) {
            cir.setReturnValue(level * 2);
        }
    }
}

package phanastrae.operation_starcleave.mixin.common.entity;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import phanastrae.operation_starcleave.entity.OperationStarcleaveDamageTypeTags;

@Mixin(CombatRules.class)
public class CombatRulesMixin {
    @ModifyVariable(method = "getDamageAfterAbsorb", at = @At(value = "STORE"), ordinal = 6)
    private static float operation_starcleave$bypassArmor(float value, @Local(argsOnly = true) DamageSource source) {
        if(source.is(OperationStarcleaveDamageTypeTags.PARTIALLY_BYPASSES_ARMOR)) {
            return value * 0.5F;
        } else {
            return value;
        }
    }
}

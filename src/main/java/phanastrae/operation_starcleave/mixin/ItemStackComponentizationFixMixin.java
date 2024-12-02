package phanastrae.operation_starcleave.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.fix.ItemStackComponentizationFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStackComponentizationFix.class)
public class ItemStackComponentizationFixMixin {

    @Inject(method = "fixStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/datafixer/fix/ItemStackComponentizationFix;fixEnchantments(Lnet/minecraft/datafixer/fix/ItemStackComponentizationFix$StackData;Lcom/mojang/serialization/Dynamic;Ljava/lang/String;Ljava/lang/String;Z)V"))
    private static void operation_starcleave$fixStack(ItemStackComponentizationFix.StackData data, Dynamic<?> dynamic, CallbackInfo ci, @Local(ordinal = 0) int i) {
        if (data.getAndRemove("operation_starcleave_Starbleached").asBoolean(false)) {
            Dynamic<?> dynamic2 = dynamic.emptyMap();
            if ((i & 4) != 0) {
                dynamic2 = dynamic2.set("show_in_tooltip", dynamic.createBoolean(false));
            }

            data.setComponent("operation_starcleave:starbleach", dynamic2);
        }
    }
}

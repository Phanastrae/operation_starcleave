package phanastrae.operation_starcleave.mixin.common.loot;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.operation_starcleave.world.loot.OperationStarcleaveLootContextParams;

import java.util.function.Consumer;

@Mixin(LootContextParamSets.class)
public class LootContextParamSetsMixin {

    @Inject(method = "register", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V", shift = At.Shift.AFTER))
    private static void addParameters(String registryName, Consumer<LootContextParamSet.Builder> builderConsumer, CallbackInfoReturnable<LootContextParamSet> cir, @Local LootContextParamSet.Builder builder) {
        if (registryName.equals("block")) {
            builder.optional(OperationStarcleaveLootContextParams.BROKEN_BY_EXPLOSION);
        }
    }
}

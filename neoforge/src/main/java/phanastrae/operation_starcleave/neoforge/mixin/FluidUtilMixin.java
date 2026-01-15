package phanastrae.operation_starcleave.neoforge.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.fluids.FluidActionResult;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.operation_starcleave.fluid.OperationStarcleaveFluids;

@Mixin(FluidUtil.class)
public class FluidUtilMixin {
    // the forbidden "mixing into NeoForge" technique
    @Inject(method = "lambda$tryEmptyContainer$2", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/fluids/FluidType;getSound(Lnet/neoforged/neoforge/fluids/FluidStack;Lnet/neoforged/neoforge/common/SoundAction;)Lnet/minecraft/sounds/SoundEvent;"))
    private static void operation_starcleave$playBucketBreakingSounds(IFluidHandler fluidDestination, int maxAmount, boolean doDrain, Player player, IFluidHandlerItem containerFluidHandler, CallbackInfoReturnable<FluidActionResult> cir, @Local FluidStack transfer) {
        // if a plasma container breaks, play the sound
        if (containerFluidHandler.getContainer().isEmpty() && transfer.getFluid().equals(OperationStarcleaveFluids.PETRICHORIC_PLASMA) && !player.getAbilities().instabuild) {
            player.level().playSound(
                    null,
                    player.getX(), player.getY() + 0.5, player.getZ(),
                    Items.IRON_PICKAXE.getBreakingSound(),
                    SoundSource.BLOCKS,
                    0.8F,
                    1.0F
            );
        }
    }
}

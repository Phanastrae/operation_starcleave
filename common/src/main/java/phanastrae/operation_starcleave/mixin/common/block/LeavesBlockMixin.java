package phanastrae.operation_starcleave.mixin.common.block;

import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.operation_starcleave.block.tag.OperationStarcleaveBlockTags;

import java.util.OptionalInt;

@Mixin(LeavesBlock.class)
public class LeavesBlockMixin {
    @Inject(method = "getOptionalDistanceAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;hasProperty(Lnet/minecraft/world/level/block/state/properties/Property;)Z"), cancellable = true)
    private static void operation_starcleave$preserveNonLogs(BlockState state, CallbackInfoReturnable<OptionalInt> cir) {
        if (state.is(OperationStarcleaveBlockTags.ALSO_PRESERVES_LEAVES)) {
            cir.setReturnValue(OptionalInt.of(0));
        }
    }
}

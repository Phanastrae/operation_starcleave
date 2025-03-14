package phanastrae.operation_starcleave.mixin;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow public abstract Item getItem();

    @Inject(method = "canPlaceOnBlockInAdventureMode", at = @At("HEAD"), cancellable = true)
    private void operation_starcleave$allowPlacingGolemBuckets(BlockInWorld block, CallbackInfoReturnable<Boolean> cir) {
        if(this.getItem().equals(OperationStarcleaveItems.STARCLEAVER_GOLEM_BUCKET)) {
            cir.setReturnValue(true);
        }
    }
}

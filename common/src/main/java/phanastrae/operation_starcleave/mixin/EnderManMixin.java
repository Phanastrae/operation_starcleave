package phanastrae.operation_starcleave.mixin;

import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;

@Mixin(EnderMan.class)
public class EnderManMixin {
    @Inject(method = "isLookingAtMe", at = @At("HEAD"), cancellable = true)
    private void operation_starcleave$isPlayerStaring(Player player, CallbackInfoReturnable<Boolean> cir) {
        ItemStack itemStack = player.getInventory().armor.get(3);
        if (itemStack.is(OperationStarcleaveBlocks.NETHERITE_PUMPKIN.asItem())) {
            cir.setReturnValue(false);
        }
    }
}

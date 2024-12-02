package phanastrae.operation_starcleave.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.operation_starcleave.OperationStarcleave;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements DataComponentHolder {

    @Inject(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;addAttributeTooltips(Ljava/util/function/Consumer;Lnet/minecraft/world/entity/player/Player;)V"))
    private void operationStarcleave$addComponentTooltips(Item.TooltipContext tooltipContext, Player player, TooltipFlag tooltipFlag, CallbackInfoReturnable<List<Component>> cir, @Local(ordinal = 0) Consumer<Component> componentConsumer) {
        OperationStarcleave.addTooltips((ItemStack)(Object)this, tooltipContext, componentConsumer, tooltipFlag);
    }
}

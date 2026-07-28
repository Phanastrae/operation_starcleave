package phanastrae.operation_starcleave.mixin.common.item;

import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.block.NetheritePumpkinBlock;

import java.util.List;

@Mixin(SmithingTemplateItem.class)
public class SmithingTemplateItemMixin {

    @Inject(method = "appendHoverText", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 3, shift = At.Shift.AFTER))
    private void operation_starcleave$addPumpkinText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag, CallbackInfo ci) {
        if (stack.is(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)) {
            tooltipComponents.add(CommonComponents.space().append(NetheritePumpkinBlock.SMITHING_TEMPLATE_ITEM_TEXT));
        }
    }
}

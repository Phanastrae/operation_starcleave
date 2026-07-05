package phanastrae.operation_starcleave.fabric.mixin.common.brewing;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;

@Mixin(BrewingStandBlockEntity.class)
public class BrewingStandBlockEntityMixin {

    @WrapOperation(method = "canPlaceItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z", ordinal = 4))
    private boolean operation_starcleave$allowAdditionalInputs(ItemStack stack, Item item, Operation<Boolean> original) {
        return original.call(stack, item) || stack.is(OperationStarcleaveItems.STARBLEACH_BOTTLE);
    }
}

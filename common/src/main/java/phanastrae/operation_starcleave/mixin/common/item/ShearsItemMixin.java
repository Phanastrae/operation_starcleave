package phanastrae.operation_starcleave.mixin.common.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.component.Tool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;

import java.util.ArrayList;
import java.util.List;

@Mixin(ShearsItem.class)
public class ShearsItemMixin {

    @WrapOperation(method = "createToolProperties", at = @At(value = "INVOKE", target = "Ljava/util/List;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;"))
    private static <E> List<E> operation_starcleave$modifyToolProperties(E e1, E e2, E e3, E e4, Operation<List<E>> original) {
        List<Tool.Rule> ogList = (List<Tool.Rule>) original.call(e1, e2, e3, e4);

        List<Tool.Rule> newList = new ArrayList<>();
        newList.add(Tool.Rule.minesAndDrops(List.of(OperationStarcleaveBlocks.STARBLEACHED_LEAVES), 15.0F));
        newList.addAll(ogList);

        return (List<E>) List.copyOf(newList);
    }
}

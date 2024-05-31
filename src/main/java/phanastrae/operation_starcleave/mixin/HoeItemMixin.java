package phanastrae.operation_starcleave.mixin;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemUsageContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Mixin(HoeItem.class)
public abstract class HoeItemMixin {

    @Mutable
    @Shadow @Final protected static Map<Block, Pair<Predicate<ItemUsageContext>, Consumer<ItemUsageContext>>> TILLING_ACTIONS;

    @Shadow
    public static Consumer<ItemUsageContext> createTillAction(BlockState result) {
        return null;
    }

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void operation_starcleave$onInit(CallbackInfo ci) {
        Consumer<ItemUsageContext> till = createTillAction(OperationStarcleaveBlocks.STELLAR_FARMLAND.getDefaultState());

        TILLING_ACTIONS.put(OperationStarcleaveBlocks.STELLAR_MULCH, Pair.of(HoeItem::canTillFarmland, till));
    }
}

package phanastrae.operation_starcleave.fabric.mixin;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
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

    @Shadow @Final protected static Map<Block, Pair<Predicate<UseOnContext>, Consumer<UseOnContext>>> TILLABLES;

    @Shadow public static Consumer<UseOnContext> changeIntoState(BlockState state) {
        throw new AssertionError();
    }

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void operation_starcleave$onInit(CallbackInfo ci) {
        Consumer<UseOnContext> till = changeIntoState(OperationStarcleaveBlocks.STELLAR_FARMLAND.defaultBlockState());

        TILLABLES.put(OperationStarcleaveBlocks.STELLAR_MULCH, Pair.of(HoeItem::onlyIfAirAbove, till));
    }
}

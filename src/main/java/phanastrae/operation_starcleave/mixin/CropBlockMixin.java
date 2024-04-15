package phanastrae.operation_starcleave.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;

@Mixin(CropBlock.class)
public class CropBlockMixin {

    @Inject(method = "canPlantOnTop", at = @At("HEAD"), cancellable = true)
    private void operation_starcleave$canPlantOnTop(BlockState floor, BlockView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if(floor.isOf(OperationStarcleaveBlocks.STELLAR_FARMLAND)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getAvailableMoisture", at = @At("HEAD"), cancellable = true)
    private static void operation_starcleave$getAvailableMoisture(Block block, BlockView world, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        BlockState state = world.getBlockState(pos.down());
        if(state.isOf(OperationStarcleaveBlocks.STELLAR_FARMLAND)) {
            if(state.get(FarmlandBlock.MOISTURE) < 7) {
                // return max moisture normally obtainable (10)
                cir.setReturnValue(10f);
            } else {
                // return enhanced moisture value
                cir.setReturnValue(20f);
            }
        }
    }
}

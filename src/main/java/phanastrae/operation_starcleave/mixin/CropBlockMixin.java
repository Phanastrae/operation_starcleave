package phanastrae.operation_starcleave.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;

@Mixin(CropBlock.class)
public class CropBlockMixin {

    @Inject(method = "mayPlaceOn", at = @At("HEAD"), cancellable = true)
    private void operation_starcleave$canPlantOnTop(BlockState floor, BlockGetter world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if(floor.is(OperationStarcleaveBlocks.STELLAR_FARMLAND)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getGrowthSpeed", at = @At("HEAD"), cancellable = true)
    private static void operation_starcleave$getAvailableMoisture(Block block, BlockGetter world, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        BlockState state = world.getBlockState(pos.below());
        if(state.is(OperationStarcleaveBlocks.STELLAR_FARMLAND)) {
            if(state.getValue(FarmBlock.MOISTURE) < 7) {
                // return max moisture normally obtainable (10)
                cir.setReturnValue(10f);
            } else {
                // return enhanced moisture value
                cir.setReturnValue(20f);
            }
        }
    }
}

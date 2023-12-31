package phanastrae.operation_starcleave.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Math;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.block.BlessedBedBlock;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;
import phanastrae.operation_starcleave.block.StellarRepulsorBlock;
import phanastrae.operation_starcleave.item.StarbleachCoating;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "eatFood", at = @At("HEAD"))
    private void operation_starcleave$eatStarbleachedFood(World world, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        StarbleachCoating.onEat((LivingEntity)(Object)this, world, stack);
    }

    @Inject(method = "jump", at = @At("RETURN"))
    private void operation_starcleave$repulsorJump(CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity)(Object)this;
        BlockState blockState = livingEntity.getWorld().getBlockState(livingEntity.getBlockPos().down());
        if(blockState.isOf(OperationStarcleaveBlocks.STELLAR_REPULSOR)) {
            StellarRepulsorBlock.launch(livingEntity);
        }
    }

    @Inject(method = "sleep", at = @At("HEAD"))
    private void operation_starcleave$blessedBed(BlockPos pos, CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity)(Object)this;
        World world = livingEntity.getWorld();
        BlockState blockState = world.getBlockState(pos);
        if(blockState.isOf(OperationStarcleaveBlocks.BLESSED_BED)) {
            BlessedBedBlock.blessedSleep(livingEntity);
        }
    }
}

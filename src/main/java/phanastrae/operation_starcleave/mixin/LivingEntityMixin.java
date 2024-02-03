package phanastrae.operation_starcleave.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Math;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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

    @Shadow protected boolean jumping;

    @Shadow private int jumpingCooldown;

    @Inject(method = "eatFood", at = @At("HEAD"))
    private void operation_starcleave$eatStarbleachedFood(World world, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        StarbleachCoating.onEat((LivingEntity)(Object)this, world, stack);
    }

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void operation_starcleave$repulsorVehicleJump(CallbackInfo ci) {
        if(this.jumping && this.jumpingCooldown == 0) {
            LivingEntity livingEntity = (LivingEntity)(Object)this;
            World world = livingEntity.getWorld();
            Entity controllingVehicle = livingEntity.getControllingVehicle();
            if(controllingVehicle != null && controllingVehicle.getVelocity().y < 0.01) {
                StellarRepulsorBlock.tryLaunch(controllingVehicle);
                this.jumpingCooldown = 10;
            }
        }
    }

    @Inject(method = "jump", at = @At("RETURN"))
    private void operation_starcleave$repulsorJump(CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity)(Object)this;
        StellarRepulsorBlock.tryLaunch(livingEntity);
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

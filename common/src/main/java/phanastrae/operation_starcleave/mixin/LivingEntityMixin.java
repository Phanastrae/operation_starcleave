package phanastrae.operation_starcleave.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.operation_starcleave.block.BlessedBedBlock;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;
import phanastrae.operation_starcleave.block.StellarRepulsorBlock;
import phanastrae.operation_starcleave.entity.OperationStarcleaveDamageTypeTags;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityAttachment;
import phanastrae.operation_starcleave.item.StarbleachCoating;
import phanastrae.operation_starcleave.world.firmament.Firmament;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow protected boolean jumping;

    @Shadow public abstract boolean hurt(DamageSource source, float amount);

    @Shadow public abstract boolean hasEffect(Holder<MobEffect> effect);

    @Shadow private int noJumpDelay;

    LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Inject(method = "eat(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/food/FoodProperties;)Lnet/minecraft/world/item/ItemStack;", at = @At("HEAD"))
    private void operation_starcleave$eatStarbleachedFood(Level world, ItemStack stack, FoodProperties foodComponent, CallbackInfoReturnable<ItemStack> cir) {
        StarbleachCoating.onEat((LivingEntity)(Object)this, world, stack);
    }

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void operation_starcleave$repulsorVehicleJump(CallbackInfo ci) {
        if(this.jumping && this.noJumpDelay == 0) {
            LivingEntity livingEntity = (LivingEntity)(Object)this;
            Entity controllingVehicle = livingEntity.getControlledVehicle();
            if(controllingVehicle != null && controllingVehicle.getDeltaMovement().y < 0.01 && controllingVehicle.onGround()) {
                StellarRepulsorBlock.tryLaunch(controllingVehicle);
                this.noJumpDelay = 10;
            }
        }
    }

    @Inject(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;handleRelativeFrictionAndCalculateMovement(Lnet/minecraft/world/phys/Vec3;F)Lnet/minecraft/world/phys/Vec3;"))
    private void operation_starcleave$reduceGlidingFriction(Vec3 travelVector, CallbackInfo ci, @Local(ordinal = 1) LocalFloatRef frictionMultiplierRef) {
        if(OperationStarcleaveEntityAttachment.fromEntity(this).isPegasusGliding() && !this.onGround()) {
            Firmament firmament = Firmament.fromLevel(this.level());
            int damage = firmament == null ? 0 : firmament.getDamage(this.getBlockX(), this.getBlockZ());
            int skyLight = this.level().getBrightness(LightLayer.SKY, this.blockPosition());
            float starlight = (damage / 7F) * (skyLight / 15F);

            frictionMultiplierRef.set(Mth.lerp(starlight, 0.965F, 0.995F));
        }
    }

    @Inject(method = "jumpFromGround", at = @At("RETURN"))
    private void operation_starcleave$repulsorJump(CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity)(Object)this;
        StellarRepulsorBlock.tryLaunch(livingEntity);
    }

    @Inject(method = "startSleeping", at = @At("HEAD"))
    private void operation_starcleave$blessedBed(BlockPos pos, CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity)(Object)this;
        Level level = livingEntity.level();
        BlockState blockState = level.getBlockState(pos);
        if(blockState.is(OperationStarcleaveBlocks.BLESSED_BED)) {
            BlessedBedBlock.blessedSleep(livingEntity);
        }
    }

    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/WalkAnimationState;setSpeed(F)V", ordinal = 0, shift = At.Shift.BEFORE))
    private void operation_starcleave$handlePhlogisticFireDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 0) LocalFloatRef localRef) {
        if(source.is(OperationStarcleaveDamageTypeTags.IS_PHLOGISTIC_FIRE)) {
            float damage = localRef.get();
            if(this.fireImmune() || this.hasEffect(MobEffects.FIRE_RESISTANCE)) {
                damage *= 0.5F;
            }

            localRef.set(damage);
        }
    }
}

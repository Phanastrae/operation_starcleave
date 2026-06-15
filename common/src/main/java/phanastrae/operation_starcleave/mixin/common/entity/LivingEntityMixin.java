package phanastrae.operation_starcleave.mixin.common.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.operation_starcleave.block.BlessedBedBlock;
import phanastrae.operation_starcleave.block.StellarRepulsorBlock;
import phanastrae.operation_starcleave.block.tag.OperationStarcleaveBlockTags;
import phanastrae.operation_starcleave.entity.OperationStarcleaveDamageTypeTags;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityAttachment;
import phanastrae.operation_starcleave.item.StarbleachCoating;
import phanastrae.operation_starcleave.world.firmament.Firmament;

import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow
    protected boolean jumping;

    @Shadow
    public abstract boolean hurt(DamageSource source, float amount);

    @Shadow
    public abstract boolean hasEffect(Holder<MobEffect> effect);

    @Shadow
    private int noJumpDelay;

    LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Inject(method = "eat(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/food/FoodProperties;)Lnet/minecraft/world/item/ItemStack;", at = @At("HEAD"))
    private void operation_starcleave$eatStarbleachedFood(Level world, ItemStack stack, FoodProperties foodComponent, CallbackInfoReturnable<ItemStack> cir) {
        StarbleachCoating.onEat((LivingEntity) (Object) this, world, stack);
    }

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void operation_starcleave$repulsorVehicleJump(CallbackInfo ci) {
        if (this.jumping && this.noJumpDelay == 0) {
            LivingEntity livingEntity = (LivingEntity) (Object) this;
            Entity controllingVehicle = livingEntity.getControlledVehicle();
            if (controllingVehicle != null && controllingVehicle.getDeltaMovement().y < 0.01 && controllingVehicle.onGround()) {
                StellarRepulsorBlock.tryLaunch(controllingVehicle);
                this.noJumpDelay = 10;
            }
        }
    }

    @Inject(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;handleRelativeFrictionAndCalculateMovement(Lnet/minecraft/world/phys/Vec3;F)Lnet/minecraft/world/phys/Vec3;"))
    private void operation_starcleave$reduceGlidingFriction(Vec3 travelVector, CallbackInfo ci, @Local(ordinal = 1) LocalFloatRef frictionMultiplierRef) {
        if (OperationStarcleaveEntityAttachment.fromEntity(this).isPegasusGliding() && !this.onGround()) {
            Firmament firmament = Firmament.fromLevel(this.level());
            int damage = firmament == null ? 0 : firmament.getDamage(this.getBlockX(), this.getBlockZ());
            int skyLight = this.level().getBrightness(LightLayer.SKY, this.blockPosition());
            float starlight = (damage / 7F) * (skyLight / 15F);

            frictionMultiplierRef.set(Mth.lerp(starlight, 0.965F, 0.995F));
        }
    }

    @WrapOperation(method = "travel", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/world/phys/Vec3;horizontalDistance()D"))
    private double operation_starcleave$elytraThingy(Vec3 currentVelocity, Operation<Double> original, @Local(ordinal = 2) double originalHorizontalSpeed, @Local(ordinal = 1) Vec3 preDragVelocity) {
        if (!this.level().isClientSide && operation_starcleave$isSoftLanding(currentVelocity, preDragVelocity)) {
            // return original horizontal speed so that difference is zero so that damage does not happen
            return originalHorizontalSpeed;
        } else {
            return original.call(currentVelocity);
        }
    }

    @Unique
    private boolean operation_starcleave$isSoftLanding(Vec3 currentVelocity, Vec3 preDragVelocity) {
        // expand with pre-drag velocity (which is slightly higher than the actual velocity used when moving, but that's fine)
        AABB box = this.getBoundingBox().expandTowards(preDragVelocity.scale(-1));
        box = box.inflate(1E-6);
        Vec3 minPos = box.getMinPosition();
        Vec3 maxPos = box.getMaxPosition();

        if (Math.abs(currentVelocity.x) < 1E-6 && Math.abs(preDragVelocity.x) >= 1E-6) {
            // was moving along x-axis, and is not anymore
            double x = preDragVelocity.x > 0
                    ? maxPos.x
                    : minPos.x;
            if (operation_starcleave$softBlocksInRange(this.level(), new Vec3(x, minPos.y, minPos.z), new Vec3(x, maxPos.y, maxPos.z))) {
                return true;
            }
        }


        if (Math.abs(currentVelocity.z) < 1E-6 && Math.abs(preDragVelocity.z) >= 1E-6) {
            // was moving along z-axis, and is not anymore
            double z = preDragVelocity.z > 0
                    ? maxPos.z
                    : minPos.z;
            if (operation_starcleave$softBlocksInRange(this.level(), new Vec3(minPos.x, minPos.y, z), new Vec3(maxPos.x, maxPos.y, z))) {
                return true;
            }
        }

        return false;
    }

    @Unique
    private static boolean operation_starcleave$softBlocksInRange(Level level, Vec3 minPos, Vec3 maxPos) {
        for (BlockPos pos : BlockPos.betweenClosed(BlockPos.containing(minPos), BlockPos.containing(maxPos))) {
            BlockState state = level.getBlockState(pos);
            if (state.is(OperationStarcleaveBlockTags.PREVENTS_ELYTRA_WALL_DAMAGE)) {
                AtomicBoolean hasCollision = new AtomicBoolean(false);

                state.getCollisionShape(level, pos).move(pos.getX(), pos.getY(), pos.getZ())
                        .forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
                            if (hasCollision.get()) {
                                // skip if collision is already found
                                return;
                            }

                            if (minX <= maxPos.x && minPos.x <= maxX
                                    && minY <= maxPos.y && minPos.y <= maxY
                                    && minZ <= maxPos.z && minPos.z <= maxZ) {
                                // boxes collide
                                hasCollision.set(true);
                            }
                        });

                return hasCollision.get();
            }
        }
        return false;
    }

    @Inject(method = "jumpFromGround", at = @At("RETURN"))
    private void operation_starcleave$repulsorJump(CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        StellarRepulsorBlock.tryLaunch(livingEntity);
    }

    @Inject(method = "stopSleeping", at = @At("HEAD"))
    private void operation_starcleave$onStopSleeping(CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        if (livingEntity.hurtTime > 0) {
            // do not grant blessed sleep if recently hurt
            return;
        }

        if (!(livingEntity instanceof Player player) || player.isSleepingLongEnough()) {
            // if night is skipped, all players sleeping in blessed beds are granted the buffs (this is done in ServerLevelMixin)
            // if night is not skipped (e.g. due to players being awake) but a player has been sleeping long enough, then give them the blessed bed buffs anyways on wakeup
            // for mobs, just give them the effects no matter how long they've slept
            BlessedBedBlock.attemptBlessedSleep(livingEntity);
        }
    }

    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/WalkAnimationState;setSpeed(F)V", ordinal = 0, shift = At.Shift.BEFORE))
    private void operation_starcleave$handlePhlogisticFireDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 0) LocalFloatRef localRef) {
        if (source.is(OperationStarcleaveDamageTypeTags.IS_PHLOGISTIC_FIRE)) {
            float damage = localRef.get();
            if (this.fireImmune() || this.hasEffect(MobEffects.FIRE_RESISTANCE)) {
                damage *= 0.5F;
            }

            localRef.set(damage);
        }
    }

    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V", ordinal = 1))
    private void operation_starcleave$reduceCooldown(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.is(OperationStarcleaveDamageTypeTags.REDUCED_COOLDOWN)) {
            this.invulnerableTime = 15;
        }
    }
}

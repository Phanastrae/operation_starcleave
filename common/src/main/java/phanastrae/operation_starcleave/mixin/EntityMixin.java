package phanastrae.operation_starcleave.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;
import phanastrae.operation_starcleave.duck.EntityDuckInterface;
import phanastrae.operation_starcleave.entity.OperationStarcleaveDamageTypeTags;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityAttachment;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypeTags;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityDuckInterface {
    @Shadow public abstract boolean isSpectator();
    @Shadow public abstract EntityType<?> getType();
    @Shadow public abstract AABB getBoundingBox();
    @Shadow public abstract Level level();
    @Shadow public abstract Component getName();

    @Unique
    private OperationStarcleaveEntityAttachment operation_starcleave$entityAttachment;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void operation_starcleave$init(EntityType type, Level world, CallbackInfo ci) {
        this.operation_starcleave$entityAttachment = new OperationStarcleaveEntityAttachment((Entity)(Object)this);
    }

    @Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", shift = At.Shift.AFTER))
    private void operation_starcleave$writeNbt(CompoundTag nbt, CallbackInfoReturnable<CompoundTag> cir) {
        if(nbt.contains("operation_starcleave", Tag.TAG_COMPOUND)) {
            CompoundTag tag = nbt.getCompound("operation_starcleave");
            this.operation_starcleave$entityAttachment.writeNbt(tag);
        } else {
            CompoundTag tag = new CompoundTag();
            this.operation_starcleave$entityAttachment.writeNbt(tag);
            nbt.put("operation_starcleave", tag);
        }
    }

    @Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", shift = At.Shift.AFTER))
    private void operation_starcleave$readNbt(CompoundTag nbt, CallbackInfo ci) {
        if(nbt.contains("operation_starcleave", Tag.TAG_COMPOUND)) {
            CompoundTag tag = nbt.getCompound("operation_starcleave");
            this.operation_starcleave$entityAttachment.readNbt(tag);
        }
    }

    @Inject(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;push(Ljava/lang/String;)V", shift = At.Shift.AFTER))
    private void operation_starcleave$baseTick(CallbackInfo ci) {
        this.operation_starcleave$entityAttachment.baseTick();
    }

    @Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V", ordinal = 1,  shift = At.Shift.AFTER))
    private void operation_starcleave$resetPhlogisticFireTicks(MoverType movementType, Vec3 movement, CallbackInfo ci) {
        if (this.operation_starcleave$entityAttachment.getPhlogisticFireTicks() <= 0) {
            if (this.level()
                    .getBlockStatesIfLoaded(this.getBoundingBox().deflate(1.0E-6))
                    .noneMatch(state -> state.is(OperationStarcleaveBlocks.PHLOGISTIC_FIRE))) {
                this.operation_starcleave$entityAttachment.setPhlogisticFireTicks(-1);
            }
        }
    }

    @Inject(method = "displayFireAnimation", at = @At("HEAD"), cancellable = true)
    private void operation_Starcleave$forceRenderOnFire(CallbackInfoReturnable<Boolean> cir) {
        if(this.operation_starcleave$entityAttachment.isOnPhlogisticFire() && !this.isSpectator()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
    private void operation_starcleave$isInvulnerableTo(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if(damageSource.is(OperationStarcleaveDamageTypeTags.IS_PHLOGISTIC_FIRE)) {
            if(this.getType().is(OperationStarcleaveEntityTypeTags.PHLOGISTIC_FIRE_IMMUNE)) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "getGravity", at = @At("HEAD"), cancellable = true)
    private void operation_starcleave$cancelGravity(CallbackInfoReturnable<Double> cir) {
        if(this.operation_starcleave$entityAttachment.shouldCancelGravity()) {
            cir.setReturnValue(0.0);
        }
    }

    @Override
    public OperationStarcleaveEntityAttachment operation_starcleave$getAttachment() {
        return this.operation_starcleave$entityAttachment;
    }
}

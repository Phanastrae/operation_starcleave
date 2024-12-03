package phanastrae.operation_starcleave.mixin;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;
import phanastrae.operation_starcleave.duck.EntityDuck;
import phanastrae.operation_starcleave.entity.OperationStarcleaveDamageTypeTags;
import phanastrae.operation_starcleave.entity.OperationStarcleaveDamageTypes;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypeTags;
import phanastrae.operation_starcleave.network.packet.EntityPhlogisticFirePayload;
import phanastrae.operation_starcleave.services.XPlatInterface;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityDuck {
    @Shadow public abstract boolean isSpectator();

    @Shadow public abstract EntityType<?> getType();

    @Shadow public abstract AABB getBoundingBox();

    @Shadow public abstract Level level();

    @Shadow private BlockPos blockPosition;

    @Shadow public abstract int getTicksFrozen();

    @Shadow public abstract void setTicksFrozen(int ticksFrozen);

    @Shadow public abstract boolean hurt(DamageSource source, float amount);

    private long operation_starcleave$lastRepulsorUse = Long.MIN_VALUE;
    private boolean operation_starcleave$onPhlogisticFire = false;
    private int operation_starcleave$phlogisticFireTicks = -1;

    @Override
    public long operation_starcleave$getLastStellarRepulsorUse() {
        return operation_starcleave$lastRepulsorUse;
    }

    @Override
    public void operation_starcleave$setLastStellarRepulsorUse(long time) {
        this.operation_starcleave$lastRepulsorUse = time;
    }

    @Override
    public boolean operation_starcleave$isOnPhlogisticFire() {
        return operation_starcleave$onPhlogisticFire;
    }

    @Override
    public void operation_starcleave$setOnPhlogisticFire(boolean onPhlogisticFire) {
        boolean wasOnPhlogisticFire = this.operation_starcleave$onPhlogisticFire;
        this.operation_starcleave$onPhlogisticFire = onPhlogisticFire;

        if(onPhlogisticFire != wasOnPhlogisticFire) {
            if (!this.level().isClientSide && this.level() instanceof ServerLevel) {
                Entity entity = (Entity)(Object)this;

                EntityPhlogisticFirePayload payload = new EntityPhlogisticFirePayload(entity.getId(), onPhlogisticFire);

                for (ServerPlayer player : PlayerLookup.tracking(entity)) {
                    if(player != entity) {
                        XPlatInterface.INSTANCE.sendPayload(player, payload);
                    }
                }
                if(entity instanceof ServerPlayer player) {
                    XPlatInterface.INSTANCE.sendPayload(player, payload);
                }
            }
        }
    }

    @Override
    public int operation_starcleave$getPhlogisticFireTicks() {
        return this.operation_starcleave$phlogisticFireTicks;
    }

    @Override
    public void operation_starcleave$setPhlogisticFireTicks(int phlogisticFireTicks) {
        Entity entity = (Entity)(Object)this;

        if(entity instanceof Player player) {
            this.operation_starcleave$phlogisticFireTicks = player.getAbilities().invulnerable ? Math.min(phlogisticFireTicks, 2) : phlogisticFireTicks;
        } else {
            this.operation_starcleave$phlogisticFireTicks = phlogisticFireTicks;
        }
    }

    @Override
    public void operation_starcleave$setOnPhlogisticFireFor(float seconds) {
        this.operation_starcleave$setOnPhlogisticFireForTicks(Mth.floor(seconds * 20.0F));
    }

    @Override
    public void operation_starcleave$setOnPhlogisticFireForTicks(int ticks) {
        Entity entity = (Entity)(Object)this;
        if(entity instanceof LivingEntity livingEntity) {
            ticks = Mth.ceil((double)ticks * livingEntity.getAttributeValue(Attributes.BURNING_TIME));

            if((livingEntity).hasEffect(MobEffects.FIRE_RESISTANCE)) {
                ticks /= 3;
            }
        }

        if(entity.fireImmune()) {
            ticks /= 2;
        }

        if (this.operation_starcleave$phlogisticFireTicks < ticks) {
            this.operation_starcleave$setPhlogisticFireTicks(ticks);
        }
    }

    @Inject(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;push(Ljava/lang/String;)V", shift = At.Shift.AFTER))
    private void operation_starcleave$baseTick(CallbackInfo ci) {
        if (!this.level().isClientSide && this.operation_starcleave$phlogisticFireTicks > 0) {
            if (this.operation_starcleave$phlogisticFireTicks % 10 == 0) {
                this.hurt(OperationStarcleaveDamageTypes.of(this.level(), OperationStarcleaveDamageTypes.ON_PHLOGISTIC_FIRE), 1.5F);
            }

            this.operation_starcleave$setPhlogisticFireTicks(this.operation_starcleave$phlogisticFireTicks - 1);

            if (this.getTicksFrozen() > 0) {
                this.setTicksFrozen(0);
                this.level() .levelEvent(null, LevelEvent.SOUND_EXTINGUISH_FIRE, this.blockPosition, 1);
            }
        }

        if (!this.level().isClientSide) {
            this.operation_starcleave$setOnPhlogisticFire(this.operation_starcleave$phlogisticFireTicks > 0);
        }
    }

    @Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V", ordinal = 1,  shift = At.Shift.AFTER))
    private void operation_starcleave$resetPhlogisticFireTicks(MoverType movementType, Vec3 movement, CallbackInfo ci) {
        if (this.operation_starcleave$phlogisticFireTicks <= 0) {
            if (this.level()
                    .getBlockStatesIfLoaded(this.getBoundingBox().deflate(1.0E-6))
                    .noneMatch(state -> state.is(OperationStarcleaveBlocks.PHLOGISTIC_FIRE))) {
                this.operation_starcleave$setPhlogisticFireTicks(-1);
            }
        }
    }

    @Inject(method = "displayFireAnimation", at = @At("HEAD"), cancellable = true)
    private void operation_Starcleave$forceRenderOnFire(CallbackInfoReturnable<Boolean> cir) {
        if(this.operation_starcleave$isOnPhlogisticFire() && !this.isSpectator()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", shift = At.Shift.AFTER))
    private void operation_starcleave$writeNbt(CompoundTag nbt, CallbackInfoReturnable<CompoundTag> cir) {
        nbt.putShort("OperationStarcleavePhlogisticFireTicks", (short)this.operation_starcleave$getPhlogisticFireTicks());
    }

    @Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", shift = At.Shift.AFTER))
    private void operation_starcleave$readNbt(CompoundTag nbt, CallbackInfo ci) {
        if(nbt.contains("OperationStarcleavePhlogisticFireTicks", Tag.TAG_SHORT)) {
            this.operation_starcleave$setPhlogisticFireTicks(nbt.getShort("OperationStarcleavePhlogisticFireTicks"));
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
}

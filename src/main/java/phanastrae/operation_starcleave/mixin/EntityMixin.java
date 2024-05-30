package phanastrae.operation_starcleave.mixin;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.operation_starcleave.entity.OperationStarcleaveDamageTypeTags;
import phanastrae.operation_starcleave.entity.OperationStarcleaveDamageTypes;
import phanastrae.operation_starcleave.duck.EntityDuck;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypeTags;
import phanastrae.operation_starcleave.network.packet.s2c.EntityPhlogisticFireS2CPacket;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityDuck {
    @Shadow private BlockPos blockPos;
    @Shadow public abstract World getWorld();
    @Shadow public abstract boolean isSpectator();
    @Shadow public abstract int getFrozenTicks();
    @Shadow public abstract void setFrozenTicks(int frozenTicks);
    @Shadow public abstract boolean damage(DamageSource source, float amount);

    @Shadow public abstract boolean isRemoved();

    @Shadow private boolean invulnerable;

    @Shadow public abstract EntityType<?> getType();

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
            if (!this.getWorld().isClient && this.getWorld() instanceof ServerWorld) {
                Entity entity = (Entity)(Object)this;

                EntityPhlogisticFireS2CPacket packet = new EntityPhlogisticFireS2CPacket(entity, onPhlogisticFire);

                for (ServerPlayerEntity player : PlayerLookup.tracking(entity)) {
                    if(player != entity) {
                        ServerPlayNetworking.send(player, packet);
                    }
                }
                if(entity instanceof ServerPlayerEntity player) {
                    ServerPlayNetworking.send(player, packet);
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

        if(entity instanceof PlayerEntity player) {
            this.operation_starcleave$phlogisticFireTicks = player.getAbilities().invulnerable ? Math.min(phlogisticFireTicks, 2) : phlogisticFireTicks;
        } else {
            this.operation_starcleave$phlogisticFireTicks = phlogisticFireTicks;
        }
    }

    @Override
    public void operation_starcleave$setOnPhlogisticFireFor(int seconds) {
        Entity entity = (Entity)(Object)this;

        int i = seconds * 20;
        if (entity instanceof LivingEntity) {
            i = ProtectionEnchantment.transformFireDuration((LivingEntity)entity, i);

            if(((LivingEntity)entity).hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
                i /= 3;
            }
        }

        if(entity.isFireImmune()) {
            i /= 2;
        }

        if (this.operation_starcleave$phlogisticFireTicks < i) {
            this.operation_starcleave$setPhlogisticFireTicks(i);
        }
    }

    @Inject(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V", shift = At.Shift.AFTER))
    private void operation_starcleave$baseTick(CallbackInfo ci) {
        if (!this.getWorld().isClient && this.operation_starcleave$phlogisticFireTicks > 0) {
            if (this.operation_starcleave$phlogisticFireTicks % 10 == 0) {
                this.damage(OperationStarcleaveDamageTypes.of(this.getWorld(), OperationStarcleaveDamageTypes.ON_PHLOGISTIC_FIRE), 1.5F);
            }

            this.operation_starcleave$setPhlogisticFireTicks(this.operation_starcleave$phlogisticFireTicks - 1);

            if (this.getFrozenTicks() > 0) {
                this.setFrozenTicks(0);
                this.getWorld().syncWorldEvent(null, WorldEvents.FIRE_EXTINGUISHED, this.blockPos, 1);
            }
        }

        if (!this.getWorld().isClient) {
            this.operation_starcleave$setOnPhlogisticFire(this.operation_starcleave$phlogisticFireTicks > 0);
        }
    }

    @Inject(method = "move", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;noneMatch(Ljava/util/function/Predicate;)Z", shift = At.Shift.BY, by = 2))
    private void operation_starcleave$resetPhlogisticFireTicks(MovementType movementType, Vec3d movement, CallbackInfo ci) {
        if (this.operation_starcleave$phlogisticFireTicks <= 0) {
            this.operation_starcleave$setPhlogisticFireTicks(-1);
        }
    }

    @Inject(method = "doesRenderOnFire", at = @At("HEAD"), cancellable = true)
    private void operation_Starcleave$forceRenderOnFire(CallbackInfoReturnable<Boolean> cir) {
        if(this.operation_starcleave$isOnPhlogisticFire() && !this.isSpectator()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "writeNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;writeCustomDataToNbt(Lnet/minecraft/nbt/NbtCompound;)V", shift = At.Shift.AFTER))
    private void operation_starcleave$writeNbt(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
        nbt.putShort("OperationStarcleavePhlogisticFireTicks", (short)this.operation_starcleave$getPhlogisticFireTicks());
    }

    @Inject(method = "readNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;readCustomDataFromNbt(Lnet/minecraft/nbt/NbtCompound;)V", shift = At.Shift.AFTER))
    private void operation_starcleave$readNbt(NbtCompound nbt, CallbackInfo ci) {
        if(nbt.contains("OperationStarcleavePhlogisticFireTicks", NbtElement.SHORT_TYPE)) {
            this.operation_starcleave$setPhlogisticFireTicks(nbt.getShort("OperationStarcleavePhlogisticFireTicks"));
        }
    }

    @Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
    private void operation_starcleave$isInvulnerableTo(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if(damageSource.isIn(OperationStarcleaveDamageTypeTags.IS_PHLOGISTIC_FIRE)) {
            if(this.getType().isIn(OperationStarcleaveEntityTypeTags.PHLOGISTIC_FIRE_IMMUNE)) {
                cir.setReturnValue(true);
            }
        }
    }
}

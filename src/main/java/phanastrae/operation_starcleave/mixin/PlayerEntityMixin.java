package phanastrae.operation_starcleave.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.duck.EntityDuck;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "onDeath", at = @At("RETURN"))
    private void operation_starcleave$onDeath(DamageSource damageSource, CallbackInfo ci) {
        ((EntityDuck)this).operation_starcleave$setPhlogisticFireTicks(0);
        ((EntityDuck)this).operation_starcleave$setOnPhlogisticFire(false);
    }
}

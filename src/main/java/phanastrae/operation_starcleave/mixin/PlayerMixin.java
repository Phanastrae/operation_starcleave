package phanastrae.operation_starcleave.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.duck.EntityDuck;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

    PlayerMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "die", at = @At("RETURN"))
    private void operation_starcleave$onDeath(DamageSource damageSource, CallbackInfo ci) {
        ((EntityDuck)this).operation_starcleave$setPhlogisticFireTicks(0);
        ((EntityDuck)this).operation_starcleave$setOnPhlogisticFire(false);
    }
}

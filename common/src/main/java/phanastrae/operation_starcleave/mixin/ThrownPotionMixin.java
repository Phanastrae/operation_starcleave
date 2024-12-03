package phanastrae.operation_starcleave.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;

@Mixin(ThrownPotion.class)
public abstract class ThrownPotionMixin extends ThrowableItemProjectile implements ItemSupplier {

    protected ThrownPotionMixin(EntityType<? extends ThrowableItemProjectile> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "dowseFire", at = @At("HEAD"), cancellable = true)
    private void operation_starcleave$cancelPhlogisticFireExtinguish(BlockPos pos, CallbackInfo ci) {
        BlockState blockState = this.level().getBlockState(pos);
        if (blockState.is(OperationStarcleaveBlocks.PHLOGISTIC_FIRE)) {
            ci.cancel();
        }
    }
}

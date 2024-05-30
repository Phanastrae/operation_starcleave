package phanastrae.operation_starcleave.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;

@Mixin(PotionEntity.class)
public abstract class PotionEntityMixin extends ThrownItemEntity implements FlyingItemEntity {

    protected PotionEntityMixin(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "extinguishFire", at = @At("HEAD"), cancellable = true)
    private void operation_starcleave$cancelPhlogisticFireExtinguish(BlockPos pos, CallbackInfo ci) {
        BlockState blockState = this.getWorld().getBlockState(pos);
        if (blockState.isOf(OperationStarcleaveBlocks.PHLOGISTIC_FIRE)) {
            ci.cancel();
        }
    }
}

package phanastrae.operation_starcleave.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.ShootTongue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.operation_starcleave.entity.mob.SubcaelicTorpedoEntity;

@Mixin(ShootTongue.class)
public class ShootTongueMixin {

    @Inject(method = "canPathfindToTarget", at = @At("HEAD"), cancellable = true)
    private void operation_starcleave$allowCatchingMidairTorpedos(Frog frog, LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        if(target instanceof SubcaelicTorpedoEntity && target.closerThan(frog, 1.75F)) {
            double dx = frog.getX() - target.getX();
            double dy = frog.getY() - target.getY();
            double dz = frog.getZ() - target.getZ();

            double horizontalDSqr = dx*dx + dz*dz;
            double dySqr = dy * dy;

            // allow catching if angle is within 60 degrees of horizontal
            double f = 1.732;
            if(dySqr < horizontalDSqr * f * f) {
                cir.setReturnValue(true);
            }
        }
    }
}

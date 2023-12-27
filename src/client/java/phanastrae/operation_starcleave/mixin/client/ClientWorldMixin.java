package phanastrae.operation_starcleave.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import phanastrae.operation_starcleave.world.OperationStarcleaveWorld;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {

    @Final
    @Shadow
    private MinecraftClient client;

    @ModifyReturnValue(method = "getSkyColor", at = @At("RETURN"))
    private Vec3d operation_starcleave$doCleavingFlash(Vec3d original, Vec3d cameraPos, float tickDelta) {
        int flashTicks = this.client.options.getHideLightningFlashes().getValue() ? 0 : ((OperationStarcleaveWorld)this).operation_starcleave$getCleavingFlashTicksLeft();
        if (flashTicks > 0) {
            float flashAmount = (float)flashTicks - tickDelta;
            if (flashAmount > 1.0F) {
                flashAmount = 1.0F;
            }

            flashAmount *= 0.8f;
            double r = original.x * (1.0F - flashAmount) + 1.0F * flashAmount;
            double g = original.y * (1.0F - flashAmount) + 0.8F * flashAmount;
            double b = original.z * (1.0F - flashAmount) + 0.3F * flashAmount;
            return new Vec3d(r, g, b);
        }
        return original;
    }
}

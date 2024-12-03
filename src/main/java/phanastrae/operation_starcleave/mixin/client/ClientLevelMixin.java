package phanastrae.operation_starcleave.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.world.firmament.FirmamentHolder;
import phanastrae.operation_starcleave.duck.LevelDuck;
import phanastrae.operation_starcleave.client.world.firmament.ClientFirmamentRegionManager;
import phanastrae.operation_starcleave.world.firmament.Firmament;
import phanastrae.operation_starcleave.client.world.starbleach.StarbleachParticles;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

@Mixin(ClientLevel.class)
public class ClientLevelMixin implements FirmamentHolder {

    @Shadow @Final private Minecraft minecraft;
    private Firmament operation_starcleave$firmament;


    @Inject(method = "<init>", at = @At("RETURN"))
    private void operation_starcleave$onInit(ClientPacketListener networkHandler, ClientLevel.ClientLevelData properties, ResourceKey registryRef, Holder dimensionTypeEntry, int loadDistance, int simulationDistance, Supplier profiler, LevelRenderer worldRenderer, boolean debugWorld, long seed, CallbackInfo ci) {
        this.operation_starcleave$firmament = new Firmament((Level)(Object)this, new ClientFirmamentRegionManager((ClientLevel)(Object)this));
    }

    @Override
    public Firmament operation_starcleave$getFirmament() {
        return this.operation_starcleave$firmament;
    }

    @ModifyReturnValue(method = "getSkyColor", at = @At("RETURN"))
    private Vec3 operation_starcleave$doCleavingFlash(Vec3 original, Vec3 cameraPos, float tickDelta) {
        int flashTicks = this.minecraft.options.hideLightningFlash().get() ? 0 : ((LevelDuck)this).operation_starcleave$getCleavingFlashTicksLeft();
        if (flashTicks > 0) {
            float flashAmount = (float)flashTicks - tickDelta;
            if (flashAmount > 1.0F) {
                flashAmount = 1.0F;
            }

            flashAmount *= 0.8f;
            double r = original.x * (1.0F - flashAmount) + 1.0F * flashAmount;
            double g = original.y * (1.0F - flashAmount) + 0.8F * flashAmount;
            double b = original.z * (1.0F - flashAmount) + 0.3F * flashAmount;
            return new Vec3(r, g, b);
        }
        return original;
    }

    @Inject(method = "animateTick", at = @At("RETURN"))
    private void operation_starcleave$firmamentParticles(int centerX, int centerY, int centerZ, CallbackInfo ci) {
        ClientLevel clientWorld = (ClientLevel)(Object)this;
        StarbleachParticles.spawnParticles(clientWorld, centerX, centerY, centerZ);
    }
}

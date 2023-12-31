package phanastrae.operation_starcleave.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;
import phanastrae.operation_starcleave.world.firmament.FirmamentHolder;
import phanastrae.operation_starcleave.world.OperationStarcleaveWorld;
import phanastrae.operation_starcleave.world.firmament.ClientFirmamentRegionManager;
import phanastrae.operation_starcleave.world.firmament.Firmament;
import phanastrae.operation_starcleave.world.starbleach.StarbleachParticles;

import java.util.function.Supplier;

@Mixin(ClientWorld.class)
public class ClientWorldMixin implements FirmamentHolder {

    @Final
    @Shadow
    private MinecraftClient client;

    private Firmament operation_starcleave$firmament;


    @Inject(method = "<init>", at = @At("RETURN"))
    private void operation_starcleave$onInit(ClientPlayNetworkHandler networkHandler, ClientWorld.Properties properties, RegistryKey registryRef, RegistryEntry dimensionTypeEntry, int loadDistance, int simulationDistance, Supplier profiler, WorldRenderer worldRenderer, boolean debugWorld, long seed, CallbackInfo ci) {
        this.operation_starcleave$firmament = new Firmament((World)(Object)this, new ClientFirmamentRegionManager((ClientWorld)(Object)this));
    }

    @Override
    public Firmament operation_starcleave$getFirmament() {
        return this.operation_starcleave$firmament;
    }

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

    @Inject(method = "doRandomBlockDisplayTicks", at = @At("RETURN"))
    private void operation_starcleave$firmamentParticles(int centerX, int centerY, int centerZ, CallbackInfo ci) {
        ClientWorld clientWorld = (ClientWorld)(Object)this;
        StarbleachParticles.spawnParticles(clientWorld, centerX, centerY, centerZ);
    }
}

package phanastrae.operation_starcleave.mixin;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.world.OperationStarcleaveWorld;
import phanastrae.operation_starcleave.world.firmament.Firmament;

import java.util.function.Supplier;

@Mixin(World.class)
public class WorldMixin implements OperationStarcleaveWorld {

    private Firmament firmament;
    private int cleavingFlashTicksLeft;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void operation_starcleave$onInit(MutableWorldProperties properties, RegistryKey registryRef, DynamicRegistryManager registryManager, RegistryEntry dimensionEntry, Supplier profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates, CallbackInfo ci) {
        this.firmament = new Firmament((World)(Object)this);
        this.cleavingFlashTicksLeft = 0;
    }

    @Override
    public Firmament operation_starcleave$getFirmament() {
        return this.firmament;
    }

    @Override
    public void operation_starcleave$setCleavingFlashTicksLeft(int ticks) {
        this.cleavingFlashTicksLeft = ticks;
    }

    @Override
    public int operation_starcleave$getCleavingFlashTicksLeft() {
        return this.cleavingFlashTicksLeft;
    }
}

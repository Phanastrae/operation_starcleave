package phanastrae.operation_starcleave.mixin;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.RandomSequencesState;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.world.firmament.FirmamentHolder;
import phanastrae.operation_starcleave.world.firmament.Firmament;
import phanastrae.operation_starcleave.world.firmament.ServerFirmamentRegionManager;
import phanastrae.operation_starcleave.world.starbleach.Starbleach;

import java.util.List;
import java.util.concurrent.Executor;

@Mixin(ServerWorld.class)
public class ServerWorldMixin implements FirmamentHolder {

    private Firmament operation_starcleave$firmament;


    @Inject(method = "<init>", at = @At("RETURN"))
    private void operation_starcleave$onInit(MinecraftServer server, Executor workerExecutor, LevelStorage.Session session, ServerWorldProperties properties, RegistryKey worldKey, DimensionOptions dimensionOptions, WorldGenerationProgressListener worldGenerationProgressListener, boolean debugWorld, long seed, List spawners, boolean shouldTickTime, RandomSequencesState randomSequencesState, CallbackInfo ci) {
        this.operation_starcleave$firmament = new Firmament((World)(Object)this, new ServerFirmamentRegionManager((ServerWorld)(Object)this));
    }

    @Override
    public Firmament operation_starcleave$getFirmament() {
        return this.operation_starcleave$firmament;
    }

    @Inject(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;pop()V", shift = At.Shift.BEFORE))
    private void operation_starcleave$starbleachChunk(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci) {
        Starbleach.starbleachChunk((ServerWorld)(Object)this, chunk, randomTickSpeed);
    }
}

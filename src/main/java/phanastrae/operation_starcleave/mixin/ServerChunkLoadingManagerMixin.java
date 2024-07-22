package phanastrae.operation_starcleave.mixin;

import com.mojang.datafixers.DataFixer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkLoadingManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.thread.ThreadExecutor;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkStatusChangeListener;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.world.firmament.*;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Mixin(ServerChunkLoadingManager.class)
public class ServerChunkLoadingManagerMixin implements FirmamentStorageHolder {

    private FirmamentStorage operation_starcleave$firmament_storage;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void operation_starcleave$onInit(ServerWorld world, LevelStorage.Session session, DataFixer dataFixer, StructureTemplateManager structureTemplateManager, Executor executor, ThreadExecutor mainThreadExecutor, ChunkProvider chunkProvider, ChunkGenerator chunkGenerator, WorldGenerationProgressListener worldGenerationProgressListener, ChunkStatusChangeListener chunkStatusChangeListener, Supplier persistentStateManagerFactory, int viewDistance, boolean dsync, CallbackInfo ci) {
        this.operation_starcleave$firmament_storage = new FirmamentStorage(world, session, dsync);
    }

    @Inject(method = "close", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/poi/PointOfInterestStorage;close()V", shift = At.Shift.AFTER))
    private void operation_starcleave$close(CallbackInfo ci) throws IOException {
        this.operation_starcleave$firmament_storage.close();
    }

    @Inject(method = "updatePosition", at = @At(value = "HEAD"))
    private void operation_starcleave$updatePosition(ServerPlayerEntity player, CallbackInfo ci) {
        ChunkSectionPos chunkSectionPos1 = player.getWatchedSection();
        RegionPos regionPos1 = RegionPos.fromWorldCoords(chunkSectionPos1.getMinX(), chunkSectionPos1.getMinZ());
        ChunkSectionPos chunkSectionPos2 = ChunkSectionPos.from(player);
        RegionPos regionPos2 = RegionPos.fromWorldCoords(chunkSectionPos2.getMinX(), chunkSectionPos2.getMinZ());

        FirmamentRegionsWatched firmamentRegionsWatched = ((FirmamentWatcher)player).operation_starcleave$getWatchedRegions();
        firmamentRegionsWatched.onPositionChanged(regionPos1, regionPos2);
    }

    @Override
    public FirmamentStorage operation_starcleave$getFirmamentStorage() {
        return operation_starcleave$firmament_storage;
    }
}

package phanastrae.operation_starcleave.mixin.common.firmament;

import com.mojang.datafixers.DataFixer;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.entity.ChunkStatusUpdateListener;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.duck.FirmamentStorageHolder;
import phanastrae.operation_starcleave.duck.FirmamentWatcher;
import phanastrae.operation_starcleave.world.firmament.FirmamentRegionsWatched;
import phanastrae.operation_starcleave.world.firmament.storage.FirmamentStorage;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Mixin(ChunkMap.class)
public class ChunkMapMixin implements FirmamentStorageHolder {

    @Unique
    private FirmamentStorage operation_starcleave$firmament_storage;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void operation_starcleave$onInit(ServerLevel world, LevelStorageSource.LevelStorageAccess session, DataFixer dataFixer, StructureTemplateManager structureTemplateManager, Executor executor, BlockableEventLoop mainThreadExecutor, LightChunkGetter chunkProvider, ChunkGenerator chunkGenerator, ChunkProgressListener worldGenerationProgressListener, ChunkStatusUpdateListener chunkStatusChangeListener, Supplier persistentStateManagerFactory, int viewDistance, boolean dsync, CallbackInfo ci) {
        this.operation_starcleave$firmament_storage = new FirmamentStorage(world, session, dsync);
    }

    @Inject(method = "close", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/village/poi/PoiManager;close()V", shift = At.Shift.AFTER))
    private void operation_starcleave$close(CallbackInfo ci) throws IOException {
        this.operation_starcleave$firmament_storage.close();
    }

    @Inject(method = "updatePlayerStatus", at = @At("HEAD"))
    private void operation_starcleave$onUpdateStatus(ServerPlayer player, boolean track, CallbackInfo ci) {
        FirmamentRegionsWatched watched = ((FirmamentWatcher) player).operation_starcleave$getWatchedRegions();
        watched.unWatchAll();

        if (track) {
            watched.onPositionChanged(player);
        }
    }

    @Inject(method = "move", at = @At(value = "HEAD"))
    private void operation_starcleave$updatePosition(ServerPlayer player, CallbackInfo ci) {
        FirmamentRegionsWatched watched = ((FirmamentWatcher) player).operation_starcleave$getWatchedRegions();

        watched.onPositionChanged(player);
    }

    @Override
    public FirmamentStorage operation_starcleave$getFirmamentStorage() {
        return operation_starcleave$firmament_storage;
    }
}

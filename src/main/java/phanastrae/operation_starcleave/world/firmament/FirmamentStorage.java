package phanastrae.operation_starcleave.world.firmament;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.storage.IOWorker;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import net.minecraft.world.level.storage.LevelStorageSource;

public class FirmamentStorage {

    private final IOWorker worker;

    public FirmamentStorage(Level world, LevelStorageSource.LevelStorageAccess session, boolean dsync) {
        this.worker = new FirmamentStorageIoWorker(
                new RegionStorageInfo(session.getLevelId(), world.dimension(), "chunk"),
                session.getDimensionPath(world.dimension()).resolve("operation_starcleave"),
                dsync);
    }

    public CompletableFuture<Optional<CompoundTag>> getNbt(ChunkPos chunkPos) {
        return this.worker.loadAsync(chunkPos);
    }

    public void setNbt(ChunkPos chunkPos, CompoundTag nbt) {
        this.worker.store(chunkPos, nbt);
    }

    public void close() throws IOException {
        this.worker.close();
    }

    public static FirmamentStorage getFrom(ChunkMap threadedAnvilChunkStorage) {
        return ((FirmamentStorageHolder)threadedAnvilChunkStorage).operation_starcleave$getFirmamentStorage();
    }

    public static FirmamentStorage getFrom(ServerLevel serverWorld) {
         return FirmamentStorage.getFrom(serverWorld.getChunkSource().chunkMap);
    }
}

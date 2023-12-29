package phanastrae.operation_starcleave.world.firmament;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.thread.ThreadExecutor;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.OperationStarcleave;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ServerFirmamentRegionManager extends FirmamentRegionManager {

    private long MAX_TIME_SINCE_LAST_ACCESS_BEFORE_UNLOAD = 100; // unload inactive regions after 5 seconds
    private Long2ObjectLinkedOpenHashMap<FirmamentRegionHolder> firmamentRegionHolders = new Long2ObjectLinkedOpenHashMap<>();
    private final ServerWorld serverWorld;
    private final ThreadExecutor<Runnable> mainThreadExectutor;
    final Thread serverThread;

    public ServerFirmamentRegionManager(ServerWorld serverWorld) {
        this.serverWorld = serverWorld;
        this.serverThread = Thread.currentThread();
        this.mainThreadExectutor = new MainThreadExecutor(serverWorld);
    }

    @Override
    public void forEachRegion(Consumer<FirmamentRegion> method) {
        this.firmamentRegionHolders.forEach((id, firmamentRegionHolder) -> {
            FirmamentRegion firmamentRegion = firmamentRegionHolder.getFirmamentRegion();
            if(firmamentRegion != null) {
                method.accept(firmamentRegion);
            }
        });
    }

    @Nullable
    @Override
    public FirmamentRegion getFirmamentRegion(long id) {
        if(this.firmamentRegionHolders.containsKey(id)) {
            return this.firmamentRegionHolders.get(id).getFirmamentRegion();
        } else {
            return null;
        }
    }

    @Override
    public void tick() {
        // load all regions in a 3x3 radius of all players
        for(ServerPlayerEntity serverPlayerEntity : this.serverWorld.getPlayers()) {
            ((FirmamentWatcher)serverPlayerEntity).operation_starcleave$getWatchedRegions().watchedRegions.forEach(id -> {
                if(this.firmamentRegionHolders.containsKey(id)) {
                    this.firmamentRegionHolders.get(id).recordAccess();
                } else {
                    loadRegion(id);
                }
            });
        }
        // keep active regions loaded
        this.firmamentRegionHolders.forEach((id, firmamentRegionHolder) -> {
            FirmamentRegion firmamentRegion = firmamentRegionHolder.getFirmamentRegion();
            if(firmamentRegion != null && firmamentRegion.shouldUpdate) {
                firmamentRegionHolder.recordAccess();
            }
        });
        // unload inactive regions
        List<Long> idsToUnload = new ArrayList<>();
        long currentTime = this.serverWorld.getTime();
        this.firmamentRegionHolders.forEach((id, firmamentRegionHolder) -> {
            if(firmamentRegionHolder.getTimeSinceLastAccess(currentTime) > MAX_TIME_SINCE_LAST_ACCESS_BEFORE_UNLOAD) {
                idsToUnload.add(id);
            }
        });
        for(long id : idsToUnload) {
            unloadRegion(id);
        }
        // start all ready regions
        this.firmamentRegionHolders.forEach((id, firmamentRegionHolders) -> {
            if(firmamentRegionHolders.getState() == FirmamentRegionHolder.FirmamentRegionState.READY_TO_START) {
                firmamentRegionHolders.setState(FirmamentRegionHolder.FirmamentRegionState.STARTED);
            }
        });
    }

    public void loadRegion(long id) {
        if(this.firmamentRegionHolders.containsKey(id)) {
            return;
        }

        RegionPos regionPos = new RegionPos(id);

        FirmamentRegion firmamentRegion = new FirmamentRegion(Firmament.fromWorld(this.serverWorld), regionPos);
        FirmamentRegionHolder firmamentRegionHolder = new FirmamentRegionHolder(firmamentRegion);
        firmamentRegionHolder.recordAccess();

        firmamentRegionHolder.setState(FirmamentRegionHolder.FirmamentRegionState.LOADING);
        CompletableFuture<Boolean> completableFuture = load(id).thenApply(onbt -> {
            if(onbt.isPresent()) {
                NbtCompound nbt = onbt.get();
                firmamentRegion.read(nbt);
                firmamentRegionHolder.setState(FirmamentRegionHolder.FirmamentRegionState.READY_TO_START);
            } else {
                firmamentRegionHolder.setState(FirmamentRegionHolder.FirmamentRegionState.READY_TO_START);
            }
            return true;
        });
        this.mainThreadExectutor.runTasks(completableFuture::isDone);


        this.firmamentRegionHolders.put(id, firmamentRegionHolder);
    }

    public void unloadRegion(long id) {
        if(!this.firmamentRegionHolders.containsKey(id)) {
            return;
        }

        save(id);
        this.firmamentRegionHolders.remove(id);
    }

    public void saveAll() {
        this.firmamentRegionHolders.forEach((id, firmamentRegionHolder) -> save(id));
    }

    private void save(long id) {
        RegionPos regionPos = new RegionPos(id);
        ChunkPos chunkPos = ChunkPos.fromRegion(regionPos.rx, regionPos.rz);

        FirmamentStorage firmamentStorage = FirmamentStorage.getFrom(serverWorld);

        NbtCompound nbt = new NbtCompound();
        FirmamentRegion firmamentRegion = this.firmamentRegionHolders.get(id).getFirmamentRegion();
        if(firmamentRegion != null) {
            firmamentRegion.write(nbt);
        }

        firmamentStorage.setNbt(chunkPos, nbt);
    }

    private CompletableFuture<Optional<NbtCompound>> load(long id) {
        RegionPos regionPos = new RegionPos(id);
        ChunkPos chunkPos = ChunkPos.fromRegion(regionPos.rx, regionPos.rz);

        FirmamentStorage firmamentStorage = FirmamentStorage.getFrom(serverWorld);
        return firmamentStorage.getNbt(chunkPos);
    }

    final class MainThreadExecutor extends ThreadExecutor<Runnable> {
        MainThreadExecutor(World world) {
            super("Firmament Region main thread executor for " + world.getRegistryKey().getValue());
        }

        @Override
        protected Runnable createTask(Runnable runnable) {
            return runnable;
        }

        @Override
        protected boolean canExecute(Runnable task) {
            return true;
        }

        @Override
        protected boolean shouldExecuteAsync() {
            return true;
        }

        @Override
        protected Thread getThread() {
            return ServerFirmamentRegionManager.this.serverThread;
        }

        @Override
        protected void executeTask(Runnable task) {
            ServerFirmamentRegionManager.this.serverWorld.getProfiler().visit("runTask");
            super.executeTask(task);
        }

        @Override
        public boolean runTask() {
            /*
            if (ServerFirmamentRegionManager.this.updateChunks()) {
                return true;
            } else {
                ServerFirmamentRegionManager.this.lightingProvider.tick();
                return super.runTask();
            }
            */
            return true;
        }
    }
}

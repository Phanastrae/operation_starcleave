package phanastrae.operation_starcleave.world.firmament;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

public class ServerFirmamentRegionManager extends FirmamentRegionManager {

    private long MAX_TIME_SINCE_LAST_ACCESS_BEFORE_UNLOAD = 100; // unload inactive regions after 5 seconds
    private Long2ObjectLinkedOpenHashMap<FirmamentRegionHolder> firmamentRegionHolders = new Long2ObjectLinkedOpenHashMap<>();
    private final ServerLevel serverWorld;
    private final BlockableEventLoop<Runnable> mainThreadExectutor;
    final Thread serverThread;

    public ServerFirmamentRegionManager(ServerLevel serverWorld) {
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
        for(ServerPlayer serverPlayerEntity : this.serverWorld.players()) {
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
        long currentTime = this.serverWorld.getGameTime();
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

        FirmamentRegion firmamentRegion = new FirmamentRegion(Firmament.fromLevel(this.serverWorld), regionPos);
        FirmamentRegionHolder firmamentRegionHolder = new FirmamentRegionHolder(firmamentRegion);
        firmamentRegionHolder.recordAccess();

        firmamentRegionHolder.setState(FirmamentRegionHolder.FirmamentRegionState.LOADING);
        CompletableFuture<Boolean> completableFuture = load(id).thenApply(onbt -> {
            if(onbt.isPresent()) {
                CompoundTag nbt = onbt.get();
                firmamentRegion.read(nbt);
                firmamentRegionHolder.setState(FirmamentRegionHolder.FirmamentRegionState.READY_TO_START);
            } else {
                firmamentRegionHolder.setState(FirmamentRegionHolder.FirmamentRegionState.READY_TO_START);
            }
            return true;
        });
        this.mainThreadExectutor.managedBlock(completableFuture::isDone);


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
        ChunkPos chunkPos = ChunkPos.minFromRegion(regionPos.rx, regionPos.rz);

        FirmamentStorage firmamentStorage = FirmamentStorage.getFrom(serverWorld);

        CompoundTag nbt = new CompoundTag();
        FirmamentRegion firmamentRegion = this.firmamentRegionHolders.get(id).getFirmamentRegion();
        if(firmamentRegion != null) {
            firmamentRegion.write(nbt);
        }

        firmamentStorage.setNbt(chunkPos, nbt);
    }

    private CompletableFuture<Optional<CompoundTag>> load(long id) {
        RegionPos regionPos = new RegionPos(id);
        ChunkPos chunkPos = ChunkPos.minFromRegion(regionPos.rx, regionPos.rz);

        FirmamentStorage firmamentStorage = FirmamentStorage.getFrom(serverWorld);
        return firmamentStorage.getNbt(chunkPos);
    }

    final class MainThreadExecutor extends BlockableEventLoop<Runnable> {
        MainThreadExecutor(Level world) {
            super("Firmament Region main thread executor for " + world.dimension().location());
        }

        @Override
        protected Runnable wrapRunnable(Runnable runnable) {
            return runnable;
        }

        @Override
        protected boolean shouldRun(Runnable task) {
            return true;
        }

        @Override
        protected boolean scheduleExecutables() {
            return true;
        }

        @Override
        protected Thread getRunningThread() {
            return ServerFirmamentRegionManager.this.serverThread;
        }

        @Override
        protected void doRunTask(Runnable task) {
            ServerFirmamentRegionManager.this.serverWorld.getProfiler().incrementCounter("runTask");
            super.doRunTask(task);
        }

        @Override
        public boolean pollTask() {
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

package phanastrae.operation_starcleave.client.render.extras_baking;

import com.mojang.blaze3d.vertex.VertexBuffer;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.renderer.chunk.RenderRegionCache;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.client.render.OperationStarcleaveRenderTypes;
import phanastrae.operation_starcleave.mixin.client.accessor.SectionRenderDispatcherAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class ExtrasSection {

    public final int index;
    private final SectionExtrasRebuildQueue queue;
    private final BlockPos.MutableBlockPos origin;
    private final BlockPos.MutableBlockPos[] relativeOrigins = Util.make(new BlockPos.MutableBlockPos[6], array -> {
        for (int i = 0; i < array.length; i++) {
            array[i] = new BlockPos.MutableBlockPos();
        }
    });
    private SectionPos sectionPos;
    private AABB bb;
    private boolean dirty = true;
    private boolean isDirtyFromPlayer = false;
    private boolean dirtyMaybeAddsSpecialBlocks = false;
    private boolean inQueue = false;
    private boolean maybeHadSpecialBlocks = false;
    private SectionRenderDispatcher.RenderSection.CompileTask lastRebuildTask;
    public final AtomicReference<CompiledExtrasSection> compiled = new AtomicReference<>(
            CompiledExtrasSection.UNCOMPILED
    );
    private final AtomicInteger initialCompilationCancelCount = new AtomicInteger(0);

    @Nullable
    private Map<RenderType, VertexBuffer> buffers;

    public static List<RenderType> getLayers() {
        ArrayList<RenderType> list = new ArrayList<>(RenderType.chunkBufferLayers());
        list.add(OperationStarcleaveRenderTypes.getIridescence());
        return list;
    }

    public ExtrasSection(SectionExtrasRebuildQueue queue, int index, int x, int y, int z) {
        this.index = index;
        this.queue = queue;
        this.origin = new BlockPos.MutableBlockPos(x, y, z);
        this.sectionPos = SectionPos.of(this.origin);
    }

    public void setOrigin(int x, int y, int z) {
        this.reset();
        this.origin.set(x, y, z);
        this.sectionPos = SectionPos.of(this.origin);
        this.bb = new AABB(
                x, y, z, x + 16, y + 16, z + 16
        );

        for (Direction direction : Direction.values()) {
            this.relativeOrigins[direction.ordinal()].set(this.origin).move(direction, 16);
        }
    }

    public BlockPos getOrigin() {
        return origin;
    }

    public SectionPos getSectionPos() {
        return sectionPos;
    }

    public AABB getBoundingBox() {
        return this.bb;
    }

    public void setDirty(boolean reRenderOnMainThread, boolean maybeAddsSpecialBlocks) {
        this.dirty = true;
        this.isDirtyFromPlayer |= reRenderOnMainThread;
        this.dirtyMaybeAddsSpecialBlocks |= maybeAddsSpecialBlocks;
        if (!this.inQueue) {
            this.inQueue = true;
            this.queue.addToQueue(this);
        }
    }

    public void setNotDirty() {
        this.dirty = false;
        this.isDirtyFromPlayer = false;
        this.dirtyMaybeAddsSpecialBlocks = false;
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public boolean isDirtyFromPlayer() {
        return this.isDirtyFromPlayer;
    }

    public boolean dirtyMaybeAddsSpecialBlocks() {
        return this.dirtyMaybeAddsSpecialBlocks;
    }

    public boolean maybeHadSpecialBlocks() {
        return this.maybeHadSpecialBlocks;
    }

    public void setMaybeHadSpecialBlocks(boolean maybeHadSpecialBlocks) {
        this.maybeHadSpecialBlocks = maybeHadSpecialBlocks;
    }

    public void setInQueue() {
        this.inQueue = true;
    }

    public void setNotInQueue() {
        this.inQueue = false;
    }

    public boolean isInQueue() {
        return inQueue;
    }

    public void initBuffersIfNeeded() {
        if (this.buffers == null) {
            this.buffers = getLayers()
                    .stream()
                    .collect(Collectors.toMap(type -> type, type -> new VertexBuffer(VertexBuffer.Usage.STATIC)));
        }
    }

    public void buildSync(RenderRegionCache renderRegionCache, SectionRenderDispatcher sectionRenderDispatcher) {
        this.initBuffersIfNeeded();
        SectionRenderDispatcher.RenderSection.CompileTask task = createBuildTask(renderRegionCache, sectionRenderDispatcher);
        task.doTask(((SectionRenderDispatcherAccessor) sectionRenderDispatcher).getFixedBuffers());
    }

    public void buildAsync(RenderRegionCache renderRegionCache, SectionRenderDispatcher sectionRenderDispatcher) {
        this.initBuffersIfNeeded();
        SectionRenderDispatcher.RenderSection.CompileTask task = createBuildTask(renderRegionCache, sectionRenderDispatcher);
        sectionRenderDispatcher.schedule(task);
    }

    public SectionRenderDispatcher.RenderSection.CompileTask createBuildTask(RenderRegionCache renderRegionCache, SectionRenderDispatcher renderDispatcher) {
        boolean canceledTasks = this.cancelTasks();
        RenderChunkRegion renderChunkRegion = renderRegionCache.createRegion(this.queue.getLevel(), SectionPos.of(this.origin));
        boolean isUncompiled = this.compiled.get() == CompiledExtrasSection.UNCOMPILED;
        if (isUncompiled && canceledTasks) {
            this.initialCompilationCancelCount.incrementAndGet();
        }

        double distAtCreation = this.getDistToPlayerSqr();
        boolean isHighPriority = !isUncompiled || this.initialCompilationCancelCount.get() > 2;

        this.lastRebuildTask = this.queue.createCompileTask(distAtCreation, isHighPriority, this, renderDispatcher, renderChunkRegion);
        return this.lastRebuildTask;
    }

    void setCompiled(CompiledExtrasSection compiled) {
        this.compiled.set(compiled);
        this.initialCompilationCancelCount.set(0);
        this.queue.addRecentlyCompiledSection(this);
    }

    protected boolean cancelTasks() {
        boolean cancelled = false;
        if (this.lastRebuildTask != null) {
            this.lastRebuildTask.cancel();
            this.lastRebuildTask = null;
            cancelled = true;
        }

        return cancelled;
    }

    private void reset() {
        this.cancelTasks();
        this.compiled.set(CompiledExtrasSection.UNCOMPILED);
        this.dirty = true;
    }

    public void releaseBuffers() {
        this.reset();
        if (this.buffers != null) {
            this.buffers.values().forEach(VertexBuffer::close);
            this.buffers = null;
        }
    }

    @Nullable
    public VertexBuffer getBuffer(RenderType renderType) {
        if (this.buffers != null) {
            return this.buffers.get(renderType);
        } else {
            return null;
        }
    }

    protected double getDistToPlayerSqr() {
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        double dx = this.origin.getX() + 8.0 - camera.getPosition().x;
        double dy = this.origin.getX() + 8.0 - camera.getPosition().y;
        double dz = this.origin.getX() + 8.0 - camera.getPosition().z;
        return dx * dx + dy * dy + dz * dz;
    }

    public CompiledExtrasSection getCompiled() {
        return this.compiled.get();
    }

    public boolean hasAllNeighbors() {
        return !(this.getDistToPlayerSqr() > 576.0) || this.doesChunkExistAt(this.relativeOrigins[Direction.WEST.ordinal()])
                && this.doesChunkExistAt(this.relativeOrigins[Direction.NORTH.ordinal()])
                && this.doesChunkExistAt(this.relativeOrigins[Direction.EAST.ordinal()])
                && this.doesChunkExistAt(this.relativeOrigins[Direction.SOUTH.ordinal()]);
    }

    private boolean doesChunkExistAt(BlockPos pos) {
        return this.queue.getLevel()
                .getChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()), ChunkStatus.FULL, false)
                != null;
    }

    public static class CompiledExtrasSection {
        public static final CompiledExtrasSection UNCOMPILED = new CompiledExtrasSection();
        public static final CompiledExtrasSection EMPTY = new CompiledExtrasSection();
        final Set<RenderType> hasBlocks = new ObjectArraySet<>(ExtrasSection.getLayers().size());

        public boolean hasNoRenderableLayers() {
            return this.hasBlocks.isEmpty();
        }

        public boolean isEmpty(RenderType renderType) {
            return !this.hasBlocks.contains(renderType);
        }
    }
}

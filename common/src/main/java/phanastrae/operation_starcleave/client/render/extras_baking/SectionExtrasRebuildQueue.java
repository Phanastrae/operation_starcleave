package phanastrae.operation_starcleave.client.render.extras_baking;

import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.CrashReport;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import phanastrae.operation_starcleave.OperationStarcleave;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;

public class SectionExtrasRebuildQueue {
    // this logic is mostly the same as vanilla's, but we can't just directly use vanilla's because sodium might override it all

    private final Level level;
    private int sectionGridSizeXZ;
    private int sectionGridSizeY;
    private int viewDistance;
    private ExtrasSection[] sections;
    private final BlockingQueue<ExtrasSection> rebuildQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<ExtrasSection> recentlyCompiledQueue = new LinkedBlockingQueue<>();
    private final HashSet<ExtrasSection> nonEmptySections = new HashSet<>();
    private final ExtrasSectionCompiler extrasSectionCompiler;
    private final SectionRenderDispatcher.RenderSection dummyRenderSection;

    // logic required because sodium overwrites setupRender()
    private boolean hasLastCamPos = false;
    private int lastCamX;
    private int lastCamZ;

    public SectionExtrasRebuildQueue(Level level, int viewDistance, SectionRenderDispatcher sectionRenderDispatcher, BlockRenderDispatcher blockRenderer, BlockEntityRenderDispatcher blockEntityRenderer) {
        this.level = level;
        this.extrasSectionCompiler = new ExtrasSectionCompiler(blockRenderer, blockEntityRenderer);
        this.setViewDistance(viewDistance);
        this.createSections();

        // dummy RenderSection, only used to create custom CompileTasks (for which the RenderSection's properties are irrelevant)
        this.dummyRenderSection = sectionRenderDispatcher.new RenderSection(0, 0, 0, 0);
        this.dummyRenderSection.releaseBuffers(); // immediately release, as we don't use these buffers
    }

    public void setViewDistance(int viewDistance) {
        this.viewDistance = viewDistance;
        this.sectionGridSizeXZ = viewDistance * 2 + 1;
        this.sectionGridSizeY = this.level.getSectionsCount();
    }

    protected void createSections() {
        int length = this.sectionGridSizeXZ * this.sectionGridSizeY * this.sectionGridSizeXZ;

        this.sections = new ExtrasSection[length];

        for (int sx = 0; sx < this.sectionGridSizeXZ; sx++) {
            for (int sy = 0; sy < this.sectionGridSizeY; sy++) {
                for (int sz = 0; sz < this.sectionGridSizeXZ; sz++) {
                    int index = getSectionIndex(sx, sy, sz);
                    ExtrasSection newSection = new ExtrasSection(this, sx * 16, this.level.getMinBuildHeight() + sy * 16, sz * 16);
                    this.sections[index] = newSection;
                    newSection.setDirty(false, true);
                }
            }
        }
    }

    private int getSectionIndex(int x, int y, int z) {
        return (z * this.sectionGridSizeY + y) * this.sectionGridSizeXZ + x;
    }

    public void addToQueue(ExtrasSection section) {
        try {
            this.rebuildQueue.put(section);
        } catch (InterruptedException exception) {
            OperationStarcleave.LOGGER.warn("Thread was interrupted whilst trying to add section to rebuild queue");
        }
    }

    public void addRecentlyCompiledSection(ExtrasSection section) {
        try {
            this.recentlyCompiledQueue.put(section);
        } catch (InterruptedException exception) {
            OperationStarcleave.LOGGER.warn("Thread was interrupted whilst trying to add recently compiled section to queue");
        }
    }

    public void updateNonEmptySections() {
        if (!this.recentlyCompiledQueue.isEmpty()) {
            List<ExtrasSection> list = new ArrayList<>();
            this.recentlyCompiledQueue.drainTo(list);

            for (ExtrasSection section : list) {
                ExtrasSection.CompiledExtrasSection compiled = section.compiled.get();
                if (compiled.hasNoRenderableLayers()) {
                    this.nonEmptySections.remove(section);
                } else {
                    this.nonEmptySections.add(section);
                }
            }
        }
    }

    public HashSet<ExtrasSection> getNonEmptySections() {
        return this.nonEmptySections;
    }

    public BlockingQueue<ExtrasSection> getRebuildQueue() {
        return this.rebuildQueue;
    }

    public Level getLevel() {
        return this.level;
    }

    public int getViewDistance() {
        return this.viewDistance;
    }

    public void setBlockDirty(BlockPos pos, boolean reRenderOnMainThread) {
        setBlockDirty(pos.getX(), pos.getY(), pos.getZ(), reRenderOnMainThread);
    }

    public void setBlockDirty(int x, int y, int z, boolean reRenderOnMainThread) {
        for (int i = (x - 1) >> 4; i <= (x + 1) >> 4; i++) {
            for (int j = (z - 1) >> 4; j <= (z + 1) >> 4; j++) {
                for (int k = (y - 1) >> 4; k <= (y + 1) >> 4; k++) {
                    this.setSectionDirty(i, k, j, reRenderOnMainThread, false); // in vanilla this is private and only called by blockChanged(level;pos;state;state;I)V, so we check there for special block addition
                }
            }
        }
    }

    public void setBlocksDirty(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        for (int i = (minX - 1) >> 4; i <= (maxX + 1) >> 4; i++) {
            for (int j = (minZ - 1) >> 4; j <= (maxZ + 1) >> 4; j++) {
                for (int k = (minY - 1) >> 4; k <= (maxY + 1) >> 4; k++) {
                    this.setSectionDirty(i, k, j, false, false); // in vanilla this is only called by setBlockDirty(pos;state;state)V, so we check there for special block addition
                }
            }
        }
    }

    public void setSectionDirtyWithNeighbours(int sectionX, int sectionY, int sectionZ) {
        for (int i = sectionX - 1; i <= sectionX + 1; i++) {
            for (int j = sectionZ - 1; j <= sectionZ + 1; j++) {
                for (int k = sectionY - 1; k <= sectionY + 1; k++) {
                    this.setSectionDirty(i, k, j, false, true); // need to always set maybeAddsSpecialBlocks as true to avoid issues
                }
            }
        }
    }

    public void setSectionDirty(int sectionX, int sectionY, int sectionZ) {
        this.setSectionDirty(sectionX, sectionY, sectionZ, false, true); // full section rebuild might add special blocks
    }

    public void setSectionDirty(int sectionX, int sectionY, int sectionZ, boolean reRenderOnMainThread, boolean maybeAddsSpecialBlocks) {
        int sx = Math.floorMod(sectionX, this.sectionGridSizeXZ);
        int sy = Math.floorMod(sectionY - this.level.getMinSection(), this.sectionGridSizeY);
        int sz = Math.floorMod(sectionZ, this.sectionGridSizeXZ);

        int index = this.getSectionIndex(sx, sy, sz);
        ExtrasSection section = this.sections[index];
        SectionPos sectionPos = section.getSectionPos();
        if (sectionPos.x() == sectionX && sectionPos.y() == sectionY && sectionPos.z() == sectionZ) {
            section.setDirty(reRenderOnMainThread, maybeAddsSpecialBlocks);
        }
    }

    public void repositionCamera(double viewEntityX, double viewEntityZ) {
        int x = Mth.ceil(viewEntityX);
        int z = Mth.ceil(viewEntityZ);

        if (this.hasLastCamPos && this.lastCamX == x && this.lastCamZ == z) {
            return;
        } else {
            this.hasLastCamPos = true;
            this.lastCamX = x;
            this.lastCamZ = z;
        }

        int widthBlocks = this.sectionGridSizeXZ * 16;
        int offset = -8 - widthBlocks / 2;
        int minCornerX = x + offset;
        int minCornerZ = z + offset;
        int minHeight = this.level.getMinBuildHeight();
        for (int sx = 0; sx < this.sectionGridSizeXZ; sx++) {
            int localX = sx * 16 - minCornerX;
            int chunkOriginX = minCornerX + Math.floorMod(localX, widthBlocks);

            for (int sz = 0; sz < this.sectionGridSizeXZ; sz++) {
                int localZ = sz * 16 - minCornerZ;
                int chunkOriginZ = minCornerZ + Math.floorMod(localZ, widthBlocks);

                BlockPos baseOriginPos = this.sections[this.getSectionIndex(sx, 0, sz)].getOrigin();
                if (chunkOriginX != baseOriginPos.getX() || chunkOriginZ != baseOriginPos.getZ()) {
                    for (int sy = 0; sy < this.sectionGridSizeY; sy++) {
                        int chunkOriginY = minHeight + sy * 16;
                        this.sections[this.getSectionIndex(sx, sy, sz)].setOrigin(chunkOriginX, chunkOriginY, chunkOriginZ);
                    }
                }
            }
        }
    }

    public void releaseAllBuffers() {
        for (ExtrasSection extrasSection : this.sections) {
            extrasSection.releaseBuffers();
        }
    }

    public SectionRenderDispatcher.RenderSection.CompileTask createCompileTask(double distAtCreation, boolean isHighPriority, ExtrasSection eSection, SectionRenderDispatcher renderDispatcher, RenderChunkRegion renderChunkRegion) {
        ExtrasSectionCompiler sectionCompiler = this.extrasSectionCompiler;

        // need to construct from dummy render section as this is a (double) inner class
        return this.dummyRenderSection.new CompileTask(distAtCreation, isHighPriority) {
            private final ExtrasSection extrasSection = eSection;
            private RenderChunkRegion region = renderChunkRegion;
            private final SectionRenderDispatcher sectionRenderDispatcher = renderDispatcher;
            private final ExtrasSectionCompiler extrasSectionCompiler = sectionCompiler;

            @Override
            public CompletableFuture<SectionRenderDispatcher.SectionTaskResult> doTask(SectionBufferBuilderPack sectionBufferBuilderPack) {
                // cancel if cancelled
                if (this.isCancelled.get()) {
                    return CompletableFuture.completedFuture(SectionRenderDispatcher.SectionTaskResult.CANCELLED);
                }

                // cancel if neighbours missing
                if (!this.extrasSection.hasAllNeighbors()) {
                    this.cancel();
                    return CompletableFuture.completedFuture(SectionRenderDispatcher.SectionTaskResult.CANCELLED);
                }

                // cancel if cancelled (again, apparently)
                if (this.isCancelled.get()) {
                    return CompletableFuture.completedFuture(SectionRenderDispatcher.SectionTaskResult.CANCELLED);
                }

                // cancel if region missing
                RenderChunkRegion regionToRender = this.region;
                this.region = null;
                if (regionToRender == null) {
                    this.extrasSection.setCompiled(ExtrasSection.CompiledExtrasSection.EMPTY);
                    return CompletableFuture.completedFuture(SectionRenderDispatcher.SectionTaskResult.SUCCESSFUL);
                }

                // create results
                SectionPos originPos = SectionPos.of(this.extrasSection.getOrigin());
                ExtrasSectionCompiler.Results results = this.extrasSectionCompiler
                        .compile(originPos, regionToRender, sectionBufferBuilderPack);

                // cancel if cancelled
                if (this.isCancelled.get()) {
                    results.release();
                    return CompletableFuture.completedFuture(SectionRenderDispatcher.SectionTaskResult.CANCELLED);
                }

                // create compiled section, create list of upload tasks
                ExtrasSection.CompiledExtrasSection compiledSection = new ExtrasSection.CompiledExtrasSection();
                List<CompletableFuture<Void>> list = new ArrayList<>(results.renderedLayers.size());
                results.renderedLayers.forEach((renderType, meshData) -> {
                    VertexBuffer buffer = this.extrasSection.getBuffer(renderType);
                    if (buffer == null) {
                        OperationStarcleave.LOGGER.warn("Tried to set extra layers on a chunk section, but the buffers were null? This should not happen.");
                        return;
                    }

                    list.add(this.sectionRenderDispatcher.uploadSectionLayer(meshData, buffer));
                    compiledSection.hasBlocks.add(renderType);
                });

                // try to execute upload tasks
                return Util.sequenceFailFast(list).handle((l, throwable) -> {
                    // throw exception if present
                    if (throwable != null && !(throwable instanceof CancellationException) && !(throwable instanceof InterruptedException)) {
                        Minecraft.getInstance().delayCrash(CrashReport.forThrowable(throwable, "(Operation: Starcleave) Rendering section extras"));
                    }

                    // if cancelled cancel
                    if (this.isCancelled.get()) {
                        return SectionRenderDispatcher.SectionTaskResult.CANCELLED;
                    }

                    // return success
                    this.extrasSection.setCompiled(compiledSection);
                    return SectionRenderDispatcher.SectionTaskResult.SUCCESSFUL;
                });
            }

            @Override
            public void cancel() {
                this.region = null;
                if (this.isCancelled.compareAndSet(false, true)) {
                    this.extrasSection.setDirty(false, true);
                }
            }

            @Override
            protected String name() {
                return "starcleave$extras_rend_chk_rebuild";
            }
        };
    }
}

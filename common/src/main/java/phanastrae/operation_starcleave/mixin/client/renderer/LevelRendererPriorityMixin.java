package phanastrae.operation_starcleave.mixin.client.renderer;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.PrioritizeChunkUpdates;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.chunk.RenderRegionCache;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;
import phanastrae.operation_starcleave.client.compat.ClientCompat;
import phanastrae.operation_starcleave.client.duck.LevelRendererDuck;
import phanastrae.operation_starcleave.client.duck.LevelRendererExtrasDuck;
import phanastrae.operation_starcleave.client.duck.RenderBuffersDuck;
import phanastrae.operation_starcleave.client.duck.SectionRenderDispatcherDuck;
import phanastrae.operation_starcleave.client.render.extras_baking.ExtrasSection;
import phanastrae.operation_starcleave.client.render.extras_baking.SectionExtrasRebuildQueue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

// mixins here have high priority, since most of them need to be applied after Overwrites
@Mixin(value = LevelRenderer.class, priority = 5000)
public class LevelRendererPriorityMixin implements LevelRendererExtrasDuck {
    // vanilla has 7 different dirty-setting functions, but they all eventually call setSectionDirty(IIIZ)V
    // setSectionDirty(IIIZ)V is private and only called by setBlockDirty(pos;Z)V and setSectionDirty(III)V,
    // so we should expect exactly one of these to get called, and can target them separately

    // sodium overwrites 4 of these methods.
    // one of these overwrites is setSectionDirty(IIIZ)V, which we can ignore
    // one of these overwrites is setBlockDirty(pos;Z)V, so we just keep the mixin there
    // the other two methods would normally lead to setSectionDirty(III)V but are now unable,
    // so we need to target them separately when sodium is installed

    @Shadow
    @Final
    private Minecraft minecraft;
    @Shadow
    @Nullable
    private ClientLevel level;

    @Shadow
    @Final
    private RenderBuffers renderBuffers;
    @Unique
    @Nullable
    private SectionExtrasRebuildQueue operation_starcleave$sectionExtrasRebuildQueue;
    @Unique
    @Nullable
    private SectionRenderDispatcher operation_starcleave$extrasSectionRenderDispatcher;

    @Override
    public SectionExtrasRebuildQueue operation_starcleave$getRebuildQueue() {
        return this.operation_starcleave$sectionExtrasRebuildQueue;
    }

    // 1)
    @Inject(method = "blockChanged", at = @At("HEAD"))
    private void operation_starcleave$blockChanged(BlockGetter level, BlockPos pos, BlockState oldState, BlockState newState, int flags, CallbackInfo ci) {
        // this mixin's method calls setBlocksDirty(pos;Z)V, so we don't normally need to call set dirty
        // since only this method calls setBlocksDirty(pos;Z)V and it is private we do not check for special blocks and check here instead, but only if the state is changed
        if (newState.is(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_BLOCK) && !oldState.is(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_BLOCK)) {
            this.operation_starcleave$sectionExtrasRebuildQueue.setSectionDirty(pos.getX() >> 4, pos.getY() >> 4, pos.getZ() >> 4, (flags & 8) != 0, true);
        }
    }

    // 2)
    @Inject(method = "setBlockDirty(Lnet/minecraft/core/BlockPos;Z)V", at = @At("HEAD"))
    private void operation_starcleave$setBlockDirty(BlockPos pos, boolean reRenderOnMainThread, CallbackInfo ci) {
        // sodium DOES overwrites this method, but we want to apply the following code either way
        ((LevelRendererDuck) this).operation_starcleave$getFirmamentTextureStorage().queueRebuild(pos);
        this.operation_starcleave$sectionExtrasRebuildQueue.setBlockDirty(pos, reRenderOnMainThread);
    }

    // 3)
    @Inject(method = "setBlocksDirty", at = @At("HEAD"))
    private void operation_starcleave$setBlocksDirty(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, CallbackInfo ci) {
        // sodium DOES overwrite this method, AND blocks access to setSectionDirty(III)V, so we need to handle things here instead
        if (ClientCompat.SODIUM_LOADED) {
            // in vanilla this is only called with minX,Y,Z = maxX,Y,Z, so we check for this first
            if (minX == maxX && minZ == maxZ) {
                // we don't actually need to check y here so we don't
                ((LevelRendererDuck) this).operation_starcleave$getFirmamentTextureStorage().queueRebuild(minX, minZ);
                if (minY == maxY) {
                    this.operation_starcleave$sectionExtrasRebuildQueue.setBlockDirty(minX, minY, minZ, false);
                } else {
                    this.operation_starcleave$sectionExtrasRebuildQueue.setBlocksDirty(minX, minY, minZ, maxX, maxY, maxZ);
                }
            } else {
                ((LevelRendererDuck) this).operation_starcleave$getFirmamentTextureStorage().queueRebuild(minX, minZ, maxX, maxZ);
                this.operation_starcleave$sectionExtrasRebuildQueue.setBlocksDirty(minX, minY, minZ, maxX, maxY, maxZ);
            }
        }
    }

    // 4)
    @Inject(method = "setBlockDirty(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;)V", at = @At("HEAD"))
    private void operations_starcleave$setBlockDirty(BlockPos pos, BlockState oldState, BlockState newState, CallbackInfo ci) {
        // this mixin's method calls setBlocksDirty(IIIIII)V, so we don't normally need to call set dirty
        // when sodium is installed we will not reach setSectionDirty(III)V, which would normally trigger a check for new special blocks, so we check here but only if the state is changed
        if (ClientCompat.SODIUM_LOADED) {
            if (newState.is(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_BLOCK) && !oldState.is(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_BLOCK)) {
                this.operation_starcleave$sectionExtrasRebuildQueue.setSectionDirty(pos.getX() >> 4, pos.getY() >> 4, pos.getZ() >> 4, false, true);
            }
        }
    }

    // 5)
    @Inject(method = "setSectionDirtyWithNeighbors", at = @At("HEAD"))
    private void operation_starcleave$setSectionDirtyWithNeighbours(int sectionX, int sectionY, int sectionZ, CallbackInfo ci) {
        // sodium DOES overwrite this method, AND blocks access to setSectionDirty(III)V, so we need to handle things here instead
        if (ClientCompat.SODIUM_LOADED) {
            // only update this section and not the neighbours, as they probably won't have a heightmap change
            ((LevelRendererDuck) this).operation_starcleave$getFirmamentTextureStorage().queueRebuildForSection(sectionX, sectionZ);
            this.operation_starcleave$sectionExtrasRebuildQueue.setSectionDirtyWithNeighbours(sectionX, sectionY, sectionZ);
        }
    }

    // 6)
    @Inject(method = "setSectionDirty(III)V", at = @At("HEAD"))
    private void operation_starcleave$setSectionDirty(int sectionX, int sectionY, int sectionZ, CallbackInfo ci) {
        // sodium does NOT overwrite this method
        ((LevelRendererDuck) this).operation_starcleave$getFirmamentTextureStorage().queueRebuildForSection(sectionX, sectionZ);
        this.operation_starcleave$sectionExtrasRebuildQueue.setSectionDirty(sectionX, sectionY, sectionZ);
    }

    @Inject(method = "setLevel", at = @At(value = "INVOKE", target = "Ljava/util/Set;clear()V"))
    private void operation_starcleave$nullQueue(ClientLevel level, CallbackInfo ci) {
        if (this.operation_starcleave$extrasSectionRenderDispatcher != null) {
            this.operation_starcleave$extrasSectionRenderDispatcher.dispose();
        }
        this.operation_starcleave$extrasSectionRenderDispatcher = null;

        if (this.operation_starcleave$sectionExtrasRebuildQueue != null) {
            this.operation_starcleave$sectionExtrasRebuildQueue.releaseAllBuffers();
            this.operation_starcleave$sectionExtrasRebuildQueue = null;
        }
    }

    @Inject(method = "allChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ViewArea;<init>(Lnet/minecraft/client/renderer/chunk/SectionRenderDispatcher;Lnet/minecraft/world/level/Level;ILnet/minecraft/client/renderer/LevelRenderer;)V"))
    private void operation_starcleave$startQueue(CallbackInfo ci) {
        if (this.operation_starcleave$extrasSectionRenderDispatcher == null) {
            this.operation_starcleave$extrasSectionRenderDispatcher = new SectionRenderDispatcher(
                    this.level,
                    (LevelRenderer) (Object) this,
                    Util.backgroundExecutor(),
                    this.renderBuffers,
                    this.minecraft.getBlockRenderer(),
                    this.minecraft.getBlockEntityRenderDispatcher()
            );
            ((SectionRenderDispatcherDuck) (this.operation_starcleave$extrasSectionRenderDispatcher)).operation_starcleave$setBufferPool(((RenderBuffersDuck) this.renderBuffers).getExtrasBufferPool());
        } else {
            this.operation_starcleave$extrasSectionRenderDispatcher.setLevel(this.level);
        }
        this.operation_starcleave$extrasSectionRenderDispatcher.blockUntilClear();

        if (this.operation_starcleave$sectionExtrasRebuildQueue != null) {
            this.operation_starcleave$sectionExtrasRebuildQueue.releaseAllBuffers();
        }
        this.operation_starcleave$sectionExtrasRebuildQueue = new SectionExtrasRebuildQueue(
                this.level,
                this.minecraft.options.getEffectiveRenderDistance(),
                this.operation_starcleave$extrasSectionRenderDispatcher,
                this.minecraft.getBlockRenderer(),
                this.minecraft.getBlockEntityRenderDispatcher()
        );

        Entity entity = this.minecraft.getCameraEntity();
        if (entity != null) {
            this.operation_starcleave$sectionExtrasRebuildQueue.repositionCamera(entity.getX(), entity.getZ());
        }
    }

    @Inject(method = "setupRender", at = @At(value = "HEAD"))
    private void operation_starcleave$setupRenderRepositionCamera(Camera camera, Frustum frustum, boolean hasCapturedFrustum, boolean isSpectator, CallbackInfo ci) {
        double playerX = this.minecraft.player.getX();
        double playerZ = this.minecraft.player.getZ();

        int renderDistance = this.minecraft.options.getEffectiveRenderDistance();
        if (ClientCompat.SODIUM_LOADED && this.operation_starcleave$sectionExtrasRebuildQueue.getViewDistance() < renderDistance) {
            // reset if render distance does not match
            // this only seems to be necessary when sodium is installed and the render distance increases, so only check then
            this.operation_starcleave$sectionExtrasRebuildQueue.releaseAllBuffers();
            this.operation_starcleave$sectionExtrasRebuildQueue = new SectionExtrasRebuildQueue(
                    this.level,
                    renderDistance,
                    this.operation_starcleave$extrasSectionRenderDispatcher,
                    this.minecraft.getBlockRenderer(),
                    this.minecraft.getBlockEntityRenderDispatcher()
            );
        }

        this.operation_starcleave$sectionExtrasRebuildQueue.repositionCamera(playerX, playerZ);
    }

    @Inject(method = "compileSections", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Lists;newArrayList()Ljava/util/ArrayList;"))
    private void operation_starcleave$compileExtrasSections(
            Camera camera, CallbackInfo ci,
            @Local RenderRegionCache renderRegionCache, @Local LevelLightEngine levelLightEngine, @Local BlockPos cameraPos
    ) {
        ProfilerFiller profiler = this.minecraft.getProfiler();
        profiler.push("starcleave_extras");
        profiler.push("sync");

        Queue<ExtrasSection> buildAsync = new LinkedList<>();

        // TODO consider adding occlusion/frustum culling here to reduce lag when chunks update off screen? unsure how viable or performant that would be though
        BlockingQueue<ExtrasSection> rebuildQueue = this.operation_starcleave$sectionExtrasRebuildQueue.getRebuildQueue();
        List<ExtrasSection> queue = new ArrayList<>();
        rebuildQueue.drainTo(queue);

        for (ExtrasSection extrasSection : queue) {
            extrasSection.setNotInQueue();
            if (!extrasSection.isDirty()) {
                continue;
            }

            if (!extrasSection.dirtyMaybeAddsSpecialBlocks() && !extrasSection.maybeHadSpecialBlocks()) {
                // exit early if there is no possibility of needing a rebuild
                extrasSection.setNotDirty();
                continue;
            }

            BlockPos origin = extrasSection.getOrigin();
            ChunkAccess chunk = this.level.getChunk(origin);
            LevelChunkSection chunkSection = chunk.getSection(chunk.getSectionIndex(origin.getY()));

            boolean maybeHas = chunkSection.maybeHas(state -> state.is(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_BLOCK));
            extrasSection.setMaybeHadSpecialBlocks(maybeHas);
            if (!maybeHas) {
                extrasSection.setNotDirty();
                continue;
            }

            PrioritizeChunkUpdates setting = this.minecraft.options.prioritizeChunkUpdates().get();
            boolean buildSync = false;
            if (setting == PrioritizeChunkUpdates.NEARBY) {
                BlockPos pos = origin.offset(8, 8, 8);
                buildSync = pos.distSqr(cameraPos) < 768.0 || extrasSection.isDirtyFromPlayer();
            } else if (setting == PrioritizeChunkUpdates.PLAYER_AFFECTED) {
                buildSync = extrasSection.isDirtyFromPlayer();
            }

            if (buildSync) {
                extrasSection.buildSync(renderRegionCache, this.operation_starcleave$extrasSectionRenderDispatcher);
                extrasSection.setNotDirty();
            } else {
                buildAsync.add(extrasSection);
            }
        }

        profiler.popPush("upload");
        this.operation_starcleave$extrasSectionRenderDispatcher.uploadAllPendingUploads();

        profiler.popPush("async");
        for (ExtrasSection section : buildAsync) {
            section.buildAsync(renderRegionCache, this.operation_starcleave$extrasSectionRenderDispatcher);
            section.setNotDirty();
        }

        profiler.pop();
        profiler.pop();
    }
}

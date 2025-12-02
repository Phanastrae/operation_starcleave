package phanastrae.operation_starcleave.client.render.extras_baking;

import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;
import phanastrae.operation_starcleave.client.render.OperationStarcleaveRenderTypes;

import java.util.List;
import java.util.Map;

public class ExtrasSectionCompiler {

    public static final List<Block> IRIDESCENT_BLOCKS = List.of(
            OperationStarcleaveBlocks.STARFLAKED_BISMUTH_BLOCK,
            OperationStarcleaveBlocks.STARFLAKED_BISMUTH_SLAB,

            OperationStarcleaveBlocks.STARFLAKED_BISMUTH_TILES,
            OperationStarcleaveBlocks.STARFLAKED_BISMUTH_TILE_STAIRS,
            OperationStarcleaveBlocks.STARFLAKED_BISMUTH_TILE_SLAB,
            OperationStarcleaveBlocks.STARFLAKED_BISMUTH_TILE_WALL,

            OperationStarcleaveBlocks.STARFLAKED_BISMUTH_PILLAR,

            OperationStarcleaveBlocks.BISREEDS
    );

    private final ModelManager modelManager;
    private final ModelBlockRenderer modelRenderer;

    public ExtrasSectionCompiler(BlockRenderDispatcher blockRenderer, BlockEntityRenderDispatcher blockEntityRenderer) {
        this.modelManager = blockRenderer.getBlockModelShaper().getModelManager();
        this.modelRenderer = blockRenderer.getModelRenderer();
    }

    public Results compile(SectionPos sectionPos, RenderChunkRegion region, SectionBufferBuilderPack sectionBufferBuilderPack) {
        Results results = new Results();

        BlockPos.MutableBlockPos pMut = new BlockPos.MutableBlockPos();
        int mbx = sectionPos.minBlockX();
        int mby = sectionPos.minBlockY();
        int mbz = sectionPos.minBlockZ();
        PoseStack poseStack = new PoseStack();
        ModelBlockRenderer.enableCaching();
        Map<RenderType, BufferBuilder> map = new Reference2ObjectArrayMap<>(ExtrasSection.getLayers().size());
        RandomSource random = RandomSource.create();

        for (int i = 0; i < 16; i++) {
            pMut.setX(i + mbx);
            for (int j = 0; j < 16; j++) {
                pMut.setY(j + mby);
                for (int k = 0; k < 16; k++) {
                    pMut.setZ(k + mbz);
                    BlockState state = region.getBlockState(pMut);

                    if (ExtrasSectionCompiler.isStateIridescent(state)) {
                        RenderType renderType = OperationStarcleaveRenderTypes.getIridescence();
                        BufferBuilder bufferBuilder = this.getOrBeginLayer(map, sectionBufferBuilderPack, renderType);

                        poseStack.pushPose();
                        poseStack.translate(
                                (float) SectionPos.sectionRelative(pMut.getX()),
                                (float) SectionPos.sectionRelative(pMut.getY()),
                                (float) SectionPos.sectionRelative(pMut.getZ())
                        );

                        BakedModel model = getModel(state, this.modelManager);
                        this.renderBatched(state, pMut, region, poseStack, bufferBuilder, true, random, model);

                        poseStack.popPose();
                    }
                }
            }
        }

        for (Map.Entry<RenderType, BufferBuilder> entry : map.entrySet()) {
            RenderType renderType = entry.getKey();
            MeshData meshData = entry.getValue().build();
            if (meshData != null) {
                results.renderedLayers.put(renderType, meshData);
            }
        }

        ModelBlockRenderer.clearCache();
        return results;
    }

    public static BakedModel getModel(BlockState state, ModelManager modelManager) {
        // TODO: consider caching these models at some point
        ResourceLocation resourceLocation = state.getBlock().builtInRegistryHolder().key().location().withSuffix("_iridescence");
        ModelResourceLocation modelResourceLocation = BlockModelShaper.stateToModelLocation(resourceLocation, state);
        return modelManager.getModel(modelResourceLocation);
    }

    // pretty much the same as vanilla's renderBatched, except with a custom model input
    private void renderBatched(
            BlockState state,
            BlockPos pos,
            BlockAndTintGetter level,
            PoseStack poseStack,
            VertexConsumer consumer,
            boolean checkSides,
            RandomSource random,
            BakedModel model
    ) {
        try {
            this.modelRenderer
                    .tesselateBlock(
                            level,
                            model,
                            state,
                            pos,
                            poseStack,
                            consumer,
                            checkSides,
                            random,
                            state.getSeed(pos),
                            OverlayTexture.NO_OVERLAY
                    );
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "(Starcleave extras rendering) Tesselating block in world");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Block being tesselated");
            CrashReportCategory.populateBlockDetails(crashreportcategory, level, pos, state);
            throw new ReportedException(crashreport);
        }
    }

    private BufferBuilder getOrBeginLayer(Map<RenderType, BufferBuilder> buffers, SectionBufferBuilderPack sectionBufferBuilderPack, RenderType renderType) {
        BufferBuilder bufferBuilder = buffers.get(renderType);
        if (bufferBuilder == null) {
            ByteBufferBuilder byteBufferBuilder = sectionBufferBuilderPack.buffer(renderType);
            bufferBuilder = new BufferBuilder(byteBufferBuilder, VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);

            buffers.put(renderType, bufferBuilder);
        }

        return bufferBuilder;
    }

    public static boolean isStateIridescent(BlockState state) {
        // TODO: this will probably need to be optimised as IRIDESCENT_BLOCKS grows
        return IRIDESCENT_BLOCKS.contains(state.getBlock());
    }

    public static class Results {
        public final Map<RenderType, MeshData> renderedLayers = new Reference2ObjectArrayMap<>();

        public void release() {
            this.renderedLayers.values().forEach(MeshData::close);
        }
    }
}

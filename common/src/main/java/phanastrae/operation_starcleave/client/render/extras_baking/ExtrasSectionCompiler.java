package phanastrae.operation_starcleave.client.render.extras_baking;

import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;
import phanastrae.operation_starcleave.client.render.OperationStarcleaveRenderTypes;
import phanastrae.operation_starcleave.client.render.TextureSwappingBufferBuilder;

import java.util.Map;
import java.util.function.Function;

public class ExtrasSectionCompiler {

    private final BlockRenderDispatcher blockRenderer;
    private final BlockEntityRenderDispatcher blockEntityRenderer;

    public ExtrasSectionCompiler(BlockRenderDispatcher blockRenderer, BlockEntityRenderDispatcher blockEntityRenderer) {
        this.blockRenderer = blockRenderer;
        this.blockEntityRenderer = blockEntityRenderer;
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

                    if (state.is(OperationStarcleaveBlocks.STARFLAKED_BISMUTH_BLOCK)) {
                        RenderType renderType = OperationStarcleaveRenderTypes.getIridescence();
                        BufferBuilder bufferBuilder = this.getOrBeginLayer(map, sectionBufferBuilderPack, renderType);

                        poseStack.pushPose();
                        poseStack.translate(
                                (float) SectionPos.sectionRelative(pMut.getX()),
                                (float) SectionPos.sectionRelative(pMut.getY()),
                                (float) SectionPos.sectionRelative(pMut.getZ())
                        );
                        this.blockRenderer.renderBatched(state, pMut, region, poseStack, bufferBuilder, true, random);
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

    private BufferBuilder getOrBeginLayer(Map<RenderType, BufferBuilder> buffers, SectionBufferBuilderPack sectionBufferBuilderPack, RenderType renderType) {
        BufferBuilder bufferBuilder = buffers.get(renderType);
        if (bufferBuilder == null) {
            ByteBufferBuilder byteBufferBuilder = sectionBufferBuilderPack.buffer(renderType);

            // TODO this should probably just be using baked models with the normal texture
            Function<ResourceLocation, TextureAtlasSprite> func = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS);
            TextureAtlasSprite bismuthSprite = func.apply(OperationStarcleave.id("block/starflaked_bismuth_block"));
            TextureAtlasSprite normalSprite = func.apply(OperationStarcleave.id("block/starflaked_bismuth_block_normal"));
            bufferBuilder = new TextureSwappingBufferBuilder(byteBufferBuilder, VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK, bismuthSprite, normalSprite);

            buffers.put(renderType, bufferBuilder);
        }

        return bufferBuilder;
    }

    public static class Results {
        public final Map<RenderType, MeshData> renderedLayers = new Reference2ObjectArrayMap<>();

        public void release() {
            this.renderedLayers.values().forEach(MeshData::close);
        }
    }
}

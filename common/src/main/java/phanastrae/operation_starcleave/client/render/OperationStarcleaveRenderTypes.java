package phanastrae.operation_starcleave.client.render;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import phanastrae.operation_starcleave.client.duck.LevelRendererDuck;
import phanastrae.operation_starcleave.client.render.extras_baking.RenderExtras;
import phanastrae.operation_starcleave.client.render.shader.OperationStarcleaveShaders;
import phanastrae.operation_starcleave.mixin.client.accessor.RenderStateShardAccessor;
import phanastrae.operation_starcleave.mixin.client.accessor.RenderTypeAccessor;

import java.util.Optional;
import java.util.function.BiFunction;

public class OperationStarcleaveRenderTypes {

    public static final RenderStateShard.OutputStateShard FIRMAMENT_SKY_TARGET = new RenderStateShard.OutputStateShard(
            "operation_starcleave$firmament_sky_target",
            () -> ((LevelRendererDuck) Minecraft.getInstance().levelRenderer).operation_starcleave$getFirmamentSkyFramebuffer().bindWrite(true),
            () -> Minecraft.getInstance().getMainRenderTarget().bindWrite(true)
    );

    private static final RenderType FRACTURE = create(
            "operation_starcleave$fracture",
            DefaultVertexFormat.BLOCK,
            VertexFormat.Mode.QUADS,
            131072,
            true,
            true,
            RenderType.CompositeState.builder()
                    .setShaderState(OperationStarcleaveShaders.RENDERTYPE_FRACTURE_SHADER)
                    .setTransparencyState(RenderStateShardAccessor.getTRANSLUCENT_TRANSPARENCY())
                    .createCompositeState(true));

    private static final RenderType SKY_RAY = create(
            "operation_starcleave$sky_ray",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS,
            1536,
            false,
            true,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShardAccessor.getPOSITION_COLOR_SHADER())
                    .setWriteMaskState(RenderStateShardAccessor.getCOLOR_WRITE())
                    .setTransparencyState(RenderStateShardAccessor.getADDITIVE_TRANSPARENCY())
                    .setOutputState(RenderStateShardAccessor.getMAIN_TARGET())
                    .createCompositeState(false)
    );

    private static final RenderType IRIDESCENCE = create(
            "operation_starcleave$iridescense",
            DefaultVertexFormat.NEW_ENTITY,
            VertexFormat.Mode.QUADS,
            786432,
            true,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(OperationStarcleaveShaders.RENDERTYPE_IRIDESCENCE_SHADER)
                    .setTextureState(IndexedMultiTextureStateShard.builder()
                            .add(0, TextureAtlas.LOCATION_BLOCKS, false, true)
                            .add(3, GradientsTexture.LOCATION, false, false)
                            .build()
                    )
                    .setTransparencyState(RenderStateShardAccessor.getTRANSLUCENT_TRANSPARENCY())
                    .setLightmapState(RenderStateShardAccessor.getLIGHTMAP())
                    .setDepthTestState(RenderStateShardAccessor.getEQUAL_DEPTH_TEST())
                    .createCompositeState(true)
    );

    private static final BiFunction<ResourceLocation, Integer, RenderType> ENTITY_IRIDESCENCE = Util.memoize(
            (resourceLocation, iridescenceId) -> {
                RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                        .setShaderState(OperationStarcleaveShaders.RENDERTYPE_ENTITY_IRIDESCENCE_SHADER)
                        .setTextureState(IndexedMultiTextureStateShard.builder()
                                .add(0, resourceLocation, false, false)
                                .add(3, GradientsTexture.LOCATION, false, false)
                                .build()
                        )
                        .setTransparencyState(RenderStateShardAccessor.getTRANSLUCENT_TRANSPARENCY())
                        .setLightmapState(RenderStateShardAccessor.getLIGHTMAP())
                        .setOverlayState(RenderStateShardAccessor.getOVERLAY())
                        .setDepthTestState(RenderStateShardAccessor.getEQUAL_DEPTH_TEST())
                        .createCompositeState(true);
                return createWrapped(
                        "operation_starcleave$entity_iridescence",
                        DefaultVertexFormat.NEW_ENTITY,
                        VertexFormat.Mode.QUADS,
                        1536,
                        true,
                        false,
                        compositeState,
                        () -> RenderExtras.setIridescenceId(iridescenceId),
                        () -> RenderExtras.setIridescenceId(0)
                );
            }
    );

    public static RenderType getFracture() {
        return FRACTURE;
    }

    public static RenderType getSkyRay() {
        return SKY_RAY;
    }

    public static RenderType getIridescence() {
        return IRIDESCENCE;
    }

    public static RenderType entityIridescence(ResourceLocation location, int iridescenceId) {
        return ENTITY_IRIDESCENCE.apply(location, iridescenceId);
    }


    private static RenderType.CompositeRenderType create(
            String name,
            VertexFormat format,
            VertexFormat.Mode mode,
            int bufferSize,
            boolean affectsCrumbling,
            boolean sortOnUpload,
            RenderType.CompositeState state
    ) {
        return RenderTypeAccessor.invokeCreate(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, state);
    }

    private static WrappedRenderType createWrapped(
            String name,
            VertexFormat format,
            VertexFormat.Mode mode,
            int bufferSize,
            boolean affectsCrumbling,
            boolean sortOnUpload,
            RenderType.CompositeState state,
            Runnable afterSetupState,
            Runnable afterClearState
    ) {
        RenderType.CompositeRenderType renderType = create(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, state);
        return new WrappedRenderType(name, renderType, afterSetupState, afterClearState);
    }

    public static class WrappedRenderType extends RenderType {
        private final RenderType wrapped;

        public WrappedRenderType(
                String name,
                RenderType wrapped,
                Runnable afterSetupState,
                Runnable afterClearState) {
            super(
                    name,
                    wrapped.format(),
                    wrapped.mode(),
                    wrapped.bufferSize(),
                    wrapped.affectsCrumbling(),
                    wrapped.sortOnUpload(),
                    () -> {wrapped.setupRenderState(); afterSetupState.run();},
                    () -> {wrapped.clearRenderState(); afterClearState.run();}
            );
            this.wrapped = wrapped;
        }

        @Override
        public Optional<RenderType> outline() {
            return this.wrapped.outline();
        }

        @Override
        public boolean isOutline() {
            return this.wrapped.isOutline();
        }

        @Override
        public String toString() {
            return "Wrapped[" + this.wrapped.toString() + "]";
        }
    }

    public static class IndexedMultiTextureStateShard extends RenderStateShard.EmptyTextureStateShard {
        private final Optional<ResourceLocation> cutoutTexture;

        IndexedMultiTextureStateShard(ImmutableList<Entry> textures) {
            super(() -> {
                for (Entry entry : textures) {
                    TextureManager textureManager = Minecraft.getInstance().getTextureManager();
                    textureManager.getTexture(entry.texture).setFilter(entry.blur, entry.mipmap);
                    RenderSystem.setShaderTexture(entry.index, entry.texture);
                }
            }, () -> {
                for (Entry entry : textures) {
                    RenderSystem.setShaderTexture(entry.index, 0);
                }
            });
            this.cutoutTexture = textures.stream().findFirst().map(Entry::texture);
        }

        @Override
        protected Optional<ResourceLocation> cutoutTexture() {
            return this.cutoutTexture;
        }

        public static IndexedMultiTextureStateShard.Builder builder() {
            return new IndexedMultiTextureStateShard.Builder();
        }

        public static final class Builder {
            private final ImmutableList.Builder<Entry> builder = new ImmutableList.Builder<>();

            public IndexedMultiTextureStateShard.Builder add(int index, ResourceLocation texture, boolean blur, boolean mipmap) {
                this.builder.add(new Entry(index, texture, blur, mipmap));
                return this;
            }

            public IndexedMultiTextureStateShard build() {
                return new IndexedMultiTextureStateShard(this.builder.build());
            }
        }

        public record Entry(int index, ResourceLocation texture, boolean blur, boolean mipmap) {
        }
    }
}

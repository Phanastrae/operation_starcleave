package phanastrae.operation_starcleave.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import phanastrae.operation_starcleave.client.duck.LevelRendererDuck;
import phanastrae.operation_starcleave.client.render.shader.OperationStarcleaveShaders;
import phanastrae.operation_starcleave.mixin.client.accessor.RenderStateShardAccessor;
import phanastrae.operation_starcleave.mixin.client.accessor.RenderTypeAccessor;

import java.util.function.Function;

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
            DefaultVertexFormat.BLOCK,
            VertexFormat.Mode.QUADS,
            786432,
            true,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(OperationStarcleaveShaders.RENDERTYPE_IRIDESCENCE_SHADER)
                    .setTextureState(RenderStateShardAccessor.getBLOCK_SHEET_MIPPED())
                    .setTransparencyState(RenderStateShardAccessor.getTRANSLUCENT_TRANSPARENCY())
                    .setLightmapState(RenderStateShardAccessor.getLIGHTMAP())
                    .setDepthTestState(RenderStateShardAccessor.getEQUAL_DEPTH_TEST())
                    .createCompositeState(true)
    );

    private static final Function<ResourceLocation, RenderType> ENTITY_IRIDESCENCE = Util.memoize(
            resourceLocation -> {
                RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                        .setShaderState(OperationStarcleaveShaders.RENDERTYPE_ENTITY_IRIDESCENCE_SHADER)
                        .setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
                        .setTransparencyState(RenderStateShardAccessor.getTRANSLUCENT_TRANSPARENCY())
                        .setLightmapState(RenderStateShardAccessor.getLIGHTMAP())
                        .setOverlayState(RenderStateShardAccessor.getOVERLAY())
                        .setDepthTestState(RenderStateShardAccessor.getEQUAL_DEPTH_TEST())
                        .createCompositeState(true);
                return create("operation_starcleave$entity_iridescence", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, false, compositeState);
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

    public static RenderType entityIridescence(ResourceLocation location) {
        return ENTITY_IRIDESCENCE.apply(location);
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
}

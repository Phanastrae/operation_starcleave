package phanastrae.operation_starcleave.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import phanastrae.operation_starcleave.client.duck.LevelRendererDuck;
import phanastrae.operation_starcleave.client.render.shader.OperationStarcleaveShaders;
import phanastrae.operation_starcleave.mixin.client.RenderStateShardAccessor;
import phanastrae.operation_starcleave.mixin.client.RenderTypeAccessor;

public class OperationStarcleaveRenderLayers {

    public static final RenderStateShard.OutputStateShard FIRMAMENT_SKY_TARGET = new RenderStateShard.OutputStateShard("operation_starcleave$firmament_sky_target", () -> {
        ((LevelRendererDuck)Minecraft.getInstance().levelRenderer).operation_starcleave$getFirmamentFramebuffer().bindWrite(true);
    }, () -> {
        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
    });

    private static final RenderType FRACTURE = create(
            "operation_starcleave$fracture",
            DefaultVertexFormat.BLOCK,
            VertexFormat.Mode.QUADS,
            131072,
            true,
            true,
            RenderType.CompositeState.builder()
                    .setShaderState(OperationStarcleaveShaders.FRACTURE_PROGRAM)
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

    public static RenderType getFracture() {
        return FRACTURE;
    }

    public static RenderType getSkyRay() {
        return SKY_RAY;
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

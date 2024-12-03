package phanastrae.operation_starcleave.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import phanastrae.operation_starcleave.client.duck.LevelRendererDuck;
import phanastrae.operation_starcleave.client.render.shader.OperationStarcleaveShaders;

public class OperationStarcleaveRenderLayers {

    public static final RenderStateShard.OutputStateShard FIRMAMENT_SKY_TARGET = new RenderStateShard.OutputStateShard("operation_starcleave$firmament_sky_target", () -> {
        ((LevelRendererDuck)Minecraft.getInstance().levelRenderer).operation_starcleave$getFirmamentFramebuffer().bindWrite(false);
    }, () -> {
        Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
    });

    private static final RenderType FRACTURE = RenderType.create(
            "operation_starcleave$fracture",
            DefaultVertexFormat.BLOCK,
            VertexFormat.Mode.QUADS,
            131072,
            true,
            true,
            RenderType.CompositeState.builder()
                    .setShaderState(OperationStarcleaveShaders.FRACTURE_PROGRAM)
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .createCompositeState(true));

    private static final RenderType SKY_RAY = RenderType.create(
            "operation_starcleave$sky_ray",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS,
            1536,
            false,
            true,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                    .setTransparencyState(RenderStateShard.ADDITIVE_TRANSPARENCY)
                    .setOutputState(RenderStateShard.MAIN_TARGET)
                    .createCompositeState(false)
    );

    public static RenderType getFracture() {
        return FRACTURE;
    }

    public static RenderType getSkyRay() {
        return SKY_RAY;
    }

}

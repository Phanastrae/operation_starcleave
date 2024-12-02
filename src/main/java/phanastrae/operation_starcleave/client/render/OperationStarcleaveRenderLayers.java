package phanastrae.operation_starcleave.client.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import phanastrae.operation_starcleave.client.duck.WorldRendererDuck;
import phanastrae.operation_starcleave.client.render.shader.OperationStarcleaveShaders;

public class OperationStarcleaveRenderLayers {

    public static final RenderPhase.Target FIRMAMENT_SKY_TARGET = new RenderPhase.Target("operation_starcleave$firmament_sky_target", () -> {
        ((WorldRendererDuck)MinecraftClient.getInstance().worldRenderer).operation_starcleave$getFirmamentFramebuffer().beginWrite(false);
    }, () -> {
        MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
    });

    private static final RenderLayer FRACTURE = RenderLayer.of(
            "operation_starcleave$fracture",
            VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
            VertexFormat.DrawMode.QUADS,
            131072,
            true,
            true,
            RenderLayer.MultiPhaseParameters.builder()
                    .program(OperationStarcleaveShaders.FRACTURE_PROGRAM)
                    .transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
                    .build(true));

    private static final RenderLayer SKY_RAY = RenderLayer.of(
            "operation_starcleave$sky_ray",
            VertexFormats.POSITION_COLOR,
            VertexFormat.DrawMode.QUADS,
            1536,
            false,
            true,
            RenderLayer.MultiPhaseParameters.builder()
                    .program(RenderPhase.COLOR_PROGRAM)
                    .writeMaskState(RenderPhase.COLOR_MASK)
                    .transparency(RenderPhase.ADDITIVE_TRANSPARENCY)
                    .target(RenderPhase.MAIN_TARGET)
                    .build(false)
    );

    public static RenderLayer getFracture() {
        return FRACTURE;
    }

    public static RenderLayer getSkyRay() {
        return SKY_RAY;
    }

}

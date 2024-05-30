package phanastrae.operation_starcleave.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import phanastrae.operation_starcleave.duck.WorldRendererDuck;
import phanastrae.operation_starcleave.render.shader.OperationStarcleaveShaders;

import static net.minecraft.client.render.RenderLayer.of;
import static net.minecraft.client.render.RenderPhase.*;

public class OperationStarcleaveRenderLayers {

    public static final RenderPhase.Target FIRMAMENT_SKY_TARGET = new RenderPhase.Target("operation_starcleave$firmament_sky_target", () -> {
        ((WorldRendererDuck)MinecraftClient.getInstance().worldRenderer).operation_starcleave$getFirmamentFramebuffer().beginWrite(false);
    }, () -> {
        MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
    });

    private static final RenderLayer FRACTURE = of(
            "operation_starcleave$fracture",
            VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
            VertexFormat.DrawMode.QUADS,
            131072,
            true,
            true,
            RenderLayer.MultiPhaseParameters.builder()
                    .program(OperationStarcleaveShaders.FRACTURE_PROGRAM)
                    .transparency(TRANSLUCENT_TRANSPARENCY)
                    .build(true));

    private static final RenderLayer SKY_RAY = of(
            "operation_starcleave$sky_ray",
            VertexFormats.POSITION_COLOR,
            VertexFormat.DrawMode.QUADS,
            1536,
            false,
            true,
            RenderLayer.MultiPhaseParameters.builder()
                    .program(COLOR_PROGRAM)
                    .writeMaskState(COLOR_MASK)
                    .transparency(ADDITIVE_TRANSPARENCY)
                    .target(MAIN_TARGET)
                    .build(false)
    );

    public static RenderLayer getFracture() {
        return FRACTURE;
    }

    public static RenderLayer getSkyRay() {
        return SKY_RAY;
    }

}

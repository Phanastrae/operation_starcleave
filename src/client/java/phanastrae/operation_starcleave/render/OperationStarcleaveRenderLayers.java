package phanastrae.operation_starcleave.render;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;

import static net.minecraft.client.render.RenderPhase.*;

public class OperationStarcleaveRenderLayers {

    private static final RenderLayer FRACTURE = RenderLayer.of(
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

    public static RenderLayer getFracture() {
        return FRACTURE;
    }

}

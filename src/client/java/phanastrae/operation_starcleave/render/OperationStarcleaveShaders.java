package phanastrae.operation_starcleave.render;

import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormats;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.OperationStarcleave;

import java.io.IOException;

public class OperationStarcleaveShaders {

    public static String fractureShaderID = "rendertype_fracture";

    @Nullable
    private static ShaderProgram fractureShader;

    @Nullable
    public static ShaderProgram getFractureShader() {
        return fractureShader;
    }

    public static final RenderPhase.ShaderProgram FRACTURE_PROGRAM = new RenderPhase.ShaderProgram(OperationStarcleaveShaders::getFractureShader);

    public static void registerShaders(CoreShaderRegistrationCallback.RegistrationContext registrationContext) throws IOException {
        registrationContext.register(OperationStarcleave.id(fractureShaderID), VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, shaderProgram -> OperationStarcleaveShaders.fractureShader = shaderProgram);
    }
}

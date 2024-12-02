package phanastrae.operation_starcleave.client.render.shader;

import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormats;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.OperationStarcleave;

import java.io.IOException;

public class OperationStarcleaveShaders {

    public static String fractureShaderID = "rendertype_fracture";
    public static String fracturePostShaderID = "fracture_post";

    @Nullable
    private static ShaderProgram fractureShader;

    @Nullable
    private static ShaderProgram fracturePostShader;

    @Nullable
    public static ShaderProgram getFractureShader() {
        return fractureShader;
    }

    @Nullable
    public static ShaderProgram getFracturePostShader() {
        return fracturePostShader;
    }

    public static final RenderPhase.ShaderProgram FRACTURE_PROGRAM = new RenderPhase.ShaderProgram(OperationStarcleaveShaders::getFractureShader);

    public static final RenderPhase.ShaderProgram FRACTURE_POST_PROGRAM = new RenderPhase.ShaderProgram(OperationStarcleaveShaders::getFracturePostShader);

    public static void registerShaders(CoreShaderRegistrationCallback.RegistrationContext registrationContext) throws IOException {
        registrationContext.register(OperationStarcleave.id(fractureShaderID), VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, shaderProgram -> OperationStarcleaveShaders.fractureShader = shaderProgram);
        registrationContext.register(OperationStarcleave.id(fracturePostShaderID), VertexFormats.POSITION_TEXTURE_COLOR, shaderProgram -> OperationStarcleaveShaders.fracturePostShader = shaderProgram);
    }
}

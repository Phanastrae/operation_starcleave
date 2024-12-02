package phanastrae.operation_starcleave.client.render.shader;

import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.ShaderInstance;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.OperationStarcleave;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.io.IOException;

public class OperationStarcleaveShaders {

    public static String fractureShaderID = "rendertype_fracture";
    public static String fracturePostShaderID = "fracture_post";

    @Nullable
    private static ShaderInstance fractureShader;

    @Nullable
    private static ShaderInstance fracturePostShader;

    @Nullable
    public static ShaderInstance getFractureShader() {
        return fractureShader;
    }

    @Nullable
    public static ShaderInstance getFracturePostShader() {
        return fracturePostShader;
    }

    public static final RenderStateShard.ShaderStateShard FRACTURE_PROGRAM = new RenderStateShard.ShaderStateShard(OperationStarcleaveShaders::getFractureShader);

    public static final RenderStateShard.ShaderStateShard FRACTURE_POST_PROGRAM = new RenderStateShard.ShaderStateShard(OperationStarcleaveShaders::getFracturePostShader);

    public static void registerShaders(CoreShaderRegistrationCallback.RegistrationContext registrationContext) throws IOException {
        registrationContext.register(OperationStarcleave.id(fractureShaderID), DefaultVertexFormat.BLOCK, shaderProgram -> OperationStarcleaveShaders.fractureShader = shaderProgram);
        registrationContext.register(OperationStarcleave.id(fracturePostShaderID), DefaultVertexFormat.POSITION_TEX_COLOR, shaderProgram -> OperationStarcleaveShaders.fracturePostShader = shaderProgram);
    }
}

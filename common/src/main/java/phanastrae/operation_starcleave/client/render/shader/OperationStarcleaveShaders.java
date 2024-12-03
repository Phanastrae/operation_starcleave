package phanastrae.operation_starcleave.client.render.shader;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.OperationStarcleave;

import java.io.IOException;
import java.util.function.Consumer;

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

    public static void registerShaders(RegistrationContext registrationCallback) throws IOException {
        registrationCallback.register(OperationStarcleave.id(fractureShaderID), DefaultVertexFormat.BLOCK, shaderProgram -> OperationStarcleaveShaders.fractureShader = shaderProgram);
        registrationCallback.register(OperationStarcleave.id(fracturePostShaderID), DefaultVertexFormat.POSITION_TEX_COLOR, shaderProgram -> OperationStarcleaveShaders.fracturePostShader = shaderProgram);
    }

    @FunctionalInterface
    public interface RegistrationContext {
        void register(ResourceLocation id, VertexFormat vertexFormat, Consumer<ShaderInstance> loadCallback) throws IOException;
    }
}

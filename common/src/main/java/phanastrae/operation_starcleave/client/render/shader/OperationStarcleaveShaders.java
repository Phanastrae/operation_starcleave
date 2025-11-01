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
    public static final String FRACTURE_SHADER_ID = "rendertype_fracture";
    public static final String FRACTURE_POST_SHADER_ID = "fracture_post";
    public static final String IRIDESCENCE_SHADER_ID = "rendertype_iridescence";

    @Nullable
    private static ShaderInstance fractureShader;

    @Nullable
    private static ShaderInstance fracturePostShader;

    @Nullable
    private static ShaderInstance iridescenceShader;

    @Nullable
    public static ShaderInstance getFractureShader() {
        return fractureShader;
    }

    @Nullable
    public static ShaderInstance getFracturePostShader() {
        return fracturePostShader;
    }

    @Nullable
    public static ShaderInstance getIridescenceShader() {
        return iridescenceShader;
    }

    public static final RenderStateShard.ShaderStateShard FRACTURE_PROGRAM = new RenderStateShard.ShaderStateShard(OperationStarcleaveShaders::getFractureShader);

    public static final RenderStateShard.ShaderStateShard FRACTURE_POST_PROGRAM = new RenderStateShard.ShaderStateShard(OperationStarcleaveShaders::getFracturePostShader);

    public static final RenderStateShard.ShaderStateShard IRIDESCENCE_PROGRAM = new RenderStateShard.ShaderStateShard(OperationStarcleaveShaders::getIridescenceShader);

    public static void registerShaders(RegistrationContext registrationCallback) throws IOException {
        register(registrationCallback, FRACTURE_SHADER_ID, DefaultVertexFormat.BLOCK, s -> OperationStarcleaveShaders.fractureShader = s);
        register(registrationCallback, FRACTURE_POST_SHADER_ID, DefaultVertexFormat.POSITION_TEX_COLOR, s -> OperationStarcleaveShaders.fracturePostShader = s);
        register(registrationCallback, IRIDESCENCE_SHADER_ID, DefaultVertexFormat.BLOCK, s -> OperationStarcleaveShaders.iridescenceShader = s);
    }

    private static void register(RegistrationContext registrationCallback, String id, VertexFormat vertexFormat, Consumer<ShaderInstance> consumer) throws IOException {
        registrationCallback.register(OperationStarcleave.id(id), vertexFormat, consumer);
    }

    @FunctionalInterface
    public interface RegistrationContext {
        void register(ResourceLocation id, VertexFormat vertexFormat, Consumer<ShaderInstance> loadCallback) throws IOException;
    }
}

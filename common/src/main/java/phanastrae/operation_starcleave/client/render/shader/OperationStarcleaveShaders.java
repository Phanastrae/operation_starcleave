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

    @Nullable
    private static ShaderInstance fractureShader;

    @Nullable
    private static ShaderInstance iridescenceShader;

    @Nullable
    private static ShaderInstance entityIridescenceShader;

    @Nullable
    private static ShaderInstance fracturePostShader;

    @Nullable
    public static ShaderInstance getFractureShader() {
        return fractureShader;
    }

    @Nullable
    public static ShaderInstance getIridescenceShader() {
        return iridescenceShader;
    }

    @Nullable
    public static ShaderInstance getEntityIridescenceShader() {
        return entityIridescenceShader;
    }

    @Nullable
    public static ShaderInstance getFracturePostShader() {
        return fracturePostShader;
    }

    public static final RenderStateShard.ShaderStateShard RENDERTYPE_FRACTURE_SHADER = new RenderStateShard.ShaderStateShard(OperationStarcleaveShaders::getFractureShader);
    public static final RenderStateShard.ShaderStateShard RENDERTYPE_IRIDESCENCE_SHADER = new RenderStateShard.ShaderStateShard(OperationStarcleaveShaders::getIridescenceShader);
    public static final RenderStateShard.ShaderStateShard RENDERTYPE_ENTITY_IRIDESCENCE_SHADER = new RenderStateShard.ShaderStateShard(OperationStarcleaveShaders::getEntityIridescenceShader);
    public static final RenderStateShard.ShaderStateShard FRACTURE_POST_PROGRAM = new RenderStateShard.ShaderStateShard(OperationStarcleaveShaders::getFracturePostShader);

    public static void registerShaders(RegistrationContext registrationCallback) throws IOException {
        register(registrationCallback, "rendertype_fracture", DefaultVertexFormat.BLOCK, s -> OperationStarcleaveShaders.fractureShader = s);
        register(registrationCallback, "rendertype_iridescence", DefaultVertexFormat.BLOCK, s -> OperationStarcleaveShaders.iridescenceShader = s);
        register(registrationCallback, "rendertype_entity_iridescence", DefaultVertexFormat.NEW_ENTITY, s -> OperationStarcleaveShaders.entityIridescenceShader = s);
        register(registrationCallback, "fracture_post", DefaultVertexFormat.POSITION_TEX_COLOR, s -> OperationStarcleaveShaders.fracturePostShader = s);
    }

    private static void register(RegistrationContext registrationCallback, String id, VertexFormat vertexFormat, Consumer<ShaderInstance> consumer) throws IOException {
        registrationCallback.register(OperationStarcleave.id(id), vertexFormat, consumer);
    }

    @FunctionalInterface
    public interface RegistrationContext {
        void register(ResourceLocation id, VertexFormat vertexFormat, Consumer<ShaderInstance> loadCallback) throws IOException;
    }
}

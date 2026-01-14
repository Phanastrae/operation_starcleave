package phanastrae.operation_starcleave.mixin.client.renderer.extra_layers;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.client.render.extras_baking.RenderExtras;

import java.util.List;

@Mixin(ShaderInstance.class)
public abstract class ShaderInstanceMixin {

    @Shadow
    @Nullable
    public abstract Uniform getUniform(String name);

    @Shadow
    @Final
    private List<Uniform> uniforms;
    @Shadow
    @Final
    private String name;
    @Unique
    @Nullable
    public Uniform operation_starcleave$POS_OFFSET;

    @Unique
    @Nullable
    public Uniform operation_starcleave$IRIDESCENCE_ID;

    @Inject(method = { // include both the normal init and NeoForge's custom init
            "<init>(Lnet/minecraft/server/packs/resources/ResourceProvider;Ljava/lang/String;Lcom/mojang/blaze3d/vertex/VertexFormat;)V",
            "<init>(Lnet/minecraft/server/packs/resources/ResourceProvider;Lnet/minecraft/resources/ResourceLocation;Lcom/mojang/blaze3d/vertex/VertexFormat;)V"
    }, at = @At("RETURN"))
    private void operation_starcleave$init(CallbackInfo ci) { // deliberately do not include extra fields
        this.operation_starcleave$POS_OFFSET = this.getUniform("OperationStarcleavePosOffset");
        this.operation_starcleave$IRIDESCENCE_ID = this.getUniform("OperationStarcleaveIridescenceId");
    }

    @Inject(method = "setDefaultUniforms", at = @At("RETURN"))
    private void operation_starcleave$setup(VertexFormat.Mode mode, Matrix4f projectionMatrix, Matrix4f frustrumMatrix, Window window, CallbackInfo ci) {
        if (this.operation_starcleave$POS_OFFSET != null) {
            this.operation_starcleave$POS_OFFSET.set(RenderExtras.getPosOffset());
        }

        if (this.operation_starcleave$IRIDESCENCE_ID != null) {
            this.operation_starcleave$IRIDESCENCE_ID.set(RenderExtras.getIridescenceId());
        }
    }
}

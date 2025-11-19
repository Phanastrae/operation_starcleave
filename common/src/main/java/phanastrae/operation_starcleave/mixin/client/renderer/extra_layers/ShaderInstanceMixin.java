package phanastrae.operation_starcleave.mixin.client.renderer.extra_layers;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.client.render.extras_baking.RenderExtras;

@Mixin(ShaderInstance.class)
public abstract class ShaderInstanceMixin {

    @Shadow
    @Nullable
    public abstract Uniform getUniform(String name);

    @Unique
    @Nullable
    public Uniform operation_starcleave$POS_OFFSET;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void operation_starcleave$init(ResourceProvider resourceProvider, String name, VertexFormat vertexFormat, CallbackInfo ci) {
        this.operation_starcleave$POS_OFFSET = this.getUniform("OperationStarcleavePosOffset");
    }

    @Inject(method = "setDefaultUniforms", at = @At("RETURN"))
    private void operation_starcleave$setup(VertexFormat.Mode mode, Matrix4f projectionMatrix, Matrix4f frustrumMatrix, Window window, CallbackInfo ci) {
        if (this.operation_starcleave$POS_OFFSET != null) {
            this.operation_starcleave$POS_OFFSET.set(RenderExtras.getPosOffset());
        }
    }
}

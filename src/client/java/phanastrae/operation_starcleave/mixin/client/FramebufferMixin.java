package phanastrae.operation_starcleave.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import phanastrae.operation_starcleave.util.FrameBufferStencilAccess;

import java.util.Objects;

@Mixin(Framebuffer.class)
public abstract class FramebufferMixin implements FrameBufferStencilAccess {
    @Shadow public abstract void resize(int width, int height, boolean getError);

    @Shadow public int textureWidth;
    @Shadow public int textureHeight;

    @Unique
    private boolean stencilBufferEnabled;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(boolean useDepth, CallbackInfo ci) {
        stencilBufferEnabled = false;
    }

    @ModifyArgs(method = "initFbo", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_texImage2D(IIIIIIIILjava/nio/IntBuffer;)V", ordinal = 0))
    public void operation_starcleave$modifyTexImage2D(Args args) {
        if(Objects.equals(args.get(2), GL11.GL_DEPTH_COMPONENT) && stencilBufferEnabled) {
            args.set(2, GL30.GL_DEPTH32F_STENCIL8);
            args.set(6, GL30.GL_DEPTH_STENCIL);
            args.set(7, GL30.GL_FLOAT_32_UNSIGNED_INT_24_8_REV);
        }
    }

    @ModifyArgs(method = "initFbo", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_glFramebufferTexture2D(IIIII)V", ordinal = 1))
    public void operation_starcleave$modifyFramebufferTexture2D(Args args) {
        if(Objects.equals(args.get(1), GL30.GL_DEPTH_ATTACHMENT) && stencilBufferEnabled) {
            args.set(1, GL30.GL_DEPTH_STENCIL_ATTACHMENT);
        }
    }

    @Override
    public void operation_starcleave$setEnabled(boolean enabled) {
        if(stencilBufferEnabled != enabled) {
            stencilBufferEnabled = enabled;
            resize(textureWidth, textureHeight, MinecraftClient.IS_SYSTEM_MAC);
        }
    }

    @Override
    public boolean operation_starcleave$stencilBufferEnabled() {
        return stencilBufferEnabled;
    }
}

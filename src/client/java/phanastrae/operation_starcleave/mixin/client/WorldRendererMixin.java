package phanastrae.operation_starcleave.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.resource.ResourceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.duck.WorldRendererDuck;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin implements WorldRendererDuck {

    @Final
    @Shadow
    private MinecraftClient client;

    Framebuffer dummyFramebuffer;
    Framebuffer firmamentFramebuffer;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void operation_starcleave$onInit(MinecraftClient client, EntityRenderDispatcher entityRenderDispatcher, BlockEntityRenderDispatcher blockEntityRenderDispatcher, BufferBuilderStorage bufferBuilders, CallbackInfo ci) {
        this.dummyFramebuffer = null;
        this.firmamentFramebuffer = null;
    }

    @Inject(method = "reload(Lnet/minecraft/resource/ResourceManager;)V", at = @At("RETURN"))
    private void operation_starcleave$loadFramebuffers(ResourceManager manager, CallbackInfo ci) {
        if(this.dummyFramebuffer != null) {
            this.dummyFramebuffer.delete();
        }
        if(this.firmamentFramebuffer != null) {
            this.firmamentFramebuffer.delete();
        }

        this.dummyFramebuffer = new SimpleFramebuffer(this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight(), true, MinecraftClient.IS_SYSTEM_MAC);
        this.firmamentFramebuffer = new SimpleFramebuffer(this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight(), true, MinecraftClient.IS_SYSTEM_MAC);
    }

    @Inject(method = "close", at = @At("RETURN"))
    private void operation_starcleave$deleteFramebuffers(CallbackInfo ci) {
        if(this.dummyFramebuffer != null) {
            this.dummyFramebuffer.delete();
        }
        if(this.firmamentFramebuffer != null) {
            this.firmamentFramebuffer.delete();
        }
    }

    @Inject(method = "onResized", at = @At("RETURN"))
    private void operation_starcleave$resizeFramebuffers(int width, int height, CallbackInfo ci) {
        if (this.dummyFramebuffer != null) {
            this.dummyFramebuffer.resize(width, height, MinecraftClient.IS_SYSTEM_MAC);
        }
        if (this.firmamentFramebuffer != null) {
            this.firmamentFramebuffer.resize(width, height, MinecraftClient.IS_SYSTEM_MAC);
        }
    }

    @Override
    public Framebuffer operation_starcleave$getFirmamentFramebuffer() {
        return this.firmamentFramebuffer;
    }

    @Override
    public Framebuffer operation_starcleave$getDummyFramebuffer() {
        return this.dummyFramebuffer;
    }
}

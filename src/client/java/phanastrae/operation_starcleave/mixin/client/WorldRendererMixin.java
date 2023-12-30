package phanastrae.operation_starcleave.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.resource.ResourceManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.render.OperationStarcleaveWorldRenderer;
import phanastrae.operation_starcleave.render.firmament.FirmamentBuiltSubRegionStorage;
import phanastrae.operation_starcleave.world.firmament.Firmament;
import phanastrae.operation_starcleave.world.firmament.SubRegionPos;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin implements OperationStarcleaveWorldRenderer {

    @Final
    @Shadow
    private MinecraftClient client;

    @Shadow private @Nullable ClientWorld world;
    Framebuffer firmamentFramebuffer;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void operation_starcleave$onInit(MinecraftClient client, EntityRenderDispatcher entityRenderDispatcher, BlockEntityRenderDispatcher blockEntityRenderDispatcher, BufferBuilderStorage bufferBuilders, CallbackInfo ci) {
        this.firmamentFramebuffer = null;
    }

    @Inject(method = "reload(Lnet/minecraft/resource/ResourceManager;)V", at = @At("RETURN"))
    private void operation_starcleave$loadFirmamentFramebuffer(ResourceManager manager, CallbackInfo ci) {
        if(this.firmamentFramebuffer != null) {
            this.firmamentFramebuffer.delete();
        }

        this.firmamentFramebuffer = new SimpleFramebuffer(this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight(), true, MinecraftClient.IS_SYSTEM_MAC);
    }

    @Inject(method = "close", at = @At("RETURN"))
    private void operation_starcleave$deleteFirmamentFramebuffer(CallbackInfo ci) {
        if(this.firmamentFramebuffer != null) {
            this.firmamentFramebuffer.delete();
        }
    }

    @Inject(method = "onResized", at = @At("RETURN"))
    private void operation_starcleave$resizeFirmamentFramebuffer(int width, int height, CallbackInfo ci) {
        if (this.firmamentFramebuffer != null) {
            this.firmamentFramebuffer.resize(width, height, MinecraftClient.IS_SYSTEM_MAC);
        }
    }

    @Inject(method = "reload()V", at = @At("RETURN"))
    private void operation_starcleave$reload(CallbackInfo ci) {
        //FirmamentBuiltSubRegionStorage.getInstance().forEach((firmamentBuiltSubRegionHolder -> firmamentBuiltSubRegionHolder.build(Firmament.fromWorld(this.world), new SubRegionPos(firmamentBuiltSubRegionHolder.id))));
    }

    @Override
    public Framebuffer operation_starcleave$getFirmamentFramebuffer() {
        return this.firmamentFramebuffer;
    }
}

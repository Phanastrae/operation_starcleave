package phanastrae.operation_starcleave.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.client.duck.WorldRendererDuck;
import phanastrae.operation_starcleave.world.firmament.Firmament;

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

    // Stop Rain and Snow from rendering beneath damaged Firmament
    @Inject(method = "renderWeather", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/World;getTopY(Lnet/minecraft/world/Heightmap$Type;II)I", shift = At.Shift.AFTER))
    private void operation_starcleave$blockPrecipitationRender(CallbackInfo ci, @Local(ordinal = 0) BlockPos.Mutable mutable, @Local(ordinal = 8) LocalIntRef refTopY) {
        World world = this.client.world;
        Firmament firmament = Firmament.fromWorld(world);
        if(firmament == null) return;

        int damage = firmament.getDamage(mutable.getX(), mutable.getZ());
        if(damage >= 5) {
            refTopY.set(firmament.getY());
        }
    }

    // Stop Rain Splashes from rendering beneath Damaged Firmament
    @Inject(method = "tickRainSplashing", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/WorldView;getTopPosition(Lnet/minecraft/world/Heightmap$Type;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/math/BlockPos;", shift = At.Shift.AFTER))
    private void operation_starcleave$blockRainSplash(CallbackInfo ci, @Local(ordinal = 2) LocalRef<BlockPos> refTopPosition) {
        World world = this.client.world;
        Firmament firmament = Firmament.fromWorld(world);
        if(firmament == null) return;

        BlockPos topPos = refTopPosition.get();
        int damage = firmament.getDamage(topPos.getX(), topPos.getZ());
        if(damage >= 5) {
            refTopPosition.set(new BlockPos(topPos.getX(), firmament.getY(), topPos.getZ()));
        }
    }
}

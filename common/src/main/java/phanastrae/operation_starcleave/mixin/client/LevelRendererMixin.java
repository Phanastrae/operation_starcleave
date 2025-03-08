package phanastrae.operation_starcleave.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.client.duck.LevelRendererDuck;
import phanastrae.operation_starcleave.world.firmament.Firmament;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin implements LevelRendererDuck {

    @Shadow @Final private Minecraft minecraft;
    RenderTarget operationStarcleave$dummyFramebuffer;
    RenderTarget operationStarcleave$firmamentFramebuffer;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void operation_starcleave$onInit(Minecraft client, EntityRenderDispatcher entityRenderDispatcher, BlockEntityRenderDispatcher blockEntityRenderDispatcher, RenderBuffers bufferBuilders, CallbackInfo ci) {
        this.operationStarcleave$dummyFramebuffer = null;
        this.operationStarcleave$firmamentFramebuffer = null;
    }

    @Inject(method = "onResourceManagerReload", at = @At("RETURN"))
    private void operation_starcleave$loadFramebuffers(ResourceManager manager, CallbackInfo ci) {
        if(this.operationStarcleave$dummyFramebuffer != null) {
            this.operationStarcleave$dummyFramebuffer.destroyBuffers();
        }
        if(this.operationStarcleave$firmamentFramebuffer != null) {
            this.operationStarcleave$firmamentFramebuffer.destroyBuffers();
        }

        this.operationStarcleave$dummyFramebuffer = new TextureTarget(this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight(), true, Minecraft.ON_OSX);
        this.operationStarcleave$firmamentFramebuffer = new TextureTarget(this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight(), true, Minecraft.ON_OSX);
    }

    @Inject(method = "close", at = @At("RETURN"))
    private void operation_starcleave$deleteFramebuffers(CallbackInfo ci) {
        if(this.operationStarcleave$dummyFramebuffer != null) {
            this.operationStarcleave$dummyFramebuffer.destroyBuffers();
        }
        if(this.operationStarcleave$firmamentFramebuffer != null) {
            this.operationStarcleave$firmamentFramebuffer.destroyBuffers();
        }
    }

    @Inject(method = "resize", at = @At("RETURN"))
    private void operation_starcleave$resizeFramebuffers(int width, int height, CallbackInfo ci) {
        if (this.operationStarcleave$dummyFramebuffer != null) {
            this.operationStarcleave$dummyFramebuffer.resize(width, height, Minecraft.ON_OSX);
        }
        if (this.operationStarcleave$firmamentFramebuffer != null) {
            this.operationStarcleave$firmamentFramebuffer.resize(width, height, Minecraft.ON_OSX);
        }
    }

    @Override
    public RenderTarget operation_starcleave$getFirmamentFramebuffer() {
        return this.operationStarcleave$firmamentFramebuffer;
    }

    @Override
    public RenderTarget operation_starcleave$getDummyFramebuffer() {
        return this.operationStarcleave$dummyFramebuffer;
    }

    // Stop Rain and Snow from rendering beneath damaged Firmament
    @Inject(method = "renderSnowAndRain", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/Level;getHeight(Lnet/minecraft/world/level/levelgen/Heightmap$Types;II)I", shift = At.Shift.AFTER))
    private void operation_starcleave$blockPrecipitationRender(CallbackInfo ci, @Local(ordinal = 0) BlockPos.MutableBlockPos mutable, @Local(ordinal = 8) LocalIntRef refTopY) {
        Level world = this.minecraft.level;
        Firmament firmament = Firmament.fromLevel(world);
        if(firmament == null) return;

        int damage = firmament.getDamage(mutable.getX(), mutable.getZ());
        if(damage >= 5) {
            refTopY.set(firmament.getY());
        }
    }

    // Stop Rain Splashes from rendering beneath Damaged Firmament
    @Inject(method = "tickRain", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/LevelReader;getHeightmapPos(Lnet/minecraft/world/level/levelgen/Heightmap$Types;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/BlockPos;", shift = At.Shift.AFTER))
    private void operation_starcleave$blockRainSplash(CallbackInfo ci, @Local(ordinal = 2) LocalRef<BlockPos> refTopPosition) {
        Level world = this.minecraft.level;
        Firmament firmament = Firmament.fromLevel(world);
        if(firmament == null) return;

        BlockPos topPos = refTopPosition.get();
        int damage = firmament.getDamage(topPos.getX(), topPos.getZ());
        if(damage >= 5) {
            refTopPosition.set(new BlockPos(topPos.getX(), firmament.getY(), topPos.getZ()));
        }
    }
}

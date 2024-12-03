package phanastrae.operation_starcleave.mixin.client;

import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LevelRenderer.class)
public interface LevelRendererAccessor {
    @Accessor
    VertexBuffer getStarBuffer();
    @Accessor
    VertexBuffer getSkyBuffer();
    @Accessor
    VertexBuffer getDarkBuffer();
    @Accessor
    VertexBuffer getCloudBuffer();
    @Accessor
    RenderBuffers getRenderBuffers();
}

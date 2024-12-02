package phanastrae.operation_starcleave.mixin.client;

import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WorldRenderer.class)
public interface WorldRendererAccessor {
    @Accessor
    VertexBuffer getStarsBuffer();
    @Accessor
    VertexBuffer getLightSkyBuffer();
    @Accessor
    VertexBuffer getDarkSkyBuffer();
    @Accessor
    VertexBuffer getCloudsBuffer();
}

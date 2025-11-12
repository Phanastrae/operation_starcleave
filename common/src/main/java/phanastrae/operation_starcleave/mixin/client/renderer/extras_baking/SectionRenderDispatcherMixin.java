package phanastrae.operation_starcleave.mixin.client.renderer.extras_baking;

import net.minecraft.client.renderer.SectionBufferBuilderPool;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import phanastrae.operation_starcleave.client.duck.SectionRenderDispatcherDuck;

@Mixin(SectionRenderDispatcher.class)
public class SectionRenderDispatcherMixin implements SectionRenderDispatcherDuck {

    @Mutable
    @Shadow
    @Final
    private SectionBufferBuilderPool bufferPool;

    @Override
    public void operation_starcleave$setBufferPool(SectionBufferBuilderPool pool) {
        this.bufferPool = pool;
    }
}

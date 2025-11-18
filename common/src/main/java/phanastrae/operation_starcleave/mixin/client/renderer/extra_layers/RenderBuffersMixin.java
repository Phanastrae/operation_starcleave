package phanastrae.operation_starcleave.mixin.client.renderer.extra_layers;

import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.SectionBufferBuilderPool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.client.duck.RenderBuffersDuck;
import phanastrae.operation_starcleave.client.render.extras_baking.ExtrasSectionBufferBuilderPack;

@Mixin(RenderBuffers.class)
public class RenderBuffersMixin implements RenderBuffersDuck {
    @Unique
    private SectionBufferBuilderPool operation_starcleave$extrasSectionBufferPool;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void operation_starcleave$initExtras(int bufferCount, CallbackInfo ci) {
        this.operation_starcleave$extrasSectionBufferPool = ExtrasSectionBufferBuilderPack.allocateExtras(bufferCount);
    }

    @Override
    public SectionBufferBuilderPool getExtrasBufferPool() {
        return this.operation_starcleave$extrasSectionBufferPool;
    }
}

package phanastrae.operation_starcleave.mixin.client.renderer.extra_layers;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SectionBufferBuilderPool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.client.duck.RenderBuffersDuck;
import phanastrae.operation_starcleave.client.render.OperationStarcleaveSheets;
import phanastrae.operation_starcleave.client.render.extras_baking.ExtrasSectionBufferBuilderPack;

@Mixin(RenderBuffers.class)
public abstract class RenderBuffersMixin implements RenderBuffersDuck {

    @Shadow
    private static void put(Object2ObjectLinkedOpenHashMap<RenderType, ByteBufferBuilder> mapBuilders, RenderType renderType) {
    }

    @Unique
    private SectionBufferBuilderPool operation_starcleave$extrasSectionBufferPool;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void operation_starcleave$initExtras(int bufferCount, CallbackInfo ci) {
        this.operation_starcleave$extrasSectionBufferPool = ExtrasSectionBufferBuilderPack.allocateExtras(bufferCount);
    }

    @Override
    public SectionBufferBuilderPool operation_starcleave$getExtrasBufferPool() {
        return this.operation_starcleave$extrasSectionBufferPool;
    }

    @Inject(method = {"method_54639", "lambda$new$1"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;armorEntityGlint()Lnet/minecraft/client/renderer/RenderType;"))
    private void operation_starcleave$addIridescence(Object2ObjectLinkedOpenHashMap map, CallbackInfo ci) {
        put(map, OperationStarcleaveSheets.iridescenceBlockSheet());
    }
}

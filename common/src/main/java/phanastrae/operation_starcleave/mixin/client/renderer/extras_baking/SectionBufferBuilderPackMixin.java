package phanastrae.operation_starcleave.mixin.client.renderer.extras_baking;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import phanastrae.operation_starcleave.client.render.extras_baking.ExtrasSectionBufferBuilderPack;

import java.util.Map;
import java.util.function.Consumer;

@Mixin(SectionBufferBuilderPack.class)
public class SectionBufferBuilderPackMixin {
    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/Util;make(Ljava/lang/Object;Ljava/util/function/Consumer;)Ljava/lang/Object;"))
    private Object operation_starcleave$useExtraBuffers(Object object, Consumer<? super Map<RenderType, ByteBufferBuilder>> consumer, Operation<Map<RenderType, ByteBufferBuilder>> original) {
        // both "Object"s are actually "Map<RenderType, ByteBufferBuilder>"s
        if (((SectionBufferBuilderPack) (Object) this) instanceof ExtrasSectionBufferBuilderPack) {
            return Util.make(new Reference2ObjectArrayMap<>(ExtrasSectionBufferBuilderPack.RENDER_TYPES.size()), map -> {
                for (RenderType rendertype : ExtrasSectionBufferBuilderPack.RENDER_TYPES) {
                    map.put(rendertype, new ByteBufferBuilder(rendertype.bufferSize()));
                }
            });
        } else {
            return original.call(object, consumer);
        }
    }
}

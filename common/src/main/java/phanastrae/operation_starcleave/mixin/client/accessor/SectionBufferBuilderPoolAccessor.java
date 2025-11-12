package phanastrae.operation_starcleave.mixin.client.accessor;

import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.SectionBufferBuilderPool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(SectionBufferBuilderPool.class)
public interface SectionBufferBuilderPoolAccessor {
    @Invoker("<init>")
    static SectionBufferBuilderPool invokeInit(List<SectionBufferBuilderPack> freeBuffers) {
        throw new AssertionError();
    }
}

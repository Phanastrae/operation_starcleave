package phanastrae.operation_starcleave.mixin.client;

import net.minecraft.client.renderer.RenderStateShard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderStateShard.class)
public interface RenderStateShardAccessor {
    @Accessor
    static RenderStateShard.TransparencyStateShard getTRANSLUCENT_TRANSPARENCY() {
        throw new AssertionError();
    }

    @Accessor
    static RenderStateShard.TransparencyStateShard getADDITIVE_TRANSPARENCY() {
        throw new AssertionError();
    }

    @Accessor
    static RenderStateShard.ShaderStateShard getPOSITION_COLOR_SHADER() {
        throw new AssertionError();
    }

    @Accessor
    static RenderStateShard.WriteMaskStateShard getCOLOR_WRITE() {
        throw new AssertionError();
    }

    @Accessor
    static RenderStateShard.OutputStateShard getMAIN_TARGET() {
        throw new AssertionError();
    }
}

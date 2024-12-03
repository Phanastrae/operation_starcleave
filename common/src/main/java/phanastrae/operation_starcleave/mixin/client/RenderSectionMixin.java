package phanastrae.operation_starcleave.mixin.client;

import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.client.render.firmament.FirmamentTextureStorage;

@Mixin(SectionRenderDispatcher.RenderSection.class)
public class RenderSectionMixin {

    @Shadow @Final BlockPos.MutableBlockPos origin;

    @Inject(method = "setNotDirty", at = @At("HEAD"))
    private void operation_starcleave$onRebuild(CallbackInfo ci) {
        FirmamentTextureStorage.getInstance().queueRebuild(origin);
    }
}

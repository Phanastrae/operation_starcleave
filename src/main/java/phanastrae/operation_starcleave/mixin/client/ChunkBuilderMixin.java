package phanastrae.operation_starcleave.mixin.client;

import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.client.render.firmament.FirmamentTextureStorage;

@Mixin(ChunkBuilder.BuiltChunk.class)
public class ChunkBuilderMixin {

    @Shadow @Final BlockPos.Mutable origin;

    @Inject(method = "cancelRebuild", at = @At("HEAD"))
    private void operation_starcleave$onRebuild(CallbackInfo ci) {
        FirmamentTextureStorage.getInstance().queueRebuild(origin);
    }
}

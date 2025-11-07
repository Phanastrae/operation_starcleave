package phanastrae.operation_starcleave.mixin.client.renderer;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.client.compat.ClientCompat;
import phanastrae.operation_starcleave.client.duck.LevelRendererDuck;

// mixins here have high priority, since most of them need to be applied after Overwrites
@Mixin(value = LevelRenderer.class, priority = 5000)
public class LevelRendererPriorityMixin {
    // vanilla has 7 different dirty-setting functions, but they all eventually call setSectionDirty(IIIZ)V
    // setSectionDirty(IIIZ)V is private and only called by setBlockDirty(pos;Z)V and setSectionDirty(III)V,
    // so we should expect exactly one of these to get called, and can target them separately

    // sodium overwrites 4 of these methods.
    // one of these overwrites is setSectionDirty(IIIZ)V, which we can ignore
    // one of these overwrites is setBlockDirty(pos;Z)V, so we just keep the mixin there
    // the other two methods would normally lead to setSectionDirty(III)V but are now unable,
    // so we need to target them separately when sodium is installed

    // 6)
    @Inject(method = "setSectionDirty(III)V", at = @At("HEAD"))
    private void operation_starcleave$setSectionDirty(int sectionX, int sectionY, int sectionZ, CallbackInfo ci) {
        // sodium does NOT overwrite this method
        ((LevelRendererDuck) this).operation_starcleave$getFirmamentTextureStorage().queueRebuildForSection(sectionX, sectionZ);
    }

    // 2)
    @Inject(method = "setBlockDirty(Lnet/minecraft/core/BlockPos;Z)V", at = @At("HEAD"))
    private void operation_starcleave$setBlockDirty(BlockPos pos, boolean reRenderOnMainThread, CallbackInfo ci) {
        // sodium DOES overwrites this method, but we want to apply the following code either way
        ((LevelRendererDuck) this).operation_starcleave$getFirmamentTextureStorage().queueRebuild(pos);
    }

    // 3)
    @Inject(method = "setBlocksDirty", at = @At("HEAD"))
    private void operation_starcleave$setBlocksDirty(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, CallbackInfo ci) {
        // sodium DOES overwrite this method, AND blocks access to setSectionDirty(III)V, so we need to handle things here instead
        if (ClientCompat.SODIUM_LOADED) {
            // in vanilla this is only called with minX,Y,Z = maxX,Y,Z, so we check for this first
            if (minX == maxX && minZ == maxZ) {
                // we don't actually need to check y here so we don't
                ((LevelRendererDuck) this).operation_starcleave$getFirmamentTextureStorage().queueRebuild(minX, minZ);
            } else {
                ((LevelRendererDuck) this).operation_starcleave$getFirmamentTextureStorage().queueRebuild(minX, minZ, maxX, maxZ);
            }
        }
    }

    // 5)
    @Inject(method = "setSectionDirtyWithNeighbors", at = @At("HEAD"))
    private void operation_starcleave$setSectionDirtyWithNeighbours(int sectionX, int sectionY, int sectionZ, CallbackInfo ci) {
        // sodium DOES overwrite this method, AND blocks access to setSectionDirty(III)V, so we need to handle things here instead
        if (ClientCompat.SODIUM_LOADED) {
            // only update this section and not the neighbours, as they probably won't have a heightmap change
            ((LevelRendererDuck) this).operation_starcleave$getFirmamentTextureStorage().queueRebuildForSection(sectionX, sectionZ);
        }
    }
}

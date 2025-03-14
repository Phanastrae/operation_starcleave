package phanastrae.operation_starcleave.mixin;

import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.world.firmament.Firmament;
import phanastrae.operation_starcleave.world.firmament.ServerFirmamentRegionManager;

@Mixin(ServerChunkCache.class)
public class ServerChunkCacheMixin {

    @Shadow @Final private ServerLevel level;

    @Inject(method = "save", at = @At("RETURN"))
    private void operation_starcleave$saveFirmament(boolean flush, CallbackInfo ci) {
        Firmament firmament = Firmament.fromLevel(this.level);
        if(firmament != null) {
            if(firmament.getFirmamentRegionManager() instanceof ServerFirmamentRegionManager serverFirmamentRegionManager) {
                serverFirmamentRegionManager.saveAll();
            }
        }
    }
}

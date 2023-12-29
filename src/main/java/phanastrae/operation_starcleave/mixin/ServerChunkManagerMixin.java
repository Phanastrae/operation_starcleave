package phanastrae.operation_starcleave.mixin;

import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.world.firmament.Firmament;
import phanastrae.operation_starcleave.world.firmament.FirmamentStorage;
import phanastrae.operation_starcleave.world.firmament.ServerFirmamentRegionManager;

@Mixin(ServerChunkManager.class)
public class ServerChunkManagerMixin {

    @Shadow @Final ServerWorld world;

    @Inject(method = "save", at = @At("RETURN"))
    private void operation_starcleave$saveFirmament(boolean flush, CallbackInfo ci) {
        Firmament firmament = Firmament.fromWorld(this.world);
        if(firmament != null) {
            if(firmament.getFirmamentRegionManager() instanceof ServerFirmamentRegionManager serverFirmamentRegionManager) {
                serverFirmamentRegionManager.saveAll();
            }
        }
    }
}

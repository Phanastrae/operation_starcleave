package phanastrae.operation_starcleave.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.world.firmament.FirmamentRegionsWatched;
import phanastrae.operation_starcleave.world.firmament.FirmamentWatcher;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements FirmamentWatcher {

    private FirmamentRegionsWatched operation_starcleave$watched_regions;


    @Inject(method = "<init>", at = @At("RETURN"))
    private void operation_starcleave$onInit(MinecraftServer server, ServerWorld world, GameProfile profile, SyncedClientOptions clientOptions, CallbackInfo ci) {
        this.operation_starcleave$watched_regions = new FirmamentRegionsWatched((ServerPlayerEntity)(Object)this);
    }

    @Override
    public FirmamentRegionsWatched operation_starcleave$getWatchedRegions() {
        return this.operation_starcleave$watched_regions;
    }
}

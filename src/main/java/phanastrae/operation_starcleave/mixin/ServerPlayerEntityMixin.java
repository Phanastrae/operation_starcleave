package phanastrae.operation_starcleave.mixin;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.TeleportTarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.operation_starcleave.duck.EntityDuck;
import phanastrae.operation_starcleave.network.packet.EntityPhlogisticFirePayload;
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

    @Inject(method = "teleportTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;sendStatusEffects(Lnet/minecraft/server/network/ServerPlayerEntity;)V", shift = At.Shift.AFTER))
    private void operation_starcleave$onPlayerWorldMove(TeleportTarget teleportTarget, CallbackInfoReturnable<Entity> cir) {
        ServerPlayerEntity player = (ServerPlayerEntity)(Object)this;
        if(((EntityDuck)player).operation_starcleave$getPhlogisticFireTicks() > 0) {
            ServerPlayNetworking.send(player, new EntityPhlogisticFirePayload(player.getId(), true));
        }
    }

    @Inject(method = "onDeath", at = @At("RETURN"))
    private void operation_starcleave$onDeath(DamageSource damageSource, CallbackInfo ci) {
        ((EntityDuck)this).operation_starcleave$setPhlogisticFireTicks(0);
        ((EntityDuck)this).operation_starcleave$setOnPhlogisticFire(false);
    }
}

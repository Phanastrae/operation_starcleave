package phanastrae.operation_starcleave.mixin;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.operation_starcleave.block.PhlogisticFireBlock;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntity;
import phanastrae.operation_starcleave.network.packet.s2c.EntityPhlogisticFireS2CPacket;
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

    @Inject(method = "moveToWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V", ordinal = 5, shift = At.Shift.BEFORE))
    private void operation_starcleave$onPlayerWorldMove(ServerWorld destination, CallbackInfoReturnable<Entity> cir) {
        ServerPlayerEntity player = (ServerPlayerEntity)(Object)this;
        if(((OperationStarcleaveEntity)player).operation_starcleave$getPhlogisticFireTicks() > 0)
        ServerPlayNetworking.send(player, new EntityPhlogisticFireS2CPacket(player, true));
    }

    @Inject(method = "onDeath", at = @At("RETURN"))
    private void operation_starcleave$onDeath(DamageSource damageSource, CallbackInfo ci) {
        ((OperationStarcleaveEntity)this).operation_starcleave$setPhlogisticFireTicks(0);
        ((OperationStarcleaveEntity)this).operation_starcleave$setOnPhlogisticFire(false);
    }
}

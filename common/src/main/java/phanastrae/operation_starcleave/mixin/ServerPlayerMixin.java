package phanastrae.operation_starcleave.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityAttachment;
import phanastrae.operation_starcleave.world.firmament.FirmamentRegionsWatched;
import phanastrae.operation_starcleave.world.firmament.FirmamentWatcher;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player implements FirmamentWatcher {
    public ServerPlayerMixin(Level level, BlockPos pos, float yRot, GameProfile gameProfile) {
        super(level, pos, yRot, gameProfile);
    }

    @Unique
    private FirmamentRegionsWatched operation_starcleave$watched_regions;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void operation_starcleave$onInit(MinecraftServer server, ServerLevel world, GameProfile profile, ClientInformation clientOptions, CallbackInfo ci) {
        this.operation_starcleave$watched_regions = new FirmamentRegionsWatched((ServerPlayer)(Object)this);
    }

    @Inject(method = "restoreFrom", at = @At("RETURN"))
    private void mirthdew_encore$restoreFrom(ServerPlayer oldPlayer, boolean alive, CallbackInfo ci) {
        OperationStarcleaveEntityAttachment.fromEntity(this).restoreFromOldServerPlayer(oldPlayer, alive);
    }

    @Inject(method = "changeDimension", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;sendActivePlayerEffects(Lnet/minecraft/server/level/ServerPlayer;)V", shift = At.Shift.AFTER))
    private void operation_starcleave$afterChangingDimension(DimensionTransition teleportTarget, CallbackInfoReturnable<Entity> cir) {
        ServerPlayer player = (ServerPlayer)(Object)this;
        OperationStarcleaveEntityAttachment.fromEntity(this).afterServerPlayerChangingDimension(player);
    }

    @Inject(method = "die", at = @At("RETURN"))
    private void operation_starcleave$onDeath(DamageSource damageSource, CallbackInfo ci) {
        OperationStarcleaveEntityAttachment.fromEntity(this).onServerPlayerDeath();
    }

    @Override
    public FirmamentRegionsWatched operation_starcleave$getWatchedRegions() {
        return this.operation_starcleave$watched_regions;
    }
}

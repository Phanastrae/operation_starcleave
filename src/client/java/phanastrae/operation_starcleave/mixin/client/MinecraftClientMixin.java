package phanastrae.operation_starcleave.mixin.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.operation_starcleave.OperationStarcleaveClient;
import phanastrae.operation_starcleave.network.packet.c2s.AttackFirmamentTileC2SPacket;
import phanastrae.operation_starcleave.world.firmament.FirmamentTilePos;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Shadow @Nullable public ClientPlayerEntity player;

    @Inject(method = "doAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/HitResult;getType()Lnet/minecraft/util/hit/HitResult$Type;", shift = At.Shift.BEFORE), cancellable = true)
    private void operation_starcleave$handleFirmamentHit(CallbackInfoReturnable<Boolean> cir) {
        FirmamentTilePos tile = OperationStarcleaveClient.FirmamentOutlineRenderer.hitTile;
        if(tile != null) {
            ClientPlayNetworking.send(new AttackFirmamentTileC2SPacket(tile));
            this.player.swingHand(Hand.MAIN_HAND);
            cir.setReturnValue(false);
        }
    }
}

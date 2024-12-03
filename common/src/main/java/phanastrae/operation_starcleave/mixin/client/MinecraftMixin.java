package phanastrae.operation_starcleave.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.operation_starcleave.client.OperationStarcleaveClient;
import phanastrae.operation_starcleave.client.services.XPlatClientInterface;
import phanastrae.operation_starcleave.network.packet.AttackFirmamentTilePayload;
import phanastrae.operation_starcleave.world.firmament.FirmamentTilePos;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Shadow @Nullable public LocalPlayer player;

    @Inject(method = "startAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/HitResult;getType()Lnet/minecraft/world/phys/HitResult$Type;", shift = At.Shift.BEFORE), cancellable = true)
    private void operation_starcleave$handleFirmamentHit(CallbackInfoReturnable<Boolean> cir) {
        FirmamentTilePos tile = OperationStarcleaveClient.firmamentOutlineRenderer.hitTile;
        if(tile != null) {
            XPlatClientInterface.INSTANCE.sendPayload(new AttackFirmamentTilePayload(tile.tileX, tile.tileZ));
            this.player.swing(InteractionHand.MAIN_HAND);
            cir.setReturnValue(false);
        }
    }
}

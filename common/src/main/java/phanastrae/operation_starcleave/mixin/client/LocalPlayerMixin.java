package phanastrae.operation_starcleave.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.block.StellarRepulsorBlock;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityAttachment;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {
    public LocalPlayerMixin(ClientLevel clientLevel, GameProfile gameProfile) {
        super(clientLevel, gameProfile);
    }

    @Shadow @Nullable
    public abstract PlayerRideableJumping jumpableVehicle();

    @Shadow @Final public ClientPacketListener connection;

    @Shadow public Input input;

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/PlayerRideableJumping;getJumpCooldown()I"))
    private void operation_starcleave$pegasusControls(CallbackInfo ci, @Local(ordinal = 0) boolean wasJumping) {
        PlayerRideableJumping vehicle = this.jumpableVehicle();

        if(vehicle instanceof AbstractHorse horse) {
            OperationStarcleaveEntityAttachment osea = OperationStarcleaveEntityAttachment.fromEntity(horse);
            if(osea.isPegasus()) {
                if (wasJumping && !this.input.jumping) {
                    // stop flying
                    OperationStarcleaveEntityAttachment.fromEntity(horse).setPegasusFlying(false);
                    this.connection
                            .send(
                                    new ServerboundPlayerCommandPacket((LocalPlayer) (Object) this, ServerboundPlayerCommandPacket.Action.STOP_RIDING_JUMP, Mth.floor(0))
                            );
                } else if (this.input.jumping) {
                    if(osea.getPegasusFlightCharge() > 0.1F) {
                        // start flying
                        StellarRepulsorBlock.tryLaunch(horse);
                        vehicle.onPlayerJump(Mth.floor(100));
                        this.connection
                                .send(
                                        new ServerboundPlayerCommandPacket(this, ServerboundPlayerCommandPacket.Action.START_RIDING_JUMP, Mth.floor(100))
                                );
                    }
                }
            }
        }
    }
}

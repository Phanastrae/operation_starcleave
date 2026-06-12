package phanastrae.operation_starcleave.fabric.mixin.common.fabric;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorageUtil;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.operation_starcleave.fluid.OperationStarcleaveFluids;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;

@Mixin(FluidStorageUtil.class)
public class FluidStorageUtilMixin {
    // the forbidden "mixing into Fabric" technique
    @Inject(method = "moveWithSound", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/transfer/v1/fluid/FluidVariant;isOf(Ljava/lang/Object;)Z"))
    private static void operation_starcleave$handleCustomSounds(Storage<FluidVariant> from, Storage<FluidVariant> _to, Player player, boolean fill, Item handItem, CallbackInfoReturnable<Boolean> cir, @Local FluidVariant resource, @Local LocalRef<SoundEvent> soundRef) {
        if (resource.isOf(OperationStarcleaveFluids.STARBLEACH)) {
            if (fill && handItem == Items.GLASS_BOTTLE) soundRef.set(SoundEvents.BOTTLE_FILL);
            if (!fill && handItem == OperationStarcleaveItems.STARBLEACH_BOTTLE) soundRef.set(SoundEvents.BOTTLE_EMPTY);
        }
    }

    @Inject(method = "moveWithSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"))
    private static void operation_starcleave$playBucketBreakingSounds(Storage<FluidVariant> from, Storage<FluidVariant> _to, Player player, boolean fill, Item handItem, CallbackInfoReturnable<Boolean> cir, @Local FluidVariant resource) {
        // if draining a plasma bucket, play the sound
        if (!fill && handItem == OperationStarcleaveItems.PETRICHORIC_PLASMA_BUCKET && !player.getAbilities().instabuild) {
            player.level().playSound(
                    null,
                    player.getX(), player.getEyeY(), player.getZ(),
                    Items.IRON_PICKAXE.getBreakingSound(),
                    SoundSource.PLAYERS,
                    0.8F,
                    1.0F
            );
        }
    }
}

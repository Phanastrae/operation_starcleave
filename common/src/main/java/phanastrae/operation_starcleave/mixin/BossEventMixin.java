package phanastrae.operation_starcleave.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.world.BossEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.duck.BossEventDuck;
import phanastrae.operation_starcleave.world.BossEventExtras;

import java.util.UUID;

@Mixin(BossEvent.class)
public abstract class BossEventMixin implements BossEventDuck {

    @Unique
    private BossEventExtras operation_starcleave$extras;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void operation_starcleave$init(UUID id, Component name, BossEvent.BossBarColor color, BossEvent.BossBarOverlay overlay, CallbackInfo ci) {
        BossEvent event = (BossEvent) (Object) this;
        this.operation_starcleave$extras = new BossEventExtras(event);
    }

    @Override
    public BossEventExtras operation_starcleave$getExtras() {
        return this.operation_starcleave$extras;
    }
}
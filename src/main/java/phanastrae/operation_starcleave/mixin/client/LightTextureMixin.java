package phanastrae.operation_starcleave.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import phanastrae.operation_starcleave.duck.WorldDuck;

@Mixin(LightTexture.class)
public class LightTextureMixin {

    @Shadow @Final private Minecraft minecraft;

    @ModifyVariable(method = "updateLightTexture", at = @At(value = "STORE"), ordinal = 1)
    private float operation_starcleave$cleavingFlash(float value) {
        ClientLevel clientWorld = this.minecraft.level;
        if(((WorldDuck)clientWorld).operation_starcleave$getCleavingFlashTicksLeft() > 0) {
            return 1.0F;
        }
        return value;
    }
}

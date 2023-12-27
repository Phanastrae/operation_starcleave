package phanastrae.operation_starcleave.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import phanastrae.operation_starcleave.world.OperationStarcleaveWorld;

@Mixin(LightmapTextureManager.class)
public class LightmapTextureManagerMixin {

    @Final
    @Shadow
    private MinecraftClient client;

    @ModifyVariable(method = "update", at = @At(value = "STORE"), ordinal = 1)
    private float operation_starcleave$cleavingFlash(float value) {
        ClientWorld clientWorld = this.client.world;
        if(((OperationStarcleaveWorld)clientWorld).operation_starcleave$getCleavingFlashTicksLeft() > 0) {
            return 1.0F;
        }
        return value;
    }
}

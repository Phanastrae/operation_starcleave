package phanastrae.operation_starcleave.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import phanastrae.operation_starcleave.client.OperationStarcleaveClient;
import phanastrae.operation_starcleave.world.firmament.Firmament;
import phanastrae.operation_starcleave.world.firmament.FirmamentTilePos;

import java.util.List;

@Mixin(DebugHud.class)
public class DebugHudMixin {
    @Inject(method = "getRightText", at = @At(value = "RETURN"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void operation_starcleave$targetedFirmamentTile(CallbackInfoReturnable<List<String>> cir, long l, long m, long n, long o, List list) {
        MinecraftClient client = MinecraftClient.getInstance();
        if(!client.hasReducedDebugInfo()) {
            Firmament firmament = Firmament.fromWorld(client.world);
            if(firmament == null) return;

            FirmamentTilePos tile = OperationStarcleaveClient.firmamentOutlineRenderer.hitTile;
            if (tile != null) {
                list.add("");
                list.add(Formatting.UNDERLINE + "Targeted Firmament Tile");
                list.add("Damage:" + firmament.getDamage(tile.blockX, tile.blockZ));
            }
        }
    }
}

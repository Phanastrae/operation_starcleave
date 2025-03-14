package phanastrae.operation_starcleave.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import phanastrae.operation_starcleave.client.OperationStarcleaveClient;
import phanastrae.operation_starcleave.world.firmament.Firmament;
import phanastrae.operation_starcleave.world.firmament.FirmamentTilePos;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.DebugScreenOverlay;

@Mixin(DebugScreenOverlay.class)
public class DebugScreenOverlayMixin {
    @Inject(method = "getSystemInformation", at = @At(value = "RETURN"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void operation_starcleave$targetedFirmamentTile(CallbackInfoReturnable<List<String>> cir, long l, long m, long n, long o, List list) {
        Minecraft client = Minecraft.getInstance();
        if(!client.showOnlyReducedInfo()) {
            Firmament firmament = Firmament.fromLevel(client.level);
            if(firmament == null) return;

            FirmamentTilePos tile = OperationStarcleaveClient.firmamentOutlineRenderer.hitTile;
            if (tile != null) {
                list.add("");
                list.add(ChatFormatting.UNDERLINE + "Targeted Firmament Tile");
                list.add("Damage:" + firmament.getDamage(tile.blockX, tile.blockZ));
            }
        }
    }
}

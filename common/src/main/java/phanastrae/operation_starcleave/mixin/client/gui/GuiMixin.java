package phanastrae.operation_starcleave.mixin.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityAttachment;
import phanastrae.operation_starcleave.item.BismuthBlasterItem;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Shadow
    @Final
    private Minecraft minecraft;
    @Shadow
    @Final
    private static ResourceLocation PUMPKIN_BLUR_LOCATION;
    @Shadow
    @Final
    private static ResourceLocation JUMP_BAR_COOLDOWN_SPRITE;

    @Unique
    private static final ResourceLocation CROSSHAIR_AMMO_BACKGROUND = OperationStarcleave.id("hud/crosshair_ammo_background");
    @Unique
    private static final ResourceLocation CROSSHAIR_AMMO_FULL = OperationStarcleave.id("hud/crosshair_ammo_filled");

    @Shadow
    protected abstract void renderTextureOverlay(GuiGraphics guiGraphics, ResourceLocation shaderLocation, float alpha);

    @Inject(method = "renderCameraOverlays", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;getArmor(I)Lnet/minecraft/world/item/ItemStack;", ordinal = 0, shift = At.Shift.BEFORE))
    private void operation_starcleave$netheritePumpkinOverlay(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci) {
        ItemStack itemStack = this.minecraft.player.getInventory().getArmor(3);
        if (itemStack.is(OperationStarcleaveBlocks.NETHERITE_PUMPKIN.asItem())) {
            this.renderTextureOverlay(context, PUMPKIN_BLUR_LOCATION, 1.0F);
        }
    }

    @Inject(method = "renderJumpMeter", at = @At("HEAD"), cancellable = true)
    private void operation_starcleave$pegasusFlightMeter(PlayerRideableJumping rideable, GuiGraphics guiGraphics, int x, CallbackInfo ci) {
        if (rideable instanceof AbstractHorse horse) {
            OperationStarcleaveEntityAttachment osea = OperationStarcleaveEntityAttachment.fromEntity(horse);
            if (osea.isPegasus()) {
                this.minecraft.getProfiler().push("jumpBar");
                float flightScale = osea.getPegasusFlightCharge();
                int textureWidth = 182;
                int uWidth = (int) (flightScale * 183.0F);
                int y = guiGraphics.guiHeight() - 32 + 3;

                RenderSystem.enableBlend();
                guiGraphics.blitSprite(OperationStarcleave.id("hud/pegasus_flight_bar_background"), x, y, textureWidth, 5);
                if (rideable.getJumpCooldown() > 0) {
                    guiGraphics.blitSprite(JUMP_BAR_COOLDOWN_SPRITE, x, y, textureWidth, 5);
                } else if (uWidth > 0) {
                    guiGraphics.blitSprite(OperationStarcleave.id("hud/pegasus_flight_bar_progress"), textureWidth, 5, 0, 0, x, y, uWidth, 5);
                }
                RenderSystem.disableBlend();

                this.minecraft.getProfiler().pop();

                ci.cancel();
            }
        }
    }

    @Inject(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;", ordinal = 1))
    private void operation_starcleave$renderAmmo(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        Player player = this.minecraft.player;
        if (this.minecraft.gameMode == null || player == null) return;

        if (this.minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR) {
            ItemStack item = player.getMainHandItem();
            ItemStack altItem = player.getOffhandItem();

            ItemStack blasterStack;
            if (item.is(OperationStarcleaveItems.BISMUTH_BLASTER)) {
                blasterStack = item;
            } else if (altItem.is(OperationStarcleaveItems.BISMUTH_BLASTER)) {
                blasterStack = altItem;
            } else {
                return;
            }

            int maxAmmoCount = BismuthBlasterItem.getStoreSize();
            int currentAmmoCount = BismuthBlasterItem.currentStoredAmmo(blasterStack);

            int width = 8;
            int spacingWidth = 10;
            int height = 8;
            int x = (guiGraphics.guiWidth() - width) / 2 - spacingWidth * (maxAmmoCount - 1) / 2;
            int y = (guiGraphics.guiHeight() - height) / 2 + 24;

            for (int i = 0; i < maxAmmoCount || i < currentAmmoCount; i++) {
                guiGraphics.blitSprite(CROSSHAIR_AMMO_BACKGROUND, x + i * spacingWidth, y, width, height);
            }

            for (int i = 0; i < currentAmmoCount; i++) {
                guiGraphics.blitSprite(CROSSHAIR_AMMO_FULL, x + i * spacingWidth, y, width, height);
            }
        }
    }
}

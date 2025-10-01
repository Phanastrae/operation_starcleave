package phanastrae.operation_starcleave.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.operation_starcleave.client.duck.BossHealthOverlayDuck;
import phanastrae.operation_starcleave.network.packet.ClientboundBossEventExtrasPayload;
import phanastrae.operation_starcleave.world.BossEventExtras;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

@Mixin(BossHealthOverlay.class)
public abstract class BossHealthOverlayMixin implements BossHealthOverlayDuck {
    @Shadow
    @Final
    private static ResourceLocation[] BAR_BACKGROUND_SPRITES;
    @Shadow
    @Final
    private static ResourceLocation[] BAR_PROGRESS_SPRITES;

    @Shadow
    @Final
    Map<UUID, LerpingBossEvent> events;

    @Unique
    private static final int BASE_WIDTH = 182;
    @Unique
    private static final int MINI_WIDTH = 90;

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Ljava/util/Map;values()Ljava/util/Collection;", ordinal = 0))
    private Collection<LerpingBossEvent> operationStarcleave$sortMiniBarsToBottom(Map<UUID, LerpingBossEvent> instance, Operation<Collection<LerpingBossEvent>> original) {
        // call original to get normal output
        Collection<LerpingBossEvent> collection = original.call(instance);

        // check if any custom starcleave boss bars are present
        boolean anyCustom = false;
        for (LerpingBossEvent event : collection) {
            BossEventExtras extras = BossEventExtras.fromEvent(event);
            if (extras.isMini()) {
                anyCustom = true;
                break;
            }
        }

        if (!anyCustom) {
            // do normal behaviour
            return collection;
        } else {
            // sort mini bars to bottom
            // return sorted list for the collection
            return collection.stream().sorted((event1, event2) -> {
                BossEventExtras extras1 = BossEventExtras.fromEvent(event1);
                int i = extras1.isMini() ? 1 : 0;
                BossEventExtras extras2 = BossEventExtras.fromEvent(event2);
                int j = extras2.isMini() ? 1 : 0;
                return (i - j);
            }).toList();
        }
    }

    @Inject(method = "drawBar(Lnet/minecraft/client/gui/GuiGraphics;IILnet/minecraft/world/BossEvent;)V", at = @At("HEAD"), cancellable = true)
    private void operation_starcleave$drawCustomBar(GuiGraphics guiGraphics, int x, int y, BossEvent bossEvent, CallbackInfo ci) {
        if (BossEventExtras.fromEvent(bossEvent).isMini()) {
            int width = MINI_WIDTH;
            int offset = (BASE_WIDTH - width) / 2;
            this.operation_starcleave$drawMiniBar(guiGraphics, x + offset, y, bossEvent, width, BAR_BACKGROUND_SPRITES);
            int progress = Mth.lerpDiscrete(bossEvent.getProgress(), 0, width);
            if (progress > 0) {
                this.operation_starcleave$drawMiniBar(guiGraphics, x + offset, y, bossEvent, progress, BAR_PROGRESS_SPRITES);
            }

            ci.cancel();
        }
    }

    @Unique
    private void operation_starcleave$drawMiniBar(
            GuiGraphics guiGraphics, int x, int y, BossEvent bossEvent, int width, ResourceLocation[] barProgressSprites
    ) {
        RenderSystem.enableBlend();
        int leftWidth = Math.min(width, MINI_WIDTH / 2);
        int rightWidth = Math.max(width - leftWidth, 0);
        ResourceLocation sprite = barProgressSprites[bossEvent.getColor().ordinal()];
        if (leftWidth > 0) {
            // draw left slice of boss bar of size halfWidth
            guiGraphics.blitSprite(sprite, BASE_WIDTH, 5, 0, 0, x, y, leftWidth, 5);
        }
        if (rightWidth > 0) {
            // draw right slice of boss bar of size (width - halfWidth)
            guiGraphics.blitSprite(sprite, BASE_WIDTH, 5, BASE_WIDTH - MINI_WIDTH + leftWidth, 0, x + leftWidth, y, rightWidth, 5);
        }

        // overlays are not (currently) supported, act as if all bars just use the PROGRESS setting

        RenderSystem.disableBlend();
    }

    @Override
    public void operation_starcleave$updateExtras(ClientboundBossEventExtrasPayload payload) {
        UUID uuid = payload.id();
        if (this.events.containsKey(uuid)) {
            BossEvent bossEvent = this.events.get(uuid);
            BossEventExtras extras = BossEventExtras.fromEvent(bossEvent);

            payload.dispatch(new ClientboundBossEventExtrasPayload.Handler() {
                @Override
                public void add(boolean isMini) {
                    extras.setMini(isMini);
                }

                @Override
                public void updateBonusProperties(boolean isMini) {
                    extras.setMini(isMini);
                }
            });
        }
    }
}

package phanastrae.operation_starcleave.client.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import phanastrae.operation_starcleave.client.duck.BossHealthOverlayDuck;
import phanastrae.operation_starcleave.client.render.ScreenShakeManager;
import phanastrae.operation_starcleave.client.render.firmament.FirmamentTextureStorage;
import phanastrae.operation_starcleave.client.world.firmament.ClientFirmamentRegionManager;
import phanastrae.operation_starcleave.client.world.firmament.FirmamentDamageGlowActor;
import phanastrae.operation_starcleave.duck.LevelDuckInterface;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityAttachment;
import phanastrae.operation_starcleave.entity.projectile.StarbleachedPearlEntity;
import phanastrae.operation_starcleave.network.packet.*;
import phanastrae.operation_starcleave.sound.OperationStarcleaveSoundEvents;
import phanastrae.operation_starcleave.world.firmament.Firmament;
import phanastrae.operation_starcleave.world.firmament.FirmamentRegion;
import phanastrae.operation_starcleave.world.firmament.FirmamentRegionHolder;
import phanastrae.operation_starcleave.world.firmament.FirmamentSubRegion;

public class OperationStarcleaveClientPacketHandler {

    public static void receiveFirmamentRegionData(FirmamentRegionDataPayload payload, Player player) {
        Level level = player.level();
        Firmament firmament = Firmament.fromLevel(level);
        if (firmament != null) {
            FirmamentRegion firmamentRegion = firmament.getFirmamentRegion(payload.regionId());
            if (firmamentRegion == null) {
                if (firmament.getFirmamentRegionManager() instanceof ClientFirmamentRegionManager clientFirmamentRegionManager) {
                    FirmamentRegionHolder firmamentRegionHolder = clientFirmamentRegionManager.loadRegion(payload.regionId());
                    firmamentRegion = firmamentRegionHolder.getFirmamentRegion();
                }
            }

            if (firmamentRegion != null) {
                firmamentRegion.readFromData(payload.firmamentRegionData());
                FirmamentTextureStorage.getMainInstance().onRegionAdded(firmamentRegion, level);
            }
        }
    }

    public static void updateFirmamentSubRegion(UpdateFirmamentSubRegionPayload payload, Player player) {
        Level level = player.level();
        Firmament firmament = Firmament.fromLevel(level);
        if (firmament != null) {
            FirmamentSubRegion firmamentSubRegion = firmament.getSubRegionFromId(payload.id());

            if (firmamentSubRegion != null) {
                firmamentSubRegion.readFromData(payload.subRegionData());

                FirmamentTextureStorage.getMainInstance().onSubRegionUpdated(firmamentSubRegion, level);
            }
        }
    }

    public static void unloadFirmamentRegion(UnloadFirmamentRegionPayload payload, Player player) {
        Firmament firmament = Firmament.fromLevel(player.level());
        if (firmament != null) {
            if (firmament.getFirmamentRegionManager() instanceof ClientFirmamentRegionManager clientFirmamentRegionManager) {
                clientFirmamentRegionManager.unloadRegion(payload.regionId());
            }
        }
    }

    public static void onFirmamentCleaved(FirmamentCleavedPayload payload, Player player) {
        Level level = player.level();
        Firmament firmament = Firmament.fromLevel(level);
        if(firmament == null) {
            return;
        }

        Vec3 pos = new Vec3(payload.x(), firmament.getY(), payload.z());

        ((LevelDuckInterface) level).operation_starcleave$setCleavingFlashTicksLeft(24);

        level.playLocalSound(
                pos.x,
                pos.y,
                pos.z,
                OperationStarcleaveSoundEvents.FIRMAMENT_CLEAVE,
                SoundSource.BLOCKS,
                500.0F,
                1.6F + level.random.nextFloat() * 0.2F,
                false);

        level.addAlwaysVisibleParticle(ParticleTypes.FLASH, pos.x, pos.y - 1, pos.z, 0, 0, 0);

        ScreenShakeManager.getInstance().setShakeAmount(3);

        firmament.addActor(new FirmamentDamageGlowActor(firmament, (int) pos.x, (int) pos.z));
    }

    public static void onStarbleachedPearlLaunch(StarbleachedPearlLaunchPayload payload, Player player) {
        Entity except = null;
        if (payload.exceptExists()) {
            Entity e = player.level().getEntity(payload.exceptId());
            if (e != null) {
                except = e;
            }
        }
        StarbleachedPearlEntity.doRepulsion(payload.pos(), payload.radius(), payload.maxAddedSpeed(), player.level(), except);
    }

    public static void handleEntityPhlogisticFire(EntityPhlogisticFirePayload payload, Player player) {
        Level level = player.level();
        Entity entity = level.getEntity(payload.id());
        if (entity != null) {
            OperationStarcleaveEntityAttachment.fromEntity(entity).setOnPhlogisticFire(payload.onPhlogisticFire());
        }
    }

    public static void handleEntityPegasusGliding(EntityPegasusGlidingPayload payload, Player player) {
        Level level = player.level();
        Entity entity = level.getEntity(payload.id());
        if (entity != null) {
            OperationStarcleaveEntityAttachment.fromEntity(entity).setPegasusGliding(payload.pegasusGliding());
        }
    }

    public static void handleEntityPegasusFlying(EntityPegasusFlyingPayload payload, Player player) {
        Level level = player.level();
        Entity entity = level.getEntity(payload.id());
        if (entity != null) {
            OperationStarcleaveEntityAttachment.fromEntity(entity).setPegasusFlying(payload.pegasusFlying());
        }
    }

    public static void handleBossExtrasPayload(ClientboundBossEventExtrasPayload payload, Player player) {
        Minecraft minecraft = Minecraft.getInstance();
        BossHealthOverlay bossHealthOverlay = minecraft.gui.getBossOverlay();
        ((BossHealthOverlayDuck) bossHealthOverlay).operation_starcleave$updateExtras(payload);
    }
}

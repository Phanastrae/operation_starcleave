package phanastrae.operation_starcleave.client.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.multiplayer.ChunkBatchSizeCalculator;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import phanastrae.operation_starcleave.client.duck.ClientPacketListenerDuck;
import phanastrae.operation_starcleave.client.render.ScreenShakeManager;
import phanastrae.operation_starcleave.client.render.firmament.FirmamentTextureStorage;
import phanastrae.operation_starcleave.client.world.firmament.ClientFirmamentRegionManager;
import phanastrae.operation_starcleave.client.world.firmament.FirmamentDamageGlowActor;
import phanastrae.operation_starcleave.duck.EntityDuck;
import phanastrae.operation_starcleave.duck.LevelDuck;
import phanastrae.operation_starcleave.entity.projectile.StarbleachedPearlEntity;
import phanastrae.operation_starcleave.network.packet.*;
import phanastrae.operation_starcleave.world.firmament.Firmament;
import phanastrae.operation_starcleave.world.firmament.FirmamentRegion;
import phanastrae.operation_starcleave.world.firmament.FirmamentRegionHolder;
import phanastrae.operation_starcleave.world.firmament.FirmamentSubRegion;

public class OperationStarcleaveClientPacketHandler {

    public static void startFirmamentRegionSend(StartFirmamentRegionSendPayload payload, Player player) {
        if(player instanceof LocalPlayer localPlayer) {
            ((ClientPacketListenerDuck) localPlayer.connection).operation_starcleave$getFirmamentRegionBatchSizeCalculator().onBatchStart();
        }
    }

    public static void receiveFirmamentRegionData(FirmamentRegionDataPayload payload, Player player) {
        Level world = player.level();
        Firmament firmament = Firmament.fromWorld(world);
        if(firmament != null) {
            FirmamentRegion firmamentRegion = firmament.getFirmamentRegion(payload.regionId());
            if(firmamentRegion == null) {
                if(firmament.getFirmamentRegionManager() instanceof ClientFirmamentRegionManager clientFirmamentRegionManager) {
                    FirmamentRegionHolder firmamentRegionHolder = clientFirmamentRegionManager.loadRegion(payload.regionId());
                    firmamentRegion = firmamentRegionHolder.getFirmamentRegion();
                }
            }

            if(firmamentRegion != null) {
                firmamentRegion.readFromData(payload.firmamentRegionData());
                FirmamentTextureStorage.getInstance().onRegionAdded(firmamentRegion, world);
            }
        }
    }

    public static void sentFirmamentRegion(FirmamentRegionSentPayload payload, Player player) {
        if(player instanceof LocalPlayer localPlayer) {
            ChunkBatchSizeCalculator firmamentRegionBatchSizeCalculator = ((ClientPacketListenerDuck) localPlayer.connection).operation_starcleave$getFirmamentRegionBatchSizeCalculator();
            firmamentRegionBatchSizeCalculator.onBatchFinished(payload.batchSize());

            ClientPlayNetworking.send(new AcknowledgeFirmamentRegionDataPayload(firmamentRegionBatchSizeCalculator.getDesiredChunksPerTick()));
        }
    }

    public static void updateFirmamentSubRegion(UpdateFirmamentSubRegionPayload payload, Player player) {
        Level world = player.level();
        Firmament firmament = Firmament.fromWorld(world);
        if(firmament != null) {
            FirmamentSubRegion firmamentSubRegion = firmament.getSubRegionFromId(payload.id());

            if(firmamentSubRegion != null) {
                firmamentSubRegion.readFromData(payload.subRegionData());

                FirmamentTextureStorage.getInstance().onSubRegionUpdated(firmamentSubRegion, world);
            }
        }
    }

    public static void unloadFirmamentRegion(UnloadFirmamentRegionPayload payload, Player player) {
        Firmament firmament = Firmament.fromWorld(player.level());
        if(firmament != null) {
            if(firmament.getFirmamentRegionManager() instanceof ClientFirmamentRegionManager clientFirmamentRegionManager) {
                clientFirmamentRegionManager.unloadRegion(payload.regionId());
            }
        }
    }

    public static void onFirmamentCleaved(FirmamentCleavedPayload payload, Player player) {
        Level world = player.level();
        ((LevelDuck)world).operation_starcleave$setCleavingFlashTicksLeft(24);
        Vec3 pos = new Vec3(payload.x(), world.getMaxBuildHeight() + 16, payload.z());
        world.playLocalSound(
                pos.x,
                pos.y,
                pos.z,
                SoundEvents.TRIDENT_THUNDER.value(),
                SoundSource.BLOCKS,
                500.0F,
                1.6F + world.random.nextFloat() * 0.2F,
                false);

        ParticleOptions particleEffect = ParticleTypes.FLASH;
        world.addAlwaysVisibleParticle(particleEffect, pos.x, pos.y - 1, pos.z, 0, 0, 0);

        ScreenShakeManager.getInstance().setShakeAmount(3);
        Firmament firmament = Firmament.fromWorld(world);
        if(firmament != null) {
            firmament.addActor(new FirmamentDamageGlowActor(firmament, (int)pos.x, (int)pos.z));
        }
    }

    public static void onStarbleachedPearlLaunch(StarbleachedPearlLaunchPayload payload, Player player) {
        Entity except = null;
        if(payload.exceptExists()) {
            Entity e = player.level().getEntity(payload.exceptId());
            if(e != null) {
                except = e;
            }
        }
        StarbleachedPearlEntity.doRepulsion(payload.pos(), payload.radius(), payload.maxAddedSpeed(), player.level(), except);
    }

    public static void handleEntityPhlogisticFire(EntityPhlogisticFirePayload payload, Player player) {
        Level world = player.level();
        Entity entity = world.getEntity(payload.id());
        if (entity != null) {
            ((EntityDuck)entity).operation_starcleave$setOnPhlogisticFire(payload.onPhlogisticFire());
        }
    }
}

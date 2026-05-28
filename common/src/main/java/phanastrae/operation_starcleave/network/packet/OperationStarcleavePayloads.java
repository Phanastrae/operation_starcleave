package phanastrae.operation_starcleave.network.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import phanastrae.operation_starcleave.client.network.OperationStarcleaveClientPacketHandler;
import phanastrae.operation_starcleave.network.packet.c2s.AttackFirmamentTilePayload;
import phanastrae.operation_starcleave.network.packet.s2c.*;
import phanastrae.operation_starcleave.server.network.OperationStarcleaveServerPacketHandler;

import java.util.function.BiConsumer;

public class OperationStarcleavePayloads {

    public static void init(Helper helper) {
        // s2c
        // firmament data
        helper.registerS2C(FirmamentRegionDataPayload.TYPE, FirmamentRegionDataPayload.STREAM_CODEC, OperationStarcleaveClientPacketHandler::receiveFirmamentRegionData);
        helper.registerS2C(UpdateFirmamentSubRegionPayload.TYPE, UpdateFirmamentSubRegionPayload.STREAM_CODEC, OperationStarcleaveClientPacketHandler::updateFirmamentSubRegion);
        helper.registerS2C(UnloadFirmamentRegionPayload.TYPE, UnloadFirmamentRegionPayload.STREAM_CODEC, OperationStarcleaveClientPacketHandler::unloadFirmamentRegion);
        // misc
        helper.registerS2C(FirmamentCleavedPayload.TYPE, FirmamentCleavedPayload.STREAM_CODEC, OperationStarcleaveClientPacketHandler::onFirmamentCleaved);
        helper.registerS2C(StarbleachedPearlLaunchPayload.TYPE, StarbleachedPearlLaunchPayload.STREAM_CODEC, OperationStarcleaveClientPacketHandler::onStarbleachedPearlLaunch);
        helper.registerS2C(EntityPhlogisticFirePayload.TYPE, EntityPhlogisticFirePayload.STREAM_CODEC, OperationStarcleaveClientPacketHandler::handleEntityPhlogisticFire);
        helper.registerS2C(EntityPegasusGlidingPayload.TYPE, EntityPegasusGlidingPayload.STREAM_CODEC, OperationStarcleaveClientPacketHandler::handleEntityPegasusGliding);
        helper.registerS2C(EntityPegasusFlyingPayload.TYPE, EntityPegasusFlyingPayload.STREAM_CODEC, OperationStarcleaveClientPacketHandler::handleEntityPegasusFlying);
        helper.registerS2C(BossEventExtrasPayload.TYPE, BossEventExtrasPayload.STREAM_CODEC, OperationStarcleaveClientPacketHandler::handleBossExtrasPayload);

        // c2s
        helper.registerC2S(AttackFirmamentTilePayload.TYPE, AttackFirmamentTilePayload.STREAM_CODEC, OperationStarcleaveServerPacketHandler::attackFirmamentTile);
    }

    public interface Helper {
        <T extends CustomPacketPayload> void registerS2C(CustomPacketPayload.Type<T> id, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, BiConsumer<T, Player> clientCallback);

        <T extends CustomPacketPayload> void registerC2S(CustomPacketPayload.Type<T> id, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, BiConsumer<T, Player> serverCallback);
    }
}

package phanastrae.operation_starcleave.network.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import phanastrae.operation_starcleave.client.network.OperationStarcleaveClientPacketHandler;
import phanastrae.operation_starcleave.server.network.OperationStarcleaveServerPacketHandler;

import java.util.function.BiConsumer;

public class OperationStarcleavePayloads {

    public static void init(Helper helper) {
        // s2c
        // firmament data
        helper.registerS2C(StartFirmamentRegionSendPayload.PACKET_ID, StartFirmamentRegionSendPayload.PACKET_CODEC, OperationStarcleaveClientPacketHandler::startFirmamentRegionSend);
        helper.registerS2C(FirmamentRegionDataPayload.PACKET_ID, FirmamentRegionDataPayload.PACKET_CODEC, OperationStarcleaveClientPacketHandler::receiveFirmamentRegionData);
        helper.registerS2C(FirmamentRegionSentPayload.PACKET_ID, FirmamentRegionSentPayload.PACKET_CODEC, OperationStarcleaveClientPacketHandler::sentFirmamentRegion);
        helper.registerS2C(UpdateFirmamentSubRegionPayload.PACKET_ID, UpdateFirmamentSubRegionPayload.PACKET_CODEC, OperationStarcleaveClientPacketHandler::updateFirmamentSubRegion);
        helper.registerS2C(UnloadFirmamentRegionPayload.PACKET_ID, UnloadFirmamentRegionPayload.PACKET_CODEC, OperationStarcleaveClientPacketHandler::unloadFirmamentRegion);
        // misc
        helper.registerS2C(FirmamentCleavedPayload.PACKET_ID, FirmamentCleavedPayload.PACKET_CODEC, OperationStarcleaveClientPacketHandler::onFirmamentCleaved);
        helper.registerS2C(StarbleachedPearlLaunchPayload.PACKET_ID, StarbleachedPearlLaunchPayload.PACKET_CODEC, OperationStarcleaveClientPacketHandler::onStarbleachedPearlLaunch);
        helper.registerS2C(EntityPhlogisticFirePayload.PACKET_ID, EntityPhlogisticFirePayload.PACKET_CODEC, OperationStarcleaveClientPacketHandler::handleEntityPhlogisticFire);

        // c2s
        helper.registerC2S(AttackFirmamentTilePayload.PACKET_ID, AttackFirmamentTilePayload.PACKET_CODEC, OperationStarcleaveServerPacketHandler::attackFirmamentTile);
        helper.registerC2S(AcknowledgeFirmamentRegionDataPayload.PACKET_ID, AcknowledgeFirmamentRegionDataPayload.PACKET_CODEC, OperationStarcleaveServerPacketHandler::acknowledgeFirmamentRegionData);
    }

    public interface Helper {
        <T extends CustomPacketPayload> void registerS2C(CustomPacketPayload.Type<T> id, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, BiConsumer<T, Player> clientCallback);
        <T extends CustomPacketPayload> void registerC2S(CustomPacketPayload.Type<T> id, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, BiConsumer<T, Player> serverCallback);
    }
}

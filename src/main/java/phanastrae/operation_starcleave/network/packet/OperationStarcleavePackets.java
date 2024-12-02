package phanastrae.operation_starcleave.network.packet;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class OperationStarcleavePackets {

    public static void init() {
        // s2c
        // firmament data
        registerS2C(StartFirmamentRegionSendPayload.PACKET_ID, StartFirmamentRegionSendPayload.PACKET_CODEC);
        registerS2C(FirmamentRegionDataPayload.PACKET_ID, FirmamentRegionDataPayload.PACKET_CODEC);
        registerS2C(FirmamentRegionSentPayload.PACKET_ID, FirmamentRegionSentPayload.PACKET_CODEC);
        registerS2C(UpdateFirmamentSubRegionPayload.PACKET_ID, UpdateFirmamentSubRegionPayload.PACKET_CODEC);
        registerS2C(UnloadFirmamentRegionPayload.PACKET_ID, UnloadFirmamentRegionPayload.PACKET_CODEC);
        // misc
        registerS2C(FirmamentCleavedPayload.PACKET_ID, FirmamentCleavedPayload.PACKET_CODEC);
        registerS2C(StarbleachedPearlLaunchPayload.PACKET_ID, StarbleachedPearlLaunchPayload.PACKET_CODEC);
        registerS2C(EntityPhlogisticFirePayload.PACKET_ID, EntityPhlogisticFirePayload.PACKET_CODEC);

        // c2s
        registerC2S(AttackFirmamentTilePayload.PACKET_ID, AttackFirmamentTilePayload.PACKET_CODEC);
        registerC2S(AcknowledgeFirmamentRegionDataPayload.PACKET_ID, AcknowledgeFirmamentRegionDataPayload.PACKET_CODEC);
    }

    public static <T extends CustomPacketPayload> void registerS2C(CustomPacketPayload.Type<T> id, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        PayloadTypeRegistry.playS2C().register(id, codec);
    }

    public static <T extends CustomPacketPayload> void registerC2S(CustomPacketPayload.Type<T> id, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        PayloadTypeRegistry.playC2S().register(id, codec);
    }
}

package phanastrae.operation_starcleave.network.packet;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

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

    public static <T extends CustomPayload> void registerS2C(CustomPayload.Id<T> id, PacketCodec<? super RegistryByteBuf, T> codec) {
        PayloadTypeRegistry.playS2C().register(id, codec);
    }

    public static <T extends CustomPayload> void registerC2S(CustomPayload.Id<T> id, PacketCodec<? super RegistryByteBuf, T> codec) {
        PayloadTypeRegistry.playC2S().register(id, codec);
    }
}

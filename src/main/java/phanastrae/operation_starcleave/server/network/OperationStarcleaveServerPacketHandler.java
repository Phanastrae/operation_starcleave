package phanastrae.operation_starcleave.server.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import phanastrae.operation_starcleave.network.packet.AcknowledgeFirmamentRegionDataPayload;
import phanastrae.operation_starcleave.network.packet.AttackFirmamentTilePayload;
import phanastrae.operation_starcleave.world.firmament.Firmament;
import phanastrae.operation_starcleave.world.firmament.FirmamentTilePos;

public class OperationStarcleaveServerPacketHandler {
    
    public static void init() {
        register(AcknowledgeFirmamentRegionDataPayload.PACKET_ID, OperationStarcleaveServerPacketHandler::acknowledgeFirmamentRegionData);
        register(AttackFirmamentTilePayload.PACKET_ID, OperationStarcleaveServerPacketHandler::attackFirmamentTile);
    }

    public static <T extends CustomPayload> boolean register(CustomPayload.Id<T> type, ServerPlayNetworking.PlayPayloadHandler<T> handler) {
        return ServerPlayNetworking.registerGlobalReceiver(type, handler);
    }

    public static void acknowledgeFirmamentRegionData(AcknowledgeFirmamentRegionDataPayload payload, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        FirmamentRegionDataSender.getFirmamentRegionDataSender(player.networkHandler).onAcknowledgeRegions(payload.desiredChunksPerTick());
    }

    public static void attackFirmamentTile(AttackFirmamentTilePayload payload, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        if(!player.getAbilities().creativeMode) return;

        World world = player.getWorld();
        if(world == null) return;
        Firmament firmament = Firmament.fromWorld(world);
        if(firmament == null) return;

        FirmamentTilePos tilePos = new FirmamentTilePos(payload.tileX(), payload.tileZ(), firmament);
        Vec3d tileCenter = tilePos.getCenter();
        double d = tileCenter.subtract(player.getEyePos()).length();
        double interactionRange = player.getAttributeBaseValue(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE);
        if(d > interactionRange + 3) return;

        if(firmament.getDamage(tilePos.blockX, tilePos.blockZ) != 0) {
            firmament.setDamage(tilePos.blockX, tilePos.blockZ, 0);
            world.playSound(null, tileCenter.x, tileCenter.y, tileCenter.z, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 0.5f, 1);
        }
    }
}

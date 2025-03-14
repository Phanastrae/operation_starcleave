package phanastrae.operation_starcleave.server.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import phanastrae.operation_starcleave.network.packet.AcknowledgeFirmamentRegionDataPayload;
import phanastrae.operation_starcleave.network.packet.AttackFirmamentTilePayload;
import phanastrae.operation_starcleave.world.firmament.Firmament;
import phanastrae.operation_starcleave.world.firmament.FirmamentTilePos;

public class OperationStarcleaveServerPacketHandler {

    public static void acknowledgeFirmamentRegionData(AcknowledgeFirmamentRegionDataPayload payload, Player player) {
        if(player instanceof ServerPlayer serverPlayer) {
            FirmamentRegionDataSender.getFirmamentRegionDataSender(serverPlayer.connection).onAcknowledgeRegions(payload.desiredChunksPerTick());
        }
    }

    public static void attackFirmamentTile(AttackFirmamentTilePayload payload, Player player) {
        if(!player.getAbilities().instabuild) return;

        Level world = player.level();
        if(world == null) return;
        Firmament firmament = Firmament.fromLevel(world);
        if(firmament == null) return;

        FirmamentTilePos tilePos = new FirmamentTilePos(payload.tileX(), payload.tileZ(), firmament);
        Vec3 tileCenter = tilePos.getCenter();
        double d = tileCenter.subtract(player.getEyePosition()).length();
        double interactionRange = player.getAttributeBaseValue(Attributes.BLOCK_INTERACTION_RANGE);
        if(d > interactionRange + 3) return;

        if(firmament.getDamage(tilePos.blockX, tilePos.blockZ) != 0) {
            firmament.setDamage(tilePos.blockX, tilePos.blockZ, 0);
            world.playSound(null, tileCenter.x, tileCenter.y, tileCenter.z, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 0.5f, 1);
        }
    }
}

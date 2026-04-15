package phanastrae.operation_starcleave.server.network;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.ChunkPos;
import phanastrae.operation_starcleave.duck.FirmamentRegionDataSenderHolder;
import phanastrae.operation_starcleave.network.packet.FirmamentRegionDataPayload;
import phanastrae.operation_starcleave.network.packet.UnloadFirmamentRegionPayload;
import phanastrae.operation_starcleave.services.XPlatInterface;
import phanastrae.operation_starcleave.world.firmament.Firmament;
import phanastrae.operation_starcleave.world.firmament.FirmamentRegion;
import phanastrae.operation_starcleave.world.firmament.data.FirmamentRegionData;
import phanastrae.operation_starcleave.world.firmament.pos.RegionPos;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class FirmamentRegionDataSender {
    private final LongSet pendingRegions = new LongOpenHashSet();

    public void add(RegionPos regionPos) {
        this.pendingRegions.add(regionPos.id);
    }

    public void unload(ServerPlayer player, RegionPos regionPos) {
        this.pendingRegions.remove(regionPos.id);
        if (player.isAlive()) {
            XPlatInterface.INSTANCE.sendPayload(player, new UnloadFirmamentRegionPayload(regionPos.id));
        }
    }

    public void sendRegions(ServerPlayer player) {
        if (!this.pendingRegions.isEmpty()) {
            ServerLevel level = player.serverLevel();
            Firmament firmament = Firmament.fromLevel(level);

            List<FirmamentRegion> list = this.makeBatch(firmament, player.chunkPosition());
            if (!list.isEmpty()) {
                ServerGamePacketListenerImpl serverPlayNetworkHandler = player.connection;

                for (FirmamentRegion region : list) {
                    sendRegionData(serverPlayNetworkHandler, region);
                }
            }
        }
    }

    private static void sendRegionData(ServerGamePacketListenerImpl handler, FirmamentRegion region) {
        XPlatInterface.INSTANCE.sendPayload(handler.player, new FirmamentRegionDataPayload(region.regionPos.id, new FirmamentRegionData(region)));
    }

    private List<FirmamentRegion> makeBatch(Firmament firmament, ChunkPos playerPos) {
        List<FirmamentRegion> list = this.pendingRegions
                .longStream()
                .mapToObj((id) -> getRegion(id, firmament))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(region -> getSquaredDistance(Firmament.getRegionId(region.x, region.z), playerPos)))
                .toList();

        for (FirmamentRegion region : list) {
            this.pendingRegions.remove(Firmament.getRegionId(region.x, region.z));
        }

        return list;
    }

    public static FirmamentRegion getRegion(long id, Firmament firmament) {
        return firmament.getFirmamentRegion(id);
    }

    public static int getSquaredDistance(long id, ChunkPos playerPos) {
        RegionPos r1 = new RegionPos(id);
        RegionPos r2 = RegionPos.fromWorldCoords(playerPos.getMinBlockX(), playerPos.getMinBlockZ());

        int dx = r1.rx - r2.rx;
        int dz = r1.rz - r2.rz;

        return dx * dx + dz * dz;
    }

    public static FirmamentRegionDataSender getFirmamentRegionDataSender(ServerGamePacketListenerImpl serverPlayNetworkHandler) {
        return ((FirmamentRegionDataSenderHolder) serverPlayNetworkHandler).operation_starcleave$getFirmamentRegionDataSender();
    }
}

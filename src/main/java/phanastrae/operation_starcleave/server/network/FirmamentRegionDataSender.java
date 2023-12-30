package phanastrae.operation_starcleave.server.network;

import com.google.common.collect.Comparators;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.network.packet.s2c.FirmamentRegionDataS2CPacket;
import phanastrae.operation_starcleave.network.packet.s2c.FirmamentRegionSentS2CPacket;
import phanastrae.operation_starcleave.network.packet.s2c.StartFirmamentRegionSendS2CPacket;
import phanastrae.operation_starcleave.network.packet.s2c.UnloadFirmamentRegionS2CPacket;
import phanastrae.operation_starcleave.world.firmament.*;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class FirmamentRegionDataSender {
    private final LongSet regions = new LongOpenHashSet();
    private final boolean local;
    private float desiredBatchSize = 9.0F;
    private float pending;
    private int unacknowledgedBatches;
    private int maxUnacknowledgedBatches = 1;

    public FirmamentRegionDataSender(boolean local) {
        this.local = local;
    }

    public void add(RegionPos regionPos) {
        this.regions.add(regionPos.id);
    }

    public void unload(ServerPlayerEntity player, RegionPos regionPos) {
        this.regions.remove(regionPos.id);
        if(player.isAlive()) {
            ServerPlayNetworking.send(player, new UnloadFirmamentRegionS2CPacket(regionPos.id));
        }
    }

    public void sendChunkBatches(ServerPlayerEntity player) {
        if (this.unacknowledgedBatches < this.maxUnacknowledgedBatches) {
            float f = Math.max(1.0F, this.desiredBatchSize);
            this.pending = Math.min(this.pending + this.desiredBatchSize, f);
            if (!(this.pending < 1.0F)) {
                if (!this.regions.isEmpty()) {
                    ServerWorld serverWorld = player.getServerWorld();

                    Firmament firmament = Firmament.fromWorld(serverWorld);
                    List<FirmamentRegion> list = this.makeBatch(firmament, player.getChunkPos());
                    if (!list.isEmpty()) {
                        ServerPlayNetworkHandler serverPlayNetworkHandler = player.networkHandler;
                        ++this.unacknowledgedBatches;
                        ServerPlayNetworking.send(player, new StartFirmamentRegionSendS2CPacket());

                        for(FirmamentRegion region : list) {
                            sendChunkData(serverPlayNetworkHandler, serverWorld, region);
                        }

                        ServerPlayNetworking.send(player, new FirmamentRegionSentS2CPacket(list.size()));
                        this.pending -= (float)list.size();
                    }
                }
            }
        }
    }

    private static void sendChunkData(ServerPlayNetworkHandler handler, ServerWorld world, FirmamentRegion region) {
        ServerPlayNetworking.send(handler.player, new FirmamentRegionDataS2CPacket(region));
    }

    private List<FirmamentRegion> makeBatch(Firmament firmament, ChunkPos playerPos) {
        int i = MathHelper.floor(this.pending);
        List<FirmamentRegion> list;
        if (!this.local && this.regions.size() > i) {
            list = this.regions.stream().collect(Comparators.least(i, Comparator.comparingInt(id -> getSquaredDistance(id, playerPos))))
                    .stream()
                    .mapToLong(Long::longValue)
                    .mapToObj((l) -> getSubRegion(l, firmament))
                    .filter(Objects::nonNull)
                    .toList();
        } else {
            list = this.regions
                    .longStream()
                    .mapToObj((l) -> getSubRegion(l, firmament))
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparingInt(region -> getSquaredDistance(Firmament.getRegionId(region.x, region.z), playerPos)))
                    .toList();
        }

        for(FirmamentRegion region : list) {
            this.regions.remove(Firmament.getRegionId(region.x, region.z));
        }

        return list;
    }

    public static FirmamentRegion getSubRegion(long id, Firmament firmament) {
        return firmament.getFirmamentRegion(id);
    }

    public static int getSquaredDistance(long id, ChunkPos playerPos) {
        RegionPos r1 = new RegionPos(id);
        RegionPos r2 = RegionPos.fromWorldCoords(playerPos.getStartX(), playerPos.getStartZ());

        int dx = r1.rx - r2.rx;
        int dz = r1.rz - r2.rz;

        return dx*dx + dz*dz;
    }

    public void onAcknowledgeRegions(float desiredBatchSize) {
        --this.unacknowledgedBatches;
        this.desiredBatchSize = Double.isNaN(desiredBatchSize) ? 0.01F : MathHelper.clamp(desiredBatchSize, 0.01F, 64.0F);
        if (this.unacknowledgedBatches == 0) {
            this.pending = 1.0F;
        }

        this.maxUnacknowledgedBatches = 10;
    }

    public static FirmamentRegionDataSender getFirmamentRegionDataSender(ServerPlayNetworkHandler serverPlayNetworkHandler) {
        return ((OperationStarcleaveServerPlayNetworkHandler)serverPlayNetworkHandler).operation_starcleave$getFirmamentRegionDataSender();
    }
}

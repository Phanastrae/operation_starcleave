package phanastrae.operation_starcleave.world.firmament;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.server.network.FirmamentRegionDataSender;
import phanastrae.operation_starcleave.world.firmament.pos.RegionPos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FirmamentRegionsWatched {

    private final Collection<Long> watchedRegions;
    private final ServerPlayer player;

    @Nullable
    private RegionPos regionPos;

    public FirmamentRegionsWatched(ServerPlayer player) {
        this.player = player;
        this.watchedRegions = new LongOpenHashSet();
    }

    public void onPositionChanged(Player player) {
        SectionPos sectionPos = SectionPos.of(player);
        RegionPos regionPos = RegionPos.fromWorldCoords(sectionPos.minBlockX(), sectionPos.minBlockZ());
        this.onPositionChanged(regionPos);
    }

    public void onPositionChanged(RegionPos newPos) {
        if (this.regionPos == newPos) {
            return;
        } else {
            this.regionPos = newPos;
        }

        List<Long> regionsToAdd = new ArrayList<>(9);
        List<Long> regionsToKeep = new ArrayList<>(25);
        List<Long> regionsToRemove = new ArrayList<>(9);

        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                RegionPos nearbyRegion = new RegionPos(newPos.rx + i, newPos.rz + j);
                if (!this.watchedRegions.contains(nearbyRegion.id) && i * i <= 1 && j * j <= 1) {
                    regionsToAdd.add(nearbyRegion.id);
                }
                regionsToKeep.add(nearbyRegion.id);
            }
        }

        for (Long id : this.watchedRegions) {
            if (!regionsToKeep.contains(id)) {
                regionsToRemove.add(id);
            }
        }

        regionsToAdd.forEach(id -> {
            if (watch(id)) {
                RegionPos regionPos = new RegionPos(id);
                FirmamentRegionDataSender.getFirmamentRegionDataSender(this.player.connection).add(regionPos);
            }
        });

        regionsToRemove.forEach(id -> {
            if (unWatch(id)) {
                RegionPos regionPos = new RegionPos(id);
                FirmamentRegionDataSender.getFirmamentRegionDataSender(this.player.connection).unload(this.player, regionPos);
            }
        });
    }

    public boolean watch(long id) {
        if (this.watchedRegions.contains(id)) {
            return false;
        } else {
            this.watchedRegions.add(id);
            return true;
        }
    }

    public boolean unWatch(long id) {
        if (!this.watchedRegions.contains(id)) {
            return false;
        } else {
            this.watchedRegions.remove(id);
            return true;
        }
    }

    public void unWatchAll() {
        this.regionPos = null;

        this.watchedRegions.forEach(id -> {
            if (unWatch(id)) {
                RegionPos regionPos = new RegionPos(id);
                FirmamentRegionDataSender.getFirmamentRegionDataSender(this.player.connection).unload(this.player, regionPos);
            }
        });
    }

    public Collection<Long> getWatchedRegions() {
        return watchedRegions;
    }
}

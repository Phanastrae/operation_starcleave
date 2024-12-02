package phanastrae.operation_starcleave.world.firmament;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import phanastrae.operation_starcleave.server.network.FirmamentRegionDataSender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;

public class FirmamentRegionsWatched {

    Collection<Long> watchedRegions;
    private final ServerPlayer player;

    public FirmamentRegionsWatched(ServerPlayer player) {
        this.player = player;
        this.watchedRegions = new LongOpenHashSet();
    }

    public void onPositionChanged(RegionPos regionPosOld, RegionPos regionPosNew) {
        List<Long> regionsToAdd = new ArrayList<>(9);
        List<Long> regionsToKeep = new ArrayList<>(25);
        List<Long> regionsToRemove = new ArrayList<>(9);

        for(int i = -2; i <= 2; i++) {
            for(int j = -2; j <= 2; j++) {
                RegionPos nearbyRegion = new RegionPos(regionPosNew.rx+i , regionPosNew.rz+j);
                if(!watchedRegions.contains(nearbyRegion.id) && i*i <= 1 && j*j <= 1) {
                    regionsToAdd.add(nearbyRegion.id);
                }
                regionsToKeep.add(nearbyRegion.id);
            }
        }
        for(Long id : watchedRegions) {
            if(!regionsToKeep.contains(id)) {
                regionsToRemove.add(id);
            }
        }

        regionsToAdd.forEach(id -> {
            if(watch(id)) {
                RegionPos regionPos = new RegionPos(id);
                FirmamentRegionDataSender.getFirmamentRegionDataSender(player.connection).add(regionPos);
            }
        });

        regionsToRemove.forEach(id -> {
            if(unWatch(id)) {
                RegionPos regionPos = new RegionPos(id);
                FirmamentRegionDataSender.getFirmamentRegionDataSender(player.connection).unload(player, regionPos);
            }
        });
    }

    public boolean watch(long id) {
        if(this.watchedRegions.contains(id)) {
            return false;
        } else {
            this.watchedRegions.add(id);
            return true;
        }
    }

    public boolean unWatch(long id) {
        if(!this.watchedRegions.contains(id)) {
            return false;
        } else {
            this.watchedRegions.remove(id);
            return true;
        }
    }

    public void unWatchAll() {
        watchedRegions.forEach(id -> {
            if(unWatch(id)) {
                RegionPos regionPos = new RegionPos(id);
                FirmamentRegionDataSender.getFirmamentRegionDataSender(player.connection).unload(player, regionPos);
            }
        });
    }
}

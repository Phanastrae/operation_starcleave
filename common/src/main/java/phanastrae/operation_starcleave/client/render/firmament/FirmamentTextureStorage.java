package phanastrae.operation_starcleave.client.render.firmament;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.world.firmament.*;

import java.util.ArrayList;
import java.util.List;

import static phanastrae.operation_starcleave.world.firmament.FirmamentRegion.SUBREGIONS;
import static phanastrae.operation_starcleave.world.firmament.FirmamentSubRegion.TILES;

public class FirmamentTextureStorage {

    private static final FirmamentTextureStorage INSTANCE = new FirmamentTextureStorage();

    private FirmamentTextureStorage() {
    }
    public static FirmamentTextureStorage getInstance() {
        return INSTANCE;
    }

    private final NativeImage image = new NativeImage(NativeImage.Format.RGBA, 512, 512, true);
    private final DynamicTexture finalTexture = new DynamicTexture(new NativeImage(NativeImage.Format.RGBA, 512, 512, true));

    private @Nullable RegionPos lastCamPos = null;
    private final RegionPos[][] regions = new RegionPos[4][4];
    private final boolean[][] filled = new boolean[4][4];
    private final boolean[][] active = new boolean[4][4];

    private final List<SubRegionPos> rebuildQueue = new ArrayList<>();
    private final boolean[][] needsRebuild = new boolean[4 * SUBREGIONS][4 * SUBREGIONS];

    private boolean needsUpdate = false;
    private final boolean[][] entireRegionHadUpdate = new boolean[4][4];
    private final boolean[][] regionHadUpdate = new boolean[4][4];
    private final boolean[][] subregionHadUpdate = new boolean[4 * SUBREGIONS][4 * SUBREGIONS];

    public void close() {
        this.image.close();
        this.finalTexture.close();
    }

    public void clearData() {
        this.clearRegions();
        clearRebuildQueue();
        this.lastCamPos = null;
    }

    public void tick() {
        Minecraft client = Minecraft.getInstance();
        ProfilerFiller profiler = client.getProfiler();
        profiler.push("starcleave_update_firmament_texture");

        // make note of what regions were filled and active
        boolean[][] wasFilledAndActiveArray = new boolean[4][4];
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++) {
                wasFilledAndActiveArray[i][j] = this.active[i][j] && this.filled[i][j];
            }
        }

        profiler.push("update_position");
        // get and update cam pos
        Entity camEntity = Minecraft.getInstance().cameraEntity;
        Level level = camEntity == null ? null : camEntity.level();
        RegionPos camPos = camEntity == null ? null : RegionPos.fromEntity(camEntity);

        if((camPos == null && lastCamPos != null) || (camPos != null && (lastCamPos == null || camPos.id != lastCamPos.id))) {
            updateCamPos(camPos, level);
        }

        profiler.popPush("process_queued");
        // rebuild queued subchunks
        if(level != null) {
            Firmament firmament = Firmament.fromLevel(level);
            if(firmament != null) {
                rebuildQueued(firmament, level);
            }
        }

        profiler.popPush("update_final");
        // update final image if necessary
        boolean changed = false;
        NativeImage finalImage = this.finalTexture.getPixels();
        if(finalImage != null) {
            for(int i = 0; i < 4; i++) {
                for(int j = 0; j < 4; j++) {
                    boolean wasFilledAndActive = wasFilledAndActiveArray[i][j];
                    boolean isFilledAndActive = this.filled[i][j] && this.active[i][j];

                    if(isFilledAndActive) {
                        if(!wasFilledAndActive || (this.needsUpdate && this.regionHadUpdate[i][j])) {
                            if(!wasFilledAndActive || this.entireRegionHadUpdate[i][j]) {
                                // copy pixels for region
                                this.image.copyRect(finalImage, i * 128, j * 128, i * 128, j * 128, 128, 128, false, false);
                                changed = true;
                            } else {
                                for(int x = 0; x < SUBREGIONS; x++) {
                                    for(int z = 0; z < SUBREGIONS; z++) {
                                        int sx = i * SUBREGIONS + x;
                                        int sz = j * SUBREGIONS + z;

                                        if(this.subregionHadUpdate[sx][sz]) {
                                            // copy pixels for subregion
                                            this.image.copyRect(finalImage, sx * TILES, sz * TILES, sx * TILES, sz * TILES, TILES, TILES, false, false);
                                            changed = true;
                                        }
                                    }
                                }
                            }
                        }
                    } else if(wasFilledAndActive) {
                        // clear pixels
                        finalImage.fillRect(i * 128, j * 128, 128, 128, 0x0);
                        changed = true;
                    }
                }
            }
        }

        profiler.popPush("upload");
        // update
        if(changed) {
            this.finalTexture.upload();
        }

        // reset update info
        if(this.needsUpdate) {
            for(int i = 0; i < 4; i++) {
                for(int j = 0; j < 4; j++) {
                    if(this.regionHadUpdate[i][j]) {
                        this.regionHadUpdate[i][j] = false;
                        this.entireRegionHadUpdate[i][j] = false;
                        for(int x = 0; x < SUBREGIONS; x++) {
                            for(int z = 0; z < SUBREGIONS; z++) {
                                int sx = i * SUBREGIONS + x;
                                int sz = j * SUBREGIONS + z;

                                this.subregionHadUpdate[sx][sz] = false;
                            }
                        }
                    }
                }
            }
        }
        this.needsUpdate = false;

        profiler.pop();
        profiler.pop();
    }

    public void queueRebuild(BlockPos blockPos) {
        SubRegionPos subRegionPos = SubRegionPos.fromWorldCoords(blockPos.getX(), blockPos.getZ());
        RegionPos regionPos = RegionPos.fromSubRegion(subRegionPos);

        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++) {
                if(!filled[i][j] || !active[i][j]) continue;

                RegionPos pos = this.regions[i][j];
                if(pos != null && pos.id == regionPos.id) {
                    queueRebuild(subRegionPos);
                    return;
                }
            }
        }
    }

    public void queueRebuild(SubRegionPos subRegionPos) {
        int sx = subRegionPos.srx & 0x3F;
        int sz = subRegionPos.srz & 0x3F;
        if(!needsRebuild[sx][sz]) {
            needsRebuild[sx][sz] = true;
            rebuildQueue.add(subRegionPos);
        }
    }

    public void clearRebuildQueue() {
        for(SubRegionPos subRegionPos : rebuildQueue) {
            int sx = subRegionPos.srx & 0x3F;
            int sz = subRegionPos.srz & 0x3F;
            if(needsRebuild[sx][sz]) {
                needsRebuild[sx][sz] = false;
            }
        }
        rebuildQueue.clear();
    }

    public void rebuildQueued(Firmament firmament, Level level) {
        for(SubRegionPos subRegionPos : rebuildQueue) {
            RegionPos regionPos = RegionPos.fromSubRegion(subRegionPos);
            int x = regionPos.rx;
            int z = regionPos.rz;
            int gx = x & 0x3;
            int gz = z & 0x3;

            RegionPos rp = this.regions[gx][gz];
            if(rp != null && rp.id == regionPos.id) {
                FirmamentSubRegion subRegion = firmament.getSubRegionFromId(subRegionPos.id);
                if(subRegion != null) {
                    updateRegionData(gx, gz, subRegion, level);
                }
            }
        }
        clearRebuildQueue();
    }

    public void updateCamPos(@Nullable RegionPos newCamPos, @Nullable Level level) {
        if(newCamPos == null) {
            markAllInactive();
        } else {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    int x = newCamPos.rx + i;
                    int z = newCamPos.rz + j;
                    int gx = x & 0x3;
                    int gz = z & 0x3;
                    RegionPos rp = regions[gx][gz];
                    if(rp == null || rp.rx != x || rp.rz != z) {
                        setRegionPos(gx, gz, new RegionPos(x, z), level);
                    }
                }
            }
        }
        this.lastCamPos = newCamPos;
    }

    public void markAllInactive() {
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++) {
                active[i][j] = false;
            }
        }
    }

    public void clearRegions() {
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++) {
                if(regions[i][j] != null) {
                    setRegionPos(i, j, null, null);
                    clearRegion(i, j);
                }
            }
        }
    }

    public void setRegionPos(int gx, int gz, @Nullable RegionPos regionPos, @Nullable Level level) {
        regions[gx][gz] = regionPos;

        FirmamentRegion region = null;
        if(regionPos != null) {
            Firmament firmament = Firmament.fromLevel(level);
            if(firmament != null) {
                region = firmament.getFirmamentRegion(regionPos);
            }
        }

        updateRegionData(gx, gz, region, level);
    }

    public void updateRegionData(int gx, int gz, @Nullable FirmamentRegion region, @Nullable Level level) {
        if(region == null || level == null) {
            active[gx][gz] = false;
            return;
        }
        boolean damaged = false;
        for(int i = 0; i < SUBREGIONS && !damaged; i++) {
            for(int j = 0; j < SUBREGIONS && !damaged; j++) {
                FirmamentSubRegion sr = region.subRegions[i][j];
                if(sr.hadDamageLastCheck()) {
                    damaged = true;
                }
            }
        }
        if(!damaged) {
            active[gx][gz] = false;
            return;
        }

        int ox = gx * 128;
        int oz = gz * 128;
        for(int x = 0; x < 128; x++) {
            for(int z = 0; z < 128; z++) {
                int damage = region.getDamage(x << 2, z << 2);
                int height = getHeight(region.x + 4 * x, region.z + 4 * z, level);
                this.image.setPixelRGBA(x + ox, z + oz, getColor(damage, height));
            }
        }

        this.needsUpdate = true;
        this.regionHadUpdate[gx][gz] = true;
        this.entireRegionHadUpdate[gx][gz] = true;

        filled[gx][gz] = true;
        active[gx][gz] = true;
    }

    public void updateRegionData(int gx, int gz, FirmamentSubRegion subRegion, Level level) {
        if(!subRegion.hadDamageLastCheck() && !filled[gx][gz]) return;

        if(!active[gx][gz]) {
            clearRegion(gx, gz);
            active[gx][gz] = true;
        }

        int ox = gx * 128 + ((subRegion.x & FirmamentRegion.REGION_MASK) >> FirmamentSubRegion.TILE_SIZE_BITS);
        int oz = gz * 128 + ((subRegion.z & FirmamentRegion.REGION_MASK) >> FirmamentSubRegion.TILE_SIZE_BITS);
        for(int x = 0; x < TILES; x++) {
            for(int z = 0; z < TILES; z++) {
                int damage = subRegion.getDamage(x << 2, z << 2);
                int height = getHeight(subRegion.x + 4 * x, subRegion.z + 4 * z, level);
                this.image.setPixelRGBA(x + ox, z + oz, getColor(damage, height));
            }
        }

        this.needsUpdate = true;
        this.regionHadUpdate[gx][gz] = true;
        int sx = gx * SUBREGIONS + ((subRegion.x >> FirmamentRegion.SUBREGION_SIZE_BITS) & 0xF);
        int sz = gz * SUBREGIONS + ((subRegion.z >> FirmamentRegion.SUBREGION_SIZE_BITS) & 0xF);
        this.subregionHadUpdate[sx][sz] = true;

        filled[gx][gz] = true;
    }

    public void clearRegion(int gx, int gz) {
        if(!filled[gx][gz]) return;

        int ox = gx * 128;
        int oz = gz * 128;
        this.image.fillRect(ox, oz, 128, 128, 0);

        this.needsUpdate = true;
        this.regionHadUpdate[gx][gz] = true;
        this.entireRegionHadUpdate[gx][gz] = true;

        filled[gx][gz] = false;
        active[gx][gz] = false;
    }

    public void onRegionAdded(FirmamentRegion region, Level level) {
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++) {
                RegionPos regionPos = regions[i][j];
                if(regionPos != null && regionPos.id == region.regionPos.id) {
                    updateRegionData(i, j, region, level);
                }
            }
        }
    }

    public void onSubRegionUpdated(FirmamentSubRegion subRegion, Level level) {
        RegionPos rp = RegionPos.fromWorldCoords(subRegion.x, subRegion.z);
        long id = rp.id;
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++) {
                RegionPos regionPos = regions[i][j];
                if(regionPos != null && regionPos.id == id) {
                    updateRegionData(i, j, subRegion, level);
                }
            }
        }
    }

    public void onRegionRemoved(long id) {
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++) {
                RegionPos regionPos = regions[i][j];
                if(regionPos != null && regionPos.id == id) {
                    active[i][j] = false;
                }
            }
        }
    }

    public DynamicTexture getTexture() {
        return this.finalTexture;
    }

    public boolean isActive(int i, int j) {
        return this.active[i][j];
    }

    public boolean isFilled(int i, int j) {
        return this.filled[i][j];
    }

    public static int getColor(int damage, int height) {
        height = height / 3;
        if(height > 255) height = 255;

        int r = damage & 0x7;
        int g = height & 0xFF;
        int b = 0;
        int a = 0xFF;
        return (a << 24) | (b << 16) | (g << 8) | r;
    }

    // returns the world height minus the topmost block's height in a tile
    public static int getHeight(int x, int z, Level level) {
        int topY = level.getMaxBuildHeight();
        int bottomY = level.getMinBuildHeight();

        int minY = topY;
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++) {
                int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x+i, z+j);
                if(y < minY) {
                    minY = y;
                }
            }
        }

        // if void is visible set to max value
        if(minY <= bottomY) {
            return 255 * 3;
        }
        return topY - minY;
    }
}

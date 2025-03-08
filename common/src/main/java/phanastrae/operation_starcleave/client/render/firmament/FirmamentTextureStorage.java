package phanastrae.operation_starcleave.client.render.firmament;

import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.world.firmament.*;
import com.mojang.blaze3d.platform.NativeImage;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

public class FirmamentTextureStorage {

    private static final FirmamentTextureStorage INSTANCE = new FirmamentTextureStorage();

    private FirmamentTextureStorage() {
    }
    public static FirmamentTextureStorage getInstance() {
        return INSTANCE;
    }

    public void close() {
        texture.close();
    }

    public DynamicTexture getTexture() {
        return this.texture;
    }

    DynamicTexture texture = new DynamicTexture(new NativeImage(NativeImage.Format.RGBA, 512, 512, true));
    @Nullable RegionPos lastCamPos = null;
    boolean needsUpdate = false;
    RegionPos[][] regions = new RegionPos[4][4];
    boolean[][] filled = new boolean[4][4];
    boolean[][] active = new boolean[4][4];

    List<SubRegionPos> rebuildQueue = new ArrayList<>();
    boolean[][] needsRebuild = new boolean[4 * FirmamentRegion.SUBREGIONS][4 * FirmamentRegion.SUBREGIONS];

    public void tick() {
        Entity camEntity = Minecraft.getInstance().cameraEntity;
        Level world = camEntity == null ? null : camEntity.level();
        RegionPos camPos = camEntity == null ? null : RegionPos.fromEntity(camEntity);

        if((camPos == null && lastCamPos != null) || (camPos != null && (lastCamPos == null || camPos.id != lastCamPos.id))) {
            updateCamPos(camPos, world);
        }


        if(world != null) {
            Firmament firmament = Firmament.fromLevel(world);
            if(firmament != null) {
                rebuildQueued(firmament, world);
            }
        }

        if(needsUpdate) {
            // TODO should this always run?
            texture.upload();
            needsUpdate = false;
        }
    }

    public void queueRebuild(BlockPos blockPos) {
        SubRegionPos subRegionPos = SubRegionPos.fromWorldCoords(blockPos.getX(), blockPos.getZ());
        RegionPos regionPos = RegionPos.fromSubRegion(subRegionPos);

        boolean regionLoaded = false;
        for(int i = 0; i < 4 && !regionLoaded; i++) {
            for(int j = 0; j < 4; j++) {
                if(!filled[i][j]) continue;

                RegionPos pos = this.regions[i][j];
                if(pos == null) continue;

                if (pos.id == regionPos.id && active[i][j]) {
                    regionLoaded = true;
                    break;
                }
            }
        }
        if(!regionLoaded) return;

        queueRebuild(subRegionPos);
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

    public void rebuildQueued(Firmament firmament, Level world) {
        for(SubRegionPos subRegionPos : rebuildQueue) {
            RegionPos regionPos = RegionPos.fromSubRegion(subRegionPos);
            int x = regionPos.rx;
            int z = regionPos.rz;
            int gx = x & 0x3;
            int gz = z & 0x3;

            RegionPos rp = this.regions[gx][gz];
            if(rp == null) continue;
            if(rp.id != regionPos.id) continue;

            FirmamentSubRegion subRegion = firmament.getSubRegionFromId(subRegionPos.id);
            if(subRegion == null) continue;
            updateRegionData(gx, gz, subRegion, world);
        }
        clearRebuildQueue();
    }

    public int getColor(int damage, int height) {
        height = height / 3;
        if(height > 255) height = 255;

        int r = damage & 0x7;
        int g = height & 0xFF;
        int b = 0;
        int a = 0xFF;
        return (a << 24) | (b << 16) | (g << 8) | r;
    }

    // returns the world height minus the topmost block's height in a tile
    public int getHeight(int x, int z, Level world) {
        int topY = world.getMaxBuildHeight();
        int bottomY = world.getMinBuildHeight();

        int minY = topY;
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++) {
                int y = world.getHeight(Heightmap.Types.MOTION_BLOCKING, x+i, z+j);
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


    public void updateCamPos(@Nullable RegionPos newCamPos, @Nullable Level world) {
        if(newCamPos == null) {
            setRegionsInactive();
        } else {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    int x = newCamPos.rx + i;
                    int z = newCamPos.rz + j;
                    int gx = x & 0x3;
                    int gz = z & 0x3;
                    RegionPos rp = regions[gx][gz];
                    if(rp == null || rp.rx != x || rp.rz != z) {
                        setRegionPos(gx, gz, new RegionPos(x, z), world);
                    }
                }
            }
        }
        lastCamPos = newCamPos;
    }

    public void setRegionsInactive() {
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

    public void setRegionPos(int gx, int gz, @Nullable RegionPos regionPos, @Nullable Level world) {
        regions[gx][gz] = regionPos;

        FirmamentRegion region = null;
        if(regionPos != null) {
            Firmament firmament = Firmament.fromLevel(world);
            if(firmament != null) {
                region = firmament.getFirmamentRegion(regionPos);
            }
        }

        updateRegionData(gx, gz, region, world);
    }

    public void updateRegionData(int gx, int gz, @Nullable FirmamentRegion region, @Nullable Level world) {
        if(region == null || world == null) {
            active[gx][gz] = false;
            return;
        }
        boolean damaged = false;
        for(int i = 0; i < FirmamentRegion.SUBREGIONS && !damaged; i++) {
            for(int j = 0; j < FirmamentRegion.SUBREGIONS && !damaged; j++) {
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

        NativeImage image = texture.getPixels();
        if(image == null) return;
        int ox = gx * 128;
        int oz = gz * 128;
        for(int x = 0; x < 128; x++) {
            for(int z = 0; z < 128; z++) {
                int damage = region.getDamage(x << 2, z << 2);
                int height = getHeight(region.x + 4 * x, region.z + 4 * z, world);
                image.setPixelRGBA(x + ox, z + oz, getColor(damage, height));
            }
        }
        filled[gx][gz] = true;
        active[gx][gz] = true;
        needsUpdate = true;
    }

    public void updateRegionData(int gx, int gz, FirmamentSubRegion subRegion, Level world) {
        if(!subRegion.hadDamageLastCheck() && !filled[gx][gz]) return;

        if(!active[gx][gz]) {
            clearRegion(gx, gz);
            active[gx][gz] = true;
        }

        NativeImage image = texture.getPixels();
        if(image == null) return;
        int ox = gx * 128 + ((subRegion.x & FirmamentRegion.REGION_MASK) >> FirmamentSubRegion.TILE_SIZE_BITS);
        int oz = gz * 128 + ((subRegion.z & FirmamentRegion.REGION_MASK) >> FirmamentSubRegion.TILE_SIZE_BITS);
        for(int x = 0; x < FirmamentSubRegion.TILES; x++) {
            for(int z = 0; z < FirmamentSubRegion.TILES; z++) {
                int damage = subRegion.getDamage(x << 2, z << 2);
                int height = getHeight(subRegion.x + 4 * x, subRegion.z + 4 * z, world);
                image.setPixelRGBA(x + ox, z + oz, getColor(damage, height));
            }
        }
        filled[gx][gz] = true;
        needsUpdate = true;
    }

    public void clearRegion(int gx, int gz) {
        if(!filled[gx][gz]) return;

        NativeImage image = texture.getPixels();
        if(image == null) return;
        int ox = gx * 128;
        int oz = gz * 128;
        image.fillRect(ox, oz, 128, 128, 0);
        filled[gx][gz] = false;
        active[gx][gz] = false;
        needsUpdate = true;
    }

    public void onRegionAdded(FirmamentRegion region, Level world) {
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++) {
                RegionPos regionPos = regions[i][j];
                if(regionPos != null && regionPos.id == region.regionPos.id) {
                    updateRegionData(i, j, region, world);
                }
            }
        }
    }

    public void onSubRegionUpdated(FirmamentSubRegion subRegion, Level world) {
        RegionPos rp = RegionPos.fromWorldCoords(subRegion.x, subRegion.z);
        long id = rp.id;
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++) {
                RegionPos regionPos = regions[i][j];
                if(regionPos != null && regionPos.id == id) {
                    updateRegionData(i, j, subRegion, world);
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

    public void clearData() {
        this.clearRegions();
        clearRebuildQueue();
        this.lastCamPos = null;
    }

    public float[] getActiveRegions() {
        float[] f = new float[16];
        for(int i = 0; i < 16; i++) {
            int x = (i & 0xC) >> 2;
            int z = i & 0x3;
            f[i] = active[x][z] ? 1 : 0;
        }
        return f;
    }
}

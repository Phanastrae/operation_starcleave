package phanastrae.operation_starcleave.render.firmament;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.world.firmament.Firmament;
import phanastrae.operation_starcleave.world.firmament.FirmamentRegion;
import phanastrae.operation_starcleave.world.firmament.FirmamentSubRegion;
import phanastrae.operation_starcleave.world.firmament.RegionPos;

public class FirmamentTextureStorage {
    // TODO should/can this be multithreaded?
    // TODO check over and tidy this code if needed
    // TODO actually implement the active/inactive thing please

    private static final FirmamentTextureStorage INSTANCE = new FirmamentTextureStorage();

    private FirmamentTextureStorage() {
    }
    public static FirmamentTextureStorage getInstance() {
        return INSTANCE;
    }

    public void close() {
        texture.close();
    }

    NativeImageBackedTexture texture = new NativeImageBackedTexture(new NativeImage(NativeImage.Format.RGBA, 512, 512, true));
    @Nullable RegionPos lastCamPos = null;
    boolean needsUpdate = false;
    RegionPos[][] regions = new RegionPos[4][4];
    boolean[][] filled = new boolean[4][4];
    boolean[][] active = new boolean[4][4];

    public void tick() {
        Entity camEntity = MinecraftClient.getInstance().cameraEntity;
        RegionPos camPos = camEntity == null ? null : RegionPos.fromEntity(camEntity);

        if((camPos == null && lastCamPos != null) || (camPos != null && (lastCamPos == null || camPos.id != lastCamPos.id))) {
            updateCamPos(camPos);
        }

        if(needsUpdate) {
            // TODO should this always run?
            texture.upload();
            needsUpdate = false;
        }
    }


    public void updateCamPos(@Nullable RegionPos newCamPos) {
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
                        setRegionPos(gx, gz, new RegionPos(x, z));
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
                    setRegionPos(i, j, null);
                    clearRegion(i, j);
                }
            }
        }
    }

    public void setRegionPos(int gx, int gz, RegionPos regionPos) {
        regions[gx][gz] = regionPos;

        FirmamentRegion region = null;
        if(regionPos != null) {
            ClientWorld world = MinecraftClient.getInstance().world;
            if(world != null) {
                Firmament firmament = Firmament.fromWorld(world);
                if(firmament != null) {
                    region = firmament.getFirmamentRegion(regionPos);
                }
            }
        }

        updateRegionData(gx, gz, region);
    }

    public void updateRegionData(int gx, int gz, @Nullable FirmamentRegion region) {
        if(region == null) {
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

        NativeImage image = texture.getImage();
        if(image == null) return;
        int ox = gx * 128;
        int oz = gz * 128;
        for(int x = 0; x < 128; x++) {
            for(int z = 0; z < 128; z++) {
                int damage = region.getDamage(x << 2, z << 2);
                int color = 0xFF000000 | (damage & 0x7);
                image.setColor(x + ox, z + oz, color);
            }
        }
        filled[gx][gz] = true;
        active[gx][gz] = true;
        needsUpdate = true;
    }

    public void updateRegionData(int gx, int gz, FirmamentSubRegion subRegion) {
        if(!subRegion.hadDamageLastCheck() && !filled[gx][gz]) return;

        if(!active[gx][gz]) {
            clearRegion(gx, gz);
            active[gx][gz] = true;
        }

        NativeImage image = texture.getImage();
        if(image == null) return;
        int ox = gx * 128 + ((subRegion.x & FirmamentRegion.REGION_MASK) >> FirmamentSubRegion.TILE_SIZE_BITS);
        int oz = gz * 128 + ((subRegion.z & FirmamentRegion.REGION_MASK) >> FirmamentSubRegion.TILE_SIZE_BITS);
        for(int x = 0; x < FirmamentSubRegion.TILES; x++) {
            for(int z = 0; z < FirmamentSubRegion.TILES; z++) {
                int damage = subRegion.getDamage(x << 2, z << 2);
                int color = 0xFF000000 | ((damage & 0x7));
                image.setColor(x + ox, z + oz, color);
            }
        }
        filled[gx][gz] = true;
        needsUpdate = true;
    }

    public void clearRegion(int gx, int gz) {
        if(!filled[gx][gz]) return;

        NativeImage image = texture.getImage();
        if(image == null) return;
        int ox = gx * 128;
        int oz = gz * 128;
        image.fillRect(ox, oz, 128, 128, 0);
        filled[gx][gz] = false;
        active[gx][gz] = false;
        needsUpdate = true;
    }

    public void onRegionAdded(FirmamentRegion region) {
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++) {
                RegionPos regionPos = regions[i][j];
                if(regionPos != null && regionPos.id == region.regionPos.id) {
                    updateRegionData(i, j, region);
                }
            }
        }
    }

    public void onSubRegionUpdated(FirmamentSubRegion subRegion) {
        RegionPos rp = RegionPos.fromWorldCoords(subRegion.x, subRegion.z);
        long id = rp.id;
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++) {
                RegionPos regionPos = regions[i][j];
                if(regionPos != null && regionPos.id == id) {
                    updateRegionData(i, j, subRegion);
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

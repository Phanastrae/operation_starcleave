package phanastrae.operation_starcleave.world.firmament;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.util.TriConsumer;
import org.joml.Math;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class FirmamentRegion {
    // the Firmament for a 32x32 Chunk (512x512 Block) Region
    public FirmamentSubRegion[][] subRegions;

    public static final int SUBREGIONS = 64;
    public static final int REGION_MASK = 0x1FF;
    public static final int SUBREGION_MASK = 0x7;
    public static final int SUBREGION_SIZE_BITS = 3;

    public static final int GRID_SIZE = 32 * 16;

    public FirmamentRegion() {
        this.subRegions = new FirmamentSubRegion[SUBREGIONS][SUBREGIONS];
        for(int i = 0; i < SUBREGIONS; i++) {
            for(int j = 0; j < SUBREGIONS; j++) {
                subRegions[i][j] = new FirmamentSubRegion();
            }
        }
    }

    private final List<Actor> actors = new ArrayList<>();
    private final List<Actor> newActors = new ArrayList<>();

    public void addActor(Actor actor) {
        this.newActors.add(actor);
    }

    public void clearActors() {
        this.actors.clear();
        this.newActors.clear();
    }

    public void updateActors() {
        actors.addAll(newActors);
        newActors.clear();

        actors.removeIf((actor) -> !actor.active);
        for(Actor actor : actors) {
            actor.tick(this);
        }
    }

    public float getDrip(int x, int z) {
        x = x&REGION_MASK;
        z = z&REGION_MASK;
        return subRegions[x>>SUBREGION_SIZE_BITS][z>>SUBREGION_SIZE_BITS].getDrip(x&SUBREGION_MASK, z&SUBREGION_MASK);
    }

    public float getDDrip(int x, int z) {
        x = x&REGION_MASK;
        z = z&REGION_MASK;
        return subRegions[x>>SUBREGION_SIZE_BITS][z>>SUBREGION_SIZE_BITS].getDDrip(x&SUBREGION_MASK, z&SUBREGION_MASK);
    }

    public float getDamage(int x, int z) {
        x = x&REGION_MASK;
        z = z&REGION_MASK;
        return subRegions[x>>SUBREGION_SIZE_BITS][z>>SUBREGION_SIZE_BITS].getDamage(x&SUBREGION_MASK, z&SUBREGION_MASK);
    }

    public float getDisplacement(int x, int z) {
        x = x&REGION_MASK;
        z = z&REGION_MASK;
        return subRegions[x>>SUBREGION_SIZE_BITS][z>>SUBREGION_SIZE_BITS].getDisplacement(x&SUBREGION_MASK, z&SUBREGION_MASK);
    }

    public float getVelocity(int x, int z) {
        x = x&REGION_MASK;
        z = z&REGION_MASK;
        return subRegions[x>>SUBREGION_SIZE_BITS][z>>SUBREGION_SIZE_BITS].getVelocity(x&SUBREGION_MASK, z&SUBREGION_MASK);
    }

    public void setDrip(int x, int z, float value) {
        x = x&REGION_MASK;
        z = z&REGION_MASK;
        subRegions[x>>SUBREGION_SIZE_BITS][z>>SUBREGION_SIZE_BITS].setDrip(x&SUBREGION_MASK, z&SUBREGION_MASK, value);
    }

    public void setDDrip(int x, int z, float value) {
        x = x&REGION_MASK;
        z = z&REGION_MASK;
        subRegions[x>>SUBREGION_SIZE_BITS][z>>SUBREGION_SIZE_BITS].setDDrip(x&SUBREGION_MASK, z&SUBREGION_MASK, value);
    }

    public void setDamage(int x, int z, float value) {
        x = x&REGION_MASK;
        z = z&REGION_MASK;
        subRegions[x>>SUBREGION_SIZE_BITS][z>>SUBREGION_SIZE_BITS].setDamage(x&SUBREGION_MASK, z&SUBREGION_MASK, value);
    }

    public void setDisplacement(int x, int z, float value) {
        x = x&REGION_MASK;
        z = z&REGION_MASK;
        subRegions[x>>SUBREGION_SIZE_BITS][z>>SUBREGION_SIZE_BITS].setDisplacement(x&SUBREGION_MASK, z&SUBREGION_MASK, value);
    }

    public void setVelocity(int x, int z, float value) {
        x = x&REGION_MASK;
        z = z&REGION_MASK;
        subRegions[x>>SUBREGION_SIZE_BITS][z>>SUBREGION_SIZE_BITS].setVelocity(x&SUBREGION_MASK, z&SUBREGION_MASK, value);
    }

    public void markActive(int x, int z) {
        x = x&REGION_MASK;
        z = z&REGION_MASK;
        subRegions[x>>SUBREGION_SIZE_BITS][z>>SUBREGION_SIZE_BITS].markActive(x&SUBREGION_MASK, z&SUBREGION_MASK);
    }

    public void clearActive() {
        for(int i = 0; i < SUBREGIONS; i++) {
            for(int j = 0; j < SUBREGIONS; j++) {
                subRegions[i][j].clearActive();
            }
        }
    }

    public boolean getActive(int x, int z) {
        x = x&REGION_MASK;
        z = z&REGION_MASK;
        return subRegions[x>>SUBREGION_SIZE_BITS][z>>SUBREGION_SIZE_BITS].active[4];
    }

    public void markShouldUpdate(int x, int z) {
        x = x&REGION_MASK;
        z = z&REGION_MASK;
        subRegions[x>>SUBREGION_SIZE_BITS][z>>SUBREGION_SIZE_BITS].markShouldUpdate();
    }

    public void clearShouldUpdate() {
        for(int i = 0; i < SUBREGIONS; i++) {
            for(int j = 0; j < SUBREGIONS; j++) {
                subRegions[i][j].clearShouldUpdate();
            }
        }
    }

    public boolean getShouldUpdate(int x, int z) {
        x = x&REGION_MASK;
        z = z&REGION_MASK;
        return subRegions[x>>SUBREGION_SIZE_BITS][z>>SUBREGION_SIZE_BITS].shouldUpdate;
    }

    public float dFdxDamage(int x, int z) {
        return (getDamage(x+1, z) - getDamage(x-1, z)) / 2;
    }

    public float dFdzDamage(int x, int z) {
        return (getDamage(x, z+1) - getDamage(x, z-1)) / 2;
    }

    public float dFdxBigDamage(int x, int z) {
        return 0.5f * dFdxDamage(x, z) + 0.25f * (dFdxDamage(x+1, z) + dFdxDamage(x-1, z));
    }

    public float dFdzBigDamage(int x, int z) {
        return 0.5f * dFdzDamage(x, z) + 0.25f * (dFdzDamage(x, z+1) + dFdzDamage(x, z-1));
    }

    int t = 0;

    public void tick() {
        t++;
        if(t == 8) {
            clearShouldUpdate();
        }
        for(int i = 0; i < SUBREGIONS; i++) {
            for(int j = 0; j < SUBREGIONS; j++) {
                FirmamentSubRegion subRegion = subRegions[i][j];

                for(int k = 0; k < 9; k++) {
                    if(subRegion.active[k]) {
                        markShouldUpdate(i * FirmamentSubRegion.GRID_SIZE + subRegion.xOffset[k], j * FirmamentSubRegion.GRID_SIZE + subRegion.zOffset[k]);
                    }
                }
            }
        }
        t++;
        if(t == 8) {
            t = 0;
            clearActive();
        }

        updateActors();

        // calculate forces
        forEachActivePos((x, z) -> {
            float drip = getDrip(x, z);
            if(drip == 0) return;

            float force = - 16 * drip * drip * (1 + getDamage(x, z));
            //force += -state.displacement.get(x, z) * (1 - Math.max(0, drip)) * (1 - state.damage.get(x, z));
            force += getTension(x, z);
            force /= 400;

            float dv = force;
            dv = (int)(dv * 32)/32f;

            float velocity = getVelocity(x, z);
            if(velocity > -8 || dv > 0) {
                if (dv != 0) {
                    velocity += dv;
                    if(velocity < -4) velocity = -4;
                    if(velocity > 0) velocity = 0;
                    setVelocity(x, z, velocity);
                    markActive(x, z);
                }
            }
        });

        forEachActivePos((x, z) -> {
            float d = getDisplacement(x, z);
            if(d > -15) {
                float dh = getVelocity(x, z);
                dh = (int) (dh * 1) / 1f;

                if (dh != 0) {
                    d += dh;
                    if(d < -15) d = -15;
                    if(d > 0) d = 0;
                    setDisplacement(x, z, d);
                    markActive(x, z);
                }
            }

            // update damage and drip

            float threshold = Math.clamp(0, 1, -d / 15);
            threshold = (int)(threshold * 8)/8f;

            float currentDamage = getDamage(x, z);
            if (currentDamage < threshold) {
                setDamage(x, z, threshold);
                markActive(x, z);
                currentDamage = threshold;
            }

            if(getDrip(x, z) < currentDamage) {
                setDrip(x, z, currentDamage);
                markActive(x, z);
            }
        });

        // spread drip
        forEachActivePos((x, z) -> {
            float drip = getDrip(x, z);
            forEachNeighbor((nx, nz, nWeight) -> {
                float drip2 = getDrip(x+nx, z+nz);

                float threshold = drip - 0.1f * Math.sqrt(nx*nx + nz*nz);

                float t = (threshold - drip2) * nWeight;
                t = (int)(t * 32)/32f;
                if(t > 0) {
                    setDDrip(x+nx, z+nz, t);
                    markActive(x, z);
                }
            });
        });
        forEachActivePos((x, z) -> {
            float drip = getDrip(x, z);
            float dDrip = getDDrip(x, z);
            drip += dDrip;
            if(drip > 8) drip = 8;

            setDrip(x, z, drip);
            setDDrip(x, z, 0);
        });
    }

    public float getTension(int x, int z) {
        float dsplc = getDisplacement(x, z);
        float damage = getDamage(x, z);
        float force = 0;
        for(int c = 0; c < connectionCount; c++) {
            float dsplc2 = getDisplacement(x+conX[c], z+conZ[c]);
            float damage2 = getDamage(x+conX[c], z+conZ[c]);
            float dif = dsplc2 - dsplc;

            float f = (1-damage2) * (1-damage);
            force += 256 * dif * connectionWeights[c] * f;
        }

        return force;
    }

    public int connectionCount = 8;
    public int[] conX = new int[]{1, -1, 0, 0, 1, 1, -1, -1};
    public int[] conZ = new int[]{0, 0, 1, -1, 1, -1, 1, -1};
    static final float aw = 0.1464f;
    static final float dw = 0.1036f;
    public float[] connectionWeights = new float[]{aw, aw, aw, aw, dw, dw, dw, dw};

    public void forEachActivePos(BiConsumer<Integer, Integer> method) {
        for(int i = 0; i < SUBREGIONS; i++) {
            for(int j = 0; j < SUBREGIONS; j++) {
                if(!subRegions[i][j].shouldUpdate) continue;

                int finalI = i;
                int finalJ = j;
                subRegions[i][j].forEachPos((x, z) -> method.accept(x + finalI * FirmamentSubRegion.GRID_SIZE, z + finalJ * FirmamentSubRegion.GRID_SIZE));
            }
        }
    }

    public void forEachPos(BiConsumer<Integer, Integer> method) {
        for(int i = 0; i < SUBREGIONS; i++) {
            for(int j = 0; j < SUBREGIONS; j++) {
                int finalI = i;
                int finalJ = j;
                subRegions[i][j].forEachPos((x, z) -> method.accept(x + finalI * FirmamentSubRegion.GRID_SIZE, z + finalJ * FirmamentSubRegion.GRID_SIZE));
            }
        }
    }

    public void forEachNeighbor(TriConsumer<Integer, Integer, Float> method) {
        for(int c = 0; c < connectionCount; c++) {
            int nx = conX[c];
            int nz = conZ[c];
            float nWeight = connectionWeights[c];
            method.accept(nx, nz, nWeight);
        }
    }
}

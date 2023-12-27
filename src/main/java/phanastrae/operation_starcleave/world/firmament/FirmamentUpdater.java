package phanastrae.operation_starcleave.world.firmament;

import org.apache.logging.log4j.util.TriConsumer;
import org.joml.Math;

import static phanastrae.operation_starcleave.world.firmament.FirmamentSubRegion.TILE_SIZE;

public class FirmamentUpdater {

    public static void update(Firmament firmament) {
        int TILE_SIZE = FirmamentSubRegion.TILE_SIZE;

        // calculate forces
        firmament.forEachActivePosition((x, z) -> {
            float drip = firmament.getDrip(x, z);
            if(drip == 0) return;
            float damage = firmament.getDamage(x, z);
            float displacement = firmament.getDisplacement(x, z);

            float force = - 16 * drip * drip * (1 + damage);

            for(int n = 0; n < nCount; n++) {
                float displacement2 = firmament.getDisplacement(x+nXs[n], z+nZs[n]);
                float damage2 = firmament.getDamage(x+nXs[n], z+nZs[n]);
                float dDisplacement = displacement2 - displacement;

                float f = (1-damage2) * (1-damage);
                force += 256 * dDisplacement * nWeights[n] * f;
            }

            force /= 400;

            float dv = force;
            dv = (int)(dv * 32)/32f;

            float velocity = firmament.getVelocity(x, z);
            if(velocity > -4 || dv > 0) {
                if (dv != 0) {
                    float newVelocity = Math.clamp(-4, 0, velocity + dv);
                    if(newVelocity != velocity) {
                        firmament.setVelocity(x, z, velocity);
                        firmament.markActive(x, z);
                    }
                }
            }
        });

        firmament.forEachActivePosition((x, z) -> {
            float d = firmament.getDisplacement(x, z);
            if(d > -15) {
                float dh = firmament.getVelocity(x, z);
                dh = (int) (dh * 1) / 1f;

                if (dh != 0) {
                    float newDisplacement = Math.clamp(-15, 0, d + dh);
                    if(newDisplacement != d) {
                        firmament.setDisplacement(x, z, d);
                        firmament.markActive(x, z);
                    }
                }
            }

            // update damage and drip

            float threshold = Math.clamp(0, 1, -d / 15);
            threshold = (int)(threshold * 8)/8f;

            float currentDamage = firmament.getDamage(x, z);
            if (currentDamage < threshold) {
                firmament.setDamage(x, z, threshold);
                firmament.markActive(x, z);
                currentDamage = threshold;
            }

            if(firmament.getDrip(x, z) < currentDamage) {
                firmament.setDrip(x, z, currentDamage);
                firmament.markActive(x, z);
            }
        });

        // spread drip
        firmament.forEachActivePosition((x, z) -> {
            float drip = firmament.getDrip(x, z);
            forEachNeighbor((nx, nz, nWeight) -> {
                float drip2 = firmament.getDrip(x+nx, z+nz);

                float threshold = drip - 0.1f * Math.sqrt(nx*nx + nz*nz);

                float t = (threshold - drip2) * nWeight;
                t = (int)(t * 32)/32f;
                if(t > 0) {
                    firmament.setDDrip(x+nx, z+nz, t);
                }
            });
        });
        firmament.forEachActivePosition((x, z) -> {
            float drip = firmament.getDrip(x, z);
            float dDrip = firmament.getDDrip(x, z);
            float newDrip = Math.min(8, drip + dDrip);
            if(newDrip != drip) {
                firmament.setDrip(x, z, drip);
                firmament.markActive(x, z);
            }
            firmament.setDDrip(x, z, 0);
        });
    }

    public static final int nCount = 8;
    public static final int[] nXs = new int[]{TILE_SIZE, -TILE_SIZE, 0, 0, TILE_SIZE, TILE_SIZE, -TILE_SIZE, -TILE_SIZE};
    public static final int[] nZs = new int[]{0, 0, TILE_SIZE, -TILE_SIZE, TILE_SIZE, -TILE_SIZE, TILE_SIZE, -TILE_SIZE};
    static final float aw = 0.1464f;
    static final float dw = 0.1036f;
    public static final float[] nWeights = new float[]{aw, aw, aw, aw, dw, dw, dw, dw};

    public static void forEachNeighbor(TriConsumer<Integer, Integer, Float> method) {
        for(int n = 0; n < nCount; n++) {
            int nx = nXs[n];
            int nz = nZs[n];
            float nWeight = nWeights[n];
            method.accept(nx, nz, nWeight);
        }
    }

    public static float dFdxDamage(Firmament firmament, int x, int z) {
        return (firmament.getDamage(x+1, z) - firmament.getDamage(x-1, z)) / 2;
    }

    public static float dFdzDamage(Firmament firmament, int x, int z) {
        return (firmament.getDamage(x, z+1) - firmament.getDamage(x, z-1)) / 2;
    }

    public static float dFdxBigDamage(Firmament firmament, int x, int z) {
        return 0.5f * dFdxDamage(firmament, x, z) + 0.25f * (dFdxDamage(firmament, x+1, z) + dFdxDamage(firmament, x-1, z));
    }

    public static float dFdzBigDamage(Firmament firmament, int x, int z) {
        return 0.5f * dFdzDamage(firmament, x, z) + 0.25f * (dFdzDamage(firmament, x, z+1) + dFdzDamage(firmament, x, z-1));
    }
}

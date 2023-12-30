package phanastrae.operation_starcleave.world.firmament;

import org.apache.logging.log4j.util.TriConsumer;
import org.joml.Math;

import java.util.concurrent.atomic.AtomicReference;

import static phanastrae.operation_starcleave.world.firmament.FirmamentSubRegion.TILE_SIZE;

public class FirmamentUpdater {

    public static void update(Firmament firmament) {
        int TILE_SIZE = FirmamentSubRegion.TILE_SIZE;

        // calculate forces
        firmament.forEachActivePosition((x, z) -> {
            float drip = firmament.getDrip(x, z);
            if(drip == 0) return;
            int damage = firmament.getDamage(x, z);
            float displacement = firmament.getDisplacement(x, z);

            float force = 16 * (damage / 7f);

            for(int n = 0; n < nCount; n++) {
                float displacement2 = firmament.getDisplacement(x+nXs[n], z+nZs[n]);
                int damage2 = firmament.getDamage(x+nXs[n], z+nZs[n]);
                float dDisplacement = displacement2 - displacement;

                float f = (1 - damage2/7f) * (1 - damage/7f);
                force += 16 * (dDisplacement / 15f) * nWeights[n] * f;
            }

            force *= (displacement * displacement / 215f + 1) / 2;

            float dv = force * (drip / 7f) * 4;
            if(dv < 0) {
                dv *= 0.25f;
            }

            int velocity = firmament.getVelocity(x, z);
            if(velocity < 7 || dv < 0) {
                if (dv != 0) {
                    int newVelocity = Math.clamp(0, 7, velocity + (int)dv);
                    if(newVelocity != velocity) {
                        firmament.setVelocity(x, z, newVelocity);
                        firmament.markActive(x, z);
                    }
                }
            }
        });

        firmament.forEachActivePosition((x, z) -> {
            int d = firmament.getDisplacement(x, z);
            int dh = (firmament.getVelocity(x, z) / 4);
            if(d < 15) {

                if (dh != 0) {
                    int newDisplacement = Math.clamp(0, 15, d + dh);
                    if(newDisplacement != d) {
                        d = newDisplacement;
                        firmament.setDisplacement(x, z, d);
                        firmament.markActive(x, z);
                    }
                }
            }

            // update damage
            int threshold = Math.clamp(0, 7, dh / 4);
            if(d == 15) {
                threshold = firmament.getDamage(x, z) + 1;
                if(threshold > 7) threshold = 7;
            }

            int currentDamage = firmament.getDamage(x, z);
            if (currentDamage < threshold) {
                firmament.setDamage(x, z, threshold);
                firmament.markActive(x, z);
            }
        });

        // spread drip
        firmament.forEachActivePosition((x, z) -> {
            int drip = firmament.getDrip(x, z);
            AtomicReference<Integer> maxPotentialDrip = new AtomicReference<>(0);
            forEachNeighbor((nx, nz, nWeight) -> {
                int nDrip = firmament.getDrip(x+nx, z+nz);
                int potentialDrip = nDrip - 1;
                if(potentialDrip > maxPotentialDrip.get()) {
                    maxPotentialDrip.set(potentialDrip);
                }
            });
            if(maxPotentialDrip.get() > drip) {
                firmament.setDrip(x, z, Math.clamp(drip, 7, maxPotentialDrip.get()));
            }
        });
        firmament.forEachActivePosition((x, z) -> {
            int drip = firmament.getDrip(x, z);
            float dDrip = firmament.getDDrip(x, z);
            int newDrip = Math.clamp(0, 7, drip + (int)dDrip);
            if(newDrip > drip) {
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
        return (firmament.getDamage(x+1, z) - firmament.getDamage(x-1, z)) / 2f;
    }

    public static float dFdzDamage(Firmament firmament, int x, int z) {
        return (firmament.getDamage(x, z+1) - firmament.getDamage(x, z-1)) / 2f;
    }

    public static float dFdxBigDamage(Firmament firmament, int x, int z) {
        return 0.5f * dFdxDamage(firmament, x, z) + 0.25f * (dFdxDamage(firmament, x+1, z) + dFdxDamage(firmament, x-1, z));
    }

    public static float dFdzBigDamage(Firmament firmament, int x, int z) {
        return 0.5f * dFdzDamage(firmament, x, z) + 0.25f * (dFdzDamage(firmament, x, z+1) + dFdzDamage(firmament, x, z-1));
    }
}

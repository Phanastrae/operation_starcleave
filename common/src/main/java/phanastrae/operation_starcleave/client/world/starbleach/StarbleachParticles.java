package phanastrae.operation_starcleave.client.world.starbleach;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Heightmap;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;
import phanastrae.operation_starcleave.world.firmament.Firmament;

public class StarbleachParticles {

    public static void spawnParticles(ClientLevel level, int centerX, int centerY, int centerZ) {
        RandomSource random = level.random;
        Firmament firmament = Firmament.fromLevel(level);
        if(firmament == null) return;

        ParticleOptions particleEffect = OperationStarcleaveParticleTypes.FIRMAMENT_GLIMMER;
        int radius = 32;
        int firmamentY = firmament.getY();
        int minBuildHeight = level.getMinBuildHeight();
        for(int n = 0; n < 14; n++) {
            int x = centerX + random.nextInt(radius) - random.nextInt(radius);
            int z = centerZ + random.nextInt(radius) - random.nextInt(radius);
            int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
            if(surfaceY > centerY + 16) continue;

            int damage = firmament.getDamage(x, z);
            if(damage >= 5) {
                int damageAboveThreshold = damage - 5; // ranges between 0 and 2

                float x2 = x + random.nextFloat();
                float z2 = z + random.nextFloat();

                // do not spawn particles on open void
                if(surfaceY > minBuildHeight) {

                    // spawn particles on the surface, if camera is sufficiently close
                    if (Math.abs(surfaceY - centerY) <= 16) {
                        for (int k = 0; k < 1 + damageAboveThreshold * 2; k++) {
                            level.addParticle(particleEffect, x2, surfaceY, z2, random.nextFloat() * 0.07 - 0.035, 0.03 + random.nextFloat() * 0.04, random.nextFloat() * 0.07 - 0.035);
                        }
                    }
                }

                int aboveSurfaceY = surfaceY + 16;
                float f = random.nextFloat();
                float y2 = Mth.lerp(f * f, aboveSurfaceY, firmamentY);
                // spawn particles somewhere in midair between the surface and firmament, if camera is sufficiently close
                if(Math.abs(y2 - centerY) <= 32) {
                    for (int k = 0; k < 1 + damageAboveThreshold * 2; k++) {
                        level.addParticle(particleEffect, x2, y2, z2, random.nextFloat() * 0.1 - 0.05, -0.07 + random.nextFloat() * -0.1, random.nextFloat() * 0.1 - 0.05);
                    }
                }

                // spawn particles at the firmament, if camera is sufficiently close
                if(Math.abs(firmamentY - centerY) <= 32) {
                    for (int k = 0; k < 3 + damageAboveThreshold * 2; k++) {
                        level.addParticle(particleEffect, x2, firmamentY, z2, random.nextFloat() * 0.1 - 0.05, -0.07 + random.nextFloat() * -0.12, random.nextFloat() * 0.1 - 0.05);
                    }
                }
            }
        }
    }
}

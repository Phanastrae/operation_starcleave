package phanastrae.operation_starcleave.client.world.starbleach;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Heightmap;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;
import phanastrae.operation_starcleave.world.firmament.Firmament;

public class StarbleachParticles {

    public static void spawnParticles(ClientLevel clientWorld, int centerX, int centerY, int centerZ) {
        RandomSource random = clientWorld.random;
        Firmament firmament = Firmament.fromLevel(clientWorld);
        if(firmament == null) return;

        ParticleOptions particleEffect = OperationStarcleaveParticleTypes.FIRMAMENT_GLIMMER;
        int radius = 32;
        int firmHeight = clientWorld.getMaxBuildHeight() + 16;
        int bottomY = clientWorld.getMinBuildHeight();
        for(int n = 0; n < 20; n++) {
            int x = centerX + random.nextInt(radius) - random.nextInt(radius);
            int z = centerZ + random.nextInt(radius) - random.nextInt(radius);
            int y = clientWorld.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
            if(y > centerY + 16) continue;

            int damage = firmament.getDamage(x, z);

            if(damage >= 5) {
                float x2 = x + random.nextFloat();
                float z2 = z + random.nextFloat();
                if(y > bottomY) {
                    if ((y - centerY) * (y - centerY) <= 16 * 16) {
                        for (int k = 0; k < 1 + (damage - 5) * 3; k++) {
                            clientWorld.addParticle(particleEffect, x2, y, z2, random.nextFloat() * 0.07 - 0.035, 0.03 + random.nextFloat() * 0.04, random.nextFloat() * 0.07 - 0.035);
                        }
                    }
                }
                float f = random.nextFloat();
                float y2 = 16 + y + (firmHeight - y - 16) * f * f;
                if((y2-centerY)*(y2-centerY) <= 32*32) {
                    for (int k = 0; k < 1 + (damage - 5) * 3; k++) {
                        clientWorld.addParticle(particleEffect, x2, y2, z2, random.nextFloat() * 0.1 - 0.05, -0.07 + random.nextFloat() * -0.1, random.nextFloat() * 0.1 - 0.05);
                    }
                }

                if((firmHeight-centerY)*(firmHeight-centerY) <= 32*32) {
                    for (int k = 0; k < 3 + (damage - 5) * 8; k++) {
                        clientWorld.addParticle(particleEffect, x2, firmHeight, z2, random.nextFloat() * 0.1 - 0.05, -0.07 + random.nextFloat() * -0.12, random.nextFloat() * 0.1 - 0.05);
                    }
                }
            }
        }

    }
}

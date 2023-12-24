package phanastrae.operation_starcleave.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Math;
import org.joml.Random;
import phanastrae.operation_starcleave.world.firmament.*;

public class FirmamentManipulatorItem extends Item {

    public FirmamentManipulatorItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(world.isClient) {
            FirmamentRegion fr = Firmament.getInstance().firmamentRegion;

            if (user.isSneaking()) {
                for(int i = 0; i < FirmamentRegion.SUBREGIONS; i++) {
                    for(int j = 0; j < FirmamentRegion.SUBREGIONS; j++) {
                        fr.subRegions[i][j] = new FirmamentSubRegion();
                    }
                }
                //FirmamentRegionState state = new FirmamentRegionState(512);
                //FirmamentGenerator.generate(state);
                //fr.state = state;

                fr.clearActors();
            } else {

                float yaw = Math.toRadians(user.getYaw());
                float pitch = Math.toRadians(user.getPitch());

                float sinYaw = Math.sin(yaw);
                float cosYaw = Math.cos(yaw);
                float sinPitch = Math.sin(pitch);
                float cosPitch = Math.cos(pitch);

                Vec3d lookVec = new Vec3d(-sinYaw * cosPitch, -sinPitch, cosYaw * cosPitch);

                Vec3d pos = user.getPos();

                float skyHeight = 256;
                double t = (skyHeight - pos.y) / lookVec.y;
                Vec3d target = pos.add(lookVec.multiply(t));

                Random random = new Random();
                for(int i = 0; i < 10; i++) {
                    world.addParticle(ParticleTypes.EXPLOSION, target.x+random.nextFloat() * 4 - 2, target.y+random.nextFloat() * 1 - 0.5f, target.z+random.nextFloat() * 4 - 2, 0, 0, 0);
                }

                int x = (int)target.x;
                int z = (int)target.z;

                int rad = 15;
                for(int i = -rad; i <= rad; i++) {
                    for(int j = -rad; j <= rad; j++) {
                        float distSqr = i*i + j*j;

                        if(distSqr < 8) {
                            fr.setDamage(x+i, z+j, 1);
                        }


                        if(distSqr > rad*rad) continue;
                        float fallOff = 1 - (distSqr)/(rad*rad);
                        //fr.state.drip.addNow(x+i, z+j, 0.2f * fallOff);
                        fr.setDrip(x+i, z+j, fr.getDrip(x+i, z+j) + (int)(0.01f * fallOff * fallOff * fallOff * 16f) / 16f);
                    }
                }

                /*
                net.minecraft.util.math.random.Random random = user.getRandom();
                for(int k = 0; k < 6; k++) {
                    float rad = random.nextFloat() * 4 + 2;
                    float theta = random.nextFloat() * 2 * (float)Math.PI;
                    float dist = random.nextFloat() * 10 + 10;
                    int px = (int)(Math.cos(theta) * dist);
                    int pz = (int)(Math.sin(theta) * dist);

                    int radCap = (int)Math.ceil(Math.abs(rad));
                    for(int i = -radCap; i <= radCap; i++) {
                        for(int j = -radCap; j <= radCap; j++) {
                            float dSqr = i*i + j*j;
                            if(dSqr > rad*rad) continue;

                            float d = Math.sqrt(i*i + j*j);
                            float fallOff = (float)Math.exp(-d * 0.5f) * (radCap * radCap - dSqr) / (radCap * radCap);
                            fr.state.drip.addNow(x+px+i, z+pz+j, 3f * fallOff);
                        }
                    }
                }
                */

                formCrack(fr, x, z);
            }
        }

        ItemStack itemStack = user.getStackInHand(hand);
        return TypedActionResult.consume(itemStack);
    }

    public void formCrack(FirmamentRegion fr, int x, int z) {
        Random random = new Random();

        float phase = random.nextFloat();
        int count = 10;
        for(int i = 0; i < count; i++) {
            float theta = (phase + i / (float)count) * 2 * (float)Math.PI;
            fr.addActor(new Actor(x, z, 4 * Math.cos(theta), 4 * Math.sin(theta), 40));
        }
    }
}

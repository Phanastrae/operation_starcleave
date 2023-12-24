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
            Firmament firmament = Firmament.getInstance();

            if (user.isSneaking()) {
                firmament.firmamentRegion = new FirmamentRegion(0, 0);
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
                            firmament.setDamage(x+i, z+j, 1);
                        }


                        if(distSqr > rad*rad) continue;
                        float fallOff = 1 - (distSqr)/(rad*rad);
                        firmament.setDrip(x+i, z+j, firmament.getDrip(x+i, z+j) + (int)(0.01f * fallOff * fallOff * fallOff * 16f) / 16f);
                    }
                }

                formCrack(firmament, x, z);
            }
        }

        ItemStack itemStack = user.getStackInHand(hand);
        return TypedActionResult.consume(itemStack);
    }

    public void formCrack(Firmament firmament, int x, int z) {
        Random random = new Random();

        float phase = random.nextFloat();
        int count = 10;
        for(int i = 0; i < count; i++) {
            float theta = (phase + i / (float)count) * 2 * (float)Math.PI;
            firmament.addActor(new FirmamentActor(firmament, x, z, 4 * Math.cos(theta), 4 * Math.sin(theta), 40));
        }
    }
}

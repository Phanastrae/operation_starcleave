package phanastrae.operation_starcleave.item;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;
import phanastrae.operation_starcleave.network.packet.s2c.FirmamentCleavedS2CPacket;
import phanastrae.operation_starcleave.world.firmament.*;

import java.util.List;

import static phanastrae.operation_starcleave.world.firmament.FirmamentSubRegion.TILE_SIZE;

public class FirmamentManipulatorItem extends Item {

    public FirmamentManipulatorItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if(!user.getAbilities().allowModifyWorld) {
            return TypedActionResult.fail(itemStack);
        }


        if(!world.isClient) {
            Firmament firmament = Firmament.fromWorld(world);
            if(firmament == null) {
                return super.use(world, user, hand);
            }

            float pitch = Math.toRadians(user.getPitch());
            if(pitch > 0) return TypedActionResult.fail(itemStack);
            Vec3d pos = user.getCameraPosVec(1.0F);
            Vec3d lookVec = user.getRotationVec(1.0F);

            float skyHeight = firmament.getY();
            double t = (skyHeight - pos.y) / lookVec.y;
            if(t > 0) {
                Vec3d target = pos.add(lookVec.multiply(t));

                if (user.isSneaking()) {
                    SubRegionPos subRegionPos = SubRegionPos.fromWorldCoords((int)target.x, (int)target.z);
                    for(int i = -1; i <= 1; i++) {
                        for(int j = -1; j <= 1; j++) {
                            SubRegionPos subRegionPos2 = new SubRegionPos(subRegionPos.srx+i, subRegionPos.srz+j);
                            FirmamentSubRegion firmamentSubRegion = firmament.getSubRegionFromId(subRegionPos2.id);
                            if(firmamentSubRegion != null) {
                                firmamentSubRegion.clear();
                            }
                        }
                    }
                } else {

                    Random random = user.getRandom();
                    for (int i = 0; i < 10; i++) {
                        world.addParticle(ParticleTypes.EXPLOSION, target.x + random.nextFloat() * 4 - 2, target.y + random.nextFloat() * 1 - 0.5f, target.z + random.nextFloat() * 4 - 2, 0, 0, 0);
                    }

                    int x = (int) target.x;
                    int z = (int) target.z;
                    fractureFirmament(firmament, x, z, random);
                }
            }
        }

        return TypedActionResult.success(itemStack);
    }

    public static void fractureFirmament(Firmament firmament, int x, int z, Random random) {
        if(firmament.getWorld() instanceof ServerWorld world) {
            for(ServerPlayerEntity player : world.getPlayers()) {
                ServerPlayNetworking.send(player, new FirmamentCleavedS2CPacket(x, z));
            }
        }

        firmament.setDamage(x, z, Math.clamp(6, 7, firmament.getDamage(x, z) + 6));
        firmament.setDamage(x+TILE_SIZE, z+TILE_SIZE, Math.clamp(2, 7, firmament.getDamage(x+TILE_SIZE, z+TILE_SIZE) + 2));
        firmament.setDamage(x-TILE_SIZE, z+TILE_SIZE, Math.clamp(2, 7, firmament.getDamage(x-TILE_SIZE, z+TILE_SIZE) + 2));
        firmament.setDamage(x+TILE_SIZE, z-TILE_SIZE, Math.clamp(2, 7, firmament.getDamage(x+TILE_SIZE, z-TILE_SIZE) + 2));
        firmament.setDamage(x-TILE_SIZE, z-TILE_SIZE, Math.clamp(2, 7, firmament.getDamage(x-TILE_SIZE, z-TILE_SIZE) + 2));
        for(int i = -1; i <= 1; i++) {
            for(int j = -1; j <= 1; j++) {
                firmament.markActive(x + i * TILE_SIZE, z + j * TILE_SIZE);
            }
        }

        int rad = 15;
        for(int i = -rad; i <= rad; i++) {
            for(int j = -rad; j <= rad; j++) {
                float distSqr = i*i + j*j;

                if(distSqr > rad*rad) continue;
                float fallOff = 1 - (distSqr)/(rad*rad);
                firmament.setDrip(x+i*TILE_SIZE, z+j*TILE_SIZE, firmament.getDrip(x+i*TILE_SIZE, z+j*TILE_SIZE) + (int)(0.07f * fallOff * fallOff * fallOff));
            }
        }

        float phase = random.nextFloat();
        int count = 10;
        for(int i = 0; i < count; i++) {
            float theta = (phase + i / (float)count) * 2 * (float)Math.PI;
            FirmamentShatterActor actor = new FirmamentShatterActor(firmament, x, z, Math.cos(theta)*TILE_SIZE, Math.sin(theta)*TILE_SIZE, 1000);
            actor.initialDelay = 32;
            firmament.addActor(actor);
        }
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        tooltip.add(Text.translatable("operation_starcleave.tooltip.firmament_manipulator.1").formatted(Formatting.GOLD));
        tooltip.add(Text.translatable("operation_starcleave.tooltip.firmament_manipulator.2").formatted(Formatting.GOLD));
        tooltip.add(Text.translatable("operation_starcleave.tooltip.firmament_manipulator.3").formatted(Formatting.DARK_RED));
    }
}

package phanastrae.operation_starcleave.item;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Math;
import phanastrae.operation_starcleave.network.packet.FirmamentCleavedPayload;
import phanastrae.operation_starcleave.services.XPlatInterface;
import phanastrae.operation_starcleave.world.firmament.Firmament;
import phanastrae.operation_starcleave.world.firmament.FirmamentShatterActor;
import phanastrae.operation_starcleave.world.firmament.FirmamentSubRegion;
import phanastrae.operation_starcleave.world.firmament.SubRegionPos;

import java.util.List;

import static phanastrae.operation_starcleave.world.firmament.FirmamentSubRegion.TILE_SIZE;

public class FirmamentManipulatorItem extends Item {

    public FirmamentManipulatorItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        if(!user.getAbilities().mayBuild) {
            return InteractionResultHolder.fail(itemStack);
        }


        if(!world.isClientSide) {
            Firmament firmament = Firmament.fromWorld(world);
            if(firmament == null) {
                return super.use(world, user, hand);
            }

            float pitch = Math.toRadians(user.getXRot());
            if(pitch > 0) return InteractionResultHolder.fail(itemStack);
            Vec3 pos = user.getEyePosition(1.0F);
            Vec3 lookVec = user.getViewVector(1.0F);

            float skyHeight = firmament.getY();
            double t = (skyHeight - pos.y) / lookVec.y;
            if(t > 0) {
                Vec3 target = pos.add(lookVec.scale(t));

                if (user.isShiftKeyDown()) {
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

                    RandomSource random = user.getRandom();
                    for (int i = 0; i < 10; i++) {
                        world.addParticle(ParticleTypes.EXPLOSION, target.x + random.nextFloat() * 4 - 2, target.y + random.nextFloat() * 1 - 0.5f, target.z + random.nextFloat() * 4 - 2, 0, 0, 0);
                    }

                    int x = (int) target.x;
                    int z = (int) target.z;
                    fractureFirmament(firmament, x, z, random);
                }
            }
        }

        return InteractionResultHolder.success(itemStack);
    }

    public static void fractureFirmament(Firmament firmament, int x, int z, RandomSource random) {
        if(firmament.getWorld() instanceof ServerLevel world) {
            for(ServerPlayer player : world.players()) {
                XPlatInterface.INSTANCE.sendPayload(player, new FirmamentCleavedPayload(x, z));
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
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        super.appendHoverText(stack, context, tooltip, type);
        tooltip.add(Component.translatable("operation_starcleave.tooltip.firmament_manipulator.1").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("operation_starcleave.tooltip.firmament_manipulator.2").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("operation_starcleave.tooltip.firmament_manipulator.3").withStyle(ChatFormatting.DARK_RED));
    }
}

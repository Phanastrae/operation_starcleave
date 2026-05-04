package phanastrae.operation_starcleave.item;

import net.minecraft.ChatFormatting;
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
import phanastrae.operation_starcleave.world.firmament.actor.FirmamentShatterActor;

import java.util.List;

import static phanastrae.operation_starcleave.world.firmament.FirmamentSubRegion.TILE_SIZE;

public class FirmamentManipulatorItem extends Item {
    public static final int MAX_HORIZONTAL_DISTANCE = 500;

    public FirmamentManipulatorItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        if (!user.getAbilities().mayBuild) {
            return InteractionResultHolder.fail(itemStack);
        }

        Firmament firmament = Firmament.fromLevel(level);
        if (firmament == null) {
            return InteractionResultHolder.fail(itemStack);
        }

        float pitch = Math.toRadians(user.getXRot());
        if (pitch >= 0) {
            return InteractionResultHolder.fail(itemStack);
        }

        Vec3 eyePos = user.getEyePosition(1.0F);
        Vec3 lookVec = user.getViewVector(1.0F);

        float skyHeight = firmament.getY();
        double t = (skyHeight - eyePos.y) / lookVec.y;
        if (t <= 0) {
            return InteractionResultHolder.fail(itemStack);
        }

        Vec3 offset = lookVec.scale(t);
        if (offset.horizontalDistance() > MAX_HORIZONTAL_DISTANCE) {
            return InteractionResultHolder.fail(itemStack);
        }
        Vec3 target = eyePos.add(offset);

        if (!level.isClientSide) {
            if (user.isShiftKeyDown()) {
                clearFirmamentCircle(firmament, (int) target.x, (int) target.z, 11);
            } else {
                fractureFirmament(firmament, (int) target.x, (int) target.z, user.getRandom());
            }
        }

        return InteractionResultHolder.success(itemStack);
    }

    public static void clearFirmamentCircle(Firmament firmament, int centerX, int centerZ, int radius) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz > radius * radius) {
                    continue;
                }

                int x = centerX + dx * TILE_SIZE;
                int z = centerZ + dz * TILE_SIZE;

                firmament.setDamage(x, z, 0);
            }
        }
    }

    public static void fractureFirmament(Firmament firmament, int x, int z, RandomSource random) {
        if (firmament.getLevel() instanceof ServerLevel world) {
            for (ServerPlayer player : world.players()) {
                XPlatInterface.INSTANCE.sendPayload(player, new FirmamentCleavedPayload(x, z));
            }
        }

        firmament.setDamage(x, z, Math.clamp(6, 7, firmament.getDamage(x, z) + 6));
        firmament.setDamage(x + TILE_SIZE, z + TILE_SIZE, Math.clamp(2, 7, firmament.getDamage(x + TILE_SIZE, z + TILE_SIZE) + 2));
        firmament.setDamage(x - TILE_SIZE, z + TILE_SIZE, Math.clamp(2, 7, firmament.getDamage(x - TILE_SIZE, z + TILE_SIZE) + 2));
        firmament.setDamage(x + TILE_SIZE, z - TILE_SIZE, Math.clamp(2, 7, firmament.getDamage(x + TILE_SIZE, z - TILE_SIZE) + 2));
        firmament.setDamage(x - TILE_SIZE, z - TILE_SIZE, Math.clamp(2, 7, firmament.getDamage(x - TILE_SIZE, z - TILE_SIZE) + 2));

        float phase = random.nextFloat();
        int count = 10;
        for (int i = 0; i < count; i++) {
            float theta = (phase + i / (float) count) * 2 * (float) Math.PI;
            FirmamentShatterActor actor = new FirmamentShatterActor(firmament, x, z, Math.cos(theta) * TILE_SIZE, Math.sin(theta) * TILE_SIZE, 1000);
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

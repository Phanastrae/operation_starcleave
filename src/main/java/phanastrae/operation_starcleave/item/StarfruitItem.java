package phanastrae.operation_starcleave.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import phanastrae.operation_starcleave.entity.OperationStarcleaveDamageTypes;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;

public class StarfruitItem extends Item {
    public StarfruitItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack itemStack = super.finishUsing(stack, world, user);

        float yaw = user.getYaw() * MathHelper.PI / 180;
        float pitch = user.getPitch() * MathHelper.PI / 180;
        float cosYaw = MathHelper.cos(yaw);
        float sinYaw = MathHelper.sin(yaw);
        float cosPitch = MathHelper.cos(pitch);
        float sinPitch = MathHelper.sin(pitch);
        Vec3d lookVec = new Vec3d(-sinYaw * cosPitch, -sinPitch, cosYaw * cosPitch);

        if (!world.isClient) {
            if (user.hasVehicle()) {
                user.stopRiding();
            }

            user.addVelocity(lookVec.multiply(2));
            user.velocityModified = true;
            user.fallDistance = -4;

            Vec3d vec3d = user.getPos();
            world.emitGameEvent(GameEvent.PROJECTILE_SHOOT, vec3d, GameEvent.Emitter.of(user));
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_SPLASH_POTION_BREAK, SoundCategory.PLAYERS);

            user.damage(OperationStarcleaveDamageTypes.of(world, OperationStarcleaveDamageTypes.INTERNAL_STARBLEACHING), 4);
        } else {
            Vec3d pos = user.getEyePos();
            Random random = user.getRandom();
            for(int i = 0; i < 500; i++) {
                world.addParticle(OperationStarcleaveParticleTypes.FIRMAMENT_GLIMMER, pos.x, pos.y, pos.z, -lookVec.x * 0.1f + random.nextGaussian() * 0.2f, -lookVec.y * 0.1f + random.nextGaussian() * 0.2f, -lookVec.z * 0.1f + random.nextGaussian() * 0.2f);
            }
        }

        return itemStack;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 12;
    }

    public static final FoodComponent STARFRUIT = new FoodComponent.Builder().hunger(2).saturationModifier(0.5F).alwaysEdible().snack().build();
}

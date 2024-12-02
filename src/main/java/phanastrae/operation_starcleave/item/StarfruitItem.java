package phanastrae.operation_starcleave.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import phanastrae.operation_starcleave.entity.OperationStarcleaveDamageTypes;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;

public class StarfruitItem extends Item {
    public StarfruitItem(Properties settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        ItemStack itemStack = super.finishUsingItem(stack, world, user);

        float yaw = user.getYRot() * Mth.PI / 180;
        float pitch = user.getXRot() * Mth.PI / 180;
        float cosYaw = Mth.cos(yaw);
        float sinYaw = Mth.sin(yaw);
        float cosPitch = Mth.cos(pitch);
        float sinPitch = Mth.sin(pitch);
        Vec3 lookVec = new Vec3(-sinYaw * cosPitch, -sinPitch, cosYaw * cosPitch);

        if (!world.isClientSide) {
            if (user.isPassenger()) {
                user.stopRiding();
            }

            user.push(lookVec.scale(2));
            user.hurtMarked = true;
            user.fallDistance = -4;

            Vec3 vec3d = user.position();
            world.gameEvent(GameEvent.PROJECTILE_SHOOT, vec3d, GameEvent.Context.of(user));
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.SPLASH_POTION_BREAK, SoundSource.PLAYERS);

            user.hurt(OperationStarcleaveDamageTypes.of(world, OperationStarcleaveDamageTypes.INTERNAL_STARBLEACHING), 4);
        } else {
            Vec3 pos = user.getEyePosition();
            RandomSource random = user.getRandom();
            for(int i = 0; i < 500; i++) {
                world.addParticle(OperationStarcleaveParticleTypes.FIRMAMENT_GLIMMER, pos.x, pos.y, pos.z, -lookVec.x * 0.1f + random.nextGaussian() * 0.2f, -lookVec.y * 0.1f + random.nextGaussian() * 0.2f, -lookVec.z * 0.1f + random.nextGaussian() * 0.2f);
            }
        }

        return itemStack;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        return 12;
    }

    public static final FoodProperties STARFRUIT = new FoodProperties.Builder().nutrition(2).saturationModifier(0.5F).alwaysEdible().fast().build();
}

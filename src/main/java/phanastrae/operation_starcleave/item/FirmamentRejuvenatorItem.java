package phanastrae.operation_starcleave.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import phanastrae.operation_starcleave.entity.projectile.FirmamentRejuvenatorEntity;

public class FirmamentRejuvenatorItem extends Item {

    public FirmamentRejuvenatorItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if(!user.getAbilities().allowModifyWorld) {
            return TypedActionResult.fail(itemStack);
        }

        world.playSound(
                null,
                user.getX(),
                user.getY(),
                user.getZ(),
                SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER,
                SoundCategory.PLAYERS,
                0.5F,
                0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F)
        );

        if (!world.isClient) {
            FirmamentRejuvenatorEntity entity = new FirmamentRejuvenatorEntity(world, user);
            entity.setVelocity(user, user.getPitch(), user.getYaw(), -20.0F, 1.5F, 1.0F);
            world.spawnEntity(entity);

            user.getItemCooldownManager().set(this, 60);
        }

        user.incrementStat(Stats.USED.getOrCreateStat(this));
        if (!user.getAbilities().creativeMode) {
            itemStack.decrement(1);
        }

        return super.use(world, user, hand);
    }
}

package phanastrae.operation_starcleave.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.entity.projectile.SplashStarbleachEntity;

import java.util.List;

public class SplashStarbleachBottleItem extends Item {

    public SplashStarbleachBottleItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        world.playSound(
                null,
                user.getX(),
                user.getY(),
                user.getZ(),
                SoundEvents.ENTITY_SPLASH_POTION_THROW,
                SoundCategory.PLAYERS,
                0.5F,
                0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F)
        );

        ItemStack itemStack = user.getStackInHand(hand);
        if (!world.isClient) {
            SplashStarbleachEntity entity = new SplashStarbleachEntity(world, user);
            entity.setCanStarbleach(user.getAbilities().allowModifyWorld);
            entity.setVelocity(user, user.getPitch(), user.getYaw(), -20.0F, 0.75F, 1.0F);
            world.spawnEntity(entity);
        }

        user.incrementStat(Stats.USED.getOrCreateStat(this));
        if (!user.getAbilities().creativeMode) {
            itemStack.decrement(1);
        }

        return TypedActionResult.success(itemStack, world.isClient());
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(StarbleachCoating.getText("operation_starcleave.tooltip.starbleach"));
        super.appendTooltip(stack, world, tooltip, context);
    }
}

package phanastrae.operation_starcleave.item;

import phanastrae.operation_starcleave.entity.projectile.SplashStarbleachEntity;

import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class SplashStarbleachBottleItem extends Item implements ProjectileItem {

    public SplashStarbleachBottleItem(Item.Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        world.playSound(
                null,
                user.getX(),
                user.getY(),
                user.getZ(),
                SoundEvents.SPLASH_POTION_THROW,
                SoundSource.PLAYERS,
                0.5F,
                0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F)
        );

        ItemStack itemStack = user.getItemInHand(hand);
        if (!world.isClientSide) {
            SplashStarbleachEntity entity = new SplashStarbleachEntity(world, user);
            entity.setCanStarbleach(user.getAbilities().mayBuild);
            entity.shootFromRotation(user, user.getXRot(), user.getYRot(), -20.0F, 0.75F, 1.0F);
            world.addFreshEntity(entity);
        }

        user.awardStat(Stats.ITEM_USED.get(this));
        if (!user.getAbilities().instabuild) {
            itemStack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(itemStack, world.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        tooltip.add(StarbleachCoating.getText("operation_starcleave.tooltip.starbleach"));
        super.appendHoverText(stack, context, tooltip, type);
    }

    @Override
    public Projectile asProjectile(Level world, Position pos, ItemStack stack, Direction direction) {
        SplashStarbleachEntity entity = new SplashStarbleachEntity(world, pos.x(), pos.y(), pos.z());
        entity.setItem(stack);
        return entity;
    }

    @Override
    public ProjectileItem.DispenseConfig createDispenseConfig() {
        return ProjectileItem.DispenseConfig.builder()
                .uncertainty(ProjectileItem.DispenseConfig.DEFAULT.uncertainty() * 0.5F)
                .power(ProjectileItem.DispenseConfig.DEFAULT.power() * 1.25F)
                .build();
    }
}

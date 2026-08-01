package phanastrae.operation_starcleave.item;

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
import phanastrae.operation_starcleave.entity.projectile.SplashStarbleachEntity;

import java.util.List;

public class SplashStarbleachBottleItem extends Item implements ProjectileItem {

    public SplashStarbleachBottleItem(Item.Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            SplashStarbleachEntity entity = new SplashStarbleachEntity(level, player);
            entity.setCanStarbleach(player.getAbilities().mayBuild);
            entity.shootFromRotation(player, player.getXRot(), player.getYRot(), -20.0F, 0.75F, 1.0F);
            level.addFreshEntity(entity);
        }

        level.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.SPLASH_POTION_THROW,
                SoundSource.PLAYERS,
                0.5F,
                0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
        );

        player.awardStat(Stats.ITEM_USED.get(this));
        itemStack.consume(1, player);

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
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
        entity.setCanStarbleach(true);
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

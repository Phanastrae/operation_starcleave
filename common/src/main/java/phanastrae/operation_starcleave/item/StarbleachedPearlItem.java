package phanastrae.operation_starcleave.item;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.Level;
import phanastrae.operation_starcleave.entity.projectile.StarbleachedPearlEntity;
import phanastrae.operation_starcleave.sound.OperationStarcleaveSoundEvents;

public class StarbleachedPearlItem extends Item implements ProjectileItem {
    public StarbleachedPearlItem(Item.Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            StarbleachedPearlEntity entity = new StarbleachedPearlEntity(level, player);
            entity.setItem(itemStack);
            entity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.5F, 1.0F);
            level.addFreshEntity(entity);
        }

        level.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                OperationStarcleaveSoundEvents.STARBLEACHED_PEARL_THROW,
                SoundSource.NEUTRAL,
                0.5F,
                0.8F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
        );

        player.getCooldowns().addCooldown(this, 10);
        player.awardStat(Stats.ITEM_USED.get(this));
        itemStack.consume(1, player);

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }

    @Override
    public Projectile asProjectile(Level world, Position pos, ItemStack stack, Direction direction) {
        StarbleachedPearlEntity entity = new StarbleachedPearlEntity(world, pos.x(), pos.y(), pos.z());
        entity.setItem(stack);
        return entity;
    }

    @Override
    public ProjectileItem.DispenseConfig createDispenseConfig() {
        return ProjectileItem.DispenseConfig.builder()
                .power(ProjectileItem.DispenseConfig.DEFAULT.power() * 1.5F)
                .build();
    }
}

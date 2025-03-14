package phanastrae.operation_starcleave.item;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
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
import net.minecraft.world.level.Level;
import phanastrae.operation_starcleave.entity.projectile.StarbleachedPearlEntity;

public class StarbleachedPearlItem extends Item implements ProjectileItem {
    public StarbleachedPearlItem(Item.Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        world.playSound(
                null,
                user.getX(),
                user.getY(),
                user.getZ(),
                SoundEvents.ENDER_PEARL_THROW,
                SoundSource.NEUTRAL,
                0.5F,
                0.8F / (world.getRandom().nextFloat() * 0.4F + 0.8F)
        );
        user.getCooldowns().addCooldown(this, 10);
        if (!world.isClientSide) {
            StarbleachedPearlEntity entity = new StarbleachedPearlEntity(world, user);
            entity.setItem(itemStack);
            entity.shootFromRotation(user, user.getXRot(), user.getYRot(), 0.0F, 2.5F, 1.0F);
            world.addFreshEntity(entity);
        }

        user.awardStat(Stats.ITEM_USED.get(this));
        if (!user.getAbilities().instabuild) {
            itemStack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(itemStack, world.isClientSide());
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

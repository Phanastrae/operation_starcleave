package phanastrae.operation_starcleave.item;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import phanastrae.operation_starcleave.entity.projectile.FirmamentRejuvenatorEntity;

public class FirmamentRejuvenatorItem extends Item implements ProjectileItem {

    public FirmamentRejuvenatorItem(Item.Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        if (!user.getAbilities().mayBuild) {
            return InteractionResultHolder.fail(itemStack);
        } else {
            user.startUsingItem(hand);
            return InteractionResultHolder.consume(itemStack);
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks) {
        int i = this.getUseDuration(stack, user) - remainingUseTicks;
        if(i < 4) return;

        world.playSound(
                null,
                user.getX(),
                user.getY(),
                user.getZ(),
                SoundEvents.LIGHTNING_BOLT_THUNDER,
                SoundSource.PLAYERS,
                0.5F,
                0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F)
        );

        if (!world.isClientSide) {
            FirmamentRejuvenatorEntity entity = new FirmamentRejuvenatorEntity(world, user);
            float speed = Math.min(6.66F, i / 8f);
            entity.shootFromRotation(user, user.getXRot(), user.getYRot(), -20.0F, speed, 1.0F);
            world.addFreshEntity(entity);

            if(user instanceof Player player) {
                player.getCooldowns().addCooldown(this, 15);
            }
        }

        if(user instanceof Player player) {
            player.awardStat(Stats.ITEM_USED.get(this));
        }
        if (!(user instanceof Player player) || !player.getAbilities().instabuild) {
            stack.shrink(1);
        }
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.SPEAR;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    @Override
    public Projectile asProjectile(Level world, Position pos, ItemStack stack, Direction direction) {
        FirmamentRejuvenatorEntity entity = new FirmamentRejuvenatorEntity(world, pos.x(), pos.y(), pos.z());
        entity.setItem(stack);
        return entity;
    }

    @Override
    public ProjectileItem.DispenseConfig createDispenseConfig() {
        return ProjectileItem.DispenseConfig.builder()
                .uncertainty(ProjectileItem.DispenseConfig.DEFAULT.uncertainty() * 0.1F)
                .power(ProjectileItem.DispenseConfig.DEFAULT.power() * 1.5F)
                .build();
    }
}

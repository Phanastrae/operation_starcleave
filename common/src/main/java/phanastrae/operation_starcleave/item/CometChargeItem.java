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
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;
import phanastrae.operation_starcleave.entity.projectile.CometChargeEntity;
import phanastrae.operation_starcleave.sound.OperationStarcleaveSoundEvents;

public class CometChargeItem extends Item implements ProjectileItem {

    public CometChargeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()) {
            CometChargeEntity cometCharge = new CometChargeEntity(player, level);
            cometCharge.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 0.4F, 3.0F);
            cometCharge.setPos(cometCharge.position().subtract(0, cometCharge.getBbHeight() / 2.0, 0));
            cometCharge.setCanDestroy(player.getAbilities().mayBuild);
            level.addFreshEntity(cometCharge);
        }

        level.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                OperationStarcleaveSoundEvents.COMET_CHARGE_THROW,
                SoundSource.NEUTRAL,
                0.6F,
                1.2F + 0.4F * level.getRandom().nextFloat()
        );

        ItemStack itemStack = player.getItemInHand(hand);

        player.getCooldowns().addCooldown(this, 10);
        player.awardStat(Stats.ITEM_USED.get(this));
        itemStack.consume(1, player);

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }

    @Override
    public Projectile asProjectile(Level level, Position position, ItemStack stack, Direction direction) {
        return new CometChargeEntity(position.x(), position.y(), position.z(), level);
    }

    @Override
    public ProjectileItem.DispenseConfig createDispenseConfig() {
        return ProjectileItem.DispenseConfig.builder()
                .positionFunction((blockSource, direction) -> DispenserBlock.getDispensePosition(blockSource, 1.0, new Vec3(0, OperationStarcleaveEntityTypes.COMET_CHARGE.getDimensions().height() * -0.5, 0)))
                .uncertainty(9.0F)
                .power(0.4F)
                .build();
    }
}

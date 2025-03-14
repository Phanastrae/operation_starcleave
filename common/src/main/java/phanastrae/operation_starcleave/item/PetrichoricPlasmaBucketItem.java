package phanastrae.operation_starcleave.item;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

public class PetrichoricPlasmaBucketItem extends BucketItem {

    public PetrichoricPlasmaBucketItem(Fluid content, Properties properties) {
        super(content, properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        InteractionResultHolder<ItemStack> result = super.use(level, player, hand);
        ItemStack stack = result.getObject();
        if(stack.is(Items.BUCKET)) {
            stack = ItemStack.EMPTY;
            level.playSound(
                    player,
                    player,
                    Items.IRON_PICKAXE.getBreakingSound(),
                    SoundSource.PLAYERS,
                    0.8F,
                    1.0F
            );
        }

        return new InteractionResultHolder<>(result.getResult(), stack);
    }
}

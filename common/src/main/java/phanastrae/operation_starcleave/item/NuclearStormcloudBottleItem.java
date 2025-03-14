package phanastrae.operation_starcleave.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;

import java.util.Objects;

public class NuclearStormcloudBottleItem extends Item {

    public NuclearStormcloudBottleItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel)) {
            return InteractionResult.SUCCESS;
        } else {
            ItemStack stack = context.getItemInHand();
            BlockPos pos = context.getClickedPos();
            Direction direction = context.getClickedFace();
            BlockState state = level.getBlockState(pos);

            BlockPos targetPos;
            if (state.getCollisionShape(level, pos).isEmpty()) {
                targetPos = pos;
            } else {
                targetPos = pos.relative(direction);
            }

            EntityType<?> entitytype = OperationStarcleaveEntityTypes.NUCLEAR_STORMCLOUD;
            if (entitytype.spawn(
                    (ServerLevel)level,
                    stack,
                    context.getPlayer(),
                    targetPos.offset(0, 3, 0),
                    MobSpawnType.SPAWN_EGG,
                    true,
                    !Objects.equals(pos, targetPos) && direction == Direction.UP
            )
                    != null) {
                stack.shrink(1);
                level.gameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, pos);
            }

            return InteractionResult.CONSUME;
        }
    }
}

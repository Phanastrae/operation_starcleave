package phanastrae.operation_starcleave.world.loot;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.Set;

public class BrokenByExplosionCondition implements LootItemCondition {
    private static final BrokenByExplosionCondition INSTANCE = new BrokenByExplosionCondition();
    public static final MapCodec<BrokenByExplosionCondition> CODEC = MapCodec.unit(INSTANCE);

    private BrokenByExplosionCondition() {
    }

    @Override
    public LootItemConditionType getType() {
        return OperationStarcleaveLootItemConditions.BROKEN_BY_EXPLOSION;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(OperationStarcleaveLootContextParams.BROKEN_BY_EXPLOSION);
    }

    public boolean test(LootContext context) {
        Boolean bool = context.getParamOrNull(OperationStarcleaveLootContextParams.BROKEN_BY_EXPLOSION);
        if (bool != null) {
            return bool;
        } else {
            return false;
        }
    }

    public static LootItemCondition.Builder destroyedByExplosion() {
        return () -> INSTANCE;
    }
}

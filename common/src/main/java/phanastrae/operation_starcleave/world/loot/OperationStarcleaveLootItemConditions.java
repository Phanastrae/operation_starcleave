package phanastrae.operation_starcleave.world.loot;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import phanastrae.operation_starcleave.OperationStarcleave;

import java.util.function.BiConsumer;

public class OperationStarcleaveLootItemConditions {

    public static final LootItemConditionType BROKEN_BY_EXPLOSION = new LootItemConditionType(BrokenByExplosionCondition.CODEC);

    public static void init(BiConsumer<ResourceLocation, LootItemConditionType> r) {
        r.accept(OperationStarcleave.id("broken_by_explosion"), BROKEN_BY_EXPLOSION);
    }
}

package phanastrae.operation_starcleave.advancement.criterion;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import phanastrae.operation_starcleave.OperationStarcleave;

public class OperationStarcleaveAdvancementCriteria {

    public static final PlayerTrigger LAUNCH_STARCLEAVER_GOLEM = register("launch_starcleaver_golem", new PlayerTrigger());
    public static final PlayerTrigger CLEAVE_FIRMAMENT = register("cleave_firmament", new PlayerTrigger());

    public static void init() {
    }

    public static <T extends CriterionTrigger<?>> T register(String id, T criterion) {
        return Registry.register(BuiltInRegistries.TRIGGER_TYPES, OperationStarcleave.id(id), criterion);
    }
}

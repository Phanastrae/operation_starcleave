package phanastrae.operation_starcleave.advancement.criterion;

import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import phanastrae.operation_starcleave.OperationStarcleave;

public class OperationStarcleaveAdvancementCriteria {

    public static final TickCriterion LAUNCH_STARCLEAVER_GOLEM = register("launch_starcleaver_golem", new TickCriterion());
    public static final TickCriterion CLEAVE_FIRMAMENT = register("cleave_firmament", new TickCriterion());

    public static void init() {
    }

    public static <T extends Criterion<?>> T register(String id, T criterion) {
        return Registry.register(Registries.CRITERION, OperationStarcleave.id(id), criterion);
    }
}

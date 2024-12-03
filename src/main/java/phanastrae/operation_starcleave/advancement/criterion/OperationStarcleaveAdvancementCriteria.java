package phanastrae.operation_starcleave.advancement.criterion;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.resources.ResourceLocation;
import phanastrae.operation_starcleave.OperationStarcleave;

import java.util.function.BiConsumer;

public class OperationStarcleaveAdvancementCriteria {

    public static final PlayerTrigger LAUNCH_STARCLEAVER_GOLEM = new PlayerTrigger();
    public static final PlayerTrigger CLEAVE_FIRMAMENT = new PlayerTrigger();

    public static void init(BiConsumer<ResourceLocation, CriterionTrigger<?>> r) {
        r.accept(id("launch_starcleaver_golem"), LAUNCH_STARCLEAVER_GOLEM);
        r.accept(id("cleave_firmament"), CLEAVE_FIRMAMENT);
    }

    public static ResourceLocation id(String key) {
        return OperationStarcleave.id(key);
    }
}

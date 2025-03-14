package phanastrae.operation_starcleave.sound;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import phanastrae.operation_starcleave.OperationStarcleave;

import java.util.function.BiConsumer;

public class OperationStarcleaveSoundEvents {
    public static final ResourceLocation ENTITY_STARCLEAVER_GOLEM_AMBIENT_KEY = id("entity.starcleaver_golem.ambient");
    public static final SoundEvent ENTITY_STARCLEAVER_GOLEM_AMBIENT = createVRE(ENTITY_STARCLEAVER_GOLEM_AMBIENT_KEY);

    public static final ResourceLocation ENTITY_STARCLEAVER_GOLEM_DEATH_KEY = id("entity.starcleaver_golem.death");
    public static final SoundEvent ENTITY_STARCLEAVER_GOLEM_DEATH = createVRE(ENTITY_STARCLEAVER_GOLEM_DEATH_KEY);

    public static final ResourceLocation ENTITY_STARCLEAVER_GOLEM_STEP_KEY = id("entity.starcleaver_golem.step");
    public static final SoundEvent ENTITY_STARCLEAVER_GOLEM_STEP = createVRE(ENTITY_STARCLEAVER_GOLEM_STEP_KEY);

    public static void init(BiConsumer<ResourceLocation, SoundEvent> r) {
        r.accept(ENTITY_STARCLEAVER_GOLEM_AMBIENT_KEY, ENTITY_STARCLEAVER_GOLEM_AMBIENT);
        r.accept(ENTITY_STARCLEAVER_GOLEM_DEATH_KEY, ENTITY_STARCLEAVER_GOLEM_DEATH);
        r.accept(ENTITY_STARCLEAVER_GOLEM_STEP_KEY, ENTITY_STARCLEAVER_GOLEM_STEP);
    }

    public static ResourceLocation id(String key) {
        return OperationStarcleave.id(key);
    }

    private static SoundEvent createVRE(ResourceLocation location) {
        return SoundEvent.createVariableRangeEvent(location);
    }
}

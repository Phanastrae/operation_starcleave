package phanastrae.operation_starcleave.sound;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import phanastrae.operation_starcleave.OperationStarcleave;

public class OperationStarcleaveSoundEvents {
    public static final SoundEvent ENTITY_STARCLEAVER_GOLEM_AMBIENT = register("entity.starcleaver_golem.ambient");
    public static final SoundEvent ENTITY_STARCLEAVER_GOLEM_DEATH = register("entity.starcleaver_golem.death");
    public static final SoundEvent ENTITY_STARCLEAVER_GOLEM_STEP = register("entity.starcleaver_golem.step");

    public static void init() {
    }

    private static SoundEvent register(String id) {
        return register(OperationStarcleave.id(id));
    }

    private static SoundEvent register(ResourceLocation id) {
        return register(id, id);
    }

    private static SoundEvent register(ResourceLocation id, ResourceLocation soundId) {
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(soundId));
    }
}

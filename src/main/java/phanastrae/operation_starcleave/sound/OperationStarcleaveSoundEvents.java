package phanastrae.operation_starcleave.sound;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
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

    private static SoundEvent register(Identifier id) {
        return register(id, id);
    }

    private static SoundEvent register(Identifier id, Identifier soundId) {
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(soundId));
    }
}

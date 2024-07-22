package phanastrae.operation_starcleave.entity.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import phanastrae.operation_starcleave.OperationStarcleave;

public class OperationStarcleaveStatusEffects {

    public static final StatusEffect STARBLEACHED_INSIDES = new StarbleachedInsidesStatusEffect();
    public static RegistryEntry<StatusEffect> STARBLEACHED_INSIDES_ENTRY;

    public static void init() {
        STARBLEACHED_INSIDES_ENTRY = register(STARBLEACHED_INSIDES, "starbleached_insides");
    }

    public static RegistryEntry.Reference<StatusEffect> register(StatusEffect effect, String name) {
        Identifier identifier = OperationStarcleave.id(name);
        return Registry.registerReference(Registries.STATUS_EFFECT, identifier, effect);
    }
}

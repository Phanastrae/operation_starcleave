package phanastrae.operation_starcleave.entity.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import phanastrae.operation_starcleave.OperationStarcleave;

public class OperationStarcleaveStatusEffects {

    public static final StatusEffect STARBLEACHED_INSIDES = new StarbleachedInsidesStatusEffect();

    public static void init() {
        Registry.register(Registries.STATUS_EFFECT, OperationStarcleave.id("starbleached_insides"), STARBLEACHED_INSIDES);
    }
}

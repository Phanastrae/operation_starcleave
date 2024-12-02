package phanastrae.operation_starcleave.entity.effect;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import phanastrae.operation_starcleave.OperationStarcleave;

public class OperationStarcleaveStatusEffects {

    public static final MobEffect STARBLEACHED_INSIDES = new StarbleachedInsidesStatusEffect();
    public static Holder<MobEffect> STARBLEACHED_INSIDES_ENTRY;

    public static void init() {
        STARBLEACHED_INSIDES_ENTRY = register(STARBLEACHED_INSIDES, "starbleached_insides");
    }

    public static Holder.Reference<MobEffect> register(MobEffect effect, String name) {
        ResourceLocation identifier = OperationStarcleave.id(name);
        return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, identifier, effect);
    }
}

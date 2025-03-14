package phanastrae.operation_starcleave.entity.effect;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import phanastrae.operation_starcleave.OperationStarcleave;

public class OperationStarcleaveStatusEffects {

    public static final MobEffect STARBLEACHED_INSIDES = new StarbleachedInsidesStatusEffect();
    public static Holder<MobEffect> STARBLEACHED_INSIDES_ENTRY;

    public static void init(OperationStarcleave.HolderRegisterHelper<MobEffect> hrh) {
        STARBLEACHED_INSIDES_ENTRY = hrh.register("starbleached_insides", STARBLEACHED_INSIDES);
    }
}

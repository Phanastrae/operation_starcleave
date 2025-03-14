package phanastrae.operation_starcleave.world;

import net.minecraft.world.level.GameRules;
import phanastrae.operation_starcleave.mixin.GameRulesAccessor;
import phanastrae.operation_starcleave.mixin.GameRulesBooleanValueAccessor;

public class OperationStarcleaveGameRules {
    public static final GameRules.Key<GameRules.BooleanValue> DO_FRACTURE_STARBLEACHING =
            GameRulesAccessor.invokeRegister("operation_starcleave:doFractureStarbleaching",
                    GameRules.Category.UPDATES,
                    GameRulesBooleanValueAccessor.invokeCreate(true, (server, rule) -> {})
            );
    public static final GameRules.Key<GameRules.BooleanValue> SPAWN_FRACTURE_BYPRODUCTS =
            GameRulesAccessor.invokeRegister("operation_starcleave:spawnFractureByproducts",
                    GameRules.Category.SPAWNING,
                    GameRulesBooleanValueAccessor.invokeCreate(true, (server, rule) -> {})
            );
    public static final GameRules.Key<GameRules.BooleanValue> DO_NUCLEOSYNTHESEED_GROWTH =
            GameRulesAccessor.invokeRegister("operation_starcleave:doNucleosyntheseedGrowth",
                    GameRules.Category.UPDATES,
                    GameRulesBooleanValueAccessor.invokeCreate(true, (server, rule) -> {})
            );

    public static void init() {
    }
}

package phanastrae.operation_starcleave.world;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public class OperationStarcleaveGameRules {
    public static final GameRules.Key<GameRules.BooleanRule> DO_FRACTURE_STARBLEACHING =
            GameRuleRegistry.register("operation_starcleave:doFractureStarbleaching", GameRules.Category.UPDATES, GameRuleFactory.createBooleanRule(true));

    public static void init() {
    }
}

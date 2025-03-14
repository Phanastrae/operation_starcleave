package phanastrae.operation_starcleave.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.BiConsumer;

@Mixin(GameRules.BooleanValue.class)
public interface GameRulesBooleanValueAccessor {

    @Invoker
    static GameRules.Type<GameRules.BooleanValue> invokeCreate(boolean initialValue, BiConsumer<MinecraftServer, GameRules.BooleanValue> changeListener) {
        throw new AssertionError();
    }
}

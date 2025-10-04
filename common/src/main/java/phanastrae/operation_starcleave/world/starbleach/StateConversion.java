package phanastrae.operation_starcleave.world.starbleach;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class StateConversion {

    private final Predicate<BlockState> predicate;
    private final ConversionStateProvider stateProvider;

    public StateConversion(Predicate<BlockState> predicate, ConversionStateProvider stateProvider) {
        this.predicate = predicate;
        this.stateProvider = stateProvider;
    }

    public StateConversion(Predicate<BlockState> predicate, BlockStateProvider provider) {
        this.predicate = predicate;
        this.stateProvider = (level, pos, state, random) -> provider.getState(random, pos);
    }

    @Nullable
    public BlockState getState(Level level, BlockPos blockPos, BlockState state, RandomSource random) {
        if (this.predicate.test(state)) {
            return this.stateProvider.getState(level, blockPos, state, random);
        } else {
            return null;
        }
    }

    @FunctionalInterface
    public interface ConversionStateProvider {
        @Nullable
        BlockState getState(Level level, BlockPos blockPos, BlockState blockState, RandomSource random);
    }
}

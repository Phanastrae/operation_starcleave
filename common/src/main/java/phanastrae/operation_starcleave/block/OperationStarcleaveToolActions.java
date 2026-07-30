package phanastrae.operation_starcleave.block;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.Direction;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.function.Predicate;

import static phanastrae.operation_starcleave.block.OperationStarcleaveBlocks.*;

public class OperationStarcleaveToolActions {
    public static final Map<Block, Block> STRIPPABLES = new ImmutableMap.Builder<Block, Block>()
            .put(NUCLEIC_FISSUREROOT, STRIPED_NUCLEIC_FISSUREROOT)
            .put(NUCLEIC_FISSURERIND, STRIPED_NUCLEIC_FISSURERIND)
            .build();

    public static final Map<Block, BlockState> FLATTENABLES = new ImmutableMap.Builder<Block, BlockState>()
            .put(STELLAR_SEDIMENT, STELLAR_PATH.defaultBlockState())
            .put(HOLY_MOSS, STELLAR_PATH.defaultBlockState())
            .put(STELLAR_MULCH, STELLAR_PATH.defaultBlockState())
            .build();

    public static final Map<Block, TillingAction> TILLABLES = new ImmutableMap.Builder<Block, TillingAction>()
            .put(STELLAR_MULCH, new BasicTillingAction(HoeItem::onlyIfAirAbove, STELLAR_FARMLAND.defaultBlockState()))
            .put(HOLY_MOSS, new BasicTillingAction(context -> context.getClickedFace() != Direction.DOWN, STELLAR_SEDIMENT.defaultBlockState()))
            .build();

    public static abstract class TillingAction {
        public abstract Predicate<UseOnContext> getPredicate();
    }

    public static class BasicTillingAction extends TillingAction {

        private final Predicate<UseOnContext> predicate;
        private final BlockState state;

        public BasicTillingAction(Predicate<UseOnContext> predicate, BlockState state) {
            this.predicate = predicate;
            this.state = state;
        }

        @Override
        public Predicate<UseOnContext> getPredicate() {
            return this.predicate;
        }

        public BlockState getState() {
            return this.state;
        }
    }
}

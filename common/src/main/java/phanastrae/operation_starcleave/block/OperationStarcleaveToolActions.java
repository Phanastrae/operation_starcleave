package phanastrae.operation_starcleave.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.List;
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

    public static final List<Triple<Block, Predicate<UseOnContext>, BlockState>> TILLABLES = createTillables();

    private static List<Triple<Block, Predicate<UseOnContext>, BlockState>> createTillables() {
        List<Triple<Block, Predicate<UseOnContext>, BlockState>> list = new ArrayList<>();
        list.add(Triple.of(OperationStarcleaveBlocks.STELLAR_MULCH, HoeItem::onlyIfAirAbove, OperationStarcleaveBlocks.STELLAR_FARMLAND.defaultBlockState()));
        return ImmutableList.copyOf(list);
    }
}

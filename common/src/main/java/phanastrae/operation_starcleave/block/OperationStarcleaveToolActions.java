package phanastrae.operation_starcleave.block;

import com.google.common.collect.ImmutableMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

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
}

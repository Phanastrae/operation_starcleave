package phanastrae.operation_starcleave.block;

import com.google.common.collect.ImmutableMap;
import net.minecraft.world.level.block.Block;

import java.util.Map;

import static phanastrae.operation_starcleave.block.OperationStarcleaveBlocks.*;

public class OperationStarcleaveLogStripping {
    public static final Map<Block, Block> STARCLEAVE_STRIPPABLES = new ImmutableMap.Builder<Block, Block>()
            .put(NUCLEIC_FISSUREROOT, STRIPED_NUCLEIC_FISSUREROOT)
            .put(NUCLEIC_FISSURERIND, STRIPED_NUCLEIC_FISSURERIND)
            .build();
}

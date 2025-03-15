package phanastrae.operation_starcleave.block.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import phanastrae.operation_starcleave.OperationStarcleave;

public class OperationStarcleaveBlockTags {
    public static final TagKey<Block> STARBLEACHED = TagKey.create(Registries.BLOCK, OperationStarcleave.id("starbleached"));
    public static final TagKey<Block> ALLOWS_BISREED_PLANTING = TagKey.create(Registries.BLOCK, OperationStarcleave.id("allows_bisreed_planting"));
    public static final TagKey<Block> STARBLEACH_IMMUNE = TagKey.create(Registries.BLOCK, OperationStarcleave.id("starbleach_immune"));
    public static final TagKey<Block> PHLOGISTIC_HYPERFLAMMABLES = TagKey.create(Registries.BLOCK, OperationStarcleave.id("phlogistic_hyperflammables"));
    public static final TagKey<Block> NUCLEOSYNTHESEED_BLAST_IMMUNE = TagKey.create(Registries.BLOCK, OperationStarcleave.id("nucleosyntheseed_blast_immune"));
}

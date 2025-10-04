package phanastrae.operation_starcleave.block.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import phanastrae.operation_starcleave.OperationStarcleave;

public class OperationStarcleaveBlockTags {
    public static final TagKey<Block> STARBLEACHED = of("starbleached");
    public static final TagKey<Block> HOLY_MOSS_REPLACEABLE = of("holy_moss_replaceable");
    public static final TagKey<Block> STELLAR_MULCH_REPLACEABLE = of("stellar_mulch_replaceable");
    public static final TagKey<Block> ALLOWS_BISREED_PLANTING = of("allows_bisreed_planting");
    public static final TagKey<Block> STARBLEACH_IMMUNE = of("starbleach_immune");
    public static final TagKey<Block> PHLOGISTIC_HYPERFLAMMABLES = of("phlogistic_hyperflammables");
    public static final TagKey<Block> NUCLEOSYNTHESEED_BLAST_IMMUNE = of("nucleosyntheseed_blast_immune");

    public static final TagKey<Block> STARBLEACHED_LOGS = of("starbleached_logs");
    public static final TagKey<Block> NUCLEIC_FISSUREROOTS = of("nucleic_fissureroots");

    private static TagKey<Block> of(String id) {
        return TagKey.create(Registries.BLOCK, OperationStarcleave.id(id));
    }
}

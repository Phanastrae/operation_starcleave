package phanastrae.operation_starcleave.block.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import phanastrae.operation_starcleave.OperationStarcleave;

public class OperationStarcleaveBlockTags {
    // block-only tags
    public static final TagKey<Block> STARBLEACHED = of("starbleached");
    public static final TagKey<Block> HOLY_MOSS_REPLACEABLE = of("holy_moss_replaceable");
    public static final TagKey<Block> STELLAR_MULCH_REPLACEABLE = of("stellar_mulch_replaceable");
    public static final TagKey<Block> ALLOWS_BISREED_PLANTING = of("allows_bisreed_planting");
    public static final TagKey<Block> STARBLEACHED_SAPLING_PLANTABLE_ON = of("starbleached_sapling_plantable_on");
    public static final TagKey<Block> PHLOGISTIC_HYPERFLAMMABLES = of("phlogistic_hyperflammables");
    public static final TagKey<Block> NUCLEOSYNTHESEED_BLAST_IMMUNE = of("nucleosyntheseed_blast_immune");
    public static final TagKey<Block> ALSO_PRESERVES_LEAVES = of("also_preserves_leaves");
    public static final TagKey<Block> PREVENTS_ELYTRA_WALL_DAMAGE = of("prevents_elytra_wall_damage");
    public static final TagKey<Block> MINED_QUICKLY_BY_SHEARS = of("mined_quickly_by_shears");
    public static final TagKey<Block> ASTERUBBLE_BOULDER_REPLACEABLE = of("asterubble_boulder_replaceable");

    // block tags that are also item tags
    public static final TagKey<Block> STARBLEACH_IMMUNE = of("starbleach_immune");

    public static final TagKey<Block> STARBLEACHED_LOGS = of("starbleached_logs");
    public static final TagKey<Block> STARTOUCHED_LOGS = of("startouched_logs");
    public static final TagKey<Block> NUCLEIC_FISSUREROOTS = of("nucleic_fissureroots");

    public static final TagKey<Block> SB_I_FELLCRUST = starbleachesInto("fellcrust");
    public static final TagKey<Block> SB_I_FELLCRUST_STAIRS = starbleachesInto("fellcrust_stairs");
    public static final TagKey<Block> SB_I_FELLCRUST_SLAB = starbleachesInto("fellcrust_slab");
    public static final TagKey<Block> SB_I_FELLCRUST_WALL = starbleachesInto("fellcrust_wall");
    public static final TagKey<Block> SB_I_CHISELED_FELLCRUST = starbleachesInto("chiseled_fellcrust");
    public static final TagKey<Block> SB_I_SMOOTH_FELLCRUST = starbleachesInto("smooth_fellcrust");
    public static final TagKey<Block> SB_I_SMOOTH_FELLCRUST_STAIRS = starbleachesInto("smooth_fellcrust_stairs");
    public static final TagKey<Block> SB_I_SMOOTH_FELLCRUST_SLAB = starbleachesInto("smooth_fellcrust_slab");
    public static final TagKey<Block> SB_I_CUT_FELLCRUST = starbleachesInto("cut_fellcrust");
    public static final TagKey<Block> SB_I_CUT_FELLCRUST_SLAB = starbleachesInto("cut_fellcrust_slab");

    private static TagKey<Block> of(String id) {
        return TagKey.create(Registries.BLOCK, OperationStarcleave.id(id));
    }

    private static TagKey<Block> starbleachesInto(String id) {
        return of("starbleaches_into/" + id);
    }
}

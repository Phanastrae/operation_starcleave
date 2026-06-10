package phanastrae.operation_starcleave.item.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.block.tag.OperationStarcleaveBlockTags;

import java.util.HashMap;
import java.util.Map;

public class OperationStarcleaveItemTags {
    public static final Map<TagKey<Block>, TagKey<Item>> BLOCK_TAG_TO_ITEM_TAG_MAP = new HashMap<>();

    // item tags that are also block tags
    public static final TagKey<Item> STARBLEACHED_LOGS = fromBlockTag(OperationStarcleaveBlockTags.STARBLEACHED_LOGS);
    public static final TagKey<Item> NUCLEIC_FISSUREROOTS = fromBlockTag(OperationStarcleaveBlockTags.NUCLEIC_FISSUREROOTS);

    public static final TagKey<Item> SB_I_FELLCRUST = fromBlockTag(OperationStarcleaveBlockTags.SB_I_FELLCRUST);
    public static final TagKey<Item> SB_I_FELLCRUST_STAIRS = fromBlockTag(OperationStarcleaveBlockTags.SB_I_FELLCRUST_STAIRS);
    public static final TagKey<Item> SB_I_FELLCRUST_SLAB = fromBlockTag(OperationStarcleaveBlockTags.SB_I_FELLCRUST_SLAB);
    public static final TagKey<Item> SB_I_FELLCRUST_WALL = fromBlockTag(OperationStarcleaveBlockTags.SB_I_FELLCRUST_WALL);
    public static final TagKey<Item> SB_I_CHISELED_FELLCRUST = fromBlockTag(OperationStarcleaveBlockTags.SB_I_CHISELED_FELLCRUST);
    public static final TagKey<Item> SB_I_SMOOTH_FELLCRUST = fromBlockTag(OperationStarcleaveBlockTags.SB_I_SMOOTH_FELLCRUST);
    public static final TagKey<Item> SB_I_SMOOTH_FELLCRUST_STAIRS = fromBlockTag(OperationStarcleaveBlockTags.SB_I_SMOOTH_FELLCRUST_STAIRS);
    public static final TagKey<Item> SB_I_SMOOTH_FELLCRUST_SLAB = fromBlockTag(OperationStarcleaveBlockTags.SB_I_SMOOTH_FELLCRUST_SLAB);
    public static final TagKey<Item> SB_I_CUT_FELLCRUST = fromBlockTag(OperationStarcleaveBlockTags.SB_I_CUT_FELLCRUST);
    public static final TagKey<Item> SB_I_CUT_FELLCRUST_SLAB = fromBlockTag(OperationStarcleaveBlockTags.SB_I_CUT_FELLCRUST_SLAB);

    // item-only tags
    public static final TagKey<Item> STOCKPILE_ENCHANTABLE = of("enchantable/stockpile");

    private static TagKey<Item> of(String id) {
        return of(OperationStarcleave.id(id));
    }

    private static TagKey<Item> of(ResourceLocation location) {
        return TagKey.create(Registries.ITEM, location);
    }

    private static TagKey<Item> fromBlockTag(TagKey<Block> tag) {
        TagKey<Item> itemTag = of(tag.location());
        BLOCK_TAG_TO_ITEM_TAG_MAP.put(tag, itemTag);
        return itemTag;
    }
}

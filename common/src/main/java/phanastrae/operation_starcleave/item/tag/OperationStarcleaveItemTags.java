package phanastrae.operation_starcleave.item.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import phanastrae.operation_starcleave.OperationStarcleave;

public class OperationStarcleaveItemTags {
    public static final TagKey<Item> STARBLEACHED_LOGS = of("starbleached_logs");
    public static final TagKey<Item> NUCLEIC_FISSUREROOTS = of("nucleic_fissureroots");

    public static final TagKey<Item> STOCKPILE_ENCHANTABLE = of("enchantable/stockpile");

    private static TagKey<Item> of(String id) {
        return TagKey.create(Registries.ITEM, OperationStarcleave.id(id));
    }
}

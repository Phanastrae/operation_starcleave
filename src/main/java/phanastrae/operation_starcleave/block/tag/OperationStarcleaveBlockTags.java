package phanastrae.operation_starcleave.block.tag;

import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import phanastrae.operation_starcleave.OperationStarcleave;

public class OperationStarcleaveBlockTags {
    public static final TagKey<Block> STARBLEACHED = TagKey.of(RegistryKeys.BLOCK, OperationStarcleave.id("starbleached"));
}

package phanastrae.operation_starcleave.block.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;

import java.util.function.BiConsumer;

public class OperationStarcleaveBlockEntityTypes {

    public static final BlockEntityType<BlessedBedBlockEntity> BLESSED_BED = create("blessed_bed", BlessedBedBlockEntity::new, OperationStarcleaveBlocks.BLESSED_BED);

    public static void init(BiConsumer<ResourceLocation, BlockEntityType<?>> r) {
        r.accept(id("blessed_bed"), BLESSED_BED);
    }

    private static ResourceLocation id(String path) {
        return OperationStarcleave.id(path);
    }

    private static <T extends BlockEntity> BlockEntityType<T> create(String id, BlockEntityType.BlockEntitySupplier<? extends T> factory, Block... blocks) {
        if (blocks.length == 0) {
            OperationStarcleave.LOGGER.warn("Block entity type {} requires at least one valid block to be defined!", id);
        }
        return BlockEntityType.Builder.<T>of(factory, blocks).build(null);
    }
}

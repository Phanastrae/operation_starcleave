package phanastrae.operation_starcleave.block.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;

public class OperationStarcleaveBlockEntityTypes {

    public static final BlockEntityType<BlessedBedBlockEntity> BLESSED_BED = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            OperationStarcleave.id("blessed_bed"),
            FabricBlockEntityTypeBuilder.<BlessedBedBlockEntity>create(BlessedBedBlockEntity::new, OperationStarcleaveBlocks.BLESSED_BED).build()
    );

    public static void init() {

    }
}

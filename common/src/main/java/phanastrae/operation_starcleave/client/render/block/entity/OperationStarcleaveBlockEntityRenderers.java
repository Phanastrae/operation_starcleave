package phanastrae.operation_starcleave.client.render.block.entity;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import phanastrae.operation_starcleave.block.entity.OperationStarcleaveBlockEntityTypes;
import phanastrae.operation_starcleave.mixin.client.BlockEntityRenderersAccessor;

public class OperationStarcleaveBlockEntityRenderers {
    public static void init() {
        register(OperationStarcleaveBlockEntityTypes.BLESSED_BED, BlessedBedBlockEntityRenderer::new);
    }

    public static <T extends BlockEntity> void register(BlockEntityType<? extends T> type, BlockEntityRendererProvider<T> factory) {
        BlockEntityRenderersAccessor.invokeRegister(type, factory);
    }
}

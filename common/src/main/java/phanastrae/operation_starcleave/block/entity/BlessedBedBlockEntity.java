package phanastrae.operation_starcleave.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BlessedBedBlockEntity extends BlockEntity {

    public BlessedBedBlockEntity(BlockPos pos, BlockState state) {
        super(OperationStarcleaveBlockEntityTypes.BLESSED_BED, pos, state);
    }

    public BlessedBedBlockEntity(BlockPos pos, BlockState state, DyeColor color) {
        super(OperationStarcleaveBlockEntityTypes.BLESSED_BED, pos, state);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}

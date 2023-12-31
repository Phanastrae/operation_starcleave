package phanastrae.operation_starcleave.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;

public class BlessedBedBlockEntity extends BlockEntity {

    public BlessedBedBlockEntity(BlockPos pos, BlockState state) {
        super(OperationStarcleaveBlockEntityTypes.BLESSED_BED, pos, state);
    }

    public BlessedBedBlockEntity(BlockPos pos, BlockState state, DyeColor color) {
        super(OperationStarcleaveBlockEntityTypes.BLESSED_BED, pos, state);
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}

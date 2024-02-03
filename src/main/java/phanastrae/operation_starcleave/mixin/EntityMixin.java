package phanastrae.operation_starcleave.mixin;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntity;

@Mixin(Entity.class)
public class EntityMixin implements OperationStarcleaveEntity {

    private long operation_starcleave$lastRepulsorUse = Long.MIN_VALUE;

    @Override
    public long operation_starcleave$getLastStellarRepulsorUse() {
        return operation_starcleave$lastRepulsorUse;
    }

    @Override
    public void operation_starcleave$setLastStellarRepulsorUse(long time) {
        this.operation_starcleave$lastRepulsorUse = time;
    }
}

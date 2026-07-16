package phanastrae.operation_starcleave.mixin.common.accessor;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockBehaviour.class)
public interface BlockBehaviourAccessor {

    @Accessor("drops")
    ResourceKey<LootTable> operation_starcleave$getDrops();

    @Accessor("drops")
    void operation_starcleave$setDrops(ResourceKey<LootTable> key);
}

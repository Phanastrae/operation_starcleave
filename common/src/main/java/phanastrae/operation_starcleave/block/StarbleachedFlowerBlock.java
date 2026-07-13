package phanastrae.operation_starcleave.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import phanastrae.operation_starcleave.block.tag.OperationStarcleaveBlockTags;

public class StarbleachedFlowerBlock extends FlowerBlock {
    public static final MapCodec<StarbleachedFlowerBlock> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(EFFECTS_FIELD.forGetter(FlowerBlock::getSuspiciousEffects), propertiesCodec()).apply(instance, StarbleachedFlowerBlock::new)
    );

    @Override
    public MapCodec<StarbleachedFlowerBlock> codec() {
        return CODEC;
    }

    public StarbleachedFlowerBlock(Holder<MobEffect> effect, float seconds, BlockBehaviour.Properties properties) {
        this(makeEffectList(effect, seconds), properties);
    }

    public StarbleachedFlowerBlock(SuspiciousStewEffects suspiciousStewEffects, BlockBehaviour.Properties properties) {
        super(suspiciousStewEffects, properties);
    }

    // TODO add particles?

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(OperationStarcleaveBlockTags.STARBLEACHED_SAPLING_PLANTABLE_ON);
    }
}

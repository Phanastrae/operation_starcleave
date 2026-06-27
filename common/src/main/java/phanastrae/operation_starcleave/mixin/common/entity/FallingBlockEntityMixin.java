package phanastrae.operation_starcleave.mixin.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;
import phanastrae.operation_starcleave.block.StarbleachedLeafLitterBlock;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin extends Entity {
    private FallingBlockEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    private BlockState blockState;

    @Shadow
    private boolean cancelDrop;

    @Shadow
    public boolean dropItem;

    @Inject(method = "causeFallDamage", at = @At(value = "HEAD", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/tags/TagKey;)Z"))
    private void operation_starcleave$splitLeaves(float fallDistance, float multiplier, DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if (!this.blockState.is(OperationStarcleaveBlocks.STARBLEACHED_LEAVES)) {
            return;
        }

        BlockPos thisPos = this.blockPosition();
        Level level = this.level();
        boolean leafBelow = level.getBlockState(thisPos.below()).is(OperationStarcleaveBlocks.STARBLEACHED_LEAVES);

        float effectiveDistance = fallDistance * multiplier - (leafBelow ? 4F : 2F);
        if (effectiveDistance <= 0.01F) {
            return;
        }

        if (this.random.nextFloat() < 0.15F + 0.85F * Math.sqrt(effectiveDistance * (leafBelow ? 0.5F : 1.0F) / 16F) + 0.01F) {
            // break into litter
            if (!this.cancelDrop) {
                // play break sound and spawn particles
                level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, thisPos, Block.getId(blockState));

                int dropCount = this.random.nextInt(2, 4);
                for (int i = 0; i < dropCount; i++) {
                    for (int attempt = 0; attempt < 4; attempt++) {
                        BlockPos targetPos = attempt == 0 ? thisPos : thisPos.offset(Direction.from2DDataValue(this.random.nextInt(4)).getNormal());
                        BlockState targetState = level.getBlockState(targetPos);

                        for (int j = 0; j < 2; j++) {
                            // try to move down by up to two blocks if placing on air
                            if (targetState.getCollisionShape(level, targetPos).isEmpty() && !OperationStarcleaveBlocks.STARBLEACHED_LEAF_LITTER.defaultBlockState().canSurvive(level, targetPos)) {
                                targetPos = targetPos.below();
                                targetState = level.getBlockState(targetPos);
                            }
                        }

                        if (targetState.isAir()) {
                            Direction direction = Direction.from2DDataValue(this.random.nextInt(4));
                            BlockState newState = OperationStarcleaveBlocks.STARBLEACHED_LEAF_LITTER.defaultBlockState().setValue(StarbleachedLeafLitterBlock.FACING, direction);
                            if (newState.canSurvive(level, targetPos)) {
                                level.setBlockAndUpdate(targetPos, newState);
                                break;
                            }
                        } else if (targetState.is(OperationStarcleaveBlocks.STARBLEACHED_LEAF_LITTER)) {
                            int segments = targetState.getValue(StarbleachedLeafLitterBlock.SEGMENT_AMOUNT);
                            if (segments < 4) {
                                BlockState newState = targetState.setValue(StarbleachedLeafLitterBlock.SEGMENT_AMOUNT, segments + 1);
                                level.setBlockAndUpdate(targetPos, newState);
                                break;
                            }
                        }
                    }
                }
            }

            this.cancelDrop = true;
            this.dropItem = false;
        }
    }
}

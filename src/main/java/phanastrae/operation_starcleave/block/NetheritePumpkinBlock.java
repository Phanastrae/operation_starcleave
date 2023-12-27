package phanastrae.operation_starcleave.block;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.WearableCarvedPumpkinBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;

import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;
import phanastrae.operation_starcleave.entity.StarcleaverGolemEntity;

import java.util.function.Predicate;

public class NetheritePumpkinBlock extends WearableCarvedPumpkinBlock {

    @Nullable
    private BlockPattern starcleaverGolemPattern;

    @Nullable
    private BlockPattern starcleaverGolemDispenserPattern;

    private static final Predicate<BlockState> IS_NETHERITE_GOLEM_HEAD_PREDICATE = state -> state != null
            && (state.isOf(OperationStarcleaveBlocks.NETHERITE_PUMPKIN));


    public NetheritePumpkinBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!oldState.isOf(state.getBlock())) {
            this.trySpawnEntityNetherite(world, pos);
        }
    }

    public void trySpawnEntityNetherite(World world, BlockPos pos) {
        BlockPattern.Result result = this.getStarcleaverGolemPattern().searchAround(world, pos);
        if (result != null) {
            StarcleaverGolemEntity starcleaverGolemEntity = OperationStarcleaveEntityTypes.STARCLEAVER_GOLEM.create(world);
            if (starcleaverGolemEntity != null) {
                spawnEntity(world, result, starcleaverGolemEntity, result.translate(0, 1, 0).getBlockPos());
            }
        }
    }

    public static void spawnEntity(World world, BlockPattern.Result patternResult, Entity entity, BlockPos pos) {
        breakPatternBlocks(world, patternResult);
        entity.refreshPositionAndAngles((double)pos.getX() + 0.5, (double)pos.getY() + 0.05, (double)pos.getZ() + 0.5, 0.0F, 0.0F);
        world.spawnEntity(entity);

        for(ServerPlayerEntity serverPlayerEntity : world.getNonSpectatingEntities(ServerPlayerEntity.class, entity.getBoundingBox().expand(5.0))) {
            Criteria.SUMMONED_ENTITY.trigger(serverPlayerEntity, entity);
        }

        updatePatternBlocks(world, patternResult);
    }

    private BlockPattern getStarcleaverGolemDispenserPattern() {
        if (this.starcleaverGolemDispenserPattern == null) {
            this.starcleaverGolemDispenserPattern = BlockPatternBuilder.start()
                    .aisle(" ", "#")
                    .where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.CRYING_OBSIDIAN)))
                    .build();
        }

        return this.starcleaverGolemDispenserPattern;
    }

    public BlockPattern getStarcleaverGolemPattern() {
        if (this.starcleaverGolemPattern == null) {
            this.starcleaverGolemPattern = BlockPatternBuilder.start()
                    .aisle("^", "#")
                    .where('^', CachedBlockPosition.matchesBlockState(IS_NETHERITE_GOLEM_HEAD_PREDICATE))
                    .where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.CRYING_OBSIDIAN)))
                    .build();
        }

        return this.starcleaverGolemPattern;
    }

    @Override
    public boolean canDispense(WorldView world, BlockPos pos) {
        return this.getStarcleaverGolemDispenserPattern().searchAround(world, pos) != null;
    }
}

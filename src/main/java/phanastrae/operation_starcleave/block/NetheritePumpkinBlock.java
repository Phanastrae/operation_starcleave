package phanastrae.operation_starcleave.block;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.WearableCarvedPumpkinBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;

import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;
import phanastrae.operation_starcleave.entity.mob.StarcleaverGolemEntity;

import java.util.List;
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
            for(int i = 0; i < 3; i++) {
                StarcleaverGolemEntity starcleaverGolemEntity = OperationStarcleaveEntityTypes.STARCLEAVER_GOLEM.create(world);
                if (starcleaverGolemEntity != null) {
                    spawnEntity(world, result, starcleaverGolemEntity, result.translate(0, 1, 0).getBlockPos());
                    Random random = starcleaverGolemEntity.getRandom();
                    Vec3d p = starcleaverGolemEntity.getPos().add(random.nextFloat() * 0.3 - 0.15, i * 0.2f, random.nextFloat() * 0.3 - 0.15);
                    starcleaverGolemEntity.setPos(p.x, p.y, p.z);
                    List<PlayerEntity> nearPlayers = world.getEntitiesByType(EntityType.PLAYER, Box.from(starcleaverGolemEntity.getPos()).expand(4), (e) -> true);
                    if(nearPlayers != null && !nearPlayers.isEmpty()) {
                        int r = random.nextInt(nearPlayers.size());
                        if(r < nearPlayers.size()) {
                            starcleaverGolemEntity.lookAtEntity(nearPlayers.get(r), 180, 90);
                        }
                    }
                }
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

package phanastrae.operation_starcleave.block;

import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;
import phanastrae.operation_starcleave.entity.mob.StarcleaverGolemEntity;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EquipableCarvedPumpkinBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class NetheritePumpkinBlock extends EquipableCarvedPumpkinBlock {

    @Nullable
    private BlockPattern starcleaverGolemPattern;

    @Nullable
    private BlockPattern starcleaverGolemDispenserPattern;

    private static final Predicate<BlockState> IS_NETHERITE_GOLEM_HEAD_PREDICATE = state -> state != null
            && (state.is(OperationStarcleaveBlocks.NETHERITE_PUMPKIN));


    public NetheritePumpkinBlock(Properties settings) {
        super(settings);
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!oldState.is(state.getBlock())) {
            this.trySpawnEntityNetherite(world, pos);
        }
    }

    public void trySpawnEntityNetherite(Level world, BlockPos pos) {
        BlockPattern.BlockPatternMatch result = this.getStarcleaverGolemPattern().find(world, pos);
        if (result != null) {
            for(int i = 0; i < 3; i++) {
                StarcleaverGolemEntity starcleaverGolemEntity = OperationStarcleaveEntityTypes.STARCLEAVER_GOLEM.create(world);
                if (starcleaverGolemEntity != null) {
                    spawnGolemInWorld(world, result, starcleaverGolemEntity, result.getBlock(0, 1, 0).getPos());
                    RandomSource random = starcleaverGolemEntity.getRandom();
                    Vec3 p = starcleaverGolemEntity.position().add(random.nextFloat() * 0.3 - 0.15, i * 0.2f, random.nextFloat() * 0.3 - 0.15);
                    starcleaverGolemEntity.setPosRaw(p.x, p.y, p.z);
                    List<Player> nearPlayers = world.getEntities(EntityType.PLAYER, AABB.unitCubeFromLowerCorner(starcleaverGolemEntity.position()).inflate(4), (e) -> true);
                    if(nearPlayers != null && !nearPlayers.isEmpty()) {
                        int r = random.nextInt(nearPlayers.size());
                        if(r < nearPlayers.size()) {
                            starcleaverGolemEntity.lookAt(nearPlayers.get(r), 180, 90);
                        }
                    }
                }
            }
        }
    }

    public static void spawnGolemInWorld(Level world, BlockPattern.BlockPatternMatch patternResult, Entity entity, BlockPos pos) {
        clearPatternBlocks(world, patternResult);
        entity.moveTo((double)pos.getX() + 0.5, (double)pos.getY() + 0.05, (double)pos.getZ() + 0.5, 0.0F, 0.0F);
        world.addFreshEntity(entity);

        for(ServerPlayer serverPlayerEntity : world.getEntitiesOfClass(ServerPlayer.class, entity.getBoundingBox().inflate(5.0))) {
            CriteriaTriggers.SUMMONED_ENTITY.trigger(serverPlayerEntity, entity);
        }

        updatePatternBlocks(world, patternResult);
    }

    private BlockPattern getStarcleaverGolemDispenserPattern() {
        if (this.starcleaverGolemDispenserPattern == null) {
            this.starcleaverGolemDispenserPattern = BlockPatternBuilder.start()
                    .aisle(" ", "#")
                    .where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.CRYING_OBSIDIAN)))
                    .build();
        }

        return this.starcleaverGolemDispenserPattern;
    }

    public BlockPattern getStarcleaverGolemPattern() {
        if (this.starcleaverGolemPattern == null) {
            this.starcleaverGolemPattern = BlockPatternBuilder.start()
                    .aisle("^", "#")
                    .where('^', BlockInWorld.hasState(IS_NETHERITE_GOLEM_HEAD_PREDICATE))
                    .where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.CRYING_OBSIDIAN)))
                    .build();
        }

        return this.starcleaverGolemPattern;
    }

    @Override
    public boolean canSpawnGolem(LevelReader world, BlockPos pos) {
        return this.getStarcleaverGolemDispenserPattern().find(world, pos) != null;
    }
}

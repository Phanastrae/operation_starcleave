package phanastrae.operation_starcleave.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;
import phanastrae.operation_starcleave.world.firmament.Firmament;

public class StellarFarmlandBlock extends FarmBlock {
    public static final MapCodec<FarmBlock> CODEC = simpleCodec(StellarFarmlandBlock::new);

    @Override
    public MapCodec<FarmBlock> codec() {
        return CODEC;
    }

    public StellarFarmlandBlock(Properties settings) {
        super(settings);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return !this.defaultBlockState().canSurvive(ctx.getLevel(), ctx.getClickedPos()) ? OperationStarcleaveBlocks.STELLAR_SEDIMENT.defaultBlockState() : this.defaultBlockState();
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.canSurvive(level, pos)) {
            setToSediment(null, state, level, pos);
        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        Firmament firmament = Firmament.fromLevel(level);
        if(firmament == null) return;

        int i = state.getValue(MOISTURE);
        if (!isStarlit(level, pos, firmament)) {
            if (i > 0) {
                level.setBlock(pos, state.setValue(MOISTURE, i - 1), Block.UPDATE_CLIENTS);
            }
        } else {
            if (i < 7) {
                level.setBlock(pos, state.setValue(MOISTURE, 7), Block.UPDATE_CLIENTS);
                hydrationParticles(level, pos);
            }

            BlockPos upPos = pos.above();
            BlockState upState = level.getBlockState(upPos);
            if(upState.getBlock() instanceof CropBlock cropBlock) {
                int age = cropBlock.getAge(upState);
                int maxAge = cropBlock.getMaxAge();
                if(age < maxAge) {
                    for(int j = 0; j < 2; j++) {
                        if(random.nextInt(77) == 0) {
                            cropBlock.growCrops(level, upPos, upState);
                            age = cropBlock.getAge(upState);

                            hydrationParticles(level, pos);
                        }
                    }
                }

                if(!upState.is(OperationStarcleaveBlocks.BISREEDS)) {
                    boolean setBisreeds = false;
                    if (age == maxAge && random.nextInt(7) == 0) {
                        setBisreeds = true;
                    } else {
                        for (Direction direction : Direction.Plane.HORIZONTAL) {
                            BlockPos adjUpPos = upPos.offset(direction.getNormal());
                            BlockState adjUpState = level.getBlockState(adjUpPos);
                            if (adjUpState.is(OperationStarcleaveBlocks.BISREEDS) && adjUpState.getBlock() instanceof BisreedBlock bisreedBlock) {
                                int bisreedAge = bisreedBlock.getAge(adjUpState);
                                int bisreedMaxAge = bisreedBlock.getMaxAge();

                                if (random.nextInt(1 + 4 * (bisreedMaxAge - bisreedAge) + (maxAge - age)) <= 3) {
                                    setBisreeds = true;
                                }
                            }
                        }
                    }

                    if (setBisreeds) {
                        level.setBlock(upPos, OperationStarcleaveBlocks.BISREEDS.defaultBlockState(), 3);

                        level.playSeededSound(null, upPos.getX(), upPos.getY(), upPos.getZ(), SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 0.1F, 1.6F + 0.4F * level.random.nextFloat(), level.random.nextLong());
                        hydrationParticles(level, pos);
                    }
                }
            }
        }
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        // no trampling
        entity.causeFallDamage(fallDistance, 1.0F, entity.damageSources().fall());
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if(state.getValue(MOISTURE) < 7) return;

        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5 + 0.5;
        double z = pos.getZ() + 0.5;
        for(int k = 0; k < 3; k++) {
            level.addParticle(OperationStarcleaveParticleTypes.FIRMAMENT_GLIMMER,
                    x + random.nextFloat() - 0.5,
                    y,
                    z + random.nextFloat() - 0.5,
                    random.nextFloat() * 0.006 - 0.003,
                    0.002 + random.nextFloat() * 0.005,
                    random.nextFloat() * 0.006 - 0.003);
        }

        for(Direction d : Direction.values()) {
            if(d.getNormal().getY() != 0) continue;

            Vec3i v = d.getNormal();
            BlockPos adjPos = pos.offset(v);
            BlockState adjState = level.getBlockState(adjPos);
            if(adjState.is(this.asBlock()) && adjState.getProperties().contains(MOISTURE) && adjState.getValue(MOISTURE) == 7) continue;

            for(int k = 0; k < 12; k++) {
                level.addParticle(OperationStarcleaveParticleTypes.FIRMAMENT_GLIMMER,
                        x + (v.getX() == 0 ? random.nextFloat() - 0.5 : v.getX() * 0.5),
                        y,
                        z + (v.getZ() == 0 ? random.nextFloat() - 0.5 : v.getZ() * 0.5),
                        random.nextFloat() * 0.006 - 0.003,
                        0.005 + random.nextFloat() * 0.01,
                        random.nextFloat() * 0.006 - 0.003);
            }
        }
    }

    public static void hydrationParticles(ServerLevel level, BlockPos pos) {
        level.sendParticles(OperationStarcleaveParticleTypes.FIRMAMENT_GLIMMER,
                pos.getX() + 0.5,
                pos.getY() + 1.125,
                pos.getZ() + 0.5,
                120,
                0.2,
                0,
                0.2,
                0.01);
    }

    public static void setToSediment(@Nullable Entity entity, BlockState state, Level level, BlockPos pos) {
        BlockState blockState = pushEntitiesUp(state, OperationStarcleaveBlocks.STELLAR_SEDIMENT.defaultBlockState(), level, pos);
        level.setBlockAndUpdate(pos, blockState);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(entity, blockState));
    }

    public static boolean isStarlit(LevelReader worldView, BlockPos pos, Firmament firmament) {
        // starlit if skylight > 7 and there is firmament with >6 damage within 9x9 area
        int skyLight = worldView.getBrightness(LightLayer.SKY, pos.above());
        if(skyLight <= 7) return false;

        int x = pos.getX();
        int z = pos.getZ();
        for(int i = -4; i <= 4; i++) {
            for(int j = -4; j <= 4; j++) {
                int damage = firmament.getDamage(x + i, z + j);
                if(damage >= 6) {
                    return true;
                }
            }
        }
        return false;
    }
}

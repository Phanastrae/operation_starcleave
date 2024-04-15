package phanastrae.operation_starcleave.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;
import phanastrae.operation_starcleave.world.firmament.Firmament;

public class StellarFarmlandBlock extends FarmlandBlock {
    public static final MapCodec<FarmlandBlock> CODEC = createCodec(StellarFarmlandBlock::new);

    @Override
    public MapCodec<FarmlandBlock> getCodec() {
        return CODEC;
    }

    public StellarFarmlandBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return !this.getDefaultState().canPlaceAt(ctx.getWorld(), ctx.getBlockPos()) ? OperationStarcleaveBlocks.STELLAR_SEDIMENT.getDefaultState() : this.getDefaultState();
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!state.canPlaceAt(world, pos)) {
            setToSediment(null, state, world, pos);
        }
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        Firmament firmament = Firmament.fromWorld(world);
        if(firmament == null) return;

        int i = state.get(MOISTURE);
        if (!isStarlit(world, pos, firmament)) {
            if (i > 0) {
                world.setBlockState(pos, state.with(MOISTURE, i - 1), Block.NOTIFY_LISTENERS);
            }
        } else if (i < 7) {
            world.setBlockState(pos, state.with(MOISTURE, 7), Block.NOTIFY_LISTENERS);
            hydrationParticles(world, pos);
        }
    }

    @Override
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        // no trampling
        entity.handleFallDamage(fallDistance, 1.0F, entity.getDamageSources().fall());
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if(state.get(MOISTURE) < 7) return;

        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5 + 0.5;
        double z = pos.getZ() + 0.5;
        for(int k = 0; k < 3; k++) {
            world.addParticle(OperationStarcleaveParticleTypes.FIRMAMENT_GLIMMER,
                    x + random.nextFloat() - 0.5,
                    y,
                    z + random.nextFloat() - 0.5,
                    random.nextFloat() * 0.006 - 0.003,
                    0.002 + random.nextFloat() * 0.005,
                    random.nextFloat() * 0.006 - 0.003);
        }

        for(Direction d : Direction.values()) {
            if(d.getVector().getY() != 0) continue;

            Vec3i v = d.getVector();
            BlockPos adjPos = pos.add(v);
            BlockState adjState = world.getBlockState(adjPos);
            if(adjState.isOf(this.asBlock()) && adjState.getProperties().contains(MOISTURE) && adjState.get(MOISTURE) == 7) continue;

            for(int k = 0; k < 12; k++) {
                world.addParticle(OperationStarcleaveParticleTypes.FIRMAMENT_GLIMMER,
                        x + (v.getX() == 0 ? random.nextFloat() - 0.5 : v.getX() * 0.5),
                        y,
                        z + (v.getZ() == 0 ? random.nextFloat() - 0.5 : v.getZ() * 0.5),
                        random.nextFloat() * 0.006 - 0.003,
                        0.005 + random.nextFloat() * 0.01,
                        random.nextFloat() * 0.006 - 0.003);
            }
        }
    }

    public static void hydrationParticles(ServerWorld world, BlockPos pos) {
        world.spawnParticles(OperationStarcleaveParticleTypes.FIRMAMENT_GLIMMER,
                pos.getX() + 0.5,
                pos.getY() + 1.125,
                pos.getZ() + 0.5,
                120,
                0.2,
                0,
                0.2,
                0.01);
    }

    public static void setToSediment(@Nullable Entity entity, BlockState state, World world, BlockPos pos) {
        BlockState blockState = pushEntitiesUpBeforeBlockChange(state, OperationStarcleaveBlocks.STELLAR_SEDIMENT.getDefaultState(), world, pos);
        world.setBlockState(pos, blockState);
        world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(entity, blockState));
    }

    public static boolean isStarlit(WorldView worldView, BlockPos pos, Firmament firmament) {
        // starlit if skylight > 7 and there is firmament with >6 damage within 9x9 area
        int skyLight = worldView.getLightLevel(LightType.SKY, pos.up());
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

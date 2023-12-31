package phanastrae.operation_starcleave;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.item.*;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.tick.TickManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import phanastrae.operation_starcleave.advancement.criterion.OperationStarcleaveAdvancementCriteria;
import phanastrae.operation_starcleave.block.NetheritePumpkinBlock;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;
import phanastrae.operation_starcleave.block.StarbleachCauldronBlock;
import phanastrae.operation_starcleave.block.entity.OperationStarcleaveBlockEntityTypes;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;
import phanastrae.operation_starcleave.entity.effect.OperationStarcleaveStatusEffects;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;
import phanastrae.operation_starcleave.network.packet.OperationStarcleavePacketTypes;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;
import phanastrae.operation_starcleave.server.network.OperationStarcleaveServerPacketHandler;
import phanastrae.operation_starcleave.world.firmament.Firmament;
import phanastrae.operation_starcleave.world.firmament.FirmamentRegion;
import phanastrae.operation_starcleave.world.firmament.FirmamentWatcher;

public class OperationStarcleave implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("operation_starcleave");

    public static Identifier id(String path) {
    	return new Identifier("operation_starcleave", path);
	}

	@Override
	public void onInitialize() {
		OperationStarcleaveEntityTypes.init();
		OperationStarcleaveBlocks.init();
		OperationStarcleaveItems.init();

		OperationStarcleaveBlockEntityTypes.init();

		OperationStarcleaveStatusEffects.init();

		OperationStarcleaveAdvancementCriteria.init();

		OperationStarcleavePacketTypes.init();
		OperationStarcleaveServerPacketHandler.init();

		OperationStarcleaveParticleTypes.init();

		DispenserBlock.registerBehavior(OperationStarcleaveBlocks.NETHERITE_PUMPKIN, new FallibleItemDispenserBehavior() {
			@Override
			protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				World world = pointer.world();
				BlockPos blockPos = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING));
				NetheritePumpkinBlock netheritePumpkinBlock = (NetheritePumpkinBlock)OperationStarcleaveBlocks.NETHERITE_PUMPKIN;
				if (world.isAir(blockPos) && netheritePumpkinBlock.canDispense(world, blockPos)) {
					if (!world.isClient) {
						world.setBlockState(blockPos, netheritePumpkinBlock.getDefaultState(), Block.NOTIFY_ALL);
						world.emitGameEvent(null, GameEvent.BLOCK_PLACE, blockPos);
					}

					stack.decrement(1);
					this.setSuccess(true);
				} else {
					this.setSuccess(ArmorItem.dispenseArmor(pointer, stack));
				}

				return stack;
			}
		});

		ServerTickEvents.START_WORLD_TICK.register((world -> {
			Firmament firmament = Firmament.fromWorld(world);
			if(firmament != null) {
				TickManager tickManager = world.getTickManager();
				boolean shouldTick = tickManager.shouldTick();
				world.getProfiler().push("");
				if (shouldTick) {
					Profiler profiler = world.getProfiler();
					profiler.push("starcleave_fracture");
					firmament.tick();
					profiler.pop();
				}

				firmament.forEachRegion(FirmamentRegion::flushUpdates);
			}
		}));

		ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register(((player, origin, destination) -> ((FirmamentWatcher)player).operation_starcleave$getWatchedRegions().unWatchAll()));

		StarbleachCauldronBlock.init();
	}
}
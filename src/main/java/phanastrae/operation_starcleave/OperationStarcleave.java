package phanastrae.operation_starcleave;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.component.ComponentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.tick.TickManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import phanastrae.operation_starcleave.advancement.criterion.OperationStarcleaveAdvancementCriteria;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;
import phanastrae.operation_starcleave.block.OperationStarcleaveDispenserBehavior;
import phanastrae.operation_starcleave.block.StarbleachCauldronBlock;
import phanastrae.operation_starcleave.block.entity.OperationStarcleaveBlockEntityTypes;
import phanastrae.operation_starcleave.component.OperationStarcleaveComponentTypes;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;
import phanastrae.operation_starcleave.entity.effect.OperationStarcleaveStatusEffects;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;
import phanastrae.operation_starcleave.network.packet.OperationStarcleavePackets;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;
import phanastrae.operation_starcleave.recipe.OperationStarcleaveRecipeSerializers;
import phanastrae.operation_starcleave.recipe.OperationStarcleaveRecipeTypes;
import phanastrae.operation_starcleave.server.network.OperationStarcleaveServerPacketHandler;
import phanastrae.operation_starcleave.sound.OperationStarcleaveSoundEvents;
import phanastrae.operation_starcleave.world.OperationStarcleaveGameRules;
import phanastrae.operation_starcleave.world.firmament.Firmament;
import phanastrae.operation_starcleave.world.firmament.FirmamentRegion;
import phanastrae.operation_starcleave.world.firmament.FirmamentWatcher;

import java.util.function.Consumer;

public class OperationStarcleave implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("operation_starcleave");

    public static Identifier id(String path) {
    	return Identifier.of("operation_starcleave", path);
	}

	@Override
	public void onInitialize() {
		OperationStarcleaveStatusEffects.init();

		OperationStarcleaveEntityTypes.init();
		OperationStarcleaveBlocks.init();
		OperationStarcleaveItems.init();
		OperationStarcleaveComponentTypes.init();

		OperationStarcleaveBlockEntityTypes.init();

		OperationStarcleaveAdvancementCriteria.init();
		OperationStarcleaveGameRules.init();

		OperationStarcleavePackets.init();
		OperationStarcleaveServerPacketHandler.init();

		OperationStarcleaveParticleTypes.init();
		OperationStarcleaveSoundEvents.init();

		OperationStarcleaveRecipeTypes.init();
		OperationStarcleaveRecipeSerializers.init();

		OperationStarcleaveDispenserBehavior.init();
		StarbleachCauldronBlock.init();

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
	}

	public static void addTooltips(ItemStack stack, Item.TooltipContext tooltipContext, Consumer<Text> componentConsumer, TooltipType tooltipFlag) {
		addToTooltip(stack, OperationStarcleaveComponentTypes.STARBLEACH_COMPONENT, tooltipContext, componentConsumer, tooltipFlag);
	}

	private static <T extends TooltipAppender> void addToTooltip(
			ItemStack stack, ComponentType<T> component, Item.TooltipContext context, Consumer<Text> tooltipAdder, TooltipType tooltipFlag
	) {
		T tooltipProvider = stack.get(component);
		if (tooltipProvider != null) {
			tooltipProvider.appendTooltip(context, tooltipAdder, tooltipFlag);
		}
	}
}
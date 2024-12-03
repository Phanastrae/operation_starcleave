package phanastrae.operation_starcleave;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import phanastrae.operation_starcleave.advancement.criterion.OperationStarcleaveAdvancementCriteria;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;
import phanastrae.operation_starcleave.block.OperationStarcleaveDispenserBehavior;
import phanastrae.operation_starcleave.block.StarbleachCauldronBlock;
import phanastrae.operation_starcleave.block.entity.OperationStarcleaveBlockEntityTypes;
import phanastrae.operation_starcleave.component.OperationStarcleaveComponentTypes;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;
import phanastrae.operation_starcleave.item.OperationStarcleaveCreativeModeTabs;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;
import phanastrae.operation_starcleave.recipe.OperationStarcleaveRecipeSerializers;
import phanastrae.operation_starcleave.recipe.OperationStarcleaveRecipeTypes;
import phanastrae.operation_starcleave.sound.OperationStarcleaveSoundEvents;
import phanastrae.operation_starcleave.world.OperationStarcleaveGameRules;
import phanastrae.operation_starcleave.world.firmament.Firmament;
import phanastrae.operation_starcleave.world.firmament.FirmamentRegion;
import phanastrae.operation_starcleave.world.firmament.FirmamentWatcher;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class OperationStarcleave {
	public static final String MOD_ID = "operation_starcleave";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static ResourceLocation id(String path) {
    	return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}

	public static void initRegistryEntries(RegistryListenerAdder rla) {
		// creative mode tabs
		rla.addRegistryListener(BuiltInRegistries.CREATIVE_MODE_TAB, OperationStarcleaveCreativeModeTabs::init);

		// data components
		rla.addRegistryListener(BuiltInRegistries.DATA_COMPONENT_TYPE, OperationStarcleaveComponentTypes::init);

		// blocks
		rla.addRegistryListener(BuiltInRegistries.BLOCK, OperationStarcleaveBlocks::init);
		// items
		rla.addRegistryListener(BuiltInRegistries.ITEM, OperationStarcleaveItems::init);

		// block entity types
		rla.addRegistryListener(BuiltInRegistries.BLOCK_ENTITY_TYPE, OperationStarcleaveBlockEntityTypes::init);

		// entity types
		rla.addRegistryListener(BuiltInRegistries.ENTITY_TYPE, OperationStarcleaveEntityTypes::init);

		// particle types
		rla.addRegistryListener(BuiltInRegistries.PARTICLE_TYPE, OperationStarcleaveParticleTypes::init);

		// sound events
		rla.addRegistryListener(BuiltInRegistries.SOUND_EVENT, OperationStarcleaveSoundEvents::init);

		// advancement triggers
		rla.addRegistryListener(BuiltInRegistries.TRIGGER_TYPES, OperationStarcleaveAdvancementCriteria::init);

		// recipe types
		rla.addRegistryListener(BuiltInRegistries.RECIPE_TYPE, OperationStarcleaveRecipeTypes::init);
		// recipe serializers
		rla.addRegistryListener(BuiltInRegistries.RECIPE_SERIALIZER, OperationStarcleaveRecipeSerializers::init);
	}

	public static void init() {
		// dispenser behaviors
		OperationStarcleaveDispenserBehavior.init();

		// cauldron interactions
		StarbleachCauldronBlock.init();

		// game rules
		OperationStarcleaveGameRules.init();
	}

	public static void startLevelTick(Level level) {
		Firmament firmament = Firmament.fromWorld(level);
		if(firmament != null) {
			TickRateManager tickManager = level.tickRateManager();
			boolean shouldTick = tickManager.runsNormally();
			level.getProfiler().push("");
			if (shouldTick) {
				ProfilerFiller profiler = level.getProfiler();
				profiler.push("starcleave_fracture");
				firmament.tick();
				profiler.pop();
			}

			firmament.forEachRegion(FirmamentRegion::flushUpdates);
		}
	}

	public static void onPlayerChangeDimension(Player player) {
		((FirmamentWatcher)player).operation_starcleave$getWatchedRegions().unWatchAll();
	}

	public static void addTooltips(ItemStack stack, Item.TooltipContext tooltipContext, Consumer<Component> componentConsumer, TooltipFlag tooltipFlag) {
		addToTooltip(stack, OperationStarcleaveComponentTypes.STARBLEACH_COMPONENT, tooltipContext, componentConsumer, tooltipFlag);
	}

	private static <T extends TooltipProvider> void addToTooltip(
			ItemStack stack, DataComponentType<T> component, Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag
	) {
		T tooltipProvider = stack.get(component);
		if (tooltipProvider != null) {
			tooltipProvider.addToTooltip(context, tooltipAdder, tooltipFlag);
		}
	}

	@FunctionalInterface
	public interface RegistryListenerAdder {
		<T> void addRegistryListener(Registry<T> registry, Consumer<BiConsumer<ResourceLocation, T>> source);
	}
}
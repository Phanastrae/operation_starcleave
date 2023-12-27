package phanastrae.operation_starcleave;

import net.fabricmc.api.ModInitializer;

import net.minecraft.block.Block;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import phanastrae.operation_starcleave.advancement.criterion.OperationStarcleaveAdvancementCriteria;
import phanastrae.operation_starcleave.block.NetheritePumpkinBlock;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;

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

		OperationStarcleaveAdvancementCriteria.init();

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
	}
}
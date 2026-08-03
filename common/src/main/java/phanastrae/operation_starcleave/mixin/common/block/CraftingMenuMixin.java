package phanastrae.operation_starcleave.mixin.common.block;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;

@Mixin(CraftingMenu.class)
public abstract class CraftingMenuMixin extends RecipeBookMenu<CraftingInput, CraftingRecipe> {
    private CraftingMenuMixin(MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    @Shadow
    @Final
    private ContainerLevelAccess access;

    @Inject(method = "stillValid", at = @At("RETURN"), cancellable = true)
    private void operation_starcleave$allowCustomTables(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (stillValid(this.access, player, OperationStarcleaveBlocks.SHELL_N_BLEACH_CRAFTING_TABLE)) {
            cir.setReturnValue(true);
        }
    }
}

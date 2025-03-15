package phanastrae.operation_starcleave.fabric.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import phanastrae.operation_starcleave.fluid.OperationStarcleaveFluids;
import phanastrae.operation_starcleave.registry.OperationStarcleaveFluidTags;

import java.util.concurrent.CompletableFuture;

public class FluidTagProvider extends FabricTagProvider.FluidTagProvider {

    public FluidTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        // starcleave
        getOrCreateTagBuilder(OperationStarcleaveFluidTags.PETRICHORIC_PLASMA)
                .add(
                        OperationStarcleaveFluids.PETRICHORIC_PLASMA,
                        OperationStarcleaveFluids.FLOWING_PETRICHORIC_PLASMA
                );
    }
}

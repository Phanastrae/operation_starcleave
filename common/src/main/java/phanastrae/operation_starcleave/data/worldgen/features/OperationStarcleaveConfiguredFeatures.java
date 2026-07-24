package phanastrae.operation_starcleave.data.worldgen.features;

import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.PinkPetalsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;
import phanastrae.operation_starcleave.block.tag.OperationStarcleaveBlockTags;
import phanastrae.operation_starcleave.world.feature.OperationStarcleaveFeatures;

public class OperationStarcleaveConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> HOLY_MOSS_VEGETATION = createKey("holy_moss_vegetation");
    public static final ResourceKey<ConfiguredFeature<?, ?>> HOLY_MOSS_PATCH_BONEMEAL = createKey("holy_moss_patch_bonemeal");
    public static final ResourceKey<ConfiguredFeature<?, ?>> STELLAR_MULCH_VEGETATION = createKey("stellar_mulch_vegetation");
    public static final ResourceKey<ConfiguredFeature<?, ?>> STELLAR_MULCH_PATCH_BONEMEAL = createKey("stellar_mulch_patch_bonemeal");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SMALL_STELLAR_MULCH_PATCH_BONEMEAL = createKey("small_stellar_mulch_patch_bonemeal");

    public static final ResourceKey<ConfiguredFeature<?, ?>> ASTERUBBLE_BOULDER = createKey("asterubble_boulder");

    public static ResourceKey<ConfiguredFeature<?, ?>> createKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, OperationStarcleave.id(name));
    }

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        HolderGetter<ConfiguredFeature<?, ?>> featureLookup = context.lookup(Registries.CONFIGURED_FEATURE);
        HolderGetter<StructureProcessorList> processorLookup = context.lookup(Registries.PROCESSOR_LIST);

        SimpleWeightedRandomList.Builder<BlockState> holyMossVegetationBuilder = SimpleWeightedRandomList.builder();
        for (int i = 1; i <= 4; i++) {
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                BlockState state = OperationStarcleaveBlocks.STARCLOVERS.defaultBlockState().setValue(PinkPetalsBlock.AMOUNT, i).setValue(PinkPetalsBlock.FACING, direction);
                holyMossVegetationBuilder.add(state, 3);
            }
        }
        holyMossVegetationBuilder
                .add(OperationStarcleaveBlocks.SHORT_HOLY_MOSS.defaultBlockState(), 168)
                .add(OperationStarcleaveBlocks.TALL_HOLY_MOSS.defaultBlockState(), 14)
                .add(OperationStarcleaveBlocks.GREAT_TREES_CARE.defaultBlockState(), 2)
                .add(OperationStarcleaveBlocks.RED_MOURNER.defaultBlockState(), 2)
                .add(OperationStarcleaveBlocks.ANGELCLAW.defaultBlockState(), 2)
                .add(OperationStarcleaveBlocks.ELDROSE.defaultBlockState(), 2)
                .add(OperationStarcleaveBlocks.WITCHGLARE.defaultBlockState(), 2)
                .add(OperationStarcleaveBlocks.BLUE_DREAMER.defaultBlockState(), 2)
                .add(OperationStarcleaveBlocks.DRAGONS_MAW.defaultBlockState(), 2);

        FeatureUtils.register(
                context,
                HOLY_MOSS_VEGETATION,
                Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(new WeightedStateProvider(holyMossVegetationBuilder.build()))
        );

        FeatureUtils.register(
                context,
                HOLY_MOSS_PATCH_BONEMEAL,
                Feature.VEGETATION_PATCH,
                new VegetationPatchConfiguration(
                        OperationStarcleaveBlockTags.HOLY_MOSS_REPLACEABLE,
                        BlockStateProvider.simple(OperationStarcleaveBlocks.HOLY_MOSS),
                        PlacementUtils.inlinePlaced(featureLookup.getOrThrow(HOLY_MOSS_VEGETATION)),
                        CaveSurface.FLOOR,
                        ConstantInt.of(1),
                        0.0F,
                        3,
                        0.5F,
                        ConstantInt.of(1),
                        0.6F
                )
        );

        FeatureUtils.register(
                context,
                STELLAR_MULCH_VEGETATION,
                Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(
                        BlockStateProvider.simple(OperationStarcleaveBlocks.MULCHBORNE_TUFT)
                )
        );

        FeatureUtils.register(
                context,
                STELLAR_MULCH_PATCH_BONEMEAL,
                Feature.VEGETATION_PATCH,
                new VegetationPatchConfiguration(
                        OperationStarcleaveBlockTags.STELLAR_MULCH_REPLACEABLE,
                        BlockStateProvider.simple(OperationStarcleaveBlocks.STELLAR_MULCH),
                        PlacementUtils.inlinePlaced(featureLookup.getOrThrow(STELLAR_MULCH_VEGETATION)),
                        CaveSurface.FLOOR,
                        ConstantInt.of(1),
                        0.0F,
                        3,
                        0.4F,
                        ConstantInt.of(1),
                        0.75F
                )
        );

        FeatureUtils.register(
                context,
                SMALL_STELLAR_MULCH_PATCH_BONEMEAL,
                Feature.VEGETATION_PATCH,
                new VegetationPatchConfiguration(
                        OperationStarcleaveBlockTags.STELLAR_MULCH_REPLACEABLE,
                        BlockStateProvider.simple(OperationStarcleaveBlocks.STELLAR_MULCH),
                        PlacementUtils.inlinePlaced(featureLookup.getOrThrow(STELLAR_MULCH_VEGETATION)),
                        CaveSurface.FLOOR,
                        ConstantInt.of(1),
                        0.0F,
                        2,
                        0.35F,
                        ConstantInt.of(0),
                        0.75F
                )
        );

        FeatureUtils.register(
                context,
                ASTERUBBLE_BOULDER,
                OperationStarcleaveFeatures.ASTERUBBLE_BOULDER,
                new BlockStateConfiguration(OperationStarcleaveBlocks.ASTERUBBLE.defaultBlockState())
        );
    }
}

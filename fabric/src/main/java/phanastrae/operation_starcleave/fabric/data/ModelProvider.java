package phanastrae.operation_starcleave.fabric.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.*;
import net.minecraft.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.block.StarbleachCauldronBlock;
import phanastrae.operation_starcleave.block.StarbleachedPearlBlock;
import phanastrae.operation_starcleave.data.OperationStarcleaveBlockFamilies;
import phanastrae.operation_starcleave.fabric.mixin.client.ModelTemplateAccessor;
import phanastrae.operation_starcleave.fabric.mixin.client.TextureMappingAccessor;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static net.minecraft.data.models.BlockModelGenerators.*;
import static phanastrae.operation_starcleave.block.OperationStarcleaveBlocks.*;

public class ModelProvider extends FabricModelProvider {
    private static final String SUFFIX_IRIDESCENCE = "_iridescence";

    public ModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators BMG) {
        OperationStarcleaveBlockFamilies
                .getAllOperationStarcleaveFamilies()
                .filter(BlockFamily::shouldGenerateModel)
                .forEach(blockFamily -> BMG.family(blockFamily.getBaseBlock()).generateFor(blockFamily));

        forEach(BMG::createTrivialCube,
                IMBUED_STARBLEACHED_TILES,
                NUCLEOSYNTHESEED,
                PLASMA_ICE,

                STARFLAKED_BISMUTH_BLOCK
        );

        forEach(BMG::createRotatedVariantBlock,
                COAGULATED_PLASMA,
                STELLAR_SEDIMENT,
                STARDUST_BLOCK,
                PETRICHORIC_VAPOR
        );

        forEach(block -> BMG.createTrivialBlock(block, TexturedModel.LEAVES),
                STARBLEACHED_LEAVES,
                NUCLEIC_FISSURELEAVES
        );

        BMG.woodProvider(STARBLEACHED_LOG).logWithHorizontal(STARBLEACHED_LOG).wood(STARBLEACHED_WOOD);
        BMG.woodProvider(NUCLEIC_FISSUREROOT).logWithHorizontal(NUCLEIC_FISSUREROOT).wood(NUCLEIC_FISSURERIND);
        BMG.woodProvider(STRIPED_NUCLEIC_FISSUREROOT).logWithHorizontal(STRIPED_NUCLEIC_FISSUREROOT).wood(STRIPED_NUCLEIC_FISSURERIND);

        registerUnevenCross(BMG, MULCHBORNE_TUFT);
        createPottedRoot(BMG, MULCHBORNE_TUFT, POTTED_MULCHBORNE_TUFT, TintState.NOT_TINTED);
        BMG.createCrossBlockWithDefaultItem(SHORT_HOLY_MOSS, BlockModelGenerators.TintState.NOT_TINTED);
        createPottedRoot(BMG, SHORT_HOLY_MOSS, POTTED_SHORT_HOLY_MOSS, TintState.NOT_TINTED);

        BMG.blockStateOutput.accept(createRotatedVariant(STELLAR_PATH, ModelLocationUtils.getModelLocation(STELLAR_PATH)));
        registerGrassLikeBlock(BMG, HOLY_MOSS, STELLAR_SEDIMENT);
        registerGrassLikeBlock(BMG, STELLAR_MULCH, STELLAR_SEDIMENT);

        BMG.createRotatedPillarWithHorizontalVariant(SUBCAELIC_PHLOGLIGHT, TexturedModel.COLUMN, TexturedModel.COLUMN_HORIZONTAL);

        BMG.createCropBlock(BISREEDS, BlockStateProperties.AGE_3, 0, 1, 2, 3);

        registerPumpkin(BMG, NETHERITE_PUMPKIN);

        registerStarbleachedPearlBlock(BMG, STARBLEACHED_PEARL_BLOCK);

        registerClothBlocks(BMG, BLESSED_CLOTH_BLOCK, BLESSED_CLOTH_CARPET, BLESSED_CLOTH_CURTAIN);

        registerStellarFarmland(BMG, STELLAR_FARMLAND, STELLAR_SEDIMENT);

        registerFire(BMG, PHLOGISTIC_FIRE);

        registerStarbleachCauldron(BMG, STARBLEACH_CAULDRON);

        createSplitSlab(BMG, STARFLAKED_BISMUTH_SLAB, STARFLAKED_BISMUTH_BLOCK);

        // fluids
        BMG.createNonTemplateModelBlock(PETRICHORIC_PLASMA);

        // iridescence
        // these functions only create the models, blockstates need to still be done manually
        forEach(block -> createTrivialCubeForSuffix(SUFFIX_IRIDESCENCE, BMG, block),
                STARFLAKED_BISMUTH_BLOCK,
                STARFLAKED_BISMUTH_TILES
        );
        createCropForSuffix(2, SUFFIX_IRIDESCENCE, BMG, BISREEDS);
        createCropForSuffix(3, SUFFIX_IRIDESCENCE, BMG, BISREEDS);

        createSplitSlabForSuffix(SUFFIX_IRIDESCENCE, BMG, STARFLAKED_BISMUTH_SLAB, STARFLAKED_BISMUTH_BLOCK);
        createSlabForSuffix(SUFFIX_IRIDESCENCE, BMG, STARFLAKED_BISMUTH_TILE_SLAB, STARFLAKED_BISMUTH_TILES);
        createStairsForSuffix(SUFFIX_IRIDESCENCE, BMG, STARFLAKED_BISMUTH_TILE_STAIRS, STARFLAKED_BISMUTH_TILES);
        createWallForSuffix(SUFFIX_IRIDESCENCE, BMG, STARFLAKED_BISMUTH_TILE_WALL, STARFLAKED_BISMUTH_TILES);
    }

    private static void createSplitSlab(BlockModelGenerators BMG, Block block, Block fullBlock) {
        TextureMapping cubeMapping = TextureMapping.cube(fullBlock);
        TextureMapping columnMapping = TextureMapping.column(TextureMapping.getBlockTexture(block, "_side"), cubeMapping.get(TextureSlot.TOP));
        ResourceLocation bottomRL = ModelTemplates.SLAB_BOTTOM.create(block, columnMapping, BMG.modelOutput);
        ResourceLocation topRL = ModelTemplates.SLAB_TOP.create(block, columnMapping, BMG.modelOutput);
        ResourceLocation doubleRL = ModelTemplates.CUBE_COLUMN.createWithOverride(block, "_double", columnMapping, BMG.modelOutput);
        BMG.blockStateOutput.accept(createSlab(block, bottomRL, topRL, doubleRL));
    }

    private void forEach(Consumer<Block> consumer, Block... list) {
        for (Block block : list) {
            consumer.accept(block);
        }
    }

    private void registerUnevenCross(BlockModelGenerators BMG, Block block) {
        BMG.createSimpleFlatItemModel(block);

        TextureMapping textureMap = TextureMapping.cross(block);
        ResourceLocation modelId = OperationStarcleaveModels.UNEVEN_CROSS.create(block, textureMap, BMG.modelOutput);
        ResourceLocation modelId2 = OperationStarcleaveModels.UNEVEN_CROSS_MIRRORED.create(block, textureMap, BMG.modelOutput);
        BMG.blockStateOutput.accept(MultiVariantGenerator.multiVariant(
                block,
                Variant.variant().with(VariantProperties.MODEL, modelId),
                Variant.variant().with(VariantProperties.MODEL, modelId2),
                Variant.variant().with(VariantProperties.MODEL, modelId).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90),
                Variant.variant().with(VariantProperties.MODEL, modelId2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
        ));
    }

    protected static void createPottedPlant(BlockModelGenerators BMG, Block plantBlock, Block pottedPlantBlock, BlockModelGenerators.TintState tintState) {
        TextureMapping textureMapping = TextureMapping.plant(plantBlock);
        createPotted(BMG, pottedPlantBlock, textureMapping, tintState);
    }

    protected static void createPottedRoot(BlockModelGenerators BMG, Block plantBlock, Block pottedPlantBlock, BlockModelGenerators.TintState tintState) {
        TextureMapping textureMapping = TextureMapping.plant(TextureMapping.getBlockTexture(plantBlock, "_pot"));
        createPotted(BMG, pottedPlantBlock, textureMapping, tintState);
    }

    protected static void createPotted(BlockModelGenerators BMG, Block pottedPlantBlock, TextureMapping textureMapping, BlockModelGenerators.TintState tintState) {
        ResourceLocation resourceLocation = tintState.getCrossPot().create(pottedPlantBlock, textureMapping, BMG.modelOutput);
        BMG.blockStateOutput.accept(createSimpleBlock(pottedPlantBlock, resourceLocation));
    }

    private void registerGrassLikeBlock(BlockModelGenerators BMG, Block block, Block baseBlock) {
        ResourceLocation baseBlockIdentifier = TextureMapping.getBlockTexture(baseBlock);
        ResourceLocation modelId = TexturedModel.CUBE_TOP_BOTTOM
                .get(block)
                .updateTextures(textures -> textures.put(TextureSlot.BOTTOM, baseBlockIdentifier))
                .create(block, BMG.modelOutput);
        BMG.blockStateOutput
                .accept(MultiVariantGenerator.multiVariant(block, createRotatedVariants(modelId)));
    }

    private void registerPumpkin(BlockModelGenerators BMG, Block block) {
        TextureMapping textureMap = TextureMapping.column(block);
        BMG.createPumpkinVariant(block, textureMap);
    }

    private void registerStarbleachedPearlBlock(BlockModelGenerators BMG, Block block) {
        ResourceLocation off = TexturedModel.CUBE.create(block, BMG.modelOutput);
        ResourceLocation on = BMG.createSuffixedVariant(block, "_on", ModelTemplates.CUBE_ALL, TextureMapping::cube);
        BMG.blockStateOutput
                .accept(MultiVariantGenerator.multiVariant(block).with(createBooleanModelDispatch(StarbleachedPearlBlock.TRIGGERED, on, off)));
    }

    private void registerClothBlocks(BlockModelGenerators BMG, Block wool, Block carpet, Block curtain) {
        BMG.createTrivialCube(wool);

        TextureMapping textureMap = new TextureMapping().put(TextureSlot.PANE, TextureMapping.getBlockTexture(curtain)).put(TextureSlot.EDGE, TextureMapping.getBlockTexture(wool));

        ResourceLocation postModel = ModelTemplates.STAINED_GLASS_PANE_POST.create(curtain, textureMap, BMG.modelOutput);
        ResourceLocation sideModel = ModelTemplates.STAINED_GLASS_PANE_SIDE.create(curtain, textureMap, BMG.modelOutput);
        ResourceLocation sideAltModel = ModelTemplates.STAINED_GLASS_PANE_SIDE_ALT.create(curtain, textureMap, BMG.modelOutput);
        ResourceLocation noSideModel = ModelTemplates.STAINED_GLASS_PANE_NOSIDE.create(curtain, textureMap, BMG.modelOutput);
        ResourceLocation noSideAltModel = ModelTemplates.STAINED_GLASS_PANE_NOSIDE_ALT.create(curtain, textureMap, BMG.modelOutput);

        Item item = curtain.asItem();
        ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(item), TextureMapping.layer0(curtain), BMG.modelOutput);
        BMG.blockStateOutput
                .accept(
                        MultiPartGenerator.multiPart(curtain)
                                .with(Variant.variant().with(VariantProperties.MODEL, postModel))
                                .with(Condition.condition().term(BlockStateProperties.NORTH, true), Variant.variant().with(VariantProperties.MODEL, sideModel))
                                .with(
                                        Condition.condition().term(BlockStateProperties.EAST, true),
                                        Variant.variant().with(VariantProperties.MODEL, sideModel).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                )
                                .with(Condition.condition().term(BlockStateProperties.SOUTH, true), Variant.variant().with(VariantProperties.MODEL, sideAltModel))
                                .with(
                                        Condition.condition().term(BlockStateProperties.WEST, true),
                                        Variant.variant().with(VariantProperties.MODEL, sideAltModel).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                )
                                .with(Condition.condition().term(BlockStateProperties.NORTH, false), Variant.variant().with(VariantProperties.MODEL, noSideModel))
                                .with(Condition.condition().term(BlockStateProperties.EAST, false), Variant.variant().with(VariantProperties.MODEL, noSideAltModel))
                                .with(
                                        Condition.condition().term(BlockStateProperties.SOUTH, false),
                                        Variant.variant().with(VariantProperties.MODEL, noSideAltModel).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                )
                                .with(
                                        Condition.condition().term(BlockStateProperties.WEST, false),
                                        Variant.variant().with(VariantProperties.MODEL, noSideModel).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                                )
                );

        ResourceLocation carpetModel = TexturedModel.CARPET.get(wool).create(carpet, BMG.modelOutput);
        BMG.blockStateOutput.accept(createSimpleBlock(carpet, carpetModel));
    }

    private void registerStellarFarmland(BlockModelGenerators BMG, Block block, Block dirtBlock) {
        TextureMapping dryTextures = new TextureMapping().put(TextureSlot.DIRT, TextureMapping.getBlockTexture(dirtBlock)).put(TextureSlot.TOP, TextureMapping.getBlockTexture(block));
        TextureMapping moistTextures = new TextureMapping().put(TextureSlot.DIRT, TextureMapping.getBlockTexture(dirtBlock)).put(TextureSlot.TOP, TextureMapping.getBlockTexture(block, "_moist"));
        ResourceLocation dryModel = ModelTemplates.FARMLAND.create(block, dryTextures, BMG.modelOutput);
        ResourceLocation moistModel = ModelTemplates.FARMLAND.create(TextureMapping.getBlockTexture(block, "_moist"), moistTextures, BMG.modelOutput);
        BMG.blockStateOutput
                .accept(MultiVariantGenerator.multiVariant(block).with(createEmptyOrFullDispatch(BlockStateProperties.MOISTURE, 7, moistModel, dryModel)));
    }

    private void registerFire(BlockModelGenerators BMG, Block block) {
        Condition when = Condition.condition()
                .term(BlockStateProperties.NORTH, false)
                .term(BlockStateProperties.EAST, false)
                .term(BlockStateProperties.SOUTH, false)
                .term(BlockStateProperties.WEST, false)
                .term(BlockStateProperties.UP, false);
        List<ResourceLocation> floorFireModels = BMG.createFloorFireModels(block);
        List<ResourceLocation> sideFireModels = BMG.createSideFireModels(block);
        List<ResourceLocation> topFireModels = BMG.createTopFireModels(block);
        BMG.blockStateOutput
                .accept(
                        MultiPartGenerator.multiPart(block)
                                .with(when, wrapModels(floorFireModels, blockStateVariant -> blockStateVariant))
                                .with(Condition.or(Condition.condition().term(BlockStateProperties.NORTH, true), when), wrapModels(sideFireModels, blockStateVariant -> blockStateVariant))
                                .with(
                                        Condition.or(Condition.condition().term(BlockStateProperties.EAST, true), when),
                                        wrapModels(sideFireModels, blockStateVariant -> blockStateVariant.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                                )
                                .with(
                                        Condition.or(Condition.condition().term(BlockStateProperties.SOUTH, true), when),
                                        wrapModels(sideFireModels, blockStateVariant -> blockStateVariant.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
                                )
                                .with(
                                        Condition.or(Condition.condition().term(BlockStateProperties.WEST, true), when),
                                        wrapModels(sideFireModels, blockStateVariant -> blockStateVariant.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                                )
                                .with(Condition.condition().term(BlockStateProperties.UP, true), wrapModels(topFireModels, blockStateVariant -> blockStateVariant))
                );
    }

    private void registerStarbleachCauldron(BlockModelGenerators BMG, Block block) {
        PropertyDispatch.C1<Integer> map = PropertyDispatch.property(StarbleachCauldronBlock.LEVEL_7);
        for (int i = 1; i <= 7; i++) {
            map = map.select(
                    i,
                    Variant.variant()
                            .with(
                                    VariantProperties.MODEL,
                                    OperationStarcleaveModels.getSevenLevelCauldron(i)
                                            .createWithSuffix(block, "_level" + i, TextureMapping.cauldron(OperationStarcleave.id("block/starbleach_still")), BMG.modelOutput)
                            )
            );
        }
        BMG.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block).with(map));
    }

    private static void createCropForSuffix(int stage, String extraSuffix, BlockModelGenerators BMG, Block block) {
        createModelForSuffix("_stage" + stage + extraSuffix, BMG, block, TextureMapping.crop(TextureMapping.getBlockTexture(block)), ModelTemplates.CROP);
    }

    private static void createTrivialCubeForSuffix(String suffix, BlockModelGenerators BMG, Block block) {
        createModelForSuffix(suffix, BMG, block, TexturedModel.CUBE);
    }

    private static void createSlabForSuffix(String suffix, BlockModelGenerators BMG, Block block, Block fullBlock) {
        TextureMapping mapping = TextureMapping.cube(fullBlock);
        createModelForSuffix(suffix, BMG, block, mapping, ModelTemplates.SLAB_BOTTOM);
        createModelForSuffix(suffix, BMG, block, mapping, ModelTemplates.SLAB_TOP);
    }

    private static void createSplitSlabForSuffix(String suffix, BlockModelGenerators BMG, Block block, Block fullBlock) {
        TextureMapping cubeMapping = TextureMapping.cube(fullBlock);
        TextureMapping columnMapping = TextureMapping.column(TextureMapping.getBlockTexture(block, "_side"), cubeMapping.get(TextureSlot.TOP));
        createModelForSuffix(suffix, BMG, block, columnMapping, ModelTemplates.SLAB_BOTTOM);
        createModelForSuffix(suffix, BMG, block, columnMapping, ModelTemplates.SLAB_TOP);
        createModelForSuffixes(suffix, "_double" + suffix, BMG, block, columnMapping, ModelTemplates.CUBE_COLUMN);
    }

    private static void createStairsForSuffix(String suffix, BlockModelGenerators BMG, Block block, Block fullBlock) {
        TextureMapping mapping = TextureMapping.cube(fullBlock);
        createModelForSuffix(suffix, BMG, block, mapping, ModelTemplates.STAIRS_INNER);
        createModelForSuffix(suffix, BMG, block, mapping, ModelTemplates.STAIRS_OUTER);
        createModelForSuffix(suffix, BMG, block, mapping, ModelTemplates.STAIRS_STRAIGHT);
    }

    private static void createWallForSuffix(String suffix, BlockModelGenerators BMG, Block block, Block fullBlock) {
        TextureMapping mapping = TextureMapping.cube(fullBlock);
        createModelForSuffix(suffix, BMG, block, mapping, ModelTemplates.WALL_POST);
        createModelForSuffix(suffix, BMG, block, mapping, ModelTemplates.WALL_LOW_SIDE);
        createModelForSuffix(suffix, BMG, block, mapping, ModelTemplates.WALL_TALL_SIDE);
        createModelForSuffix(suffix, BMG, block, mapping, ModelTemplates.WALL_INVENTORY);
    }

    private static void createModelForSuffix(String suffix, BlockModelGenerators BMG, Block block, TexturedModel.Provider provider) {
        TexturedModel model = provider.get(block);
        createModelForSuffix(suffix, BMG, block, model.getMapping(), model.getTemplate());
    }

    private static void createModelForSuffix(String suffix, BlockModelGenerators BMG, Block block, TextureMapping mapping, ModelTemplate template) {
        TextureMapping suffixMapping = createMappingForSuffix(suffix, mapping);
        template.create(ModelLocationUtils.getModelLocation(block, ((ModelTemplateAccessor) template).getSuffix().orElse("") + suffix), suffixMapping, BMG.modelOutput);
    }

    private static void createModelForSuffixes(String mappingSuffix, String modelSuffix, BlockModelGenerators BMG, Block block, TextureMapping mapping, ModelTemplate template) {
        TextureMapping suffixMapping = createMappingForSuffix(mappingSuffix, mapping);
        template.create(ModelLocationUtils.getModelLocation(block, ((ModelTemplateAccessor) template).getSuffix().orElse("") + modelSuffix), suffixMapping, BMG.modelOutput);
    }

    private static TextureMapping createMappingForSuffix(String suffix, TextureMapping mapping) {
        TextureMappingAccessor tma = (TextureMappingAccessor) mapping;
        Map<TextureSlot, ResourceLocation> slots = tma.getSlots();
        Set<TextureSlot> forcedSlots = tma.getForcedSlots();

        TextureMapping suffixMapping = new TextureMapping();
        slots.forEach((slot, location) -> {
            if (forcedSlots.contains(slot)) {
                suffixMapping.putForced(slot, location.withSuffix(suffix));
            } else {
                suffixMapping.put(slot, location.withSuffix(suffix));
            }
        });
        return suffixMapping;
    }

    @Override
    public void generateItemModels(ItemModelGenerators IMG) {
        forEach(item -> generateFlat(IMG, item),
                OperationStarcleaveItems.STARCLEAVER_GOLEM_BUCKET,

                OperationStarcleaveItems.BISMUTH_FLAKE,
                OperationStarcleaveItems.STARFLAKED_BISMUTH,

                OperationStarcleaveItems.STARDUST_CLUSTER,

                OperationStarcleaveItems.BLESSED_BED,

                OperationStarcleaveItems.STARBLEACH_BOTTLE,
                OperationStarcleaveItems.SPLASH_STARBLEACH_BOTTLE,

                OperationStarcleaveItems.STARBLEACHED_PEARL,
                OperationStarcleaveItems.STARFRUIT,

                OperationStarcleaveItems.HOLY_STRANDS,
                OperationStarcleaveItems.BLESSED_CLOTH,

                OperationStarcleaveItems.HOLLOWED_SAC,
                OperationStarcleaveItems.PHLOGISTON_SAC,

                OperationStarcleaveItems.BISMUTH_PEGASUS_ARMOR,

                OperationStarcleaveItems.PETRICHORIC_PLASMA_BUCKET,

                OperationStarcleaveItems.NUCLEAR_STORMCLOUD_BOTTLE,

                OperationStarcleaveItems.FIRMAMENT_REJUVENATOR
        );

        IMG.generateFlatItem(OperationStarcleaveItems.FIRMAMENT_MANIPULATOR, ModelTemplates.FLAT_HANDHELD_ITEM);

        // iridescence
        forEach(item -> generateFlatForSuffix(SUFFIX_IRIDESCENCE, IMG, item, ModelTemplates.FLAT_ITEM),
                OperationStarcleaveItems.BISMUTH_FLAKE,
                OperationStarcleaveItems.STARFLAKED_BISMUTH,
                OperationStarcleaveItems.BISMUTH_PEGASUS_ARMOR
        );

        forEach(block -> addDelegateModelForSuffix(SUFFIX_IRIDESCENCE, IMG, block),
                STARFLAKED_BISMUTH_BLOCK,
                STARFLAKED_BISMUTH_SLAB,

                STARFLAKED_BISMUTH_TILES,
                STARFLAKED_BISMUTH_TILE_STAIRS,
                STARFLAKED_BISMUTH_TILE_SLAB
        );
        addDelegateWallModelForSuffix(SUFFIX_IRIDESCENCE, IMG, STARFLAKED_BISMUTH_TILE_WALL);
    }

    private void forEach(Consumer<Item> consumer, Item... list) {
        for (Item item : list) {
            consumer.accept(item);
        }
    }

    private static void generateFlat(ItemModelGenerators IMG, Item item) {
        IMG.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
    }

    private static void generateFlatForSuffix(String suffix, ItemModelGenerators IMG, Item item, ModelTemplate modelTemplate) {
        modelTemplate.create(
                ModelLocationUtils.getModelLocation(item).withSuffix(suffix),
                new TextureMapping().put(TextureSlot.LAYER0, TextureMapping.getItemTexture(item).withSuffix(suffix)),
                IMG.output
        );
    }

    private static void addDelegateModelForSuffix(String suffix, ItemModelGenerators IMG, Block block) {
        ResourceLocation resourceLocation = ModelLocationUtils.getModelLocation(block.asItem()).withSuffix(suffix);
        IMG.output.accept(resourceLocation, new DelegatedModel(ModelLocationUtils.getModelLocation(block).withSuffix(suffix)));
    }

    private static void addDelegateWallModelForSuffix(String suffix, ItemModelGenerators IMG, Block block) {
        ResourceLocation resourceLocation = ModelLocationUtils.getModelLocation(block.asItem()).withSuffix(suffix);
        IMG.output.accept(resourceLocation, new DelegatedModel(ModelLocationUtils.getModelLocation(block).withSuffix("_inventory" + suffix)));
    }
}

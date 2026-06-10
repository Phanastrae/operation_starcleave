package phanastrae.operation_starcleave.fabric.data;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.core.Direction;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.*;
import net.minecraft.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.block.StarbleachCauldronBlock;
import phanastrae.operation_starcleave.block.StarbleachedPearlBlock;
import phanastrae.operation_starcleave.data.OperationStarcleaveBlockFamilies;
import phanastrae.operation_starcleave.fabric.mixin.client.ModelTemplateAccessor;
import phanastrae.operation_starcleave.fabric.mixin.client.TextureMappingAccessor;
import phanastrae.operation_starcleave.fabric.mixin.client.datagen.BlockFamilyProviderAccessor;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static net.minecraft.data.models.BlockModelGenerators.*;
import static phanastrae.operation_starcleave.block.OperationStarcleaveBlocks.*;

public class ModelProvider extends FabricModelProvider {
    private static final String SUFFIX_IRIDESCENCE = "_iridescence";
    public static final Map<Block, TexturedModel> CUSTOM_TEXTURED_MODELS = ImmutableMap.<Block, TexturedModel>builder()
            .put(FELLCRUST, TexturedModel.TOP_BOTTOM_WITH_WALL.get(FELLCRUST))
            .put(CHISELED_FELLCRUST, TexturedModel.CUBE_TOP_BOTTOM.get(FELLCRUST)
                    .updateTextures(mapping -> {
                        mapping.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(CHISELED_FELLCRUST));
                        mapping.put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(CUT_FELLCRUST, "_bottom"));
                    })
            )
            .put(CUT_FELLCRUST, TexturedModel.CUBE_TOP_BOTTOM.get(FELLCRUST)
                    .updateTextures(mapping -> {
                        mapping.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(CUT_FELLCRUST));
                        mapping.put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(CUT_FELLCRUST, "_bottom"));
                    })
            )
            .put(SMOOTH_FELLCRUST, TexturedModel.createAllSame(TextureMapping.getBlockTexture(FELLCRUST, "_top")))
            .put(COBBLED_FELLCRUST, TexturedModel.createDefault(
                            block -> {
                                ResourceLocation resourceLocation = TextureMapping.getBlockTexture(block);
                                return new TextureMapping()
                                        .put(TextureSlot.WALL, resourceLocation)
                                        .put(TextureSlot.SIDE, resourceLocation)
                                        .put(TextureSlot.TOP, TextureMapping.getBlockTexture(FELLCRUST, "_bottom"))
                                        .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(FELLCRUST, "_bottom"));
                            },
                            ModelTemplates.CUBE_BOTTOM_TOP
                    ).get(COBBLED_FELLCRUST)
            )
            .build();
    public static final Set<Block> SKIP_FAMILY_MODEL_GENERATION = ImmutableSet.<Block>builder()
            .add(
                    FELLCRUST_SLAB,
                    FELLCRUST_STAIRS,
                    FELLCRUST_WALL,
                    CUT_FELLCRUST_STAIRS
            )
            .build();

    public ModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators BMG) {
        OperationStarcleaveBlockFamilies
                .getAllOperationStarcleaveFamilies()
                .filter(BlockFamily::shouldGenerateModel)
                .forEach(blockFamily -> {
                    BlockModelGenerators.BlockFamilyProvider provider = BMG.family(blockFamily.getBaseBlock());

                    Set<Block> skipModels = ((BlockFamilyProviderAccessor) provider).getSkipGeneratingModelsFor();
                    for (Block block : blockFamily.getVariants().values()) {
                        if (SKIP_FAMILY_MODEL_GENERATION.contains(block)) {
                            skipModels.add(block);
                        }
                    }

                    provider.generateFor(blockFamily);
                });

        forEach(BMG::createTrivialCube,
                IMBUED_STARBLEACHED_TILES,
                NUCLEOSYNTHESEED,
                PLASMA_ICE,

                STARFLAKED_BISMUTH_BLOCK,
                CHISELED_STARFLAKED_BISMUTH_BLOCK,

                BUDDING_CELESTIAL_OPAL,

                MUCKY_SINGUT_BLOCK,
                CLEANSED_SINGUT_BLOCK
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

        forEach(block -> BMG.createRotatedPillarWithHorizontalVariant(block, TexturedModel.COLUMN_ALT, TexturedModel.COLUMN_HORIZONTAL_ALT),
                SMOOTH_FELLCRUST_PILLAR,
                MUCKY_SINGUT_COIL,
                CLEANSED_SINGUT_COIL,
                OURANIC_PILLAR
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
        BMG.createRotatedPillarWithHorizontalVariant(STARFLAKED_BISMUTH_PILLAR, TexturedModel.COLUMN_ALT, TexturedModel.COLUMN_HORIZONTAL_ALT);

        BMG.createDoor(STARFLAKED_BISMUTH_DOOR);
        BMG.createOrientableTrapdoor(STARFLAKED_BISMUTH_TRAPDOOR);

        forEach(BMG::createAmethystCluster,
                CELESTIAL_OPAL_SPIRE,
                CELESTIAL_OPAL_CLUSTER,
                LARGE_CELESTIAL_OPAL_BUD,
                MEDIUM_CELESTIAL_OPAL_BUD,
                SMALL_CELESTIAL_OPAL_BUD
        );

        BMG.createRotatedPillarWithHorizontalVariant(POLISHED_CELESTIAL_OPAL_PILLAR, TexturedModel.COLUMN_ALT, TexturedModel.COLUMN_HORIZONTAL_ALT);

        createSplitSlab(BMG, FELLCRUST_SLAB,
                new TextureMapping()
                        .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(FELLCRUST_SLAB, "_side"))
                        .put(TextureSlot.TOP, TextureMapping.getBlockTexture(FELLCRUST, "_top"))
                        .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(FELLCRUST, "_bottom")),
                ModelTemplates.CUBE_BOTTOM_TOP
        );
        createFellcrustStairs(BMG, FELLCRUST_STAIRS);
        createFellcrustWall(BMG, FELLCRUST_WALL);
        createCutFellcrustStairs(BMG, CUT_FELLCRUST_STAIRS);

        // fluids
        BMG.createNonTemplateModelBlock(PETRICHORIC_PLASMA);

        // iridescence
        // these functions only create the models, blockstates need to still be done manually
        forEach(block -> createTrivialCubeForSuffix(SUFFIX_IRIDESCENCE, BMG, block),
                STARFLAKED_BISMUTH_BLOCK,
                CHISELED_STARFLAKED_BISMUTH_BLOCK,

                STARFLAKED_BISMUTH_BRICKS,
                CHISELED_STARFLAKED_BISMUTH_BRICKS,

                STARFLAKED_BISMUTH_TILES,
                STARFLAKED_BISMUTH_MOSAIC,

                BUDDING_CELESTIAL_OPAL
        );
        createCropForSuffix(2, SUFFIX_IRIDESCENCE, BMG, BISREEDS);
        createCropForSuffix(3, SUFFIX_IRIDESCENCE, BMG, BISREEDS);

        createSplitSlabForSuffix(SUFFIX_IRIDESCENCE, BMG, STARFLAKED_BISMUTH_SLAB, STARFLAKED_BISMUTH_BLOCK);

        createPillarForSuffix(SUFFIX_IRIDESCENCE, BMG, STARFLAKED_BISMUTH_PILLAR);

        createSlabForSuffix(SUFFIX_IRIDESCENCE, BMG, STARFLAKED_BISMUTH_BRICK_SLAB, STARFLAKED_BISMUTH_BRICKS);
        createStairsForSuffix(SUFFIX_IRIDESCENCE, BMG, STARFLAKED_BISMUTH_BRICK_STAIRS, STARFLAKED_BISMUTH_BRICKS);
        createWallForSuffix(SUFFIX_IRIDESCENCE, BMG, STARFLAKED_BISMUTH_BRICK_WALL, STARFLAKED_BISMUTH_BRICKS);

        createSlabForSuffix(SUFFIX_IRIDESCENCE, BMG, STARFLAKED_BISMUTH_TILE_SLAB, STARFLAKED_BISMUTH_TILES);
        createStairsForSuffix(SUFFIX_IRIDESCENCE, BMG, STARFLAKED_BISMUTH_TILE_STAIRS, STARFLAKED_BISMUTH_TILES);
        createWallForSuffix(SUFFIX_IRIDESCENCE, BMG, STARFLAKED_BISMUTH_TILE_WALL, STARFLAKED_BISMUTH_TILES);

        createSlabForSuffix(SUFFIX_IRIDESCENCE, BMG, STARFLAKED_BISMUTH_MOSAIC_SLAB, STARFLAKED_BISMUTH_MOSAIC);
        createStairsForSuffix(SUFFIX_IRIDESCENCE, BMG, STARFLAKED_BISMUTH_MOSAIC_STAIRS, STARFLAKED_BISMUTH_MOSAIC);
        createWallForSuffix(SUFFIX_IRIDESCENCE, BMG, STARFLAKED_BISMUTH_MOSAIC_WALL, STARFLAKED_BISMUTH_MOSAIC);

        createDoorForSuffix(SUFFIX_IRIDESCENCE, BMG, STARFLAKED_BISMUTH_DOOR);
        createOrientableTrapdoorForSuffix(SUFFIX_IRIDESCENCE, BMG, STARFLAKED_BISMUTH_TRAPDOOR);

        forEach(block -> createGemClusterForSuffix(SUFFIX_IRIDESCENCE, BMG, block),
                CELESTIAL_OPAL_SPIRE,
                CELESTIAL_OPAL_CLUSTER,
                LARGE_CELESTIAL_OPAL_BUD,
                MEDIUM_CELESTIAL_OPAL_BUD,
                SMALL_CELESTIAL_OPAL_BUD
        );

        createFamilyBlocksForSuffix(SUFFIX_IRIDESCENCE, BMG, OperationStarcleaveBlockFamilies.CELESTIAL_OPAL_BLOCK);
        createFamilyBlocksForSuffix(SUFFIX_IRIDESCENCE, BMG, OperationStarcleaveBlockFamilies.POLISHED_CELESTIAL_OPAL_BLOCK);
        createFamilyBlocksForSuffix(SUFFIX_IRIDESCENCE, BMG, OperationStarcleaveBlockFamilies.POLISHED_CELESTIAL_OPAL_BRICKS);
        createPillarForSuffix(SUFFIX_IRIDESCENCE, BMG, POLISHED_CELESTIAL_OPAL_PILLAR);
    }

    protected static void createFamilyBlocksForSuffix(String suffix, BlockModelGenerators BMG, BlockFamily family) {
        Block baseBlock = family.getBaseBlock();
        createTrivialCubeForSuffix(suffix, BMG, baseBlock);

        Block slab = family.get(BlockFamily.Variant.SLAB);
        if (slab != null) {
            createSlabForSuffix(SUFFIX_IRIDESCENCE, BMG, slab, baseBlock);
        }

        Block stairs = family.get(BlockFamily.Variant.STAIRS);
        if (stairs != null) {
            createStairsForSuffix(SUFFIX_IRIDESCENCE, BMG, stairs, baseBlock);
        }

        Block wall = family.get(BlockFamily.Variant.WALL);
        if (wall != null) {
            createWallForSuffix(SUFFIX_IRIDESCENCE, BMG, wall, baseBlock);
        }
    }

    private static void createSplitSlab(BlockModelGenerators BMG, Block slabBlock, Block fullBlock) {
        createSplitSlab(BMG, slabBlock, TextureMapping.getBlockTexture(fullBlock));
    }

    private static void createSplitSlab(BlockModelGenerators BMG, Block slabBlock, ResourceLocation endTextureLocation) {
        TextureMapping textureMapping = TextureMapping.column(TextureMapping.getBlockTexture(slabBlock, "_side"), endTextureLocation);
        createSplitSlab(BMG, slabBlock, textureMapping, ModelTemplates.CUBE_COLUMN);
    }

    private static void createSplitSlab(BlockModelGenerators BMG, Block slabBlock, TextureMapping textureMapping, ModelTemplate modelTemplate) {
        ResourceLocation bottomLocation = ModelTemplates.SLAB_BOTTOM.create(slabBlock, textureMapping, BMG.modelOutput);
        ResourceLocation topLocation = ModelTemplates.SLAB_TOP.create(slabBlock, textureMapping, BMG.modelOutput);
        ResourceLocation doubleLocation = modelTemplate.createWithOverride(slabBlock, "_double", textureMapping, BMG.modelOutput);

        BMG.blockStateOutput.accept(createSlab(slabBlock, bottomLocation, topLocation, doubleLocation));
    }

    private static void createFellcrustStairs(BlockModelGenerators BMG, Block stairsBlock) {
        TextureMapping bottomTextureMapping = new TextureMapping()
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(FELLCRUST, "_top"))
                .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(FELLCRUST, "_bottom"))
                .put(TextureSlot.FRONT, TextureMapping.getBlockTexture(FELLCRUST_SLAB, "_side"))
                .put(TextureSlot.BACK, TextureMapping.getBlockTexture(FELLCRUST))
                .put(OperationStarcleaveModelTemplates.LEFT, TextureMapping.getBlockTexture(FELLCRUST_STAIRS, "_left"))
                .put(OperationStarcleaveModelTemplates.RIGHT, TextureMapping.getBlockTexture(FELLCRUST_STAIRS, "_right"));

        TextureMapping topTextureMapping = new TextureMapping() // top and bottom are swapped
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(FELLCRUST))
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(FELLCRUST, "_bottom"))
                .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(FELLCRUST, "_top"));

        ResourceLocation bottomInnerLocation = OperationStarcleaveModelTemplates.SIDED_STAIRS_INNER.create(stairsBlock, bottomTextureMapping, BMG.modelOutput);
        ResourceLocation bottomStraightLocation = OperationStarcleaveModelTemplates.SIDED_STAIRS_STRAIGHT.create(stairsBlock, bottomTextureMapping, BMG.modelOutput);
        ResourceLocation bottomOuterLocation = OperationStarcleaveModelTemplates.SIDED_STAIRS_OUTER.create(stairsBlock, bottomTextureMapping, BMG.modelOutput);

        ResourceLocation topInnerLocation = ModelTemplates.STAIRS_INNER.createWithSuffix(stairsBlock, "_upper", topTextureMapping, BMG.modelOutput);
        ResourceLocation topStraightLocation = ModelTemplates.STAIRS_STRAIGHT.createWithSuffix(stairsBlock, "_upper", topTextureMapping, BMG.modelOutput);
        ResourceLocation topOuterLocation = ModelTemplates.STAIRS_OUTER.createWithSuffix(stairsBlock, "_upper", topTextureMapping, BMG.modelOutput);

        BMG.blockStateOutput.accept(createSplitStairs(stairsBlock, bottomInnerLocation, bottomStraightLocation, bottomOuterLocation, topInnerLocation, topStraightLocation, topOuterLocation));
        BMG.delegateItemModel(stairsBlock, bottomStraightLocation);
    }

    private static void createCutFellcrustStairs(BlockModelGenerators BMG, Block stairsBlock) {
        TextureMapping bottomTextureMapping = new TextureMapping()
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(CUT_FELLCRUST))
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(FELLCRUST, "_top"))
                .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(CUT_FELLCRUST, "_bottom"));

        TextureMapping topTextureMapping = new TextureMapping() // top and bottom are swapped
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(CUT_FELLCRUST))
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(CUT_FELLCRUST, "_bottom"))
                .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(FELLCRUST, "_top"));

        ResourceLocation bottomInnerLocation = ModelTemplates.STAIRS_INNER.create(stairsBlock, bottomTextureMapping, BMG.modelOutput);
        ResourceLocation bottomStraightLocation = ModelTemplates.STAIRS_STRAIGHT.create(stairsBlock, bottomTextureMapping, BMG.modelOutput);
        ResourceLocation bottomOuterLocation = ModelTemplates.STAIRS_OUTER.create(stairsBlock, bottomTextureMapping, BMG.modelOutput);

        ResourceLocation topInnerLocation = ModelTemplates.STAIRS_INNER.createWithSuffix(stairsBlock, "_upper", topTextureMapping, BMG.modelOutput);
        ResourceLocation topStraightLocation = ModelTemplates.STAIRS_STRAIGHT.createWithSuffix(stairsBlock, "_upper", topTextureMapping, BMG.modelOutput);
        ResourceLocation topOuterLocation = ModelTemplates.STAIRS_OUTER.createWithSuffix(stairsBlock, "_upper", topTextureMapping, BMG.modelOutput);

        BMG.blockStateOutput.accept(createSplitStairs(stairsBlock, bottomInnerLocation, bottomStraightLocation, bottomOuterLocation, topInnerLocation, topStraightLocation, topOuterLocation));
        BMG.delegateItemModel(stairsBlock, bottomStraightLocation);
    }

    private static BlockStateGenerator createSplitStairs(
            Block stairsBlock, ResourceLocation bottomInnerModelLocation, ResourceLocation bottomStraightModelLocation, ResourceLocation bottomOuterModelLocation, ResourceLocation topInnerModelLocation, ResourceLocation topStraightModelLocation, ResourceLocation topOuterModelLocation
    ) {
        return MultiVariantGenerator.multiVariant(stairsBlock)
                .with(
                        PropertyDispatch.properties(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.HALF, BlockStateProperties.STAIRS_SHAPE)
                                .select(Direction.EAST, Half.BOTTOM, StairsShape.STRAIGHT, Variant.variant().with(VariantProperties.MODEL, bottomStraightModelLocation))
                                .select(
                                        Direction.WEST,
                                        Half.BOTTOM,
                                        StairsShape.STRAIGHT,
                                        Variant.variant()
                                                .with(VariantProperties.MODEL, bottomStraightModelLocation)
                                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                                                .with(VariantProperties.UV_LOCK, true)
                                )
                                .select(
                                        Direction.SOUTH,
                                        Half.BOTTOM,
                                        StairsShape.STRAIGHT,
                                        Variant.variant()
                                                .with(VariantProperties.MODEL, bottomStraightModelLocation)
                                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                                .with(VariantProperties.UV_LOCK, true)
                                )
                                .select(
                                        Direction.NORTH,
                                        Half.BOTTOM,
                                        StairsShape.STRAIGHT,
                                        Variant.variant()
                                                .with(VariantProperties.MODEL, bottomStraightModelLocation)
                                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                                                .with(VariantProperties.UV_LOCK, true)
                                )
                                .select(Direction.EAST, Half.BOTTOM, StairsShape.OUTER_RIGHT, Variant.variant().with(VariantProperties.MODEL, bottomOuterModelLocation))
                                .select(
                                        Direction.WEST,
                                        Half.BOTTOM,
                                        StairsShape.OUTER_RIGHT,
                                        Variant.variant()
                                                .with(VariantProperties.MODEL, bottomOuterModelLocation)
                                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                                                .with(VariantProperties.UV_LOCK, true)
                                )
                                .select(
                                        Direction.SOUTH,
                                        Half.BOTTOM,
                                        StairsShape.OUTER_RIGHT,
                                        Variant.variant()
                                                .with(VariantProperties.MODEL, bottomOuterModelLocation)
                                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                                .with(VariantProperties.UV_LOCK, true)
                                )
                                .select(
                                        Direction.NORTH,
                                        Half.BOTTOM,
                                        StairsShape.OUTER_RIGHT,
                                        Variant.variant()
                                                .with(VariantProperties.MODEL, bottomOuterModelLocation)
                                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                                                .with(VariantProperties.UV_LOCK, true)
                                )
                                .select(
                                        Direction.EAST,
                                        Half.BOTTOM,
                                        StairsShape.OUTER_LEFT,
                                        Variant.variant()
                                                .with(VariantProperties.MODEL, bottomOuterModelLocation)
                                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                                                .with(VariantProperties.UV_LOCK, true)
                                )
                                .select(
                                        Direction.WEST,
                                        Half.BOTTOM,
                                        StairsShape.OUTER_LEFT,
                                        Variant.variant()
                                                .with(VariantProperties.MODEL, bottomOuterModelLocation)
                                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                                .with(VariantProperties.UV_LOCK, true)
                                )
                                .select(Direction.SOUTH, Half.BOTTOM, StairsShape.OUTER_LEFT, Variant.variant().with(VariantProperties.MODEL, bottomOuterModelLocation))
                                .select(
                                        Direction.NORTH,
                                        Half.BOTTOM,
                                        StairsShape.OUTER_LEFT,
                                        Variant.variant()
                                                .with(VariantProperties.MODEL, bottomOuterModelLocation)
                                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                                                .with(VariantProperties.UV_LOCK, true)
                                )
                                .select(Direction.EAST, Half.BOTTOM, StairsShape.INNER_RIGHT, Variant.variant().with(VariantProperties.MODEL, bottomInnerModelLocation))
                                .select(
                                        Direction.WEST,
                                        Half.BOTTOM,
                                        StairsShape.INNER_RIGHT,
                                        Variant.variant()
                                                .with(VariantProperties.MODEL, bottomInnerModelLocation)
                                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                                                .with(VariantProperties.UV_LOCK, true)
                                )
                                .select(
                                        Direction.SOUTH,
                                        Half.BOTTOM,
                                        StairsShape.INNER_RIGHT,
                                        Variant.variant()
                                                .with(VariantProperties.MODEL, bottomInnerModelLocation)
                                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                                .with(VariantProperties.UV_LOCK, true)
                                )
                                .select(
                                        Direction.NORTH,
                                        Half.BOTTOM,
                                        StairsShape.INNER_RIGHT,
                                        Variant.variant()
                                                .with(VariantProperties.MODEL, bottomInnerModelLocation)
                                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                                                .with(VariantProperties.UV_LOCK, true)
                                )
                                .select(
                                        Direction.EAST,
                                        Half.BOTTOM,
                                        StairsShape.INNER_LEFT,
                                        Variant.variant()
                                                .with(VariantProperties.MODEL, bottomInnerModelLocation)
                                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                                                .with(VariantProperties.UV_LOCK, true)
                                )
                                .select(
                                        Direction.WEST,
                                        Half.BOTTOM,
                                        StairsShape.INNER_LEFT,
                                        Variant.variant()
                                                .with(VariantProperties.MODEL, bottomInnerModelLocation)
                                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                                .with(VariantProperties.UV_LOCK, true)
                                )
                                .select(Direction.SOUTH, Half.BOTTOM, StairsShape.INNER_LEFT, Variant.variant().with(VariantProperties.MODEL, bottomInnerModelLocation))
                                .select(
                                        Direction.NORTH,
                                        Half.BOTTOM,
                                        StairsShape.INNER_LEFT,
                                        Variant.variant()
                                                .with(VariantProperties.MODEL, bottomInnerModelLocation)
                                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                                                .with(VariantProperties.UV_LOCK, true)
                                )
                                .select(
                                        Direction.EAST,
                                        Half.TOP,
                                        StairsShape.STRAIGHT,
                                        Variant.variant()
                                                .with(VariantProperties.MODEL, topStraightModelLocation)
                                                .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                                .with(VariantProperties.UV_LOCK, true)
                                )
                                .select(
                                        Direction.WEST,
                                        Half.TOP,
                                        StairsShape.STRAIGHT,
                                        Variant.variant()
                                                .with(VariantProperties.MODEL, topStraightModelLocation)
                                                .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                                                .with(VariantProperties.UV_LOCK, true)
                                )
                                .select(
                                        Direction.SOUTH,
                                        Half.TOP,
                                        StairsShape.STRAIGHT,
                                        Variant.variant()
                                                .with(VariantProperties.MODEL, topStraightModelLocation)
                                                .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                                .with(VariantProperties.UV_LOCK, true)
                                )
                                .select(
                                        Direction.NORTH,
                                        Half.TOP,
                                        StairsShape.STRAIGHT,
                                        Variant.variant()
                                                .with(VariantProperties.MODEL, topStraightModelLocation)
                                                .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                                                .with(VariantProperties.UV_LOCK, true)
                                )
                                .select(
                                        Direction.EAST,
                                        Half.TOP,
                                        StairsShape.OUTER_RIGHT,
                                        Variant.variant()
                                                .with(VariantProperties.MODEL, topOuterModelLocation)
                                                .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                                .with(VariantProperties.UV_LOCK, true)
                                )
                                .select(
                                        Direction.WEST,
                                        Half.TOP,
                                        StairsShape.OUTER_RIGHT,
                                        Variant.variant()
                                                .with(VariantProperties.MODEL, topOuterModelLocation)
                                                .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                                                .with(VariantProperties.UV_LOCK, true)
                                )
                                .select(
                                        Direction.SOUTH,
                                        Half.TOP,
                                        StairsShape.OUTER_RIGHT,
                                        Variant.variant()
                                                .with(VariantProperties.MODEL, topOuterModelLocation)
                                                .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                                                .with(VariantProperties.UV_LOCK, true)
                                )
                                .select(
                                        Direction.NORTH,
                                        Half.TOP,
                                        StairsShape.OUTER_RIGHT,
                                        Variant.variant()
                                                .with(VariantProperties.MODEL, topOuterModelLocation)
                                                .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                                .with(VariantProperties.UV_LOCK, true)
                                )
                                .select(
                                        Direction.EAST,
                                        Half.TOP,
                                        StairsShape.OUTER_LEFT,
                                        Variant.variant()
                                                .with(VariantProperties.MODEL, topOuterModelLocation)
                                                .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                                .with(VariantProperties.UV_LOCK, true)
                                )
                                .select(
                                        Direction.WEST,
                                        Half.TOP,
                                        StairsShape.OUTER_LEFT,
                                        Variant.variant()
                                                .with(VariantProperties.MODEL, topOuterModelLocation)
                                                .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                                                .with(VariantProperties.UV_LOCK, true)
                                )
                                .select(
                                        Direction.SOUTH,
                                        Half.TOP,
                                        StairsShape.OUTER_LEFT,
                                        Variant.variant()
                                                .with(VariantProperties.MODEL, topOuterModelLocation)
                                                .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                                .with(VariantProperties.UV_LOCK, true)
                                )
                                .select(
                                        Direction.NORTH,
                                        Half.TOP,
                                        StairsShape.OUTER_LEFT,
                                        Variant.variant()
                                                .with(VariantProperties.MODEL, topOuterModelLocation)
                                                .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                                                .with(VariantProperties.UV_LOCK, true)
                                )
                                .select(
                                        Direction.EAST,
                                        Half.TOP,
                                        StairsShape.INNER_RIGHT,
                                        Variant.variant()
                                                .with(VariantProperties.MODEL, topInnerModelLocation)
                                                .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                                .with(VariantProperties.UV_LOCK, true)
                                )
                                .select(
                                        Direction.WEST,
                                        Half.TOP,
                                        StairsShape.INNER_RIGHT,
                                        Variant.variant()
                                                .with(VariantProperties.MODEL, topInnerModelLocation)
                                                .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                                                .with(VariantProperties.UV_LOCK, true)
                                )
                                .select(
                                        Direction.SOUTH,
                                        Half.TOP,
                                        StairsShape.INNER_RIGHT,
                                        Variant.variant()
                                                .with(VariantProperties.MODEL, topInnerModelLocation)
                                                .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                                                .with(VariantProperties.UV_LOCK, true)
                                )
                                .select(
                                        Direction.NORTH,
                                        Half.TOP,
                                        StairsShape.INNER_RIGHT,
                                        Variant.variant()
                                                .with(VariantProperties.MODEL, topInnerModelLocation)
                                                .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                                .with(VariantProperties.UV_LOCK, true)
                                )
                                .select(
                                        Direction.EAST,
                                        Half.TOP,
                                        StairsShape.INNER_LEFT,
                                        Variant.variant()
                                                .with(VariantProperties.MODEL, topInnerModelLocation)
                                                .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                                .with(VariantProperties.UV_LOCK, true)
                                )
                                .select(
                                        Direction.WEST,
                                        Half.TOP,
                                        StairsShape.INNER_LEFT,
                                        Variant.variant()
                                                .with(VariantProperties.MODEL, topInnerModelLocation)
                                                .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                                                .with(VariantProperties.UV_LOCK, true)
                                )
                                .select(
                                        Direction.SOUTH,
                                        Half.TOP,
                                        StairsShape.INNER_LEFT,
                                        Variant.variant()
                                                .with(VariantProperties.MODEL, topInnerModelLocation)
                                                .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                                                .with(VariantProperties.UV_LOCK, true)
                                )
                                .select(
                                        Direction.NORTH,
                                        Half.TOP,
                                        StairsShape.INNER_LEFT,
                                        Variant.variant()
                                                .with(VariantProperties.MODEL, topInnerModelLocation)
                                                .with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)
                                                .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                                                .with(VariantProperties.UV_LOCK, true)
                                )
                );
    }

    public void createFellcrustWall(BlockModelGenerators BMG, Block wallBlock) {
        TextureMapping textureMapping = new TextureMapping()
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(FELLCRUST))
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(FELLCRUST, "_top"))
                .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(FELLCRUST, "_bottom"))
                .put(TextureSlot.WALL, TextureMapping.getBlockTexture(FELLCRUST_WALL));

        ResourceLocation postLocation = OperationStarcleaveModelTemplates.SIDED_WALL_POST.create(wallBlock, textureMapping, BMG.modelOutput);
        ResourceLocation lowLocation = OperationStarcleaveModelTemplates.SIDED_WALL_LOW_SIDE.create(wallBlock, textureMapping, BMG.modelOutput);
        ResourceLocation tallLocation = OperationStarcleaveModelTemplates.SIDED_WALL_TALL_SIDE.create(wallBlock, textureMapping, BMG.modelOutput);
        BMG.blockStateOutput.accept(BlockModelGenerators.createWall(wallBlock, postLocation, lowLocation, tallLocation));

        ResourceLocation inventoryLocation = OperationStarcleaveModelTemplates.SIDED_WALL_INVENTORY.create(wallBlock, textureMapping, BMG.modelOutput);
        BMG.delegateItemModel(wallBlock, inventoryLocation);
    }

    private void forEach(Consumer<Block> consumer, Block... list) {
        for (Block block : list) {
            consumer.accept(block);
        }
    }

    private void registerUnevenCross(BlockModelGenerators BMG, Block block) {
        BMG.createSimpleFlatItemModel(block);

        TextureMapping textureMap = TextureMapping.cross(block);
        ResourceLocation modelId = OperationStarcleaveModelTemplates.UNEVEN_CROSS.create(block, textureMap, BMG.modelOutput);
        ResourceLocation modelId2 = OperationStarcleaveModelTemplates.UNEVEN_CROSS_MIRRORED.create(block, textureMap, BMG.modelOutput);
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
                                    OperationStarcleaveModelTemplates.getSevenLevelCauldron(i)
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
        createModelsForSuffix(suffix, BMG, block, mapping,
                ModelTemplates.STAIRS_INNER,
                ModelTemplates.STAIRS_OUTER,
                ModelTemplates.STAIRS_STRAIGHT
        );
    }

    private static void createWallForSuffix(String suffix, BlockModelGenerators BMG, Block block, Block fullBlock) {
        TextureMapping mapping = TextureMapping.cube(fullBlock);
        createModelsForSuffix(suffix, BMG, block, mapping,
                ModelTemplates.WALL_POST,
                ModelTemplates.WALL_LOW_SIDE,
                ModelTemplates.WALL_TALL_SIDE,
                ModelTemplates.WALL_INVENTORY
        );
    }

    private static void createPillarForSuffix(String suffix, BlockModelGenerators BMG, Block block) {
        createModelForSuffix(suffix, BMG, block, TexturedModel.COLUMN_ALT);
        createModelForSuffix(suffix, BMG, block, TexturedModel.COLUMN_HORIZONTAL_ALT);
    }

    private static void createDoorForSuffix(String suffix, BlockModelGenerators BMG, Block doorBlock) {
        TextureMapping textureMapping = TextureMapping.door(doorBlock);
        createModelsForSuffix(suffix, BMG, doorBlock, textureMapping,
                ModelTemplates.DOOR_BOTTOM_LEFT,
                ModelTemplates.DOOR_BOTTOM_LEFT_OPEN,
                ModelTemplates.DOOR_BOTTOM_RIGHT,
                ModelTemplates.DOOR_BOTTOM_RIGHT_OPEN,
                ModelTemplates.DOOR_TOP_LEFT,
                ModelTemplates.DOOR_TOP_LEFT_OPEN,
                ModelTemplates.DOOR_TOP_RIGHT,
                ModelTemplates.DOOR_TOP_RIGHT_OPEN
        );
    }

    private static void createOrientableTrapdoorForSuffix(String suffix, BlockModelGenerators BMG, Block trapdoorBlock) {
        TextureMapping textureMapping = TextureMapping.defaultTexture(trapdoorBlock);
        createModelsForSuffix(suffix, BMG, trapdoorBlock, textureMapping,
                ModelTemplates.ORIENTABLE_TRAPDOOR_TOP,
                ModelTemplates.ORIENTABLE_TRAPDOOR_BOTTOM,
                ModelTemplates.ORIENTABLE_TRAPDOOR_OPEN
        );
    }

    private static void createGemClusterForSuffix(String suffix, BlockModelGenerators BMG, Block clusterBlock) {
        createModelForSuffix(suffix, BMG, clusterBlock, TextureMapping.cross(clusterBlock), ModelTemplates.CROSS);
    }

    private static void createModelForSuffix(String suffix, BlockModelGenerators BMG, Block block, TexturedModel.Provider provider) {
        TexturedModel model = provider.get(block);
        createModelForSuffix(suffix, BMG, block, model.getMapping(), model.getTemplate());
    }

    private static void createModelsForSuffix(String suffix, BlockModelGenerators BMG, Block block, TextureMapping mapping, ModelTemplate... templates) {
        for (ModelTemplate template : templates) {
            createModelForSuffix(suffix, BMG, block, mapping, template);
        }
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
                OperationStarcleaveItems.BISBLAST_CANISTER,

                OperationStarcleaveItems.CELESTIAL_OPAL_SHARD,

                OperationStarcleaveItems.MUCKY_SINGUTS,
                OperationStarcleaveItems.CLEANSED_SINGUTS,

                OperationStarcleaveItems.OURANIC_CHIP,

                OperationStarcleaveItems.STARBLEACH_BUCKET,
                OperationStarcleaveItems.PETRICHORIC_PLASMA_BUCKET,
                OperationStarcleaveItems.LIMESLAGGED_BUCKET,

                OperationStarcleaveItems.NUCLEAR_STORMCLOUD_BOTTLE,

                OperationStarcleaveItems.FIRMAMENT_REJUVENATOR
        );

        forEach(item -> IMG.generateFlatItem(item, ModelTemplates.FLAT_HANDHELD_ITEM),
                OperationStarcleaveItems.BISMUTH_BLASTER,

                OperationStarcleaveItems.FIRMAMENT_MANIPULATOR
        );

        // iridescence
        forEach(item -> generateFlatForSuffix(SUFFIX_IRIDESCENCE, IMG, item, ModelTemplates.FLAT_ITEM),
                OperationStarcleaveItems.STARFLAKED_BISMUTH_DOOR,

                OperationStarcleaveItems.BISMUTH_FLAKE,
                OperationStarcleaveItems.STARFLAKED_BISMUTH,
                OperationStarcleaveItems.BISMUTH_PEGASUS_ARMOR,
                OperationStarcleaveItems.BISBLAST_CANISTER,

                OperationStarcleaveItems.CELESTIAL_OPAL_SHARD
        );

        forEach(item -> generateFlatForSuffix(SUFFIX_IRIDESCENCE, IMG, item, ModelTemplates.FLAT_HANDHELD_ITEM),
                OperationStarcleaveItems.BISMUTH_BLASTER
        );

        forEach(block -> addDelegateModelForSuffix(SUFFIX_IRIDESCENCE, IMG, block),
                STARFLAKED_BISMUTH_BLOCK,
                STARFLAKED_BISMUTH_SLAB,
                CHISELED_STARFLAKED_BISMUTH_BLOCK,
                STARFLAKED_BISMUTH_PILLAR,

                STARFLAKED_BISMUTH_BRICKS,
                STARFLAKED_BISMUTH_BRICK_STAIRS,
                STARFLAKED_BISMUTH_BRICK_SLAB,
                CHISELED_STARFLAKED_BISMUTH_BRICKS,

                STARFLAKED_BISMUTH_TILES,
                STARFLAKED_BISMUTH_TILE_STAIRS,
                STARFLAKED_BISMUTH_TILE_SLAB,

                STARFLAKED_BISMUTH_MOSAIC,
                STARFLAKED_BISMUTH_MOSAIC_STAIRS,
                STARFLAKED_BISMUTH_MOSAIC_SLAB,

                CELESTIAL_OPAL_BLOCK,
                CELESTIAL_OPAL_STAIRS,
                CELESTIAL_OPAL_SLAB,

                BUDDING_CELESTIAL_OPAL,

                POLISHED_CELESTIAL_OPAL_BLOCK,
                POLISHED_CELESTIAL_OPAL_STAIRS,
                POLISHED_CELESTIAL_OPAL_SLAB,

                POLISHED_CELESTIAL_OPAL_BRICKS,
                POLISHED_CELESTIAL_OPAL_BRICK_STAIRS,
                POLISHED_CELESTIAL_OPAL_BRICK_SLAB,

                POLISHED_CELESTIAL_OPAL_PILLAR
        );
        forEach(wallBlock -> addDelegateWallModelForSuffix(SUFFIX_IRIDESCENCE, IMG, wallBlock),
                STARFLAKED_BISMUTH_BRICK_WALL,
                STARFLAKED_BISMUTH_TILE_WALL,
                STARFLAKED_BISMUTH_MOSAIC_WALL,
                CELESTIAL_OPAL_WALL,
                POLISHED_CELESTIAL_OPAL_BRICK_WALL
        );
        addDelegateTrapDoorModelForSuffix(SUFFIX_IRIDESCENCE, IMG, STARFLAKED_BISMUTH_TRAPDOOR);
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

    private static void addDelegateModelForSuffixes(String suffix1, String suffix2, ItemModelGenerators IMG, Block block) {
        ResourceLocation resourceLocation = ModelLocationUtils.getModelLocation(block.asItem()).withSuffix(suffix1);
        IMG.output.accept(resourceLocation, new DelegatedModel(ModelLocationUtils.getModelLocation(block).withSuffix(suffix2)));
    }

    private static void addDelegateModelForSuffix(String suffix, ItemModelGenerators IMG, Block block) {
        addDelegateModelForSuffixes(suffix, suffix, IMG, block);
    }

    private static void addDelegateWallModelForSuffix(String suffix, ItemModelGenerators IMG, Block block) {
        addDelegateModelForSuffixes(suffix, "_inventory" + suffix, IMG, block);
    }

    private static void addDelegateTrapDoorModelForSuffix(String suffix, ItemModelGenerators IMG, Block block) {
        addDelegateModelForSuffixes(suffix, "_bottom" + suffix, IMG, block);
    }
}

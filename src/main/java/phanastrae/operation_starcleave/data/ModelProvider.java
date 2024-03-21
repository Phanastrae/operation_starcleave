package phanastrae.operation_starcleave.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.data.client.*;
import net.minecraft.item.Item;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.block.StarbleachedPearlBlock;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;

import static net.minecraft.data.client.BlockStateModelGenerator.*;
import static phanastrae.operation_starcleave.block.OperationStarcleaveBlocks.*;

public class ModelProvider extends FabricModelProvider {
    public ModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerCubeAllModelTexturePool(STARBLEACHED_TILES)
                .slab(STARBLEACHED_TILE_SLAB)
                .stairs(STARBLEACHED_TILE_STAIRS)
                .wall(STARBLEACHED_TILE_WALL);

        blockStateModelGenerator.registerCubeAllModelTexturePool(STELLAR_TILES)
                .slab(STELLAR_TILE_SLAB);

        blockStateModelGenerator.registerSimpleCubeAll(CHISELED_STARBLEACHED_TILES);
        blockStateModelGenerator.registerSimpleCubeAll(IMBUED_STARBLEACHED_TILES);

        blockStateModelGenerator.registerLog(STARBLEACHED_LOG).log(STARBLEACHED_LOG).wood(STARBLEACHED_WOOD);

        blockStateModelGenerator.registerSingleton(STARBLEACHED_LEAVES, TexturedModel.LEAVES);

        blockStateModelGenerator.registerRotatable(STELLAR_SEDIMENT);
        blockStateModelGenerator.registerRotatable(STARDUST_BLOCK);

        blockStateModelGenerator.registerTintableCross(SHORT_HOLY_MOSS, BlockStateModelGenerator.TintType.NOT_TINTED);

        {
            TextureMap textureMap = TextureMap.sideEnd(NETHERITE_PUMPKIN);
            blockStateModelGenerator.registerNorthDefaultHorizontalRotatable(NETHERITE_PUMPKIN, textureMap);
        }

        {
            Identifier off = TexturedModel.CUBE_ALL.upload(STARBLEACHED_PEARL_BLOCK, blockStateModelGenerator.modelCollector);
            Identifier on = blockStateModelGenerator.createSubModel(STARBLEACHED_PEARL_BLOCK, "_on", Models.CUBE_ALL, TextureMap::all);
            blockStateModelGenerator.blockStateCollector
                    .accept(VariantsBlockStateSupplier.create(STARBLEACHED_PEARL_BLOCK).coordinate(createBooleanModelMap(StarbleachedPearlBlock.TRIGGERED, on, off)));
        }

        {
            Block wool = BLESSED_CLOTH_BLOCK;
            Block carpet = BLESSED_CLOTH_CARPET;
            Block curtain = BLESSED_CLOTH_CURTAIN;

            blockStateModelGenerator.registerSimpleCubeAll(wool);
            TextureMap textureMap = new TextureMap().put(TextureKey.PANE, TextureMap.getId(curtain)).put(TextureKey.EDGE, TextureMap.getId(wool));
            Identifier identifier = Models.TEMPLATE_GLASS_PANE_POST.upload(curtain, textureMap, blockStateModelGenerator.modelCollector);
            Identifier identifier2 = Models.TEMPLATE_GLASS_PANE_SIDE.upload(curtain, textureMap, blockStateModelGenerator.modelCollector);
            Identifier identifier3 = Models.TEMPLATE_GLASS_PANE_SIDE_ALT.upload(curtain, textureMap, blockStateModelGenerator.modelCollector);
            Identifier identifier4 = Models.TEMPLATE_GLASS_PANE_NOSIDE.upload(curtain, textureMap, blockStateModelGenerator.modelCollector);
            Identifier identifier5 = Models.TEMPLATE_GLASS_PANE_NOSIDE_ALT.upload(curtain, textureMap, blockStateModelGenerator.modelCollector);
            Item item = curtain.asItem();
            Models.GENERATED.upload(ModelIds.getItemModelId(item), TextureMap.layer0(curtain), blockStateModelGenerator.modelCollector);
            blockStateModelGenerator.blockStateCollector
                    .accept(
                            MultipartBlockStateSupplier.create(curtain)
                                    .with(BlockStateVariant.create().put(VariantSettings.MODEL, identifier))
                                    .with(When.create().set(Properties.NORTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, identifier2))
                                    .with(
                                            When.create().set(Properties.EAST, true),
                                            BlockStateVariant.create().put(VariantSettings.MODEL, identifier2).put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                    )
                                    .with(When.create().set(Properties.SOUTH, true), BlockStateVariant.create().put(VariantSettings.MODEL, identifier3))
                                    .with(
                                            When.create().set(Properties.WEST, true),
                                            BlockStateVariant.create().put(VariantSettings.MODEL, identifier3).put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                    )
                                    .with(When.create().set(Properties.NORTH, false), BlockStateVariant.create().put(VariantSettings.MODEL, identifier4))
                                    .with(When.create().set(Properties.EAST, false), BlockStateVariant.create().put(VariantSettings.MODEL, identifier5))
                                    .with(
                                            When.create().set(Properties.SOUTH, false),
                                            BlockStateVariant.create().put(VariantSettings.MODEL, identifier5).put(VariantSettings.Y, VariantSettings.Rotation.R90)
                                    )
                                    .with(
                                            When.create().set(Properties.WEST, false),
                                            BlockStateVariant.create().put(VariantSettings.MODEL, identifier4).put(VariantSettings.Y, VariantSettings.Rotation.R270)
                                    )
                    );
            Identifier identifier6 = TexturedModel.CARPET.get(wool).upload(carpet, blockStateModelGenerator.modelCollector);
            blockStateModelGenerator.blockStateCollector.accept(createSingletonBlockState(carpet, identifier6));
        }
        blockStateModelGenerator.blockStateCollector
                .accept(
                        VariantsBlockStateSupplier.create(STARBLEACH_CAULDRON)
                                .coordinate(
                                        BlockStateVariantMap.create(LeveledCauldronBlock.LEVEL)
                                                .register(
                                                        1,
                                                        BlockStateVariant.create()
                                                                .put(
                                                                        VariantSettings.MODEL,
                                                                        Models.TEMPLATE_CAULDRON_LEVEL1
                                                                                .upload(STARBLEACH_CAULDRON, "_level1", TextureMap.cauldron(OperationStarcleave.id("block/starbleach_still")), blockStateModelGenerator.modelCollector)
                                                                )
                                                )
                                                .register(
                                                        2,
                                                        BlockStateVariant.create()
                                                                .put(
                                                                        VariantSettings.MODEL,
                                                                        Models.TEMPLATE_CAULDRON_LEVEL2
                                                                                .upload(STARBLEACH_CAULDRON, "_level2", TextureMap.cauldron(OperationStarcleave.id("block/starbleach_still")), blockStateModelGenerator.modelCollector)
                                                                )
                                                )
                                                .register(
                                                        3,
                                                        BlockStateVariant.create()
                                                                .put(
                                                                        VariantSettings.MODEL,
                                                                        Models.TEMPLATE_CAULDRON_FULL
                                                                                .upload(STARBLEACH_CAULDRON, "_full", TextureMap.cauldron(OperationStarcleave.id("block/starbleach_still")), blockStateModelGenerator.modelCollector)
                                                                )
                                                )
                                )
                );

        {
            Identifier identifier = TextureMap.getId(STELLAR_SEDIMENT);
            Identifier identifier2 = TexturedModel.CUBE_BOTTOM_TOP
                    .get(HOLY_MOSS)
                    .textures(textures -> textures.put(TextureKey.BOTTOM, identifier))
                    .upload(HOLY_MOSS, blockStateModelGenerator.modelCollector);
            blockStateModelGenerator.blockStateCollector
                    .accept(VariantsBlockStateSupplier.create(HOLY_MOSS, createModelVariantWithRandomHorizontalRotations(identifier2)));
        }
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        registerGenerated(itemModelGenerator, OperationStarcleaveItems.STARCLEAVER_GOLEM_BUCKET);
        registerGenerated(itemModelGenerator, OperationStarcleaveItems.BLESSED_BED);
        registerGenerated(itemModelGenerator, OperationStarcleaveItems.BLESSED_CLOTH);
        registerGenerated(itemModelGenerator, OperationStarcleaveItems.FIRMAMENT_REJUVENATOR);
        registerGenerated(itemModelGenerator, OperationStarcleaveItems.HOLY_STRANDS);
        registerGenerated(itemModelGenerator, OperationStarcleaveItems.SPLASH_STARBLEACH_BOTTLE);
        registerGenerated(itemModelGenerator, OperationStarcleaveItems.STARBLEACH_BOTTLE);
        registerGenerated(itemModelGenerator, OperationStarcleaveItems.STARBLEACHED_PEARL);
        registerGenerated(itemModelGenerator, OperationStarcleaveItems.STARDUST_CLUSTER);
        registerGenerated(itemModelGenerator, OperationStarcleaveItems.STARFRUIT);

        itemModelGenerator.register(OperationStarcleaveItems.FIRMAMENT_MANIPULATOR, Models.HANDHELD);
    }

    public static void registerGenerated(ItemModelGenerator itemModelGenerator, Item item) {
        itemModelGenerator.register(item, Models.GENERATED);
    }
}

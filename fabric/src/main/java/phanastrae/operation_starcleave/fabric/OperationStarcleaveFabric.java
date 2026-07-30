package phanastrae.operation_starcleave.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.fabricmc.fabric.api.registry.TillableBlockRegistry;
import net.fabricmc.fabric.api.transfer.v1.fluid.CauldronFluidContent;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.EmptyItemFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.FullItemFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BlockEntityType;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;
import phanastrae.operation_starcleave.block.OperationStarcleaveToolActions;
import phanastrae.operation_starcleave.block.StarbleachCauldronBlock;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;
import phanastrae.operation_starcleave.fabric.fluid.OperationStarcleaveFluidVariantAttributes;
import phanastrae.operation_starcleave.fluid.OperationStarcleaveFluids;
import phanastrae.operation_starcleave.item.OperationStarcleaveCreativeModeTabs;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;
import phanastrae.operation_starcleave.network.packet.OperationStarcleavePayloads;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class OperationStarcleaveFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        // init registry entries
        OperationStarcleave.initRegistryEntries(new OperationStarcleave.RegistryListenerAdder() {
            @Override
            public <T> void addRegistryListener(Registry<T> registry, Consumer<BiConsumer<ResourceLocation, T>> source) {
                source.accept((rl, t) -> Registry.register(registry, rl, t));
            }

            @Override
            public <T> void addHolderRegistryListener(Registry<T> registry, Consumer<OperationStarcleave.HolderRegisterHelper<T>> source) {
                source.accept((name, t) -> Registry.registerForHolder(registry, OperationStarcleave.id(name), t));
            }
        });

        // register fluid attributes
        OperationStarcleaveFluidVariantAttributes.init();

        // common init
        OperationStarcleave.init();

        // creative tabs
        setupCreativeTabs();

        // register serverside payloads
        registerServerPayloads();

        // entity attributes
        OperationStarcleaveEntityTypes.registerEntityAttributes((FabricDefaultAttributeRegistry::register));

        // setup stripping
        OperationStarcleaveToolActions.STRIPPABLES.forEach(StrippableBlockRegistry::register);

        // setup tilling
        OperationStarcleaveToolActions.TILLABLES.forEach((block, action) -> {
            Predicate<UseOnContext> predicate = action.getPredicate();
            if (action instanceof OperationStarcleaveToolActions.BasicTillingAction basicTillingAction) {
                TillableBlockRegistry.register(block, predicate, basicTillingAction.getState());
            }
        });

        // setup fluid storages
        setupFluidStorages();

        // setup cauldron fluids
        setupCauldronFluids();

        // add valid blocks for block entity types
        addBlockEntityTypeBlocks();


        // world tick start
        ServerTickEvents.START_WORLD_TICK.register((OperationStarcleave::startLevelTick));
    }

    public void setupCreativeTabs() {
        OperationStarcleaveCreativeModeTabs.setupEntries(new OperationStarcleaveCreativeModeTabs.Helper() {
            @Override
            public void add(ResourceKey<CreativeModeTab> groupKey, ItemLike item) {
                ItemGroupEvents.modifyEntriesEvent(groupKey).register(entries -> entries.accept(item));
            }

            @Override
            public void add(ResourceKey<CreativeModeTab> groupKey, ItemLike... items) {
                ItemGroupEvents.modifyEntriesEvent(groupKey).register(entries -> {
                    for (ItemLike item : items) {
                        entries.accept(item);
                    }
                });
            }

            @Override
            public void add(ResourceKey<CreativeModeTab> groupKey, ItemStack item) {
                ItemGroupEvents.modifyEntriesEvent(groupKey).register(entries -> entries.accept(item));
            }

            @Override
            public void add(ResourceKey<CreativeModeTab> groupKey, Collection<ItemStack> items) {
                ItemGroupEvents.modifyEntriesEvent(groupKey).register(entries -> {
                    for (ItemStack item : items) {
                        entries.accept(item);
                    }
                });
            }

            @Override
            public void addAfter(ItemLike after, ResourceKey<CreativeModeTab> groupKey, ItemLike item) {
                ItemGroupEvents.modifyEntriesEvent(groupKey).register(entries -> entries.addAfter(after, item));
            }

            @Override
            public void addAfter(ItemStack after, ResourceKey<CreativeModeTab> groupKey, ItemStack item) {
                ItemGroupEvents.modifyEntriesEvent(groupKey).register(entries -> entries.addAfter(after, item));
            }

            @Override
            public void addAfter(ItemLike after, ResourceKey<CreativeModeTab> groupKey, ItemLike... items) {
                ItemGroupEvents.modifyEntriesEvent(groupKey).register(entries -> entries.addAfter(after, items));
            }

            @Override
            public void addBefore(ItemLike before, ResourceKey<CreativeModeTab> groupKey, ItemLike item) {
                ItemGroupEvents.modifyEntriesEvent(groupKey).register(entries -> entries.addBefore(before, item));
            }

            @Override
            public void addBefore(ItemStack before, ResourceKey<CreativeModeTab> groupKey, ItemStack item) {
                ItemGroupEvents.modifyEntriesEvent(groupKey).register(entries -> entries.addBefore(before, item));
            }

            @Override
            public void addBefore(ItemLike before, ResourceKey<CreativeModeTab> groupKey, ItemLike... items) {
                ItemGroupEvents.modifyEntriesEvent(groupKey).register(entries -> entries.addBefore(before, items));
            }

            @Override
            public void forTabRun(ResourceKey<CreativeModeTab> groupKey, BiConsumer<CreativeModeTab.ItemDisplayParameters, CreativeModeTab.Output> biConsumer) {
                ItemGroupEvents.modifyEntriesEvent(groupKey).register(entries -> {
                    CreativeModeTab.ItemDisplayParameters displayContext = entries.getContext();
                    biConsumer.accept(displayContext, entries);
                });
            }

            @Override
            public boolean operatorTabEnabled() {
                // fabric seems to hide the operator tab automatically, so we can just return true here
                return true;
            }
        });
    }

    public void registerServerPayloads() {
        OperationStarcleavePayloads.init(new OperationStarcleavePayloads.Helper() {
            @Override
            public <T extends CustomPacketPayload> void registerS2C(CustomPacketPayload.Type<T> id, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, BiConsumer<T, Player> clientCallback) {
                PayloadTypeRegistry.playS2C().register(id, codec);
            }

            @Override
            public <T extends CustomPacketPayload> void registerC2S(CustomPacketPayload.Type<T> id, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, BiConsumer<T, Player> serverCallback) {
                PayloadTypeRegistry.playC2S().register(id, codec);
                ServerPlayNetworking.registerGlobalReceiver(id, (payload, context) -> serverCallback.accept(payload, context.player()));
            }
        });
    }

    public void setupFluidStorages() {
        // specifically do NOT use the combinedItemApiProvider here, to make sure that the default bucket behaviour does NOT get run.
        FluidStorage.ITEM.registerForItems(
                (itemStack, context) -> new FullItemFluidStorage(
                        context,
                        OperationStarcleaveItems.LIMESLAGGED_BUCKET,
                        FluidVariant.of(OperationStarcleaveFluids.PETRICHORIC_PLASMA),
                        FluidConstants.BUCKET
                ),
                OperationStarcleaveItems.PETRICHORIC_PLASMA_BUCKET
        );

        // Register empty bottle storage, only water potion is supported!
        FluidStorage.combinedItemApiProvider(Items.GLASS_BOTTLE).register(
                context -> new EmptyItemFluidStorage(context, emptyBottle -> ItemVariant.of(OperationStarcleaveItems.STARBLEACH_BOTTLE),
                        OperationStarcleaveFluids.STARBLEACH,
                        FluidConstants.BUCKET / 4
                )
        );
        // Register water potion storage
        FluidStorage.combinedItemApiProvider(OperationStarcleaveItems.STARBLEACH_BOTTLE).register(
                context -> new FullItemFluidStorage(
                        context,
                        Items.GLASS_BOTTLE,
                        FluidVariant.of(OperationStarcleaveFluids.STARBLEACH),
                        FluidConstants.BUCKET / 4
                )
        );
    }

    public void setupCauldronFluids() {
        CauldronFluidContent.registerCauldron(OperationStarcleaveBlocks.STARBLEACH_CAULDRON, OperationStarcleaveFluids.STARBLEACH, FluidConstants.BUCKET / 4, StarbleachCauldronBlock.LEVEL_7);
    }

    public void addBlockEntityTypeBlocks() {
        BlockEntityType.SIGN.addSupportedBlock(OperationStarcleaveBlocks.STARTOUCHED_SIGN);
        BlockEntityType.SIGN.addSupportedBlock(OperationStarcleaveBlocks.STARTOUCHED_WALL_SIGN);
        BlockEntityType.HANGING_SIGN.addSupportedBlock(OperationStarcleaveBlocks.STARTOUCHED_HANGING_SIGN);
        BlockEntityType.HANGING_SIGN.addSupportedBlock(OperationStarcleaveBlocks.STARTOUCHED_WALL_HANGING_SIGN);
    }
}

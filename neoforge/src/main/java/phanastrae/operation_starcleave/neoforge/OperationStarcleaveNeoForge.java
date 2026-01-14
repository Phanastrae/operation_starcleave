package phanastrae.operation_starcleave.neoforge;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.commons.lang3.tuple.Triple;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.block.OperationStarcleaveToolActions;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;
import phanastrae.operation_starcleave.item.OperationStarcleaveCreativeModeTabs;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;
import phanastrae.operation_starcleave.mixin.common.accessor.item.AxeItemAccessor;
import phanastrae.operation_starcleave.neoforge.client.fluid.OperationStarcleaveFluidTypeExtensions;
import phanastrae.operation_starcleave.neoforge.fluid.OperationStarcleaveFluidTypes;
import phanastrae.operation_starcleave.neoforge.fluid.PetrichoricPlasmaFluidBucketWrapper;
import phanastrae.operation_starcleave.network.packet.OperationStarcleavePayloads;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Mod(OperationStarcleave.MOD_ID)
public class OperationStarcleaveNeoForge {

    public OperationStarcleaveNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        setupModBusEvents(modEventBus);
        setupGameBusEvents(NeoForge.EVENT_BUS);
    }

    public void setupModBusEvents(IEventBus modEventBus) {
        // init registry entries
        OperationStarcleave.RegistryListenerAdder RLA = new OperationStarcleave.RegistryListenerAdder() {
            @Override
            public <T> void addRegistryListener(Registry<T> registry, Consumer<BiConsumer<ResourceLocation, T>> source) {
                modEventBus.addListener((RegisterEvent event) -> {
                    ResourceKey<? extends Registry<T>> registryKey = registry.key();
                    if (registryKey.equals(event.getRegistryKey())) {
                        source.accept((resourceLocation, t) -> event.register(registryKey, resourceLocation, () -> t));
                    }
                });
            }

            @Override
            public <T> void addHolderRegistryListener(Registry<T> registry, Consumer<OperationStarcleave.HolderRegisterHelper<T>> source) {
                DeferredRegister<T> defRegister = DeferredRegister.create(registry, OperationStarcleave.MOD_ID);
                defRegister.register(modEventBus);
                source.accept((name, t) -> defRegister.register(name, () -> t));
            }
        };
        OperationStarcleave.initRegistryEntries(RLA);
        this.neoforgeRegistriesInit(RLA);

        // common init
        modEventBus.addListener(this::commonInit);

        // creative tabs
        modEventBus.addListener(this::buildCreativeModeTabContents);

        // register serverside payloads
        modEventBus.addListener(this::registerPayloadHandlers);

        // entity attributes
        modEventBus.addListener(this::entityAttributeCreation);

        // register client extensions // TODO move this to client?
        modEventBus.addListener(this::registerClientExtensions);

        // register capabilities
        modEventBus.addListener(this::registerCapabilities);
    }

    public void setupGameBusEvents(IEventBus gameEventBus) {
        // world tick start
        gameEventBus.addListener(this::tickLevel);

        // player changes dimension
        gameEventBus.addListener(this::onPlayerChangeLevel);

        // add tooltips
        gameEventBus.addListener(this::addTooltips);

        // handle tilling
        gameEventBus.addListener(this::handleToolModification);
    }

    public void neoforgeRegistriesInit(OperationStarcleave.RegistryListenerAdder registryListenerAdder) {
        registryListenerAdder.addRegistryListener(NeoForgeRegistries.FLUID_TYPES, OperationStarcleaveFluidTypes::init);
    }

    public void commonInit(FMLCommonSetupEvent event) {
        // everything here needs to be multithread safe
        event.enqueueWork(() -> {
            OperationStarcleave.init();
            OperationStarcleaveFluidTypes.registerFluidInteractions();

            // setup stripping
            // need to make sure map is mutable, fabric does this itself but we have to do it manually on neoforge
            Map<Block, Block> map = AxeItemAccessor.getSTRIPPABLES();
            if (!(map instanceof HashMap<Block, Block>)) {
                map = new HashMap<>(map);
                AxeItemAccessor.setSTRIPPABLES(map);
            }
            map.putAll(OperationStarcleaveToolActions.STRIPPABLES);
        });
    }

    public void buildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
        ResourceKey<CreativeModeTab> eventKey = event.getTabKey();
        OperationStarcleaveCreativeModeTabs.setupEntries(new OperationStarcleaveCreativeModeTabs.Helper() {
            @Override
            public void add(ResourceKey<CreativeModeTab> groupKey, ItemLike item) {
                if (eventKey.equals(groupKey)) {
                    event.accept(item);
                }
            }

            @Override
            public void add(ResourceKey<CreativeModeTab> groupKey, ItemLike... items) {
                if (eventKey.equals(groupKey)) {
                    for (ItemLike item : items) {
                        event.accept(item);
                    }
                }
            }

            @Override
            public void add(ResourceKey<CreativeModeTab> groupKey, ItemStack item) {
                if (eventKey.equals(groupKey)) {
                    event.accept(item);
                }
            }

            @Override
            public void add(ResourceKey<CreativeModeTab> groupKey, Collection<ItemStack> items) {
                if (eventKey.equals(groupKey)) {
                    for (ItemStack item : items) {
                        event.accept(item);
                    }
                }
            }

            @Override
            public void addAfter(ItemLike after, ResourceKey<CreativeModeTab> groupKey, ItemLike item) {
                if (eventKey.equals(groupKey)) {
                    event.insertAfter(new ItemStack(after), new ItemStack(item), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                }
            }

            @Override
            public void addAfter(ItemStack after, ResourceKey<CreativeModeTab> groupKey, ItemStack item) {
                if (eventKey.equals(groupKey)) {
                    event.insertAfter(after, item, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                }
            }

            @Override
            public void addAfter(ItemLike after, ResourceKey<CreativeModeTab> groupKey, ItemLike... items) {
                if (eventKey.equals(groupKey)) {
                    for (ItemLike item : items) {
                        event.insertAfter(new ItemStack(after), new ItemStack(item), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                        after = item;
                    }
                }
            }

            @Override
            public void addBefore(ItemLike before, ResourceKey<CreativeModeTab> groupKey, ItemLike item) {
                if (eventKey.equals(groupKey)) {
                    event.insertBefore(new ItemStack(before), new ItemStack(item), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                }
            }

            @Override
            public void addBefore(ItemStack before, ResourceKey<CreativeModeTab> groupKey, ItemStack item) {
                if (eventKey.equals(groupKey)) {
                    event.insertBefore(before, item, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                }
            }

            @Override
            public void addBefore(ItemLike before, ResourceKey<CreativeModeTab> groupKey, ItemLike... items) {
                if (eventKey.equals(groupKey)) {
                    for (ItemLike item : items) {
                        event.insertBefore(new ItemStack(before), new ItemStack(item), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    }
                }
            }

            @Override
            public void forTabRun(ResourceKey<CreativeModeTab> groupKey, BiConsumer<CreativeModeTab.ItemDisplayParameters, CreativeModeTab.Output> biConsumer) {
                if (eventKey.equals(groupKey)) {
                    biConsumer.accept(event.getParameters(), event);
                }
            }

            @Override
            public boolean operatorTabEnabled() {
                return event.getParameters().hasPermissions();
            }
        });
    }

    public void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        OperationStarcleavePayloads.init(new OperationStarcleavePayloads.Helper() {
            @Override
            public <T extends CustomPacketPayload> void registerS2C(CustomPacketPayload.Type<T> id, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, BiConsumer<T, Player> clientCallback) {
                registrar.playToClient(id, codec, (payload, context) -> clientCallback.accept(payload, context.player()));
            }

            @Override
            public <T extends CustomPacketPayload> void registerC2S(CustomPacketPayload.Type<T> id, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, BiConsumer<T, Player> serverCallback) {
                registrar.playToServer(id, codec, (payload, context) -> serverCallback.accept(payload, context.player()));
            }
        });
    }

    public void entityAttributeCreation(EntityAttributeCreationEvent event) {
        OperationStarcleaveEntityTypes.registerEntityAttributes(((entityType, builder) -> event.put(entityType, builder.build())));
    }

    public void registerClientExtensions(RegisterClientExtensionsEvent event) {
        OperationStarcleaveFluidTypeExtensions.init(event::registerFluidType);
    }

    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new PetrichoricPlasmaFluidBucketWrapper(stack), OperationStarcleaveItems.PETRICHORIC_PLASMA_BUCKET);
    }

    public void tickLevel(LevelTickEvent.Pre event) {
        OperationStarcleave.startLevelTick(event.getLevel());
    }

    public void onPlayerChangeLevel(PlayerEvent.PlayerChangedDimensionEvent event) {
        OperationStarcleave.onPlayerChangeDimension(event.getEntity());
    }

    public void addTooltips(ItemTooltipEvent event) {
        OperationStarcleave.addTooltips(event.getItemStack(), event.getContext(), event.getToolTip()::add, event.getFlags());
    }

    public void handleToolModification(BlockEvent.BlockToolModificationEvent event) {
        UseOnContext context = event.getContext();
        // handle tilling
        if (event.getItemAbility() == ItemAbilities.HOE_TILL) {
            Level level = context.getLevel();
            BlockPos pos = context.getClickedPos();
            BlockState state = level.getBlockState(pos);
            for (Triple<Block, Predicate<UseOnContext>, BlockState> action : OperationStarcleaveToolActions.TILLABLES) {
                Block block = action.getLeft();
                Predicate<UseOnContext> predicate = action.getMiddle();
                BlockState newState = action.getRight();

                if (state.is(block) && predicate.test(context)) {
                    event.setFinalState(newState);
                    return;
                }
            }
        }
    }
}

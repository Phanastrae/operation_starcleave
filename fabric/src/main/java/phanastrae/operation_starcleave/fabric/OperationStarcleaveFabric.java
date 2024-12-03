package phanastrae.operation_starcleave.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.entity.OperationStarcleaveEntityTypes;
import phanastrae.operation_starcleave.entity.effect.OperationStarcleaveStatusEffects;
import phanastrae.operation_starcleave.item.OperationStarcleaveCreativeModeTabs;
import phanastrae.operation_starcleave.network.packet.OperationStarcleavePayloads;
import phanastrae.operation_starcleave.world.firmament.FirmamentWatcher;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class OperationStarcleaveFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        // mob effect registry
        OperationStarcleaveStatusEffects.init((name, mobEffect) -> Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, OperationStarcleave.id(name), mobEffect));

        // init registry entries
        OperationStarcleave.initRegistryEntries(new OperationStarcleave.RegistryListenerAdder() {
            @Override
            public <T> void addRegistryListener(Registry<T> registry, Consumer<BiConsumer<ResourceLocation, T>> source) {
                source.accept((rl, t) -> Registry.register(registry, rl, t));
            }
        });

        // common init
        OperationStarcleave.init();

        // creative tabs
        setupCreativeTabs();

        // register serverside payloads
        registerServerPayloads();

        // entity attributes
        OperationStarcleaveEntityTypes.registerEntityAttributes((FabricDefaultAttributeRegistry::register));



        // world tick start
        ServerTickEvents.START_WORLD_TICK.register((OperationStarcleave::startLevelTick));

        // after player changes world
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register(((player, origin, destination) -> ((FirmamentWatcher)player).operation_starcleave$getWatchedRegions().unWatchAll()));
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
                    for(ItemLike item : items) {
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
                    for(ItemStack item : items) {
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
}

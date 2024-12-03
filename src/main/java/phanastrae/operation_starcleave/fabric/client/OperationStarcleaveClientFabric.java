package phanastrae.operation_starcleave.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import phanastrae.operation_starcleave.client.OperationStarcleaveClient;
import phanastrae.operation_starcleave.client.particle.OperationStarcleaveParticles;
import phanastrae.operation_starcleave.client.render.entity.OperationStarcleaveEntityRenderers;
import phanastrae.operation_starcleave.client.render.entity.model.OperationStarcleaveEntityModelLayers;
import phanastrae.operation_starcleave.client.render.shader.OperationStarcleaveShaders;
import phanastrae.operation_starcleave.network.packet.OperationStarcleavePayloads;

import java.util.function.BiConsumer;

public class OperationStarcleaveClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        OperationStarcleaveClient.init();

        // register clientside payloads
        registerClientPayloads();

        // entity renderers
        OperationStarcleaveEntityRenderers.init(this::registerEntityRenderer);

        // entity model layers
        OperationStarcleaveEntityModelLayers.init(((modelLayerLocation, layerDefinitionSupplier) -> EntityModelLayerRegistry.registerModelLayer(modelLayerLocation, layerDefinitionSupplier::get)));

        // particles
        OperationStarcleaveParticles.init(new OperationStarcleaveParticles.ClientParticleRegistrar() {
            @Override
            public <T extends ParticleOptions> void register(ParticleType<T> type, OperationStarcleaveParticles.ParticleRegistration<T> registration) {
                ParticleFactoryRegistry.getInstance().register(type, registration::create);
            }
        });

        // events
        ClientLifecycleEvents.CLIENT_STOPPING.register(OperationStarcleaveClient::onClientShutdown);

        CoreShaderRegistrationCallback.EVENT.register(OperationStarcleaveShaders::registerShaders);

        ClientTickEvents.START_WORLD_TICK.register(OperationStarcleaveClient::startLevelTick);

        WorldRenderEvents.BEFORE_ENTITIES.register(worldRenderContext -> OperationStarcleaveClient.renderBeforeEntities(
                worldRenderContext.world(),
                worldRenderContext.consumers(),
                worldRenderContext.matrixStack(),
                worldRenderContext.camera(),
                worldRenderContext.frustum(),
                worldRenderContext.worldRenderer(),
                worldRenderContext.projectionMatrix()
        ));

        WorldRenderEvents.AFTER_ENTITIES.register(worldRenderContext -> OperationStarcleaveClient.renderAfterEntities(
                worldRenderContext.world(),
                worldRenderContext.matrixStack(),
                worldRenderContext.consumers(),
                worldRenderContext.tickCounter(),
                worldRenderContext.camera()
        ));

        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register((worldRenderContext, hitResult) -> OperationStarcleaveClient.renderBeforeBlockOutline(worldRenderContext.blockOutlines(), worldRenderContext.consumers(), worldRenderContext.camera(), worldRenderContext.matrixStack()));

        InvalidateRenderStateCallback.EVENT.register(OperationStarcleaveClient::invalidateRenderState);
    }

    public void registerClientPayloads() {
        OperationStarcleavePayloads.init(new OperationStarcleavePayloads.Helper() {
            @Override
            public <T extends CustomPacketPayload> void registerS2C(CustomPacketPayload.Type<T> id, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, BiConsumer<T, Player> clientCallback) {
                ClientPlayNetworking.registerGlobalReceiver(id, (payload, context) -> clientCallback.accept(payload, context.player()));
            }

            @Override
            public <T extends CustomPacketPayload> void registerC2S(CustomPacketPayload.Type<T> id, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, BiConsumer<T, Player> serverCallback) {
                // empty
            }
        });
    }

    public <E extends Entity> void registerEntityRenderer(EntityType<? extends E> entityType, EntityRendererProvider<E> entityRendererFactory) {
        EntityRendererRegistry.register(entityType, entityRendererFactory);
    }
}

package phanastrae.operation_starcleave.neoforge.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.GameShuttingDownEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.joml.Matrix4f;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.client.OperationStarcleaveClient;
import phanastrae.operation_starcleave.client.particle.OperationStarcleaveParticles;
import phanastrae.operation_starcleave.client.render.entity.OperationStarcleaveEntityRenderers;
import phanastrae.operation_starcleave.client.render.entity.model.OperationStarcleaveEntityModelLayers;
import phanastrae.operation_starcleave.client.render.shader.OperationStarcleaveShaders;
import phanastrae.operation_starcleave.mixin.client.LevelRendererAccessor;

import java.io.IOException;
import java.io.UncheckedIOException;

import static net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS;
import static net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage.AFTER_ENTITIES;

@Mod(value = OperationStarcleave.MOD_ID, dist = Dist.CLIENT)
public class OperationStarcleaveClientNeoForge {

    public OperationStarcleaveClientNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        setupModBusEvents(modEventBus);
        setupGameBusEvents(NeoForge.EVENT_BUS);
    }

    public void setupModBusEvents(IEventBus modEventBus) {
        // client init
        modEventBus.addListener(this::onClientInit);

        // register clientside payloads
        // not needed here

        // entity renderers
        modEventBus.addListener(this::registerEntityRenderers);

        // entity model layers
        modEventBus.addListener(this::registerEntityLayers);

        // particles
        modEventBus.addListener(this::registerParticleProviders);

        // register shaders
        modEventBus.addListener(this::registerShaders);
    }

    public void setupGameBusEvents(IEventBus gameEventBus) {
        // on client stop
        NeoForge.EVENT_BUS.addListener(this::onGameShutdown);

        // client world tick start
        NeoForge.EVENT_BUS.addListener(this::startClientTick);

        // render level
        NeoForge.EVENT_BUS.addListener(this::renderLevel);

        // render before block outline
        NeoForge.EVENT_BUS.addListener(this::renderBlockHighlight);
    }

    public void onClientInit(FMLClientSetupEvent event) {
        // everything here needs to be multithread safe
        event.enqueueWork(OperationStarcleaveClient::init);
    }

    public void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        OperationStarcleaveEntityRenderers.init(event::registerEntityRenderer);
    }

    public void registerEntityLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        OperationStarcleaveEntityModelLayers.init(event::registerLayerDefinition);
    }

    public void registerParticleProviders(RegisterParticleProvidersEvent event) {
        OperationStarcleaveParticles.init(new OperationStarcleaveParticles.ClientParticleRegistrar() {
            @Override
            public <T extends ParticleOptions> void register(ParticleType<T> type, OperationStarcleaveParticles.ParticleRegistration<T> registration) {
                event.registerSpriteSet(type, registration::create);
            }
        });
    }

    public void registerShaders(RegisterShadersEvent event) {
        try {
            OperationStarcleaveShaders.registerShaders((id, vertexFormat, callback) -> event.registerShader(new ShaderInstance(event.getResourceProvider(), id, vertexFormat), callback));
        } catch (IOException e) {
            // TODO check if this is fine
            throw new UncheckedIOException(e);
        }
    }

    public void onGameShutdown(GameShuttingDownEvent event) {
        OperationStarcleaveClient.onClientShutdown(Minecraft.getInstance());
    }

    public void startClientTick(LevelTickEvent.Pre event) {
        if(event.getLevel().isClientSide) {
            OperationStarcleaveClient.startLevelTick(event.getLevel());
        }
    }

    public void renderLevel(RenderLevelStageEvent event) {
        ClientLevel level = Minecraft.getInstance().level;
        if(level == null) return;

        PoseStack matrixStack = event.getPoseStack();
        LevelRenderer levelRenderer = event.getLevelRenderer();
        MultiBufferSource vertexConsumers = ((LevelRendererAccessor)levelRenderer).getRenderBuffers().bufferSource();
        DeltaTracker deltaTracker = event.getPartialTick();
        Camera camera = event.getCamera();
        Matrix4f projectionMatirx = event.getProjectionMatrix();
        Frustum frustum = event.getFrustum();

        RenderLevelStageEvent.Stage stage = event.getStage();
        if(stage.equals(AFTER_CUTOUT_BLOCKS)) {
            // render before entities
            OperationStarcleaveClient.renderBeforeEntities(level, vertexConsumers, matrixStack, camera, frustum, levelRenderer, projectionMatirx);
        } else if(stage.equals(AFTER_ENTITIES)) {
            // render after entities
            OperationStarcleaveClient.renderAfterEntities(level, matrixStack, vertexConsumers, deltaTracker, camera);
        }
    }

    public void renderBlockHighlight(RenderHighlightEvent.Block event) {
        OperationStarcleaveClient.renderBeforeBlockOutline(true, event.getMultiBufferSource(), event.getCamera(), event.getPoseStack());
    }
}

package phanastrae.operation_starcleave;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.tick.TickManager;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;
import phanastrae.operation_starcleave.block.entity.BlessedBedBlockEntity;
import phanastrae.operation_starcleave.block.entity.OperationStarcleaveBlockEntityTypes;
import phanastrae.operation_starcleave.item.StarbleachCoating;
import phanastrae.operation_starcleave.network.OperationStarcleaveClientPacketHandler;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticleTypes;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticles;
import phanastrae.operation_starcleave.render.OperationStarcleaveRenderLayers;
import phanastrae.operation_starcleave.render.OperationStarcleaveShaders;
import phanastrae.operation_starcleave.render.ScreenShakeManager;
import phanastrae.operation_starcleave.render.entity.BlessedBedBlockEntityRenderer;
import phanastrae.operation_starcleave.render.entity.OperationStarcleaveEntityRenderers;
import phanastrae.operation_starcleave.render.firmament.FirmamentActorRenderable;
import phanastrae.operation_starcleave.render.firmament.FirmamentBuiltSubRegionStorage;
import phanastrae.operation_starcleave.world.OperationStarcleaveWorld;
import phanastrae.operation_starcleave.world.firmament.Firmament;
import phanastrae.operation_starcleave.render.firmament.FirmamentRenderer;

public class OperationStarcleaveClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		OperationStarcleaveParticles.init();

		BlockEntityRendererFactories.register(OperationStarcleaveBlockEntityTypes.BLESSED_BED, BlessedBedBlockEntityRenderer::new);

		ClientTickEvents.START_WORLD_TICK.register((world) -> {
			OperationStarcleaveWorld opscw = (OperationStarcleaveWorld)world;
			if(opscw.operation_starcleave$getCleavingFlashTicksLeft() > 0) {
				opscw.operation_starcleave$setCleavingFlashTicksLeft(opscw.operation_starcleave$getCleavingFlashTicksLeft() - 1);
			}

			TickManager tickManager = world.getTickManager();
			boolean bl = tickManager.shouldTick();
			if(bl) {
				//Profiler profiler = world.getProfiler();
				//profiler.push("starcleave_fracture");
				//Firmament.fromWorld(world).tick();
				//profiler.pop();
				Firmament firmament = Firmament.fromWorld(world);
				if(firmament != null) {
					firmament.getFirmamentRegionManager().tick();
					firmament.manageActors();
					firmament.tickActors();
				}
			}

			ScreenShakeManager.getInstance().update();
		});

		WorldRenderEvents.BEFORE_ENTITIES.register(worldRenderContext -> {
			FirmamentRenderer.render(worldRenderContext);

			Firmament firmament = Firmament.fromWorld(worldRenderContext.world());
			if(firmament != null) {
				firmament.forEachActor(firmamentActor -> {
					if(firmamentActor instanceof FirmamentActorRenderable far) {
						far.render(worldRenderContext.matrixStack(), worldRenderContext.consumers(), worldRenderContext.tickDelta(), worldRenderContext.camera());
					}
				});
			}
		});

		CoreShaderRegistrationCallback.EVENT.register(OperationStarcleaveShaders::registerShaders);

		OperationStarcleaveEntityRenderers.init();
		OperationStarcleaveClientPacketHandler.init();

		ClientLifecycleEvents.CLIENT_STOPPING.register(OperationStarcleaveClient::onClientShutdown);

		BlockRenderLayerMap.INSTANCE.putBlock(OperationStarcleaveBlocks.STARBLEACHED_LEAVES, RenderLayer.getCutoutMipped());
		BlockRenderLayerMap.INSTANCE.putBlock(OperationStarcleaveBlocks.SHORT_HOLY_MOSS, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(OperationStarcleaveBlocks.BLESSED_BED, RenderLayer.getCutout());

		ItemTooltipCallback.EVENT.register(((stack, context, lines) -> {
			if(StarbleachCoating.hasStarbleachCoating(stack)) {
				lines.add(StarbleachCoating.getText());
			}
		}));
	}

	public static void onClientShutdown(MinecraftClient client) {
		FirmamentBuiltSubRegionStorage.getInstance().close();
	}
}
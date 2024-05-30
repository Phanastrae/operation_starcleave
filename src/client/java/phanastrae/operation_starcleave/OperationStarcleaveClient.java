package phanastrae.operation_starcleave;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.InvalidateRenderStateCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.world.tick.TickManager;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;
import phanastrae.operation_starcleave.block.entity.OperationStarcleaveBlockEntityTypes;
import phanastrae.operation_starcleave.item.StarbleachCoating;
import phanastrae.operation_starcleave.network.OperationStarcleaveClientPacketHandler;
import phanastrae.operation_starcleave.particle.OperationStarcleaveParticles;
import phanastrae.operation_starcleave.render.entity.model.OperationStarcleaveEntityModelLayers;
import phanastrae.operation_starcleave.render.shader.OperationStarcleaveShaders;
import phanastrae.operation_starcleave.render.ScreenShakeManager;
import phanastrae.operation_starcleave.render.block.entity.BlessedBedBlockEntityRenderer;
import phanastrae.operation_starcleave.render.entity.OperationStarcleaveEntityRenderers;
import phanastrae.operation_starcleave.render.firmament.*;
import phanastrae.operation_starcleave.duck.WorldDuck;
import phanastrae.operation_starcleave.world.firmament.Firmament;

public class OperationStarcleaveClient implements ClientModInitializer {

	public static FirmamentOutlineRenderer FirmamentOutlineRenderer = new FirmamentOutlineRenderer();

	@Override
	public void onInitializeClient() {
		OperationStarcleaveParticles.init();

		BlockEntityRendererFactories.register(OperationStarcleaveBlockEntityTypes.BLESSED_BED, BlessedBedBlockEntityRenderer::new);

		ClientTickEvents.START_WORLD_TICK.register((world) -> {
			WorldDuck opscw = (WorldDuck)world;
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
			FirmamentTextureStorage.getInstance().tick();

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

		WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register((worldRenderContext, hitResult) -> {
			if(!worldRenderContext.blockOutlines()) return true;
			if(worldRenderContext.consumers() == null) return true;
			OperationStarcleaveClient.FirmamentOutlineRenderer.renderOutline(worldRenderContext.consumers(), worldRenderContext.camera(), worldRenderContext.matrixStack());
			return true;
		});

		InvalidateRenderStateCallback.EVENT.register(() -> {
			FirmamentTextureStorage.getInstance().clearData();
		});

		CoreShaderRegistrationCallback.EVENT.register(OperationStarcleaveShaders::registerShaders);

		OperationStarcleaveEntityRenderers.init();
		OperationStarcleaveEntityModelLayers.init();
		OperationStarcleaveClientPacketHandler.init();

		ClientLifecycleEvents.CLIENT_STOPPING.register(OperationStarcleaveClient::onClientShutdown);

		BlockRenderLayerMap.INSTANCE.putBlock(OperationStarcleaveBlocks.STARBLEACHED_LEAVES, RenderLayer.getCutoutMipped());
		BlockRenderLayerMap.INSTANCE.putBlock(OperationStarcleaveBlocks.SHORT_HOLY_MOSS, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(OperationStarcleaveBlocks.BLESSED_BED, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(OperationStarcleaveBlocks.PHLOGISTIC_FIRE, RenderLayer.getCutout());

		ItemTooltipCallback.EVENT.register(((stack, context, lines) -> {
			if(StarbleachCoating.hasStarbleachCoating(stack)) {
				lines.add(StarbleachCoating.getText());
			}
		}));
	}

	public static void onClientShutdown(MinecraftClient client) {
		FirmamentTextureStorage.getInstance().close();
	}
}
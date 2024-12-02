package phanastrae.operation_starcleave.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.InvalidateRenderStateCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.TickRateManager;
import org.joml.Quaternionf;
import phanastrae.operation_starcleave.block.OperationStarcleaveBlocks;
import phanastrae.operation_starcleave.block.entity.OperationStarcleaveBlockEntityTypes;
import phanastrae.operation_starcleave.duck.WorldDuck;
import phanastrae.operation_starcleave.client.network.OperationStarcleaveClientPacketHandler;
import phanastrae.operation_starcleave.client.particle.OperationStarcleaveParticles;
import phanastrae.operation_starcleave.client.render.ScreenShakeManager;
import phanastrae.operation_starcleave.client.render.block.entity.BlessedBedBlockEntityRenderer;
import phanastrae.operation_starcleave.client.render.entity.OperationStarcleaveEntityRenderers;
import phanastrae.operation_starcleave.client.render.entity.model.OperationStarcleaveEntityModelLayers;
import phanastrae.operation_starcleave.client.render.firmament.FirmamentActorRenderable;
import phanastrae.operation_starcleave.client.render.firmament.FirmamentOutlineRenderer;
import phanastrae.operation_starcleave.client.render.firmament.FirmamentRenderer;
import phanastrae.operation_starcleave.client.render.firmament.FirmamentTextureStorage;
import phanastrae.operation_starcleave.client.render.shader.OperationStarcleaveShaders;
import phanastrae.operation_starcleave.world.firmament.Firmament;

public class OperationStarcleaveClient implements ClientModInitializer {

	public static FirmamentOutlineRenderer firmamentOutlineRenderer = new FirmamentOutlineRenderer();

	@Override
	public void onInitializeClient() {
		OperationStarcleaveParticles.init();

		BlockEntityRenderers.register(OperationStarcleaveBlockEntityTypes.BLESSED_BED, BlessedBedBlockEntityRenderer::new);

		ClientTickEvents.START_WORLD_TICK.register((world) -> {
			WorldDuck opscw = (WorldDuck)world;
			if(opscw.operation_starcleave$getCleavingFlashTicksLeft() > 0) {
				opscw.operation_starcleave$setCleavingFlashTicksLeft(opscw.operation_starcleave$getCleavingFlashTicksLeft() - 1);
			}

			TickRateManager tickManager = world.tickRateManager();
			boolean bl = tickManager.runsNormally();
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
			Quaternionf quaternionf = worldRenderContext.camera().rotation().conjugate(new Quaternionf());
			PoseStack matrixStack = new PoseStack();
			matrixStack.mulPose(quaternionf);

			FirmamentTextureStorage.getInstance().tick();

			FirmamentRenderer.render(matrixStack, worldRenderContext);
		});

		WorldRenderEvents.AFTER_ENTITIES.register(worldRenderContext -> {
			Firmament firmament = Firmament.fromWorld(worldRenderContext.world());
			if(firmament != null) {
				firmament.forEachActor(firmamentActor -> {
					if(firmamentActor instanceof FirmamentActorRenderable far) {
						far.render(worldRenderContext.matrixStack(), worldRenderContext.consumers(), worldRenderContext.tickCounter().getGameTimeDeltaPartialTick(false), worldRenderContext.camera());
					}
				});
			}
		});

		WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register((worldRenderContext, hitResult) -> {
			if(!worldRenderContext.blockOutlines()) return true;
			if(worldRenderContext.consumers() == null) return true;
			OperationStarcleaveClient.firmamentOutlineRenderer.renderOutline(worldRenderContext.consumers(), worldRenderContext.camera(), worldRenderContext.matrixStack());
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

		BlockRenderLayerMap.INSTANCE.putBlock(OperationStarcleaveBlocks.STARBLEACHED_LEAVES, RenderType.cutoutMipped());
		BlockRenderLayerMap.INSTANCE.putBlock(OperationStarcleaveBlocks.MULCHBORNE_TUFT, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(OperationStarcleaveBlocks.SHORT_HOLY_MOSS, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(OperationStarcleaveBlocks.BLESSED_BED, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(OperationStarcleaveBlocks.PHLOGISTIC_FIRE, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(OperationStarcleaveBlocks.PETRICHORIC_VAPOR, RenderType.translucent());
	}

	public static void onClientShutdown(Minecraft client) {
		FirmamentTextureStorage.getInstance().close();
	}
}
package phanastrae.operation_starcleave;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.tick.TickManager;
import phanastrae.operation_starcleave.network.OperationStarcleaveClientPacketHandler;
import phanastrae.operation_starcleave.render.OperationStarcleaveShaders;
import phanastrae.operation_starcleave.render.entity.OperationStarcleaveEntityRenderers;
import phanastrae.operation_starcleave.render.firmament.FirmamentBuiltSubRegionStorage;
import phanastrae.operation_starcleave.world.OperationStarcleaveWorld;
import phanastrae.operation_starcleave.world.firmament.Firmament;
import phanastrae.operation_starcleave.render.firmament.FirmamentRenderer;

public class OperationStarcleaveClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
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
				}
			}

		});

		WorldRenderEvents.BEFORE_ENTITIES.register(FirmamentRenderer::render);

		CoreShaderRegistrationCallback.EVENT.register(OperationStarcleaveShaders::registerShaders);

		OperationStarcleaveEntityRenderers.init();
		OperationStarcleaveClientPacketHandler.init();

		ClientLifecycleEvents.CLIENT_STOPPING.register((c) -> FirmamentBuiltSubRegionStorage.getInstance().close());
	}
}
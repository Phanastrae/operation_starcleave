package phanastrae.operation_starcleave;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.tick.TickManager;
import phanastrae.operation_starcleave.render.OperationStarcleaveShaders;
import phanastrae.operation_starcleave.world.firmament.Firmament;
import phanastrae.operation_starcleave.render.firmament.FirmamentRenderer;

public class OperationStarcleaveClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ClientTickEvents.START_WORLD_TICK.register((world) -> {
			TickManager tickManager = world.getTickManager();
			boolean bl = tickManager.shouldTick();
			if(bl) {
				Profiler profiler = MinecraftClient.getInstance().getProfiler();
				profiler.push("starcleave_fracture");
				Firmament.getInstance().tick(world);
				profiler.pop();
			}
		});

		WorldRenderEvents.AFTER_SETUP.register(FirmamentRenderer::render);

		CoreShaderRegistrationCallback.EVENT.register(OperationStarcleaveShaders::registerShaders);

	}
}
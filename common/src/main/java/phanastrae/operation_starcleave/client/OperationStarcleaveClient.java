package phanastrae.operation_starcleave.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import phanastrae.operation_starcleave.client.compat.ClientCompat;
import phanastrae.operation_starcleave.client.render.ScreenShakeManager;
import phanastrae.operation_starcleave.client.render.block.OperationStarcleaveBlockRenderTypes;
import phanastrae.operation_starcleave.client.render.block.entity.OperationStarcleaveBlockEntityRenderers;
import phanastrae.operation_starcleave.client.render.extras_baking.RenderExtras;
import phanastrae.operation_starcleave.client.render.firmament.*;
import phanastrae.operation_starcleave.duck.LevelDuckInterface;
import phanastrae.operation_starcleave.world.firmament.Firmament;

public class OperationStarcleaveClient {

    public static FirmamentOutlineHandler FIRMAMENT_OUTLINE_HANDLER = new FirmamentOutlineHandler();

    public static void init() {
        ClientCompat.init();

        // register block layers
        OperationStarcleaveBlockRenderTypes.init();

        // register block entity renderers
        OperationStarcleaveBlockEntityRenderers.init();
    }

    public static void renderBeforeEntities(Level level, Camera camera, Frustum frustum, LevelRenderer levelRenderer, Matrix4f projectionMatrix, Matrix4f positionMatrix) {
        FirmamentTextureStorage.fromLevelRenderer(levelRenderer).tick();
        FirmamentRenderer.render(level, camera, frustum, levelRenderer, projectionMatrix, positionMatrix);
        RenderExtras.renderExtras(levelRenderer, projectionMatrix, positionMatrix, camera, frustum);
    }

    public static void renderAfterEntities(Level level, PoseStack matrixStack, MultiBufferSource vertexConsumers, DeltaTracker deltaTracker, Camera camera) {
        Firmament firmament = Firmament.fromLevel(level);
        if (firmament != null) {
            firmament.forEachActor(firmamentActor -> {
                if (firmamentActor instanceof FirmamentActorRenderable far) {
                    far.render(matrixStack, vertexConsumers, deltaTracker.getGameTimeDeltaPartialTick(false), camera);
                }
            });
        }
    }

    public static boolean renderBeforeBlockOutline(@Nullable MultiBufferSource vertexConsumers, Camera camera, PoseStack matrixStack) {
        if (vertexConsumers == null || Minecraft.getInstance().options.hideGui) {
            return true;
        } else {
            return !FIRMAMENT_OUTLINE_HANDLER.renderOutline(vertexConsumers, camera, matrixStack);
        }
    }

    public static void startLevelTick(Level level) {
        LevelDuckInterface levelDuck = (LevelDuckInterface) level;
        if (levelDuck.operation_starcleave$getCleavingFlashTicksLeft() > 0) {
            levelDuck.operation_starcleave$setCleavingFlashTicksLeft(levelDuck.operation_starcleave$getCleavingFlashTicksLeft() - 1);
        }

        Firmament firmament = Firmament.fromLevel(level);
        if (firmament != null) {
            TickRateManager tickManager = level.tickRateManager();
            boolean runsNormally = tickManager.runsNormally();
            firmament.clientTick(runsNormally);
        }

        ScreenShakeManager.getInstance().update();
    }

    public static void invalidateRenderState(LevelRenderer levelRenderer) {
        FirmamentTextureStorage.fromLevelRenderer(levelRenderer).clearData();
    }

    public static void onClientShutdown(Minecraft client) {
        FirmamentSkyRenderer.getInstance().close();
    }
}
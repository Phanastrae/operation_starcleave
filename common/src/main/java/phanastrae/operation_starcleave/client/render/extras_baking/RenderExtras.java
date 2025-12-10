package phanastrae.operation_starcleave.client.render.extras_baking;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.client.compat.ClientCompat;
import phanastrae.operation_starcleave.client.duck.LevelRendererExtrasDuck;
import phanastrae.operation_starcleave.client.render.OperationStarcleaveRenderTypes;
import phanastrae.operation_starcleave.item.OperationStarcleaveItems;

import java.util.Collection;
import java.util.List;

public class RenderExtras {

    public static final List<Item> IRIDESCENT_ITEMS = List.of(
            OperationStarcleaveItems.STARFLAKED_BISMUTH_BLOCK,
            OperationStarcleaveItems.STARFLAKED_BISMUTH_SLAB,
            OperationStarcleaveItems.CHISELED_STARFLAKED_BISMUTH_BLOCK,

            OperationStarcleaveItems.STARFLAKED_BISMUTH_PILLAR,

            OperationStarcleaveItems.STARFLAKED_BISMUTH_BRICKS,
            OperationStarcleaveItems.STARFLAKED_BISMUTH_BRICK_SLAB,
            OperationStarcleaveItems.STARFLAKED_BISMUTH_BRICK_STAIRS,
            OperationStarcleaveItems.STARFLAKED_BISMUTH_BRICK_WALL,
            OperationStarcleaveItems.CHISELED_STARFLAKED_BISMUTH_BRICKS,

            OperationStarcleaveItems.STARFLAKED_BISMUTH_TILES,
            OperationStarcleaveItems.STARFLAKED_BISMUTH_TILE_SLAB,
            OperationStarcleaveItems.STARFLAKED_BISMUTH_TILE_STAIRS,
            OperationStarcleaveItems.STARFLAKED_BISMUTH_TILE_WALL,

            OperationStarcleaveItems.STARFLAKED_BISMUTH_MOSAIC,
            OperationStarcleaveItems.STARFLAKED_BISMUTH_MOSAIC_SLAB,
            OperationStarcleaveItems.STARFLAKED_BISMUTH_MOSAIC_STAIRS,
            OperationStarcleaveItems.STARFLAKED_BISMUTH_MOSAIC_WALL,

            OperationStarcleaveItems.STARFLAKED_BISMUTH_DOOR,
            OperationStarcleaveItems.STARFLAKED_BISMUTH_TRAPDOOR,

            OperationStarcleaveItems.CELESTIAL_OPAL_BLOCK,

            OperationStarcleaveItems.BISMUTH_FLAKE,
            OperationStarcleaveItems.STARFLAKED_BISMUTH,
            OperationStarcleaveItems.BISMUTH_PEGASUS_ARMOR
    );

    private static float[] posOffset = new float[3];
    private static boolean inScreen = false;
    private static int iridescenceId = 0;

    public static boolean isItemIridescent(Item item) {
        // TODO this may need optimising as the list grows
        return IRIDESCENT_ITEMS.contains(item);
    }

    public static int getIridescenceId(Item item) {
        // TODO tidy this
        if (item.equals(OperationStarcleaveItems.CELESTIAL_OPAL_BLOCK)) {
            return getOpalIridescenceId();
        } else {
            return getBismuthIridescenceId();
        }
    }

    public static int getBismuthIridescenceId() {
        return 1;
    }

    public static int getOpalIridescenceId() {
        return 2;
    }

    public static void renderExtras(LevelRenderer levelRenderer, Matrix4f projectionMatrix, Matrix4f positionMatrix, Camera camera, Frustum frustum) {
        SectionExtrasRebuildQueue rebuildQueue = ((LevelRendererExtrasDuck) levelRenderer).operation_starcleave$getRebuildQueue();
        if (rebuildQueue == null) {
            return;
        }

        rebuildQueue.updateNonEmptySections();
        Collection<ExtrasSection> sections = rebuildQueue.getNonEmptySections();
        if (sections.isEmpty()) {
            return;
        }

        Vec3 camPos = camera.getPosition();
        double camX = camPos.x();
        double camY = camPos.y();
        double camZ = camPos.z();

        renderSectionLayer(
                OperationStarcleaveRenderTypes.getIridescence(),
                camX, camY, camZ,
                positionMatrix,
                projectionMatrix,
                sections,
                frustum,
                camera,
                rebuildQueue.getViewDistance()
        );
    }

    public static void renderSectionLayer(RenderType renderType, double x, double y, double z, Matrix4f frustrumMatrix, Matrix4f projectionMatrix, Collection<ExtrasSection> sections, Frustum frustum, Camera camera, int viewDistance) {
        RenderSystem.assertOnRenderThread();

        Minecraft minecraft = Minecraft.getInstance();
        ProfilerFiller profiler = minecraft.getProfiler();

        renderType.setupRenderState();

        profiler.push("filterempty");
        // frustum cull sections, ideally we'd also do occlusion culling but making that sodium compatible might be tricky
        BlockPos camPos = camera.getBlockPosition();
        int cx = camPos.getX() >> 4;
        int cz = camPos.getZ() >> 4;
        ExtrasSection[] filteredSections = sections.stream().filter(section -> {
            BlockPos originPos = section.getOrigin();
            return isWithinDistance(cx, cz, viewDistance, originPos.getX() >> 4, originPos.getZ() >> 4) && frustum.isVisible(section.getBoundingBox());
        }).toArray(ExtrasSection[]::new);
        ObjectArrayList<ExtrasSection> visibleSections = ObjectArrayList.wrap(filteredSections);

        profiler.popPush(() -> "starcleave$render_" + renderType);
        ObjectListIterator<ExtrasSection> iterator = visibleSections
                .listIterator(0);

        ShaderInstance shaderInstance = RenderSystem.getShader();
        shaderInstance.setDefaultUniforms(VertexFormat.Mode.QUADS, frustrumMatrix, projectionMatrix, minecraft.getWindow());
        shaderInstance.apply();
        Uniform uniform = shaderInstance.CHUNK_OFFSET;

        while (iterator.hasNext()) {
            ExtrasSection section = iterator.next();
            if (!section.getCompiled().isEmpty(renderType)) {
                VertexBuffer buffer = section.getBuffer(renderType);
                if (buffer == null) {
                    OperationStarcleave.LOGGER.warn("Tried to render extra layers on a chunk section, but the buffers were null? This should not happen.");
                    continue;
                }

                BlockPos origin = section.getOrigin();

                if (uniform != null) {
                    if (ClientCompat.SODIUM_LOADED) {
                        // sodium slightly adjusts these values, so we need to match them to avoid z-fighting
                        uniform.set(
                                getSodiumCameraTranslation(origin.getX(), x),
                                getSodiumCameraTranslation(origin.getY(), y),
                                getSodiumCameraTranslation(origin.getZ(), z)
                        );
                    } else {
                        uniform.set(
                                (float) ((double) origin.getX() - x),
                                (float) ((double) origin.getY() - y),
                                (float) ((double) origin.getZ() - z)
                        );
                    }
                    uniform.upload();
                }

                buffer.bind();
                buffer.draw();
            }
        }

        if (uniform != null) {
            uniform.set(0.0F, 0.0F, 0.0F);
        }

        shaderInstance.clear();
        VertexBuffer.unbind();
        profiler.pop();
        renderType.clearRenderState();
    }

    public static float getSodiumCameraTranslation(int blockPos, double camPos) {
        int camBlock = (int) camPos;
        float camFrac = (float) (camPos - camBlock);
        float mod = Math.copySign(8 * 16, camFrac);
        float adjustedCamFrac = (camFrac + mod) - mod;

        return (blockPos - camBlock) - adjustedCamFrac;
    }

    private static boolean isWithinDistance(int centerX, int centerZ, int viewDistance, int x, int z) {
        int dxIsh = Math.max(0, Math.abs(x - centerX) - 1);
        int dzIsh = Math.max(0, Math.abs(z - centerZ) - 1);

        long max = Math.max(dxIsh, dzIsh);
        long min = Math.min(dxIsh, dzIsh);

        long distSqr = min * min + max * max;
        int viewDistSqr = viewDistance * viewDistance;

        return distSqr < (long) viewDistSqr;
    }

    public static void resetPosOffset() {
        posOffset[0] = 0F;
        posOffset[1] = 0F;
        posOffset[2] = 0F;
    }

    public static void setPosOffset(float x, float y, float z) {
        posOffset[0] = x;
        posOffset[1] = y;
        posOffset[2] = z;
    }

    public static float[] getPosOffset() {
        return posOffset;
    }

    public static void setInScreen(boolean value) {
        inScreen = value;
    }

    public static boolean isInScreen() {
        return inScreen;
    }

    public static int getIridescenceId() {
        return iridescenceId;
    }

    public static void setIridescenceId(int id) {
        iridescenceId = id;
    }
}

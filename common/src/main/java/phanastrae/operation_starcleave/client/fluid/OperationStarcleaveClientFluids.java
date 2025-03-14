package phanastrae.operation_starcleave.client.fluid;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import phanastrae.operation_starcleave.OperationStarcleave;
import phanastrae.operation_starcleave.fluid.OperationStarcleaveFluids;
import phanastrae.operation_starcleave.mixin.client.CameraAccessor;
import phanastrae.operation_starcleave.registry.OperationStarcleaveFluidTags;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class OperationStarcleaveClientFluids {

    private static final List<XPlatGenericClientFluid> XPGCF_LIST = new ObjectArrayList<>();

    public static final XPlatGenericClientFluid PETRICHORIC_PLASMA = new XPlatGenericClientFluid(OperationStarcleaveFluidTags.PETRICHORIC_PLASMA, OperationStarcleaveFluids.PETRICHORIC_PLASMA, OperationStarcleaveFluids.FLOWING_PETRICHORIC_PLASMA)
            .setTint(0xFFFFFFFF)
            .setFogStart(-2.0F)
            .setFogEnd(8.0F)
            .setFogColorInt(154, 201, 52)
            .setStillTexture(OperationStarcleave.id("block/petrichoric_plasma_still"))
            .setFlowTexture(OperationStarcleave.id("block/petrichoric_plasma_flow"))
            .setOverlayTexture(null);

    public static void init() {
        addXPGCFsToLists(
                PETRICHORIC_PLASMA
        );
    }

    public static void forEachXPGCF(Consumer<XPlatGenericClientFluid> consumer) {
        XPGCF_LIST.forEach(consumer);
    }

    private static void addXPGCFsToLists(XPlatGenericClientFluid... xpgcfs) {
        XPGCF_LIST.addAll(Arrays.asList(xpgcfs));
    }

    @Nullable
    public static XPlatGenericClientFluid getXPGCF(Camera camera) {
        BlockGetter level = ((CameraAccessor)camera).getLevel();
        if(level == null) {
            return null;
        }

        BlockPos cameraBlockPos = camera.getBlockPosition();
        FluidState cameraFluidState = level.getFluidState(cameraBlockPos);
        if(cameraFluidState.isEmpty() || cameraFluidState.is(FluidTags.WATER) || cameraFluidState.is(FluidTags.LAVA)) {
            return null;
        }

        double fluidHeight = cameraBlockPos.getY() + cameraFluidState.getHeight(level, cameraBlockPos);
        double camHeight = camera.getPosition().y();

        if(camHeight >= fluidHeight) {
            return null;
        }

        return OperationStarcleaveClientFluids.getXPGCF(cameraFluidState);
    }

    @Nullable
    public static XPlatGenericClientFluid getXPGCF(FluidState fluidState) {
        for(XPlatGenericClientFluid xpgcf : XPGCF_LIST) {
            if(fluidState.is(xpgcf.getFluidTag())) {
                return xpgcf;
            }
        }

        return null;
    }

    public static class XPlatGenericClientFluid {

        private final TagKey<Fluid> fluidTag;
        private final Fluid still;
        private final Fluid flow;

        private ResourceLocation stillTexture = ResourceLocation.withDefaultNamespace("block/water_still");
        private ResourceLocation flowTexture = ResourceLocation.withDefaultNamespace("block/water_flow");
        @Nullable
        private ResourceLocation overlayTexture = ResourceLocation.withDefaultNamespace("block/water_overlay");
        @Nullable
        private ResourceLocation screenEffectTexture = ResourceLocation.withDefaultNamespace("textures/misc/underwater.png");
        private int tint = 0xFFFFFFFF;

        private float fogStart = 0.0F;
        private float fogEnd = Float.POSITIVE_INFINITY;
        private Vector3f fogColor = new Vector3f(1, 1, 1);

        private float fovScaleFactor = 0.85714287F; // vanilla default

        public XPlatGenericClientFluid(TagKey<Fluid> fluidTag, Fluid still, Fluid flow) {
            this.fluidTag = fluidTag;
            this.still = still;
            this.flow = flow;
        }

        public TagKey<Fluid> getFluidTag() {
            return fluidTag;
        }

        public Fluid getStill() {
            return still;
        }

        public Fluid getFlow() {
            return flow;
        }

        public XPlatGenericClientFluid setStillTexture(ResourceLocation stillTexture) {
            this.stillTexture = stillTexture;
            return this;
        }

        public ResourceLocation getStillTexture() {
            return stillTexture;
        }

        public XPlatGenericClientFluid setFlowTexture(ResourceLocation flowTexture) {
            this.flowTexture = flowTexture;
            return this;
        }

        public ResourceLocation getFlowTexture() {
            return flowTexture;
        }

        public XPlatGenericClientFluid setOverlayTexture(@Nullable ResourceLocation overlayTexture) {
            this.overlayTexture = overlayTexture;
            return this;
        }

        @Nullable
        public ResourceLocation getOverlayTexture() {
            return overlayTexture;
        }

        public XPlatGenericClientFluid setScreenEffectTexture(@Nullable ResourceLocation screenEffectTexture) {
            this.screenEffectTexture = screenEffectTexture;
            return this;
        }

        @Nullable
        public ResourceLocation getScreenEffectTexture() {
            return screenEffectTexture;
        }

        public XPlatGenericClientFluid setTint(int tint) {
            // input tint as ARGB
            // fabric ignores tint's alpha values, so inputs should ideally have an alpha of 0xFF for xplat parity
            this.tint = tint;
            return this;
        }

        public int getTint() {
            return tint;
        }

        public XPlatGenericClientFluid setFogStart(float fogStart) {
            this.fogStart = fogStart;
            return this;
        }

        public float getFogStart() {
            return fogStart;
        }

        public XPlatGenericClientFluid setFogEnd(float fogEnd) {
            this.fogEnd = fogEnd;
            return this;
        }

        public float getFogEnd() {
            return fogEnd;
        }

        public XPlatGenericClientFluid setFogColor(Vector3f fogColor) {
            this.fogColor = fogColor;
            return this;
        }

        public XPlatGenericClientFluid setFogColor(float r, float g, float b) {
            return this.setFogColor(new Vector3f(r, g, b));
        }

        public XPlatGenericClientFluid setFogColorInt(int r, int g, int b) {
            return this.setFogColor(r/255F, g/255F, b/255F);
        }

        public float getFovScaleFactor() {
            return fovScaleFactor;
        }

        public XPlatGenericClientFluid setFovScaleFactor(float fovScaleFactor) {
            this.fovScaleFactor = fovScaleFactor;
            return this;
        }

        public Vector3f getFogColor() {
            return this.fogColor;
        }

        public void setupFog(float farPlaneDistance) {
            RenderSystem.setShaderFogStart(this.fogStart);
            if (this.fogEnd > farPlaneDistance) {
                RenderSystem.setShaderFogEnd(farPlaneDistance);
                RenderSystem.setShaderFogShape(FogShape.CYLINDER);
            } else {
                RenderSystem.setShaderFogEnd(this.fogEnd);
                RenderSystem.setShaderFogShape(FogShape.SPHERE);
            }
        }
    }
}

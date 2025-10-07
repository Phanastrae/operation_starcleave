package phanastrae.operation_starcleave.client.compat;

import phanastrae.operation_starcleave.services.XPlatInterface;

public class ClientCompat {

    // mod loaded flags
    public static boolean IRIS_LOADED;

    public static void init() {
        setupModLoadedFlags();
        setupToggleFlags();
    }

    public static void setupModLoadedFlags() {
        XPlatInterface XPLAT = XPlatInterface.INSTANCE;
        IRIS_LOADED = XPLAT.isModLoaded("iris");
    }

    public static void setupToggleFlags() {
        // empty for now
    }

    public static boolean shadersEnabled() {
        return IRIS_LOADED && net.irisshaders.iris.api.v0.IrisApi.getInstance().isShaderPackInUse();
    }

    public static boolean renderingShadows() {
        return IRIS_LOADED && net.irisshaders.iris.api.v0.IrisApi.getInstance().isRenderingShadowPass();
    }

    public static boolean useAltFractureRendering() {
        return shadersEnabled();
    }
}

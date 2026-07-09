package dev.sxilverr.chunkanimatorembeddiumcompat.access;

import java.lang.reflect.Method;

public final class IrisShaderAccess {

    private static boolean unavailable;
    private static Method getOverride;
    private static Method getInterface;
    private static Method setRegionOffset;

    private IrisShaderAccess() {}

    public static boolean applyRegionOffset(Object renderer, float x, float y, float z) {
        if (unavailable) {
            return false;
        }
        try {
            if (getOverride == null) {
                getOverride = renderer.getClass().getMethod("iris$getOverride");
            }
            Object override = getOverride.invoke(renderer);
            if (override == null) {
                return false;
            }
            if (getInterface == null) {
                getInterface = override.getClass().getMethod("getInterface");
            }
            Object shaderInterface = getInterface.invoke(override);
            if (shaderInterface == null) {
                return false;
            }
            if (setRegionOffset == null) {
                setRegionOffset = shaderInterface.getClass().getMethod("setRegionOffset", float.class, float.class, float.class);
            }
            setRegionOffset.invoke(shaderInterface, x, y, z);
            return true;
        } catch (Throwable t) {
            unavailable = true;
            return false;
        }
    }
}

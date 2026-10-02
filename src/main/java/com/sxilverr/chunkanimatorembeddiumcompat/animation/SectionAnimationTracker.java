package com.sxilverr.chunkanimatorembeddiumcompat.animation;

import net.minecraft.client.Minecraft;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.sxilverr.chunkanimatorembeddiumcompat.config.ChunkAnimatorEmbeddiumCompatConfig.*;

public final class SectionAnimationTracker {

    private static final Map<Long, Long> startTimes = new ConcurrentHashMap<>();

    private SectionAnimationTracker() {}

    private static long key(int x, int y, int z) {
        return ((long) (x & 0x3FFFFF) << 42) | ((long) (y & 0xFFFFF) << 22) | (z & 0x3FFFFF);
    }

    public static void markBuilt(int x, int y, int z) {
        if (SPEC.isLoaded() && ENABLED.get() && !isNearPlayer(x, y, z)) {
            startTimes.putIfAbsent(key(x, y, z), System.currentTimeMillis());
        }
    }

    public static void clear(int x, int y, int z) {
        startTimes.remove(key(x, y, z));
    }

    public static boolean isAnimating(int x, int y, int z) {
        return offset(x, y, z) != null;
    }

    public static float[] offset(int x, int y, int z) {
        long key = key(x, y, z);
        Long start = startTimes.get(key);
        if (start == null) {
            return null;
        }
        float t = (System.currentTimeMillis() - start) / (float) ANIMATION_DURATION_MS.get();
        if (t >= 1.0f) {
            startTimes.remove(key);
            return null;
        }
        float remaining = 1.0f - EASING.get().apply(t);
        return new float[] {
                START_OFFSET_X.get().floatValue() * remaining,
                START_OFFSET_Y.get().floatValue() * remaining,
                START_OFFSET_Z.get().floatValue() * remaining
        };
    }

    private static boolean isNearPlayer(int x, int y, int z) {
        Minecraft mc = Minecraft.getInstance();
        double radius = PLAYER_RADIUS.get();
        return DISABLE_AROUND_PLAYER.get() && mc.player != null
                && mc.player.distanceToSqr((x << 4) + 8.0, (y << 4) + 8.0, (z << 4) + 8.0) <= radius * radius;
    }
}

package dev.sxilverr.chunkanimatorembeddiumcompat.animation;

import dev.sxilverr.chunkanimatorembeddiumcompat.config.ChunkAnimatorEmbeddiumCompatConfig;
import dev.sxilverr.chunkanimatorembeddiumcompat.config.EasingFunction;
import net.minecraft.client.Minecraft;

import java.util.concurrent.ConcurrentHashMap;

public final class SectionAnimationTracker {

    private static final ConcurrentHashMap<Long, Long> startTimes = new ConcurrentHashMap<>();

    private SectionAnimationTracker() {}

    private static long key(int x, int y, int z) {
        return ((long) (x & 0x3FFFFF) << 42) | ((long) (y & 0xFFFFF) << 22) | (long) (z & 0x3FFFFF);
    }

    public static void markBuilt(int chunkX, int chunkY, int chunkZ) {
        if (!isConfigReady() || !ChunkAnimatorEmbeddiumCompatConfig.ENABLED.get()) {
            return;
        }
        if (isInsidePlayerRadius(chunkX, chunkY, chunkZ)) {
            return;
        }
        startTimes.putIfAbsent(key(chunkX, chunkY, chunkZ), System.currentTimeMillis());
    }

    public static void clear(int chunkX, int chunkY, int chunkZ) {
        startTimes.remove(key(chunkX, chunkY, chunkZ));
    }

    public static boolean isAnimating(int chunkX, int chunkY, int chunkZ) {
        Long start = startTimes.get(key(chunkX, chunkY, chunkZ));
        if (start == null) {
            return false;
        }
        return System.currentTimeMillis() - start < durationMs();
    }

    public static float getOffsetX(int chunkX, int chunkY, int chunkZ) {
        return computeOffset(chunkX, chunkY, chunkZ, ChunkAnimatorEmbeddiumCompatConfig.START_OFFSET_X.get().floatValue());
    }

    public static float getOffsetY(int chunkX, int chunkY, int chunkZ) {
        return computeOffset(chunkX, chunkY, chunkZ, ChunkAnimatorEmbeddiumCompatConfig.START_OFFSET_Y.get().floatValue());
    }

    public static float getOffsetZ(int chunkX, int chunkY, int chunkZ) {
        return computeOffset(chunkX, chunkY, chunkZ, ChunkAnimatorEmbeddiumCompatConfig.START_OFFSET_Z.get().floatValue());
    }

    private static float computeOffset(int chunkX, int chunkY, int chunkZ, float startOffset) {
        if (startOffset == 0.0f) {
            return 0.0f;
        }
        long key = key(chunkX, chunkY, chunkZ);
        Long start = startTimes.get(key);
        if (start == null) {
            return 0.0f;
        }
        long elapsed = System.currentTimeMillis() - start;
        long duration = durationMs();
        if (elapsed >= duration) {
            startTimes.remove(key);
            return 0.0f;
        }
        float t = elapsed / (float) duration;
        EasingFunction easing = ChunkAnimatorEmbeddiumCompatConfig.EASING.get();
        float eased = easing.apply(t);
        return startOffset * (1.0f - eased);
    }

    private static long durationMs() {
        if (!isConfigReady()) {
            return 250L;
        }
        return ChunkAnimatorEmbeddiumCompatConfig.ANIMATION_DURATION_MS.get();
    }

    private static boolean isConfigReady() {
        return ChunkAnimatorEmbeddiumCompatConfig.SPEC.isLoaded();
    }

    private static boolean isInsidePlayerRadius(int chunkX, int chunkY, int chunkZ) {
        if (!ChunkAnimatorEmbeddiumCompatConfig.DISABLE_AROUND_PLAYER.get()) {
            return false;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return false;
        }
        int radius = ChunkAnimatorEmbeddiumCompatConfig.PLAYER_RADIUS.get();
        if (radius <= 0) {
            return false;
        }
        double cx = (chunkX << 4) + 8.0;
        double cy = (chunkY << 4) + 8.0;
        double cz = (chunkZ << 4) + 8.0;
        double dx = cx - mc.player.getX();
        double dy = cy - mc.player.getY();
        double dz = cz - mc.player.getZ();
        double distSq = dx * dx + dy * dy + dz * dz;
        return distSq <= (double) radius * radius;
    }
}

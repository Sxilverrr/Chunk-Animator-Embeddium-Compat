package com.sxilverr.chunkanimatorembeddiumcompat.mixin;

import net.minecraftforge.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class CompatMixinPlugin implements IMixinConfigPlugin {

    private static final String LOG_PREFIX = "[ChunkAnimatorEmbeddiumCompat] ";

    @Override
    public void onLoad(String mixinPackage) {
        String renderer = detectRenderer();
        if (renderer != null) {
            System.out.println(LOG_PREFIX + renderer + " detected; neutralized " + neutralizeChunkAnimator() + " ChunkAnimator mixin config(s).");
        }
    }

    private String detectRenderer() {
        try {
            LoadingModList list = LoadingModList.get();
            for (String id : new String[] {"xenon", "embeddium", "rubidium", "magnesium"}) {
                if (list != null && list.getModFileById(id) != null) {
                    return id;
                }
            }
        } catch (Throwable ignored) {}
        return getClass().getClassLoader().getResource("me/jellysquid/mods/sodium/client/SodiumClientMod.class") != null ? "sodium" : null;
    }

    private int neutralizeChunkAnimator() {
        int count = 0;
        try {
            for (Object handle : Mixins.getConfigs()) {
                String name = (String) handle.getClass().getMethod("getName").invoke(handle);
                if (name != null && name.startsWith("chunkanimator.")) {
                    clearMixinLists(handle.getClass().getMethod("getConfig").invoke(handle));
                    count++;
                }
            }
        } catch (Throwable t) {
            System.err.println(LOG_PREFIX + "Failed to neutralize ChunkAnimator configs: " + t);
        }
        return count;
    }

    private void clearMixinLists(Object config) throws IllegalAccessException {
        for (Field field : config.getClass().getDeclaredFields()) {
            if (field.getName().matches("mixinClasses(Client|Server)?|pendingMixins|mixins")) {
                field.setAccessible(true);
                Object value = field.get(config);
                if (value instanceof Collection) {
                    ((Collection<?>) value).clear();
                }
            }
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}

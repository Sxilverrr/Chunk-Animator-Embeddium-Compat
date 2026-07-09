package dev.sxilverr.chunkanimatorembeddiumcompat.mixin;

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
    private static final String[] MIXIN_LIST_FIELDS = {
            "mixinClasses", "mixinClassesClient", "mixinClassesServer", "pendingMixins", "mixins"
    };

    @Override
    public void onLoad(String mixinPackage) {
        String detected = detectCompatibleRenderer();
        if (detected == null) {
            return;
        }
        int neutralized = neutralizeChunkAnimatorConfigs();
        System.out.println(LOG_PREFIX + detected + " detected; neutralized " + neutralized + " ChunkAnimator mixin config(s).");
    }

    private String detectCompatibleRenderer() {
        try {
            LoadingModList list = LoadingModList.get();
            if (list != null) {
                if (list.getModFileById("xenon") != null) {
                    return "Xenon";
                }
                if (list.getModFileById("embeddium") != null) {
                    return "Embeddium";
                }
                if (list.getModFileById("rubidium") != null) {
                    return "Rubidium";
                }
                if (list.getModFileById("magnesium") != null) {
                    return "Magnesium";
                }
            }
        } catch (Throwable ignored) {}
        try {
            if (getClass().getClassLoader().getResource("me/jellysquid/mods/sodium/client/SodiumClientMod.class") != null) {
                return "Embeddium/Xenon";
            }
        } catch (Throwable ignored) {}
        return null;
    }

    private int neutralizeChunkAnimatorConfigs() {
        int count = 0;
        try {
            Set<?> configs = Mixins.getConfigs();
            for (Object handle : configs) {
                String name = (String) handle.getClass().getMethod("getName").invoke(handle);
                if (name == null || !name.startsWith("chunkanimator.")) {
                    continue;
                }
                Object mixinConfig = handle.getClass().getMethod("getConfig").invoke(handle);
                clearMixinLists(mixinConfig);
                count++;
            }
        } catch (Throwable t) {
            System.err.println(LOG_PREFIX + "Failed to neutralize ChunkAnimator configs: " + t);
            t.printStackTrace();
        }
        return count;
    }

    private void clearMixinLists(Object mixinConfig) {
        Class<?> cls = mixinConfig.getClass();
        for (String fieldName : MIXIN_LIST_FIELDS) {
            try {
                Field field = cls.getDeclaredField(fieldName);
                field.setAccessible(true);
                Object value = field.get(mixinConfig);
                if (value instanceof Collection) {
                    ((Collection<?>) value).clear();
                }
            } catch (NoSuchFieldException ignored) {
            } catch (Throwable t) {
                System.err.println(LOG_PREFIX + "Could not clear " + fieldName + ": " + t);
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

package dev.sxilverr.chunkanimatorembeddiumcompat;

import dev.sxilverr.chunkanimatorembeddiumcompat.config.ChunkAnimatorEmbeddiumCompatConfig;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(ChunkAnimatorEmbeddiumCompat.MOD_ID)
public final class ChunkAnimatorEmbeddiumCompat {

    public static final String MOD_ID = "chunkanimatorembeddiumcompat";

    public ChunkAnimatorEmbeddiumCompat() {
        ModLoadingContext context = ModLoadingContext.get();
        //? if >=1.18 {
        context.registerExtensionPoint(
                net.minecraftforge.fml.IExtensionPoint.DisplayTest.class,
                () -> new net.minecraftforge.fml.IExtensionPoint.DisplayTest(
                        () -> net.minecraftforge.network.NetworkConstants.IGNORESERVERONLY, (a, b) -> true)
        );
        //?} else {
        /*context.registerExtensionPoint(net.minecraftforge.fml.ExtensionPoint.DISPLAYTEST,
                () -> org.apache.commons.lang3.tuple.Pair.of(
                        () -> net.minecraftforge.fml.network.FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
        *///?}
        context.registerConfig(ModConfig.Type.CLIENT, ChunkAnimatorEmbeddiumCompatConfig.SPEC);
    }
}

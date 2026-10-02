package com.sxilverr.chunkanimatorembeddiumcompat.mixin.sodium;

import com.sxilverr.chunkanimatorembeddiumcompat.animation.SectionAnimationTracker;
import me.jellysquid.mods.sodium.client.gl.device.CommandList;
import me.jellysquid.mods.sodium.client.render.chunk.ChunkCameraContext;
import me.jellysquid.mods.sodium.client.render.chunk.backends.multidraw.ChunkDrawParamsVector;
import me.jellysquid.mods.sodium.client.render.chunk.backends.multidraw.MultidrawChunkRenderBackend;
import me.jellysquid.mods.sodium.client.render.chunk.backends.multidraw.MultidrawGraphicsState;
import me.jellysquid.mods.sodium.client.render.chunk.lists.ChunkRenderListIterator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = MultidrawChunkRenderBackend.class, remap = false)
public abstract class MultidrawChunkRenderBackendMixin {

    @Redirect(method = "setupDrawBatches",
            at = @At(value = "INVOKE", target = "Lme/jellysquid/mods/sodium/client/render/chunk/backends/multidraw/ChunkDrawParamsVector;pushChunkDrawParams(FFF)V"))
    private void chunkanimatorembeddiumcompat$offset(ChunkDrawParamsVector builder, float x, float y, float z,
            CommandList commandList, ChunkRenderListIterator<MultidrawGraphicsState> it, ChunkCameraContext camera) {
        MultidrawGraphicsState state = it.getGraphicsState();
        float[] offset = SectionAnimationTracker.offset((state.getX() + 8) >> 4, (state.getY() + 8) >> 4, (state.getZ() + 8) >> 4);
        if (offset == null) {
            builder.pushChunkDrawParams(x, y, z);
        } else {
            builder.pushChunkDrawParams(x + offset[0], y + offset[1], z + offset[2]);
        }
    }
}

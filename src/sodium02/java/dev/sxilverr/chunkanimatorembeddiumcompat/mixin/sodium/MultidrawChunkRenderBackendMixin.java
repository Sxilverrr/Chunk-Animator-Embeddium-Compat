package dev.sxilverr.chunkanimatorembeddiumcompat.mixin.sodium;

import dev.sxilverr.chunkanimatorembeddiumcompat.animation.SectionAnimationTracker;
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

    @Redirect(
            method = "setupDrawBatches",
            at = @At(
                    value = "INVOKE",
                    target = "Lme/jellysquid/mods/sodium/client/render/chunk/backends/multidraw/ChunkDrawParamsVector;pushChunkDrawParams(FFF)V"
            )
    )
    private void chunkanimatorembeddiumcompat$offset(
            ChunkDrawParamsVector builder, float x, float y, float z,
            CommandList commandList, ChunkRenderListIterator<MultidrawGraphicsState> it, ChunkCameraContext camera
    ) {
        MultidrawGraphicsState state = it.getGraphicsState();
        int cx = (state.getX() + 8) >> 4;
        int cy = (state.getY() + 8) >> 4;
        int cz = (state.getZ() + 8) >> 4;
        builder.pushChunkDrawParams(
                x + SectionAnimationTracker.getOffsetX(cx, cy, cz),
                y + SectionAnimationTracker.getOffsetY(cx, cy, cz),
                z + SectionAnimationTracker.getOffsetZ(cx, cy, cz)
        );
    }
}

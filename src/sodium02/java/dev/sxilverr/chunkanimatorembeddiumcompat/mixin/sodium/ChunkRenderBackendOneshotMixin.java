package dev.sxilverr.chunkanimatorembeddiumcompat.mixin.sodium;

import dev.sxilverr.chunkanimatorembeddiumcompat.animation.SectionAnimationTracker;
import me.jellysquid.mods.sodium.client.render.chunk.ChunkCameraContext;
import me.jellysquid.mods.sodium.client.render.chunk.backends.oneshot.ChunkOneshotGraphicsState;
import me.jellysquid.mods.sodium.client.render.chunk.backends.oneshot.ChunkRenderBackendOneshot;
import org.lwjgl.opengl.GL20C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.nio.FloatBuffer;

@Mixin(value = ChunkRenderBackendOneshot.class, remap = false)
public abstract class ChunkRenderBackendOneshotMixin {

    @Redirect(
            method = "prepareDrawBatch",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL20C;glVertexAttrib4fv(ILjava/nio/FloatBuffer;)V"
            )
    )
    private void chunkanimatorembeddiumcompat$offset(
            int index, FloatBuffer buffer,
            ChunkCameraContext camera, ChunkOneshotGraphicsState state
    ) {
        int cx = (state.getX() + 8) >> 4;
        int cy = (state.getY() + 8) >> 4;
        int cz = (state.getZ() + 8) >> 4;
        buffer.put(0, buffer.get(0) + SectionAnimationTracker.getOffsetX(cx, cy, cz));
        buffer.put(1, buffer.get(1) + SectionAnimationTracker.getOffsetY(cx, cy, cz));
        buffer.put(2, buffer.get(2) + SectionAnimationTracker.getOffsetZ(cx, cy, cz));
        GL20C.glVertexAttrib4fv(index, buffer);
    }
}

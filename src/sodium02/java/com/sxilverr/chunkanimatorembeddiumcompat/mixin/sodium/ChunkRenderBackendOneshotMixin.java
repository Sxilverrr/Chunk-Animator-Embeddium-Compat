package com.sxilverr.chunkanimatorembeddiumcompat.mixin.sodium;

import com.sxilverr.chunkanimatorembeddiumcompat.animation.SectionAnimationTracker;
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

    @Redirect(method = "prepareDrawBatch",
            at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL20C;glVertexAttrib4fv(ILjava/nio/FloatBuffer;)V"))
    private void chunkanimatorembeddiumcompat$offset(int index, FloatBuffer buffer, ChunkCameraContext camera, ChunkOneshotGraphicsState state) {
        float[] offset = SectionAnimationTracker.offset((state.getX() + 8) >> 4, (state.getY() + 8) >> 4, (state.getZ() + 8) >> 4);
        if (offset != null) {
            for (int i = 0; i < 3; i++) {
                buffer.put(i, buffer.get(i) + offset[i]);
            }
        }
        GL20C.glVertexAttrib4fv(index, buffer);
    }
}

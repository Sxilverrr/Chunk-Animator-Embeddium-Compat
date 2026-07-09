package dev.sxilverr.chunkanimatorembeddiumcompat.mixin.sodium;

import dev.sxilverr.chunkanimatorembeddiumcompat.access.ShaderAccess;
import me.jellysquid.mods.sodium.client.gl.shader.GlProgram;
import me.jellysquid.mods.sodium.client.render.chunk.ShaderChunkRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.shader.ChunkShaderInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = ShaderChunkRenderer.class, remap = false)
public abstract class ShaderChunkRendererMixin implements ShaderAccess {

    @Shadow
    protected GlProgram<ChunkShaderInterface> activeProgram;

    @Override
    public ChunkShaderInterface chunkanimatorembeddiumcompat$activeShader() {
        return this.activeProgram == null ? null : this.activeProgram.getInterface();
    }
}

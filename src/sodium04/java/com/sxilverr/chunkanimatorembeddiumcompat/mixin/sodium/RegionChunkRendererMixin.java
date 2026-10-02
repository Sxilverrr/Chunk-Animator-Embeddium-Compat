package com.sxilverr.chunkanimatorembeddiumcompat.mixin.sodium;

import com.google.common.collect.Iterables;
import com.sxilverr.chunkanimatorembeddiumcompat.access.IrisShaderAccess;
import com.sxilverr.chunkanimatorembeddiumcompat.animation.SectionAnimationTracker;
import me.jellysquid.mods.sodium.client.gl.device.CommandList;
import me.jellysquid.mods.sodium.client.gl.shader.GlProgram;
import me.jellysquid.mods.sodium.client.gl.tessellation.GlTessellation;
import me.jellysquid.mods.sodium.client.render.chunk.ChunkCameraContext;
import me.jellysquid.mods.sodium.client.render.chunk.ChunkRenderList;
import me.jellysquid.mods.sodium.client.render.chunk.ChunkRenderMatrices;
import me.jellysquid.mods.sodium.client.render.chunk.RegionChunkRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSection;
import me.jellysquid.mods.sodium.client.render.chunk.passes.BlockRenderPass;
import me.jellysquid.mods.sodium.client.render.chunk.region.RenderRegion;
import me.jellysquid.mods.sodium.client.render.chunk.shader.ChunkShaderInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Mixin(value = RegionChunkRenderer.class, remap = false)
public abstract class RegionChunkRendererMixin {

    @Shadow
    private boolean buildDrawBatches(List<RenderSection> sections, BlockRenderPass pass, ChunkCameraContext camera) {
        throw new AssertionError();
    }

    @Shadow
    private void executeDrawBatches(CommandList commandList, GlTessellation tessellation) {
        throw new AssertionError();
    }

    @Shadow
    private GlTessellation createTessellationForRegion(CommandList commandList, RenderRegion.RenderRegionArenas arenas, BlockRenderPass pass) {
        throw new AssertionError();
    }

    @Shadow
    private static Iterable<RenderSection> sortedChunks(List<RenderSection> chunks, boolean translucent) {
        throw new AssertionError();
    }

    @Unique
    private boolean chunkanimatorembeddiumcompat$animationPass;

    @Redirect(method = "buildDrawBatches",
            at = @At(value = "INVOKE", target = "Lme/jellysquid/mods/sodium/client/render/chunk/RegionChunkRenderer;sortedChunks(Ljava/util/List;Z)Ljava/lang/Iterable;"))
    private Iterable<RenderSection> chunkanimatorembeddiumcompat$filterAnimating(List<RenderSection> chunks, boolean translucent) {
        Iterable<RenderSection> sorted = sortedChunks(chunks, translucent);
        return this.chunkanimatorembeddiumcompat$animationPass ? sorted
                : Iterables.filter(sorted, s -> !SectionAnimationTracker.isAnimating(s.getChunkX(), s.getChunkY(), s.getChunkZ()));
    }

    @Inject(method = "render",
            at = @At(value = "INVOKE", target = "Lme/jellysquid/mods/sodium/client/render/chunk/ShaderChunkRenderer;end()V"))
    private void chunkanimatorembeddiumcompat$drawAnimating(ChunkRenderMatrices matrices, CommandList commandList,
            ChunkRenderList list, BlockRenderPass pass, ChunkCameraContext camera, CallbackInfo ci) {
        GlProgram<ChunkShaderInterface> program = ((ShaderChunkRendererAccessor) this).getActiveProgram();
        this.chunkanimatorembeddiumcompat$animationPass = true;
        try {
            for (Map.Entry<RenderRegion, List<RenderSection>> entry : list.sorted(pass.isTranslucent())) {
                RenderRegion region = entry.getKey();
                for (RenderSection section : entry.getValue()) {
                    float[] offset = SectionAnimationTracker.offset(section.getChunkX(), section.getChunkY(), section.getChunkZ());
                    if (offset == null || !this.buildDrawBatches(Collections.singletonList(section), pass, camera)) {
                        continue;
                    }
                    float x = region.getOriginX() - camera.blockX - camera.deltaX + offset[0];
                    float y = region.getOriginY() - camera.blockY - camera.deltaY + offset[1];
                    float z = region.getOriginZ() - camera.blockZ - camera.deltaZ + offset[2];
                    if (program != null) {
                        program.getInterface().setRegionOffset(x, y, z);
                    } else if (!IrisShaderAccess.applyRegionOffset(this, x, y, z)) {
                        continue;
                    }
                    this.executeDrawBatches(commandList, this.createTessellationForRegion(commandList, region.getArenas(), pass));
                }
            }
        } finally {
            this.chunkanimatorembeddiumcompat$animationPass = false;
        }
    }
}

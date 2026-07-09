package dev.sxilverr.chunkanimatorembeddiumcompat.mixin.sodium;

import dev.sxilverr.chunkanimatorembeddiumcompat.access.IrisShaderAccess;
import dev.sxilverr.chunkanimatorembeddiumcompat.access.ShaderAccess;
import dev.sxilverr.chunkanimatorembeddiumcompat.animation.SectionAnimationTracker;
import me.jellysquid.mods.sodium.client.gl.device.CommandList;
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

import java.util.ArrayList;
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

    @Redirect(
            method = "buildDrawBatches",
            at = @At(
                    value = "INVOKE",
                    target = "Lme/jellysquid/mods/sodium/client/render/chunk/RegionChunkRenderer;sortedChunks(Ljava/util/List;Z)Ljava/lang/Iterable;"
            )
    )
    private Iterable<RenderSection> chunkanimatorembeddiumcompat$filterAnimating(List<RenderSection> chunks, boolean translucent) {
        Iterable<RenderSection> base = sortedChunks(chunks, translucent);
        if (this.chunkanimatorembeddiumcompat$animationPass) {
            return base;
        }
        List<RenderSection> filtered = new ArrayList<>();
        for (RenderSection section : base) {
            if (!SectionAnimationTracker.isAnimating(section.getChunkX(), section.getChunkY(), section.getChunkZ())) {
                filtered.add(section);
            }
        }
        return filtered;
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lme/jellysquid/mods/sodium/client/render/chunk/ShaderChunkRenderer;end()V"
            )
    )
    private void chunkanimatorembeddiumcompat$drawAnimating(
            ChunkRenderMatrices matrices,
            CommandList commandList,
            ChunkRenderList list,
            BlockRenderPass pass,
            ChunkCameraContext camera,
            CallbackInfo ci
    ) {
        ChunkShaderInterface shader = ((ShaderAccess) (Object) this).chunkanimatorembeddiumcompat$activeShader();
        boolean translucent = pass.isTranslucent();
        this.chunkanimatorembeddiumcompat$animationPass = true;
        try {
            for (Map.Entry<RenderRegion, List<RenderSection>> entry : list.sorted(translucent)) {
                RenderRegion region = entry.getKey();
                for (RenderSection section : entry.getValue()) {
                    int cx = section.getChunkX();
                    int cy = section.getChunkY();
                    int cz = section.getChunkZ();
                    float offsetX = SectionAnimationTracker.getOffsetX(cx, cy, cz);
                    float offsetY = SectionAnimationTracker.getOffsetY(cx, cy, cz);
                    float offsetZ = SectionAnimationTracker.getOffsetZ(cx, cy, cz);
                    if (offsetX == 0.0f && offsetY == 0.0f && offsetZ == 0.0f) {
                        continue;
                    }
                    if (!this.buildDrawBatches(Collections.singletonList(section), pass, camera)) {
                        continue;
                    }
                    float regionX = (region.getOriginX() - camera.blockX) - camera.deltaX + offsetX;
                    float regionY = (region.getOriginY() - camera.blockY) - camera.deltaY + offsetY;
                    float regionZ = (region.getOriginZ() - camera.blockZ) - camera.deltaZ + offsetZ;
                    if (shader != null) {
                        shader.setRegionOffset(regionX, regionY, regionZ);
                    } else if (!IrisShaderAccess.applyRegionOffset(this, regionX, regionY, regionZ)) {
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

package com.sxilverr.chunkanimatorembeddiumcompat.mixin.sodium;

import com.sxilverr.chunkanimatorembeddiumcompat.animation.AnimatingSectionSkippingIterator;
import com.sxilverr.chunkanimatorembeddiumcompat.animation.SectionAnimationTracker;
import me.jellysquid.mods.sodium.client.gl.device.CommandList;
import me.jellysquid.mods.sodium.client.gl.device.MultiDrawBatch;
import me.jellysquid.mods.sodium.client.gl.tessellation.GlTessellation;
import me.jellysquid.mods.sodium.client.render.chunk.ChunkRenderMatrices;
import me.jellysquid.mods.sodium.client.render.chunk.DefaultChunkRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.LocalSectionIndex;
import me.jellysquid.mods.sodium.client.render.chunk.data.SectionRenderDataStorage;
import me.jellysquid.mods.sodium.client.render.chunk.data.SectionRenderDataUnsafe;
import me.jellysquid.mods.sodium.client.render.chunk.lists.ChunkRenderList;
import me.jellysquid.mods.sodium.client.render.chunk.lists.ChunkRenderListIterable;
import me.jellysquid.mods.sodium.client.render.chunk.region.RenderRegion;
import me.jellysquid.mods.sodium.client.render.chunk.shader.ChunkShaderInterface;
import me.jellysquid.mods.sodium.client.render.chunk.terrain.TerrainRenderPass;
import me.jellysquid.mods.sodium.client.render.viewport.CameraTransform;
import me.jellysquid.mods.sodium.client.util.iterator.ByteIterator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;

@Mixin(value = DefaultChunkRenderer.class, remap = false)
public abstract class DefaultChunkRendererMixin {

    @Shadow
    private MultiDrawBatch batch;

    @Shadow
    private static void addDrawCommands(MultiDrawBatch batch, long pMeshData, int mask, int indexPointerMask) {
        throw new AssertionError();
    }

    @Shadow
    private static void executeDrawBatch(CommandList commandList, GlTessellation tessellation, MultiDrawBatch batch) {
        throw new AssertionError();
    }

    @Redirect(method = "fillCommandBuffer",
            at = @At(value = "INVOKE", target = "Lme/jellysquid/mods/sodium/client/render/chunk/lists/ChunkRenderList;sectionsWithGeometryIterator(Z)Lme/jellysquid/mods/sodium/client/util/iterator/ByteIterator;"))
    private static ByteIterator chunkanimatorembeddiumcompat$filterAnimating(ChunkRenderList list, boolean reverse) {
        ByteIterator sections = list.sectionsWithGeometryIterator(reverse);
        return sections == null ? null : new AnimatingSectionSkippingIterator(sections, list.getRegion());
    }

    @Inject(method = "render",
            at = @At(value = "INVOKE", target = "Lme/jellysquid/mods/sodium/client/render/chunk/DefaultChunkRenderer;executeDrawBatch(Lme/jellysquid/mods/sodium/client/gl/device/CommandList;Lme/jellysquid/mods/sodium/client/gl/tessellation/GlTessellation;Lme/jellysquid/mods/sodium/client/gl/device/MultiDrawBatch;)V",
                    shift = At.Shift.AFTER),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void chunkanimatorembeddiumcompat$drawAnimatingSections(ChunkRenderMatrices matrices, CommandList commandList,
            ChunkRenderListIterable renderLists, TerrainRenderPass renderPass, CameraTransform camera, CallbackInfo ci,
            boolean useBlockFaceCulling, ChunkShaderInterface shader, Iterator<ChunkRenderList> iterator,
            ChunkRenderList renderList, RenderRegion region, SectionRenderDataStorage storage, GlTessellation tessellation) {
        ByteIterator sections = renderList.sectionsWithGeometryIterator(renderPass.isReverseOrder());
        if (sections == null) {
            return;
        }
        int indexPointerMask = renderPass.isSorted() ? 0xFFFFFFFF : 0;
        float baseX = region.getOriginX() - camera.intX - camera.fracX;
        float baseY = region.getOriginY() - camera.intY - camera.fracY;
        float baseZ = region.getOriginZ() - camera.intZ - camera.fracZ;
        while (sections.hasNext()) {
            int index = sections.nextByteAsInt();
            float[] offset = SectionAnimationTracker.offset(region.getChunkX() + LocalSectionIndex.unpackX(index),
                    region.getChunkY() + LocalSectionIndex.unpackY(index), region.getChunkZ() + LocalSectionIndex.unpackZ(index));
            long pMeshData = storage.getDataPointer(index);
            int sliceMask = SectionRenderDataUnsafe.getSliceMask(pMeshData);
            if (offset == null || sliceMask == 0) {
                continue;
            }
            this.batch.clear();
            addDrawCommands(this.batch, pMeshData, sliceMask, indexPointerMask);
            shader.setRegionOffset(baseX + offset[0], baseY + offset[1], baseZ + offset[2]);
            executeDrawBatch(commandList, tessellation, this.batch);
        }
        shader.setRegionOffset(baseX, baseY, baseZ);
    }
}

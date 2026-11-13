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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DefaultChunkRenderer.class, remap = false)
public abstract class DefaultChunkRendererMixin {

    @Shadow
    private MultiDrawBatch batch;

    @Unique
    private ChunkShaderInterface chunkanimatorembeddiumcompat$shader;

    @Unique
    private RenderRegion chunkanimatorembeddiumcompat$region;

    @Unique
    private GlTessellation chunkanimatorembeddiumcompat$tessellation;

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

    @ModifyArg(method = "render",
            at = @At(value = "INVOKE", target = "Lme/jellysquid/mods/sodium/client/render/chunk/DefaultChunkRenderer;setModelMatrixUniforms(Lme/jellysquid/mods/sodium/client/render/chunk/shader/ChunkShaderInterface;Lme/jellysquid/mods/sodium/client/render/chunk/region/RenderRegion;Lme/jellysquid/mods/sodium/client/render/viewport/CameraTransform;)V"),
            index = 1)
    private RenderRegion chunkanimatorembeddiumcompat$captureRegion(ChunkShaderInterface shader, RenderRegion region, CameraTransform camera) {
        this.chunkanimatorembeddiumcompat$shader = shader;
        this.chunkanimatorembeddiumcompat$region = region;
        return region;
    }

    @ModifyArg(method = "render",
            at = @At(value = "INVOKE", target = "Lme/jellysquid/mods/sodium/client/render/chunk/DefaultChunkRenderer;executeDrawBatch(Lme/jellysquid/mods/sodium/client/gl/device/CommandList;Lme/jellysquid/mods/sodium/client/gl/tessellation/GlTessellation;Lme/jellysquid/mods/sodium/client/gl/device/MultiDrawBatch;)V"),
            index = 1)
    private GlTessellation chunkanimatorembeddiumcompat$captureTessellation(GlTessellation tessellation) {
        this.chunkanimatorembeddiumcompat$tessellation = tessellation;
        return tessellation;
    }

    @Inject(method = "render",
            at = @At(value = "INVOKE", target = "Lme/jellysquid/mods/sodium/client/render/chunk/DefaultChunkRenderer;executeDrawBatch(Lme/jellysquid/mods/sodium/client/gl/device/CommandList;Lme/jellysquid/mods/sodium/client/gl/tessellation/GlTessellation;Lme/jellysquid/mods/sodium/client/gl/device/MultiDrawBatch;)V",
                    shift = At.Shift.AFTER))
    private void chunkanimatorembeddiumcompat$drawAnimatingSections(ChunkRenderMatrices matrices, CommandList commandList,
            ChunkRenderListIterable renderLists, TerrainRenderPass renderPass, CameraTransform camera, CallbackInfo ci) {
        ChunkShaderInterface shader = this.chunkanimatorembeddiumcompat$shader;
        RenderRegion region = this.chunkanimatorembeddiumcompat$region;
        GlTessellation tessellation = this.chunkanimatorembeddiumcompat$tessellation;
        SectionRenderDataStorage storage = region.getStorage(renderPass);
        ByteIterator sections = region.getRenderList().sectionsWithGeometryIterator(renderPass.isReverseOrder());
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

package com.sxilverr.chunkanimatorembeddiumcompat.mixin.sodium;

import com.sxilverr.chunkanimatorembeddiumcompat.animation.SectionAnimationTracker;
import me.jellysquid.mods.sodium.client.render.chunk.ChunkRenderContainer;
import me.jellysquid.mods.sodium.client.render.chunk.data.ChunkRenderData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ChunkRenderContainer.class, remap = false)
public abstract class ChunkRenderContainerMixin {

    @Inject(method = "setData", at = @At("HEAD"))
    private void chunkanimatorembeddiumcompat$markBuilt(ChunkRenderData info, CallbackInfo ci) {
        ChunkRenderContainer<?> self = (ChunkRenderContainer<?>) (Object) this;
        if (info != null && self.getData() == ChunkRenderData.ABSENT) {
            SectionAnimationTracker.markBuilt(self.getChunkX(), self.getChunkY(), self.getChunkZ());
        }
    }

    @Inject(method = "delete", at = @At("HEAD"))
    private void chunkanimatorembeddiumcompat$clearAnimation(CallbackInfo ci) {
        ChunkRenderContainer<?> self = (ChunkRenderContainer<?>) (Object) this;
        SectionAnimationTracker.clear(self.getChunkX(), self.getChunkY(), self.getChunkZ());
    }
}

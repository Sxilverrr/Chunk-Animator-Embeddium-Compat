package dev.sxilverr.chunkanimatorembeddiumcompat.mixin.sodium;

import dev.sxilverr.chunkanimatorembeddiumcompat.animation.SectionAnimationTracker;
import me.jellysquid.mods.sodium.client.render.chunk.ChunkRenderContainer;
import me.jellysquid.mods.sodium.client.render.chunk.data.ChunkRenderData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ChunkRenderContainer.class, remap = false)
public abstract class ChunkRenderContainerMixin {

    @Shadow
    public abstract ChunkRenderData getData();

    @Shadow
    public abstract int getChunkX();

    @Shadow
    public abstract int getChunkY();

    @Shadow
    public abstract int getChunkZ();

    @Inject(method = "setData", at = @At("HEAD"))
    private void chunkanimatorembeddiumcompat$markBuilt(ChunkRenderData info, CallbackInfo ci) {
        if (info == null || this.getData() != ChunkRenderData.ABSENT) {
            return;
        }
        SectionAnimationTracker.markBuilt(this.getChunkX(), this.getChunkY(), this.getChunkZ());
    }

    @Inject(method = "delete", at = @At("HEAD"))
    private void chunkanimatorembeddiumcompat$clearAnimation(CallbackInfo ci) {
        SectionAnimationTracker.clear(this.getChunkX(), this.getChunkY(), this.getChunkZ());
    }
}

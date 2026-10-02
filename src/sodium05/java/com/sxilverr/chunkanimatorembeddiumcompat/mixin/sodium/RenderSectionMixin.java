package com.sxilverr.chunkanimatorembeddiumcompat.mixin.sodium;

import com.sxilverr.chunkanimatorembeddiumcompat.animation.SectionAnimationTracker;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSection;
import me.jellysquid.mods.sodium.client.render.chunk.data.BuiltSectionInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderSection.class, remap = false)
public abstract class RenderSectionMixin {

    @Shadow
    private boolean built;

    @Inject(method = "setInfo", at = @At("HEAD"))
    private void chunkanimatorembeddiumcompat$markBuilt(BuiltSectionInfo info, CallbackInfo ci) {
        RenderSection self = (RenderSection) (Object) this;
        if (info != null && !this.built) {
            SectionAnimationTracker.markBuilt(self.getChunkX(), self.getChunkY(), self.getChunkZ());
        }
    }

    @Inject(method = "delete", at = @At("HEAD"))
    private void chunkanimatorembeddiumcompat$clearAnimation(CallbackInfo ci) {
        RenderSection self = (RenderSection) (Object) this;
        SectionAnimationTracker.clear(self.getChunkX(), self.getChunkY(), self.getChunkZ());
    }
}

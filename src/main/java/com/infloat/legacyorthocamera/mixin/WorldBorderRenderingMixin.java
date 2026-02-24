package com.infloat.legacyorthocamera.mixin;

import com.infloat.legacyorthocamera.client.LegacyOrthoCamera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldBorderRenderingMixin {

    @Inject(
            method = "renderWorldBorder",
            at = @At("HEAD"),
            cancellable = true
    )
    private void renderWorldBorder(Entity entity, float tickDelta, CallbackInfo ci) {
        if (LegacyOrthoCamera.isEnabled() && LegacyOrthoCamera.CONFIG.hide_world_border) {
            ci.cancel();
        }
    }
}

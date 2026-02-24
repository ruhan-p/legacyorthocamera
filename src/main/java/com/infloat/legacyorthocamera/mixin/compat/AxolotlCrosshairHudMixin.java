package com.infloat.legacyorthocamera.mixin.compat;

import com.infloat.legacyorthocamera.client.LegacyOrthoCamera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(
        targets = {
                "io.github.axolotlclient.modules.hud.gui.hud.vanilla.CrosshairHud",
                "io.github.axolotlclient.modules.hud.gui.hud.CrosshairHud"
        },
        remap = false
)
public abstract class AxolotlCrosshairHudMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true, remap = false)
    private void legacyorthocamera$hideAxolotlCrosshair(CallbackInfo ci) {
        if (LegacyOrthoCamera.isEnabled() && LegacyOrthoCamera.CONFIG.hide_cursor_in_ortho) {
            ci.cancel();
        }
    }
}

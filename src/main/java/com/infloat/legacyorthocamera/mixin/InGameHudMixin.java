package com.infloat.legacyorthocamera.mixin;

import com.infloat.legacyorthocamera.client.LegacyOrthoCamera;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Inject(method = "showCrosshair", at = @At("HEAD"), cancellable = true)
    private void hideCursorInOrtho(CallbackInfoReturnable<Boolean> cir) {
        if (LegacyOrthoCamera.isEnabled() && LegacyOrthoCamera.CONFIG.hide_cursor_in_ortho) {
            cir.setReturnValue(false);
        }
    }
}

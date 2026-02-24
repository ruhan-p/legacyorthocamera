package com.infloat.legacyorthocamera.mixin.compat;

import com.infloat.legacyorthocamera.client.LegacyOrthoCamera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "io.github.axolotlclient.modules.freelook.Freelook", remap = false)
public abstract class AxolotlFreelookMixin {

    @Inject(method = "yaw(F)F", at = @At("HEAD"), cancellable = true, remap = false)
    private void legacyorthocamera$fixedYaw(float defaultValue, CallbackInfoReturnable<Float> cir) {
        if (LegacyOrthoCamera.isEnabled() && LegacyOrthoCamera.CONFIG.fixed) {
            cir.setReturnValue(LegacyOrthoCamera.CONFIG.fixed_yaw - 180.0F);
        }
    }

    @Inject(method = "pitch(F)F", at = @At("HEAD"), cancellable = true, remap = false)
    private void legacyorthocamera$fixedPitch(float defaultValue, CallbackInfoReturnable<Float> cir) {
        if (LegacyOrthoCamera.isEnabled() && LegacyOrthoCamera.CONFIG.fixed) {
            cir.setReturnValue(LegacyOrthoCamera.CONFIG.fixed_pitch);
        }
    }
}

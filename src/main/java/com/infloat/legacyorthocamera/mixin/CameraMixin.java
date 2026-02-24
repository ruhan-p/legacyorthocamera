package com.infloat.legacyorthocamera.mixin;

import com.infloat.legacyorthocamera.client.LegacyOrthoCamera;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GameRenderer.class)
public abstract class CameraMixin {

    @ModifyArg(
            method = "transformCamera",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/platform/GlStateManager;translate(FFF)V",
                    ordinal = 2
            ),
            index = 0
    )
    private float moveByHeadX(float value) {
        return LegacyOrthoCamera.isEnabled() ? 0.0F : value;
    }

    @ModifyArg(
            method = "transformCamera",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/platform/GlStateManager;translate(FFF)V",
                    ordinal = 2
            ),
            index = 2
    )
    private float moveByHeadZ(float value) {
        return LegacyOrthoCamera.isEnabled() ? 0.0F : value;
    }

}

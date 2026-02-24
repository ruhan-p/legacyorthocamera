package com.infloat.legacyorthocamera.mixin;

import com.infloat.legacyorthocamera.client.LegacyOrthoCamera;
import net.minecraft.entity.Entity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.WorldRenderer;
import org.lwjgl.util.glu.Project;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Redirect(
            method = "renderClouds(Lnet/minecraft/client/render/WorldRenderer;FI)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/WorldRenderer;renderClouds(FI)V"
            ),
            require = 0
    )
    private void hideCloudsInOrtho(WorldRenderer worldRenderer, float tickDelta, int anaglyphFilter) {
        if (!LegacyOrthoCamera.isEnabled() || !LegacyOrthoCamera.CONFIG.hide_clouds_in_ortho) {
            worldRenderer.renderClouds(tickDelta, anaglyphFilter);
        }
    }

    @Redirect(
            method = {
                    "setupCamera(FI)V",
                    "renderHand(FI)V",
                    "renderWorld(IFJ)V",
                    "renderWorld(FJ)V",
                    "renderClouds(Lnet/minecraft/client/render/WorldRenderer;FI)V"
            },
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"
            ),
            require = 0
    )
    private void redirectPerspective(float fov, float aspect, float near, float far) {
        if (LegacyOrthoCamera.isEnabled()) {
            LegacyOrthoCamera.applyOrthoProjection(aspect, 0.0F);
            return;
        }
        Project.gluPerspective(fov, aspect, near, far);
    }

    @Redirect(
            method = "transformCamera(F)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/entity/Entity;yaw:F"
            ),
            require = 0
    )
    private float redirectYaw(Entity entity, float tickDelta) {
        if (isFixedOrthoEnabled()) {
            return LegacyOrthoCamera.CONFIG.getFixedYaw(tickDelta) - 180.0F;
        }
        return entity.yaw;
    }

    @Redirect(
            method = "transformCamera(F)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/entity/Entity;prevYaw:F"
            ),
            require = 0
    )
    private float redirectPrevYaw(Entity entity, float tickDelta) {
        if (isFixedOrthoEnabled()) {
            return LegacyOrthoCamera.CONFIG.getFixedYaw(tickDelta) - 180.0F;
        }
        return entity.prevYaw;
    }

    @Redirect(
            method = "transformCamera(F)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/entity/Entity;pitch:F"
            ),
            require = 0
    )
    private float redirectPitch(Entity entity, float tickDelta) {
        if (isFixedOrthoEnabled()) {
            return LegacyOrthoCamera.CONFIG.getFixedPitch(tickDelta);
        }
        return entity.pitch;
    }

    @Redirect(
            method = "transformCamera(F)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/entity/Entity;prevPitch:F"
            ),
            require = 0
    )
    private float redirectPrevPitch(Entity entity, float tickDelta) {
        if (isFixedOrthoEnabled()) {
            return LegacyOrthoCamera.CONFIG.getFixedPitch(tickDelta);
        }
        return entity.prevPitch;
    }

    private static boolean isFixedOrthoEnabled() {
        return LegacyOrthoCamera.isEnabled() && LegacyOrthoCamera.CONFIG.fixed;
    }
}

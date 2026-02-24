package com.infloat.legacyorthocamera.client.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModConfig extends JsonConfig {

    public static final float MIN_SCALE = 0.01F;
    public static final float MAX_SCALE = 10000.0F;
    public static final float MIN_DISTANCE_LIMIT = -100000.0F;
    public static final float MAX_DISTANCE_LIMIT = 100000.0F;
    private static final int THIRD_PERSON_BACK = 1;

    private transient boolean dirty;
    private transient float prevScaleX;
    private transient float prevScaleY;
    private transient float prevFixedYaw;
    private transient float prevFixedPitch;
    private transient int prevPerspective = -1;

    public boolean enabled = false;
    public boolean save_enabled_state = false;
    public boolean hide_world_border = false;
    public boolean hide_clouds_in_ortho = false;
    public boolean hide_cursor_in_ortho = false;
    public float scale_x = 3.0F;
    public float scale_y = 3.0F;
    public float min_distance = -1000.0F;
    public float max_distance = 1000.0F;
    public boolean fixed = false;
    public float fixed_yaw = 0.0F;
    public float fixed_pitch = 0.0F;
    public float fixed_rotate_speed_y = 3.0F;
    public float fixed_rotate_speed_x = 3.0F;
    public boolean auto_third_person = true;

    public ModConfig(String path, String defaultPath) {
        super(path, defaultPath);
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void tick() {
        prevScaleX = scale_x;
        prevScaleY = scale_y;
        prevFixedYaw = fixed_yaw;
        prevFixedPitch = fixed_pitch;
    }

    public float getScaleX(float delta) {
        return lerp(delta, prevScaleX, scale_x);
    }

    public float getScaleY(float delta) {
        return lerp(delta, prevScaleY, scale_y);
    }

    public float getFixedYaw(float delta) {
        return rotLerp(delta, prevFixedYaw, fixed_yaw);
    }

    public float getFixedPitch(float delta) {
        return rotLerp(delta, prevFixedPitch, fixed_pitch);
    }

    public void setScaleX(float scale) {
        scale = MathHelper.clamp(scale, MIN_SCALE, MAX_SCALE);
        if (scale != scale_x) {
            scale_x = scale;
            setDirty(true);
        }
    }

    public void setScaleY(float scale) {
        scale = MathHelper.clamp(scale, MIN_SCALE, MAX_SCALE);
        if (scale != scale_y) {
            scale_y = scale;
            setDirty(true);
        }
    }

    public void setFixedYaw(float yaw) {
        if (yaw < 0.0F) {
            yaw = 360.0F + yaw;
        }
        yaw = yaw % 360.0F;
        if (yaw != fixed_yaw) {
            fixed_yaw = yaw;
            setDirty(true);
        }
    }

    public void setFixedPitch(float pitch) {
        pitch = MathHelper.clamp(pitch, -90.0F, 90.0F);
        if (pitch != fixed_pitch) {
            fixed_pitch = pitch;
            setDirty(true);
        }
    }

    public void setMinDistance(float distance) {
        float clamped = MathHelper.clamp(distance, MIN_DISTANCE_LIMIT, MAX_DISTANCE_LIMIT);
        clamped = Math.min(clamped, max_distance - 1.0F);
        if (clamped != min_distance) {
            min_distance = clamped;
            setDirty(true);
        }
    }

    public void setMaxDistance(float distance) {
        float clamped = MathHelper.clamp(distance, MIN_DISTANCE_LIMIT, MAX_DISTANCE_LIMIT);
        clamped = Math.max(clamped, min_distance + 1.0F);
        if (clamped != max_distance) {
            max_distance = clamped;
            setDirty(true);
        }
    }

    public void setFixed(boolean fixed) {
        this.fixed = fixed;
        if (fixed) {
            Entity entity = MinecraftClient.getInstance().getCameraEntity();
            if (entity != null) {
                setFixedYaw(entity.yaw + 180.0F);
                prevFixedYaw = fixed_yaw;
                setFixedPitch(entity.pitch);
                prevFixedPitch = fixed_pitch;
            }
        }
        setDirty(true);
    }

    public void toggle() {
        enabled = !enabled;
        if (auto_third_person) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (enabled) {
                prevPerspective = client.options.perspective;
                client.options.perspective = THIRD_PERSON_BACK;
            } else if (prevPerspective >= 0) {
                client.options.perspective = prevPerspective;
            }
        }
        setDirty(true);
    }

    public void toggleHideCloudsInOrtho() {
        hide_clouds_in_ortho = !hide_clouds_in_ortho;
        setDirty(true);
    }

    public void toggleHideCursorInOrtho() {
        hide_cursor_in_ortho = !hide_cursor_in_ortho;
        setDirty(true);
    }

    private static float lerp(float delta, float start, float end) {
        return start + delta * (end - start);
    }

    private static float rotLerp(float delta, float start, float end) {
        return start + delta * MathHelper.wrapDegrees(end - start);
    }
}

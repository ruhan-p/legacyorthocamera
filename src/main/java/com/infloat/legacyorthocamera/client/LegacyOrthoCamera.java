package com.infloat.legacyorthocamera.client;

import com.infloat.legacyorthocamera.client.config.ModConfig;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.ClientModInitializer;
import net.legacyfabric.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.legacyfabric.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.legacyfabric.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.LiteralText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

public class LegacyOrthoCamera implements ClientModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("OrthoCamera");
    public static final ModConfig CONFIG = new ModConfig("config/orthocamera.json", "assets/orthocamera/default_config.json");

    private static final String KEY_CATEGORY = "Legacy OrthoCamera";
    private static final KeyBinding TOGGLE_KEY = createKeybinding("toggle", Keyboard.KEY_NUMPAD4);
    private static final KeyBinding SCALE_INCREASE_KEY = createKeybinding("zoom_in", Keyboard.KEY_SUBTRACT);
    private static final KeyBinding SCALE_DECREASE_KEY = createKeybinding("zoom_out", Keyboard.KEY_ADD);
    private static final KeyBinding FIX_CAMERA_KEY = createKeybinding("fix_camera", Keyboard.KEY_MULTIPLY);
    private static final KeyBinding FIXED_CAMERA_ROTATE_UP_KEY = createKeybinding("rotate_up", Keyboard.KEY_NONE);
    private static final KeyBinding FIXED_CAMERA_ROTATE_DOWN_KEY = createKeybinding("rotate_down", Keyboard.KEY_NONE);
    private static final KeyBinding FIXED_CAMERA_ROTATE_LEFT_KEY = createKeybinding("rotate_left", Keyboard.KEY_NONE);
    private static final KeyBinding FIXED_CAMERA_ROTATE_RIGHT_KEY = createKeybinding("rotate_right", Keyboard.KEY_NONE);
    private static final KeyBinding TOGGLE_ORTHO_CLOUDS_KEY = createKeybinding("toggle_clouds", Keyboard.KEY_NUMPAD5);
    private static final KeyBinding TOGGLE_ORTHO_CURSOR_KEY = createKeybinding("toggle_cursor", Keyboard.KEY_NUMPAD6);

    private static final float SCALE_STEP_PER_TICK = 0.05F;

    @Override
    public void onInitializeClient() {
        CONFIG.loadOrCreate();
        CONFIG.enabled &= CONFIG.save_enabled_state;
        KeyBindingHelper.registerKeyBinding(TOGGLE_KEY);
        KeyBindingHelper.registerKeyBinding(SCALE_INCREASE_KEY);
        KeyBindingHelper.registerKeyBinding(SCALE_DECREASE_KEY);
        KeyBindingHelper.registerKeyBinding(FIX_CAMERA_KEY);
        KeyBindingHelper.registerKeyBinding(FIXED_CAMERA_ROTATE_UP_KEY);
        KeyBindingHelper.registerKeyBinding(FIXED_CAMERA_ROTATE_DOWN_KEY);
        KeyBindingHelper.registerKeyBinding(FIXED_CAMERA_ROTATE_LEFT_KEY);
        KeyBindingHelper.registerKeyBinding(FIXED_CAMERA_ROTATE_RIGHT_KEY);
        KeyBindingHelper.registerKeyBinding(TOGGLE_ORTHO_CLOUDS_KEY);
        KeyBindingHelper.registerKeyBinding(TOGGLE_ORTHO_CURSOR_KEY);
        ClientTickEvents.START_CLIENT_TICK.register(c -> CONFIG.tick());
        ClientTickEvents.END_CLIENT_TICK.register(this::handleInput);
        ClientLifecycleEvents.CLIENT_STOPPING.register(this::onClientStopping);
    }

    private void handleInput(MinecraftClient client) {
        while (TOGGLE_KEY.wasPressed()) {
            CONFIG.toggle();
        }

        if (CONFIG.enabled) {
            if (SCALE_INCREASE_KEY.isPressed() && !SCALE_DECREASE_KEY.isPressed()) {
                CONFIG.setScaleX(CONFIG.scale_x + SCALE_STEP_PER_TICK);
                CONFIG.setScaleY(CONFIG.scale_y + SCALE_STEP_PER_TICK);
            } else if (SCALE_DECREASE_KEY.isPressed() && !SCALE_INCREASE_KEY.isPressed()) {
                CONFIG.setScaleX(CONFIG.scale_x - SCALE_STEP_PER_TICK);
                CONFIG.setScaleY(CONFIG.scale_y - SCALE_STEP_PER_TICK);
            }
        }

        while (FIX_CAMERA_KEY.wasPressed()) {
            CONFIG.setFixed(!CONFIG.fixed);
        }

        while (TOGGLE_ORTHO_CLOUDS_KEY.wasPressed()) {
            if (CONFIG.enabled) {
                CONFIG.toggleHideCloudsInOrtho();
            }
        }

        while (TOGGLE_ORTHO_CURSOR_KEY.wasPressed()) {
            if (CONFIG.enabled) {
                CONFIG.toggleHideCursorInOrtho();
            }
        }

        if (FIXED_CAMERA_ROTATE_LEFT_KEY.isPressed()) {
            CONFIG.setFixedYaw(CONFIG.fixed_yaw + CONFIG.fixed_rotate_speed_y);
        }
        if (FIXED_CAMERA_ROTATE_RIGHT_KEY.isPressed()) {
            CONFIG.setFixedYaw(CONFIG.fixed_yaw - CONFIG.fixed_rotate_speed_y);
        }
        if (FIXED_CAMERA_ROTATE_UP_KEY.isPressed()) {
            CONFIG.setFixedPitch(CONFIG.fixed_pitch + CONFIG.fixed_rotate_speed_x);
        }
        if (FIXED_CAMERA_ROTATE_DOWN_KEY.isPressed()) {
            CONFIG.setFixedPitch(CONFIG.fixed_pitch - CONFIG.fixed_rotate_speed_x);
        }
    }

    private void onClientStopping(MinecraftClient client) {
        if (CONFIG.isDirty()) {
            CONFIG.save();
            CONFIG.setDirty(false);
        }
    }

    public static boolean isEnabled() {
        return CONFIG.enabled;
    }

    public static void applyOrthoProjection(float aspect, float minScale) {
        float width = Math.max(minScale, CONFIG.scale_x * aspect);
        float height = Math.max(minScale, CONFIG.scale_y);
        GlStateManager.ortho(
                -width, width,
                -height, height,
                CONFIG.min_distance, CONFIG.max_distance
        );
    }

    private static KeyBinding createKeybinding(String name, int key) {
        return new KeyBinding("orthocamera.key." + name, key, KEY_CATEGORY);
    }
}

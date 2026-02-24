package com.infloat.legacyorthocamera.client;

import com.infloat.legacyorthocamera.client.config.ModConfig;
import com.infloat.legacyorthocamera.client.config.ModConfigScreen;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.ClientModInitializer;
import net.legacyfabric.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.legacyfabric.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.legacyfabric.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

public class LegacyOrthoCamera implements ClientModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("OrthoCamera");
    public static final ModConfig CONFIG = new ModConfig("config/orthocamera.json", "assets/orthocamera/default_config.json");

    private static final String KEY_CATEGORY = "orthocamera.category";
    private static final KeyBinding TOGGLE_KEY = createKeybinding("toggle", Keyboard.KEY_NUMPAD4);
    private static final KeyBinding OPEN_CONFIG_KEY = createKeybinding("open_config", Keyboard.KEY_NUMPAD5);
    private static final KeyBinding SCALE_INCREASE_KEY = createKeybinding("scale_increase", Keyboard.KEY_EQUALS);
    private static final KeyBinding SCALE_DECREASE_KEY = createKeybinding("scale_decrease", Keyboard.KEY_MINUS);
    private static final KeyBinding FIXED_CAMERA_ROTATE_UP_KEY = createKeybinding("fixed_camera_up", Keyboard.KEY_NONE);
    private static final KeyBinding FIXED_CAMERA_ROTATE_DOWN_KEY = createKeybinding("fixed_camera_down", Keyboard.KEY_NONE);
    private static final KeyBinding FIXED_CAMERA_ROTATE_LEFT_KEY = createKeybinding("fixed_camera_left", Keyboard.KEY_NONE);
    private static final KeyBinding FIXED_CAMERA_ROTATE_RIGHT_KEY = createKeybinding("fixed_camera_right", Keyboard.KEY_NONE);

    private static final float SCALE_STEP_PER_TICK = 1.00F;

    @Override
    public void onInitializeClient() {
        CONFIG.loadOrCreate();
        CONFIG.setMinDistance(CONFIG.min_distance);
        CONFIG.setMaxDistance(CONFIG.max_distance);
        CONFIG.enabled &= CONFIG.save_enabled_state;
        KeyBindingHelper.registerKeyBinding(TOGGLE_KEY);
        KeyBindingHelper.registerKeyBinding(OPEN_CONFIG_KEY);
        KeyBindingHelper.registerKeyBinding(SCALE_INCREASE_KEY);
        KeyBindingHelper.registerKeyBinding(SCALE_DECREASE_KEY);
        KeyBindingHelper.registerKeyBinding(FIXED_CAMERA_ROTATE_UP_KEY);
        KeyBindingHelper.registerKeyBinding(FIXED_CAMERA_ROTATE_DOWN_KEY);
        KeyBindingHelper.registerKeyBinding(FIXED_CAMERA_ROTATE_LEFT_KEY);
        KeyBindingHelper.registerKeyBinding(FIXED_CAMERA_ROTATE_RIGHT_KEY);
        ClientTickEvents.START_CLIENT_TICK.register(c -> CONFIG.tick());
        ClientTickEvents.END_CLIENT_TICK.register(this::handleInput);
        ClientLifecycleEvents.CLIENT_STOPPING.register(this::onClientStopping);
    }

    private void handleInput(MinecraftClient client) {
        while (TOGGLE_KEY.wasPressed()) {
            CONFIG.toggle();
        }

        while (OPEN_CONFIG_KEY.wasPressed()) {
            if (!(client.currentScreen instanceof ModConfigScreen)) {
                client.setScreen(new ModConfigScreen(client.currentScreen));
            }
        }

        if (CONFIG.enabled) {
            if (SCALE_INCREASE_KEY.isPressed() && !SCALE_DECREASE_KEY.isPressed()) {
                CONFIG.setScaleX(CONFIG.scale_x - SCALE_STEP_PER_TICK);
                CONFIG.setScaleY(CONFIG.scale_y - SCALE_STEP_PER_TICK);
            } else if (SCALE_DECREASE_KEY.isPressed() && !SCALE_INCREASE_KEY.isPressed()) {
                CONFIG.setScaleX(CONFIG.scale_x + SCALE_STEP_PER_TICK);
                CONFIG.setScaleY(CONFIG.scale_y + SCALE_STEP_PER_TICK);
            }
        }

        if (CONFIG.enabled && CONFIG.fixed) {
            if (FIXED_CAMERA_ROTATE_LEFT_KEY.isPressed() && !FIXED_CAMERA_ROTATE_RIGHT_KEY.isPressed()) {
                CONFIG.setFixedYaw(CONFIG.fixed_yaw + CONFIG.fixed_rotate_speed_y);
            } else if (FIXED_CAMERA_ROTATE_RIGHT_KEY.isPressed() && !FIXED_CAMERA_ROTATE_LEFT_KEY.isPressed()) {
                CONFIG.setFixedYaw(CONFIG.fixed_yaw - CONFIG.fixed_rotate_speed_y);
            }

            if (FIXED_CAMERA_ROTATE_UP_KEY.isPressed() && !FIXED_CAMERA_ROTATE_DOWN_KEY.isPressed()) {
                CONFIG.setFixedPitch(CONFIG.fixed_pitch + CONFIG.fixed_rotate_speed_x);
            } else if (FIXED_CAMERA_ROTATE_DOWN_KEY.isPressed() && !FIXED_CAMERA_ROTATE_UP_KEY.isPressed()) {
                CONFIG.setFixedPitch(CONFIG.fixed_pitch - CONFIG.fixed_rotate_speed_x);
            }
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
        float near = CONFIG.min_distance;
        float far = CONFIG.max_distance;
        if (far <= near) {
            far = near + 1.0F;
        }
        GlStateManager.ortho(-width, width, -height, height, near, far);
    }

    private static KeyBinding createKeybinding(String name, int key) {
        return new KeyBinding("orthocamera.key." + name, key, KEY_CATEGORY);
    }
}

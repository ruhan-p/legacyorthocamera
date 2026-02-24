package com.infloat.legacyorthocamera.client.config;

import com.infloat.legacyorthocamera.client.LegacyOrthoCamera;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.lwjgl.input.Keyboard;

public class ModConfigScreen extends Screen {

    private static final int ID_DONE = 1;
    private static final int ID_RESET = 2;

    private static final int ID_ENABLED = 10;
    private static final int ID_SAVE_ENABLED = 11;
    private static final int ID_AUTO_THIRD_PERSON = 12;
    private static final int ID_FIXED = 13;
    private static final int ID_HIDE_BORDER = 14;
    private static final int ID_HIDE_CLOUDS = 15;
    private static final int ID_HIDE_CURSOR = 16;

    private static final int NUM_MIN_DISTANCE = 0;
    private static final int NUM_MAX_DISTANCE = 1;
    private static final int NUM_FIXED_YAW = 2;
    private static final int NUM_FIXED_PITCH = 3;
    private static final int NUMERIC_COUNT = 4;

    private static final int ID_NUM_MINUS_BASE = 100;
    private static final int ID_NUM_PLUS_BASE = 200;

    private static final float[] STEPS = {
            10.0F, // min_distance
            10.0F, // max_distance
            1.0F,  // fixed_yaw
            1.0F   // fixed_pitch
    };

    private static final String[] NUMERIC_LABELS = {
            "Min Distance",
            "Max Distance",
            "Fixed Yaw",
            "Fixed Pitch"
    };

    private final Screen parent;
    private final ModConfig config = LegacyOrthoCamera.CONFIG;

    public ModConfigScreen(Screen parent) {
        this.parent = parent;
    }

    @Override
    public void init() {
        buttons.clear();

        int leftX = (width / 2) - 205;
        int rightX = (width / 2) + 5;
        int colWidth = 200;
        int y = 30;
        int rowStep = 20;

        buttons.add(new ButtonWidget(ID_ENABLED, leftX, y, colWidth, 18, boolLabel("Enabled", config.enabled)));
        addNumericButtons(NUM_MIN_DISTANCE, rightX, y);
        y += rowStep;

        buttons.add(new ButtonWidget(ID_SAVE_ENABLED, leftX, y, colWidth, 18, boolLabel("Save Enabled State", config.save_enabled_state)));
        addNumericButtons(NUM_MAX_DISTANCE, rightX, y);
        y += rowStep;

        addNumericButtons(NUM_FIXED_YAW, rightX, y);
        y += rowStep;

        addNumericButtons(NUM_FIXED_PITCH, rightX, y);
        y += rowStep;

        buttons.add(new ButtonWidget(ID_AUTO_THIRD_PERSON, leftX, y, colWidth, 18, boolLabel("Auto Third Person", config.auto_third_person)));
        y += rowStep;

        buttons.add(new ButtonWidget(ID_FIXED, leftX, y, colWidth, 18, boolLabel("Fixed Camera", config.fixed)));
        y += rowStep;

        buttons.add(new ButtonWidget(ID_HIDE_BORDER, leftX, y, colWidth, 18, boolLabel("Hide World Border", config.hide_world_border)));
        y += rowStep;

        buttons.add(new ButtonWidget(ID_HIDE_CLOUDS, leftX, y, colWidth, 18, boolLabel("Hide Clouds In Ortho", config.hide_clouds_in_ortho)));
        y += rowStep;

        buttons.add(new ButtonWidget(ID_HIDE_CURSOR, leftX, y, colWidth, 18, boolLabel("Hide Cursor In Ortho", config.hide_cursor_in_ortho)));

        buttons.add(new ButtonWidget(ID_RESET, leftX, height - 30, 95, 20, "Reset"));
        buttons.add(new ButtonWidget(ID_DONE, rightX + 80, height - 30, 95, 20, "Done"));
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        if (!button.active) {
            return;
        }

        switch (button.id) {
            case ID_DONE:
                close();
                return;
            case ID_RESET:
                config.reset();
                config.setDirty(true);
                init();
                return;
            case ID_ENABLED:
                config.toggle();
                init();
                return;
            case ID_SAVE_ENABLED:
                config.save_enabled_state = !config.save_enabled_state;
                config.setDirty(true);
                init();
                return;
            case ID_AUTO_THIRD_PERSON:
                config.auto_third_person = !config.auto_third_person;
                config.setDirty(true);
                init();
                return;
            case ID_FIXED:
                config.setFixed(!config.fixed);
                init();
                return;
            case ID_HIDE_BORDER:
                config.hide_world_border = !config.hide_world_border;
                config.setDirty(true);
                init();
                return;
            case ID_HIDE_CLOUDS:
                config.toggleHideCloudsInOrtho();
                init();
                return;
            case ID_HIDE_CURSOR:
                config.toggleHideCursorInOrtho();
                init();
                return;
            default:
                break;
        }

        if (button.id >= ID_NUM_MINUS_BASE && button.id < ID_NUM_MINUS_BASE + NUMERIC_COUNT) {
            adjustNumeric(button.id - ID_NUM_MINUS_BASE, -getStep(button.id - ID_NUM_MINUS_BASE));
            init();
            return;
        }
        if (button.id >= ID_NUM_PLUS_BASE && button.id < ID_NUM_PLUS_BASE + NUMERIC_COUNT) {
            adjustNumeric(button.id - ID_NUM_PLUS_BASE, getStep(button.id - ID_NUM_PLUS_BASE));
            init();
        }
    }

    @Override
    protected void keyPressed(char character, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            close();
            return;
        }
        super.keyPressed(character, keyCode);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        renderBackground();
        super.render(mouseX, mouseY, delta);

        int rightX = (width / 2) + 5;
        int y = 35;
        int rowStep = 20;

        drawCenteredString(textRenderer, "Legacy OrthoCamera Config", width / 2, 10, 0xFFFFFF);
        for (int i = 0; i < NUMERIC_COUNT; i++) {
            drawWithShadow(textRenderer, NUMERIC_LABELS[i] + ": " + formatNumeric(i), rightX, y, 0xFFFFFF);
            y += rowStep;
        }
    }

    @Override
    public void removed() {
        if (config.isDirty()) {
            config.save();
            config.setDirty(false);
        }
    }

    private void close() {
        client.setScreen(parent);
    }

    private String boolLabel(String name, boolean value) {
        return name + ": " + (value ? "ON" : "OFF");
    }

    private float getStep(int index) {
        float base = STEPS[index];
        return hasShiftDown() ? base * 10.0F : base;
    }

    private void adjustNumeric(int index, float delta) {
        switch (index) {
            case NUM_MIN_DISTANCE:
                config.setMinDistance(config.min_distance + delta);
                break;
            case NUM_MAX_DISTANCE:
                config.setMaxDistance(config.max_distance + delta);
                break;
            case NUM_FIXED_YAW:
                config.setFixedYaw(config.fixed_yaw + delta);
                break;
            case NUM_FIXED_PITCH:
                config.setFixedPitch(config.fixed_pitch + delta);
                break;
            default:
                break;
        }
    }

    private String formatNumeric(int index) {
        switch (index) {
            case NUM_MIN_DISTANCE:
                return fmt(config.min_distance);
            case NUM_MAX_DISTANCE:
                return fmt(config.max_distance);
            case NUM_FIXED_YAW:
                return fmt(config.fixed_yaw);
            case NUM_FIXED_PITCH:
                return fmt(config.fixed_pitch);
            default:
                return "";
        }
    }

    private void addNumericButtons(int index, int rightX, int y) {
        buttons.add(new ButtonWidget(ID_NUM_MINUS_BASE + index, rightX + 130, y, 20, 18, "-"));
        buttons.add(new ButtonWidget(ID_NUM_PLUS_BASE + index, rightX + 155, y, 20, 18, "+"));
    }

    private String fmt(float value) {
        return String.format("%.2f", value);
    }
}

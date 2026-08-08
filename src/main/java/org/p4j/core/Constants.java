package org.p4j.core;

import java.awt.Color;

/**
 * Central repository for all constant values used throughout the application.
 * This class eliminates magic numbers and provides a single point of configuration.
 */
public final class Constants {
    private Constants() {}

    public static final String APP_TITLE = "Powder4J ";
    public static final String VERSION = "0.4.0";
    public static final int DEFAULT_SIM_WIDTH = 200;
    public static final int DEFAULT_SIM_HEIGHT = 200;
    public static final int DEFAULT_SCALE = 4;

    public static boolean IS_RUNNING = true;

    public static final double TICKS_PER_SECOND = 60.0;
    public static final long NANOSECONDS_IN_SECOND = 1_000_000_000L;

    public static final int DEFAULT_BRUSH_RADIUS = 3;
    public static final int MIN_BRUSH_RADIUS = 1;
    public static final int MAX_BRUSH_RADIUS = 50;
    public static final int TNT_EXPLOSION_RADIUS = 24;
    public static final int CHLORINE_EXPLOSION_RADIUS = 12;
    public static final int GENERAL_EXPLOSION_RADIUS = 4;

    public static final double TNT_FIRE_SPAWN_THRESHOLD = 0.4;
    public static final double TNT_SPAWN_DEBRIS_CHANCE = 0.8;

    public static final float TNT_DEBRIS_MAX_VELOCITY = 8.0f;
    public static final float TNT_DEBRIS_MIN_VELOCITY = 4.0f;

    public static final int BUFFER_STRATEGY_COUNT = 3;
    public static final int DEFAULT_PALETTE_COLOR = 0xFF000000;

    public static final int HUD_OUTER_RADIUS = 120;
    public static final int HUD_INNER_RADIUS = 40;

    public static final double HUD_FULL_CIRCLE = 360.0;
    public static final double HUD_START_OFFSET_DEG = -90.0;
    
    public static final Color HUD_BACKGROUND_COLOR = new Color(30, 35, 45, 200);
    public static final Color HUD_BORDER_COLOR = new Color(255, 255, 255, 50);
    public static final Color HUD_CENTER_COLOR = new Color(15, 18, 25);
    public static final Color HUD_TEXT_UNSELECTED = Color.WHITE;
    public static final String HUD_FONT_FAMILY = "Arial";
    public static final int HUD_FONT_SIZE = 16;
    public static final int HUD_TEXT_Y_OFFSET = 20;
    public static final int PAUSE_FONT_SIZE = 32;

    public static final double RANDOM_THRESHOLD = 0.5;

    public static final float GRAVITY = 0.4f;

    public static final int MAX_FALL_SPEED = 6;
    public static final int WATER_FALL_SPEED = 2;
    public static final int WATER_DISPERSION_RATE = 16;
    public static final int MOUSE_BUTTON_RIGHT = 3;

    public static final long ESCAPE_DOUBLE_PRESS_INTERVAL = 500;

    public static final double SMOKE_DISSIPATION_CHANCE = 0.05;
    public static final double FIRE_EVAPORATION_CHANCE = 0.2;
    public static final double FIRE_DISSIPATION_CHANCE = 0.05;
    public static final double WOOD_IGNITION_CHANCE = 0.02;
    public static final double WOOD_BURN_CHANCE = 0.05;
    public static final double FIRE_IGNITION_CHANCE = 0.3;
    public static final double TNT_CRAFTING_CHANCE = 0.1;
    public static final double LAVA_FLOW_SKIP_CHANCE = 0.65;
    public static final double STEAM_CONDENSE_CHANCE = 0.005;
    public static final double GLASS_FUSION_CHANCE = 0.05;
    public static final double MUD_SPREAD_CHANCE = 0.15;
    public static final double GROW_TREE_CHANCE = 0.005;
    public static final double GROW_GRASS_CHANCE = 0.960;

    public static final float CHLORINE_DEBRIS_MAX_VELOCITY = 3.0f;
    public static final float CHLORINE_DEBRIS_MIN_VELOCITY = 1.0f;

    public static final int HUD_SLIDER_X_PADDING = 25;
    public static final int HUD_SLIDER_Y_PADDING = 50;
    public static final int HUD_SLIDER_WIDTH = 10;
    public static final int HUD_SLIDER_VISIBLE_MS = 2500;

    public static final Color HUD_SLIDER_COLOR = Color.WHITE;

    public static final int HUD_SLIDER_SYMBOL_OFFSET = 15;
    public static final int WINDOW_SHAKING_THRESHOLD = 5;
    public static final int INERTIA_MAX_STEP_LIMIT = 20;

    public static final float INERTIA_SENSITIVITY = 0.35f;

    public static final double FIRE_SMOKE_GRAY_THRESHOLD = 0.66;
    public static final double FIRE_NEAR_FUEL_PAUSE_CHANCE = 0.7;

    public static final float FLUID_DIAGONAL_VELOCITY_RETENTION = 0.5f;
    public static final float FLUID_MOMENTUM_THRESHOLD = 2.0f;
    public static final float FLUID_MOMENTUM_DISPERSION_MULTIPLIER = 0.8f;

    public static final int FLUID_HYDROSTATIC_PRESSURE_BONUS = 2;
    public static final int SHOCKWAVE_RADIUS_MAX = 10;

    public static final float SHOCKWAVE_ALPHA = 1.0f;
    public static final float SHOCKWAVE_INCREMENT = 2.0f;
    public static final float SHOCKWAVE_WIDTH = 4f;

    public static final int RENDERING_FULL_CIRCLE_DEGREES = 360;

    public static final float SHOCKWAVE_RING_EXTRA_THICKNESS = 2.0f;
    public static final float SHOCKWAVE_MIN_ALPHA = 0.0f;
    public static final float SHOCKWAVE_MAX_ALPHA = 1.0f;

    public static final float HUD_BORDER_STROKE_WIDTH = 1.0f;
    public static final float HUD_SELECTED_STROKE_WIDTH = 2.5f;
    public static final float HUD_CENTER_STROKE_WIDTH = 1.5f;

    public static final int SHAPER_ICON_FONT_SIZE = 22;
    public static final int SHAPER_ICON_Y_OFFSET = 2;

    public static final long HUD_SLIDER_FADE_DURATION_MS = 500L;
    public static final float HUD_SLIDER_MIN_OPACITY = 0.0f;
    public static final float HUD_SLIDER_MAX_OPACITY = 1.0f;
    public static final int HUD_SLIDER_CORNER_RADIUS = 5;
    public static final int HUD_SLIDER_LABEL_FONT_SIZE = 20;
    public static final int HUD_SLIDER_LABEL_ALPHA = 60;
    public static final int HUD_SLIDER_TRACK_ALPHA = 60;

    public static final double MERCURY_COLOR_WAVE_FREQUENCY = 0.5;
    public static final int MERCURY_COLOR_SHIFT_MULTIPLIER = 15;

    public static final int FIRE_LAVA_COLOR_NOISE_RANGE = 20;
    public static final int FIRE_LAVA_COLOR_NOISE_OFFSET = 10;

    public static final int PARTICLE_GRAIN_X_MULTIPLIER = 7;
    public static final int PARTICLE_GRAIN_Y_MULTIPLIER = 13;
    public static final int PARTICLE_GRAIN_MODULO = 21;
    public static final int PARTICLE_GRAIN_OFFSET = 10;

    public static final int COLOR_CHANNEL_MAX = 255;
}

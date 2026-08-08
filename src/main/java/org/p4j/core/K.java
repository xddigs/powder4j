package org.p4j.core;

import java.awt.Color;

/**
 * Repository for constant values actively used throughout the application.
 */
public final class K {
    private K() {}

    public static boolean IS_RUNNING = true;

    public static final String TITLE = "P4J";
    public static final String VERSION = "0.1.0";
    public static final int DEFAULT_SIM_WIDTH = 200;
    public static final int DEFAULT_SIM_HEIGHT = 200;
    public static final int DEFAULT_SCALE = 4;

    public static int WINDOW_SHAKING_THRESHOLD = 5;

    public static final double NANOSECONDS_IN_SECOND = 1_000_000_000.0;
    public static final double TICKS_PER_SECOND = 60.0;

    public static final float GRAVITY = 0.4f;
    public static final float MAX_GAS_SPEED = 3.0f;
    public static final int MAX_FALL_SPEED = 6;

    public static final float INERTIA_SENSITIVITY = 0.5f;
    public static final int INERTIA_MAX_STEP_LIMIT = 5;

    public static final int DEFAULT_BRUSH_RADIUS = 4;
    public static final int MIN_BRUSH_RADIUS = 1;
    public static final int MAX_BRUSH_RADIUS = 50;

    public static final int TNT_EXPLOSION_RADIUS = 24;
    public static final int CHLORINE_EXPLOSION_RADIUS = 12;
    public static final int GENERAL_EXPLOSION_RADIUS = 4;

    public static final int BUFFER_STRATEGY_COUNT = 3;
    public static final int DEFAULT_PALETTE_COLOR = 0xFF000000;
    public static final int COLOR_CHANNEL_MAX = 255;

    public static final int WHEEL_OUTER_RADIUS = 120;
    public static final int WHEEL_INNER_RADIUS = 40;
    public static final Color WHEEL_BG_COLOR = new Color(30, 35, 45, 200);
    public static final Color WHEEL_BORDER_COLOR = new Color(255, 255, 255, 50);
    public static final Color MENU_BACKGROUND_COLOR = new Color(15, 18, 25);
    public static final Color MENU_BORDER_COLOR = Color.WHITE;
    public static final float MENU_BORDER_STROKE = 1.5f;
    public static final int MENU_SYMBOL_FONT_SIZE = 14;
    public static final int MENU_TAB_FONT_SIZE = 12;

    public static final int HUD_OUTER_RADIUS = 120;
    public static final int HUD_INNER_RADIUS = 40;
    public static final double HUD_FULL_CIRCLE = 360.0;
    public static final double HUD_START_OFFSET_DEG = -90.0;
    public static final Color HUD_BACKGROUND_COLOR = new Color(30, 35, 45, 200);
    public static final Color HUD_BORDER_COLOR = new Color(255, 255, 255, 50);
    public static final Color HUD_CENTER_COLOR = new Color(15, 18, 25);
    public static final Color HUD_TEXT_UNSELECTED = Color.WHITE;
    public static final float HUD_BORDER_STROKE_WIDTH = 1.5f;
    public static final float HUD_SELECTED_STROKE_WIDTH = 2.0f;
    public static final float HUD_CENTER_STROKE_WIDTH = 1.0f;
    public static final String HUD_FONT_FAMILY = "Arial";
    public static final int HUD_FONT_SIZE = 16;
    public static final int HUD_TEXT_Y_OFFSET = 20;
    public static final int PAUSE_FONT_SIZE = 32;
    public static final int SHAPER_ICON_FONT_SIZE = 14;
    public static final int SHAPER_ICON_Y_OFFSET = 2;

    public static final long HUD_SLIDER_VISIBLE_MS = 2000;
    public static final long HUD_SLIDER_FADE_DURATION_MS = 500;
    public static final int HUD_SLIDER_WIDTH = 12;
    public static final int HUD_SLIDER_X_PADDING = 15;
    public static final int HUD_SLIDER_Y_PADDING = 30;
    public static final int HUD_SLIDER_CORNER_RADIUS = 6;
    public static final int HUD_SLIDER_TRACK_ALPHA = 80;
    public static final Color HUD_SLIDER_COLOR = new Color(60, 120, 210, 220);
    public static final int HUD_SLIDER_LABEL_FONT_SIZE = 14;
    public static final int HUD_SLIDER_SYMBOL_OFFSET = 5;
    public static final float HUD_SLIDER_MAX_OPACITY = 1.0f;
    public static final float HUD_SLIDER_MIN_OPACITY = 0.0f;

    public static final int MOUSE_RIGHT_CLICK = 2;
    public static final int ESCAPE_DOUBLE_PRESS_INTERVAL = 500;

    public static final float SHOCKWAVE_ALPHA = 1.0f;
    public static final float SHOCKWAVE_MIN_ALPHA = 0.0f;
    public static final float SHOCKWAVE_MAX_ALPHA = 1.0f;
    public static final float SHOCKWAVE_RADIUS_MAX = 0.0f;
    public static final float SHOCKWAVE_INCREMENT = 8.0f;
    public static final float SHOCKWAVE_WIDTH = 3.0f;
    public static final float SHOCKWAVE_RING_EXTRA_THICKNESS = 1.0f;

    public static final double MERCURY_COLOR_WAVE_FREQUENCY = 0.1;
    public static final double MERCURY_COLOR_SHIFT_MULTIPLIER = 10.0;
    public static final double FIRE_LAVA_COLOR_NOISE_RANGE = 40.0;
    public static final double FIRE_LAVA_COLOR_NOISE_OFFSET = 20.0;
    public static final int PARTICLE_GRAIN_X_MULTIPLIER = 17;
    public static final int PARTICLE_GRAIN_Y_MULTIPLIER = 31;
    public static final int PARTICLE_GRAIN_MODULO = 13;
    public static final int PARTICLE_GRAIN_OFFSET = 6;

    public static final int MUD_MAX_DEPTH = 4;
    public static final int TREE_WATER_ABSORB_RADIUS = 3;
    public static final int TREE_WATER_ABSORB_MAX = 10;
    public static final int TREE_BASE_HEIGHT_MIN = 8;
    public static final int TREE_BASE_HEIGHT_MAX = 16;
    public static final int TREE_HEIGHT_PER_WATER = 2;
    public static final int TREE_TRUNK_BASE_WIDTH = 1;
    public static final int TREE_WATER_DIVISOR_TRUNK_WIDTH = 4;
    public static final int TREE_LEAF_BASE_RADIUS = 3;
    public static final int TREE_WATER_DIVISOR_LEAF_RADIUS = 3;
    public static final int TREE_LEAF_CANOPY_OFFSET_BASE = 2;
    public static final int TREE_WATER_DIVISOR_CANOPY_OFFSET = 5;
    public static final int TREE_LEAF_RADIUS_HEIGHT_OFFSET = 1;
    public static final int TREE_LEAF_CIRCLE_TOLERANCE = 1;
    public static final float TREE_CURVE_CHANCE_THICK = 0.05f;
    public static final float TREE_CURVE_CHANCE_THIN = 0.15f;
    public static final int TREE_CURVE_MIN_HEIGHT_STEP = 3;
    public static final int TREE_MIN_X_MARGIN = 2;
    public static final int TREE_MAX_X_MARGIN_OFFSET = 2;

    public static final double SALT_CHANCE = 0.05;
    public static final double SMOKE_DISSIPATION_CHANCE = 0.05;
    public static final double HYDROGEN_DISSIPATION_CHANCE = 0.05;
    public static final double FIRE_EVAPORATION_CHANCE = 0.2;
    public static final double FIRE_DISSIPATION_CHANCE = 0.05;
    public static final double FIRE_SMOKE_GRAY_THRESHOLD = 0.7;
    public static final double FIRE_NEAR_FUEL_PAUSE_CHANCE = 0.1;
    public static final double WOOD_IGNITION_CHANCE = 0.02;
    public static final double FIRE_IGNITION_CHANCE = 0.3;
    public static final double STEAM_CONDENSE_CHANCE = 0.1;
    public static final double GLASS_FUSION_CHANCE = 0.05;
    public static final double MUD_SPREAD_CHANCE = 0.15;
    public static final double GROW_TREE_CHANCE = 0.005;
    public static final double GROW_GRASS_CHANCE = 0.960;
    public static final double WOOD_ABSORPTION_CHANCE = 0.2;
    public static final double GASOLINE_CREATION_CHANCE = 0.15;
    public static final double ICE_CREATION_CHANCE = 0.2;
    public static final double GRAVEL_CREATION_CHANCE = 0.2;
    public static final double CEMENT_CREATION_CHANCE = 0.1;
    public static final double METHANE_CREATION_CHANCE = 0.1;
}
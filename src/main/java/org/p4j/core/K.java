package org.p4j.core;

import java.awt.Color;

/**
 * Repository for constant values actively used throughout the application.
 */
public final class K {
    private K() {}

    public static final String APP_TITLE = "Powder4J ";
    public static final String VERSION = "0.6.0";
    public static final int DEFAULT_SIM_WIDTH = 200;
    public static final int DEFAULT_SIM_HEIGHT = 200;
    public static final int DEFAULT_SCALE = 4;

    public static final Color GAME_BACKGROUND_COLOR = new Color(24, 24, 37);
    public static final Color TEXT_COLOR = new Color(202, 211, 245);
    public static final Color TEXT_COLOR_UNSELECTED = new Color(150, 160, 200);
    public static final Color UI_BACKGROUND_COLOR = new Color(110, 115, 141);
    public static final Color UI_BACKGROUND_BORDER_COLOR = new Color(110, 115, 141, 100);
    public static final Color HIGHLIGHT_COLOR = new Color(60, 120, 210, 200);

    public static boolean IS_RUNNING = true;

    public static final double TICKS_PER_SECOND = 60.0;
    public static final long NANOSECONDS_IN_SECOND = 1_000_000_000L;

    public static final int DEFAULT_BRUSH_RADIUS = 2;
    public static final int MIN_BRUSH_RADIUS = 1;
    public static final int MAX_BRUSH_RADIUS = 50;
    public static final int TNT_EXPLOSION_RADIUS = 24;
    public static final int CHLORINE_EXPLOSION_RADIUS = 12;
    public static final int GENERAL_EXPLOSION_RADIUS = 4;

    public static final int BUFFER_STRATEGY_COUNT = 3;
    public static final int DEFAULT_PALETTE_COLOR = 0xFF000000;

    public static final int HUD_OUTER_RADIUS = 120;
    public static final int HUD_INNER_RADIUS = 40;

    public static final double HUD_FULL_CIRCLE = 360.0;
    public static final double HUD_START_OFFSET_DEG = -90.0;

    public static final String HUD_FONT_FAMILY = "Arial";
    public static final int HUD_FONT_SIZE = 16;
    public static final int HUD_TEXT_Y_OFFSET = 20;

    public static final String NANINF = "naninf";
    public static final String PAUSE_TEXT = "PAUSED";
    public static final Color PAUSE_TEXT_COLOR = TEXT_COLOR;
    public static final int PAUSE_FONT_SIZE = 32;

    public static final Color MENU_BACKGROUND_COLOR = UI_BACKGROUND_COLOR;

    public static final float GRAVITY = 0.4f;
    public static final float GRAVITY_MASS_FACTOR = 3.0f;

    public static final float MAX_GAS_SPEED = 3.0f;
    public static final int MAX_FALL_SPEED = 6;
    public static final int MOUSE_BUTTON_RIGHT = 3;

    public static final long ESCAPE_DOUBLE_PRESS_INTERVAL = 500;

    public static final double SALT_CHANCE = 0.05;
    public static final double SMOKE_DISSIPATION_CHANCE = 0.05;
    public static final double HYDROGEN_DISSIPATION_CHANCE = 0.15;
    public static final double FIRE_EVAPORATION_CHANCE = 0.2;
    public static final double FIRE_DISSIPATION_CHANCE = 0.05;
    public static final double WOOD_IGNITION_CHANCE = 0.02;
    public static final double FIRE_IGNITION_CHANCE = 0.03;
    public static final double WATER_CREATION_CHANCE = 0.5;
    public static final double STEAM_CONDENSE_CHANCE = 0.1;
    public static final double GLASS_FUSION_CHANCE = 0.05;
    public static final double MUD_SPREAD_CHANCE = 0.15;
    public static final double GROW_TREE_CHANCE = 0.005;
    public static final double GROW_GRASS_CHANCE = 0.960;
    public static final double WOOD_ABSORPTION_CHANCE = 0.2;
    public static final double GASOLINE_CREATION_CHANCE = 0.15;
    public static final double ICE_CREATION_CHANCE = 0.2;
    public static final double GRAVEL_CREATION_CHANCE = 0.2;
    public static final double LAVA_CREATION_CHANCE = 0.001;
    public static final double CEMENT_CREATION_CHANCE = 0.15;
    public static final double METHANE_CREATION_CHANCE = 0.1;
    public static final double GAS_TRAIL_CHANCE = 0.15;

    public static final int TREE_WATER_ABSORB_RADIUS = 3;
    public static final int TREE_WATER_ABSORB_MAX = 12;
    public static final int TREE_BASE_HEIGHT_MIN = 6;
    public static final int TREE_BASE_HEIGHT_MAX = 12;
    public static final int TREE_HEIGHT_PER_WATER = 2;
    public static final int TREE_TRUNK_BASE_WIDTH = 1;
    public static final int TREE_WATER_DIVISOR_TRUNK_WIDTH = 4;
    public static final int TREE_LEAF_BASE_RADIUS = 4;
    public static final int TREE_WATER_DIVISOR_LEAF_RADIUS = 1;
    public static final int TREE_LEAF_CANOPY_OFFSET_BASE = 7;
    public static final int TREE_WATER_DIVISOR_CANOPY_OFFSET = 1;
    public static final int TREE_LEAF_RADIUS_HEIGHT_OFFSET = 1;
    public static final int TREE_LEAF_CIRCLE_TOLERANCE = 3;
    public static final int TREE_CURVE_MIN_HEIGHT_STEP = 3;

    public static final float TREE_CURVE_CHANCE_THICK = 0.15f;
    public static final float TREE_CURVE_CHANCE_THIN = 0.30f;

    public static final int TREE_MIN_X_MARGIN = 1;
    public static final int TREE_MAX_X_MARGIN_OFFSET = 2;

    public static final double EXPLOSION_CORE_RADIUS_RATIO = 0.3;
    public static final double EXPLOSION_MID_RADIUS_RATIO = 0.7;
    public static final float EXPLOSION_MID_FIRE_CHANCE = 0.25f;
    public static final float EXPLOSION_OUTER_CO2_CHANCE = 0.35f;
    public static final float EXPLOSION_OUTER_EMPTY_CHANCE = 0.60f;

    public static final float HEAT_ADD_FIRE = 25.0f;
    public static final float HEAT_ADD_LAVA = 40.0f;

    public static final float DEFAULT_AMBIENT_TEMP = 20.0f;
    public static final float DEFAULT_AMBIENT_LOSS_RATE = 0.0002f;
    public static final float DEFAULT_SIMULATION_SPEED = 0.18f;

    public static final float LATENT_HEAT_ACTIVATION_DELTA = 1.2f;
    public static final float BOIL_LATENT_HEAT_CONSUMPTION = 8.0f;
    public static final float MELT_LATENT_HEAT_CONSUMPTION = 2.0f;

    public static final float BOUNDS_CONDUCTIVITY_FACTOR = 0.02f;
    public static final float EMPTY_CONDUCTIVITY_FACTOR = 0.01f;

    public static final float MIN_TEMP_DIFF = 0.01f;
    public static final float CONDUCTIVITY_AVG_FACTOR = 0.5f;
    public static final float MAX_DELTA_RATIO = 0.35f;

    public static final int HUD_SLIDER_X_PADDING = 25;
    public static final int HUD_SLIDER_Y_PADDING = 50;
    public static final int HUD_SLIDER_WIDTH = 10;
    public static final int HUD_SLIDER_VISIBLE_MS = 2500;

    public static final Color HUD_SLIDER_COLOR = TEXT_COLOR;
    public static final String HUD_SLIDER_PLUS_SYMBOL = "+";
    public static final String HUD_SLIDER_MINUS_SYMBOL = "-";

    public static final int HUD_SLIDER_SYMBOL_OFFSET = 15;
    public static final int WINDOW_SHAKING_THRESHOLD = 5;
    public static final int INERTIA_MAX_STEP_LIMIT = 20;

    public static final float INERTIA_SENSITIVITY = 0.35f;

    public static final long HOVER_DELAY_MS = 2000L;
    public static final int CARD_WIDTH = 180;
    public static final int CARD_HEIGHT = 90;
    public static final int CARD_PADDING = 8;
    public static final int CARD_LINEHEIGHT = 20;
    public static final int CARD_OFFSET = 12;
    public static final int MOUSE_OFFSET_X = 20;
    public static final int MOUSE_OFFSET_Y = 20;

    public static final int SHAPER_ICON_FONT_SIZE = 22;
    public static final int SHAPER_ICON_Y_OFFSET = 2;

    public static final double FIRE_SMOKE_GRAY_THRESHOLD = 0.66;
    public static final double FIRE_NEAR_FUEL_PAUSE_CHANCE = 0.7;

    public static final int MUD_MAX_DEPTH = 15;

    public static final int SHOCKWAVE_RADIUS_MAX = 10;

    public static final float SHOCKWAVE_ALPHA = 1.0f;
    public static final float SHOCKWAVE_INCREMENT = 2.0f;
    public static final float SHOCKWAVE_WIDTH = 4f;
    public static final Color SHOCKWAVE_COLOR = TEXT_COLOR;

    public static final int RENDERING_FULL_CIRCLE_DEGREES = 360;

    public static final float SHOCKWAVE_RING_EXTRA_THICKNESS = 2.0f;
    public static final float SHOCKWAVE_MIN_ALPHA = 0.0f;
    public static final float SHOCKWAVE_MAX_ALPHA = 1.0f;

    public static final float HUD_BORDER_STROKE_WIDTH = 1.0f;
    public static final float HUD_SELECTED_STROKE_WIDTH = 2.5f;
    public static final float HUD_CENTER_STROKE_WIDTH = 1.5f;

    public static final long HUD_SLIDER_FADE_DURATION_MS = 500L;
    public static final float HUD_SLIDER_MIN_OPACITY = 0.0f;
    public static final float HUD_SLIDER_MAX_OPACITY = 1.0f;
    public static final int HUD_SLIDER_CORNER_RADIUS = 5;
    public static final int HUD_SLIDER_LABEL_FONT_SIZE = 20;
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
    public static final int COLOR_ALPHA_SHIFT = 24;
    public static final int COLOR_RED_SHIFT = 16;
    public static final int COLOR_GREEN_SHIFT = 8;
}
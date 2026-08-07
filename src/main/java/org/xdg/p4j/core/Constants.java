package org.xdg.p4j.core;

import java.awt.Color;

/**
 * Central repository for all constant values used throughout the application.
 * This class eliminates magic numbers and provides a single point of configuration.
 */
public final class Constants {
    private Constants() {}

    public static final String APP_TITLE = "Powder4J ";
    public static final String VERSION = "0.1.0";
    public static final int DEFAULT_SIM_WIDTH = 200;
    public static final int DEFAULT_SIM_HEIGHT = 200;
    public static final int DEFAULT_SCALE = 4;

    public static boolean IS_RUNNING = true;

    public static final double TICKS_PER_SECOND = 60.0;
    public static final long NANOSECONDS_IN_SECOND = 1_000_000_000L;

    public static final int DEFAULT_BRUSH_RADIUS = 3;
    public static final int MIN_BRUSH_RADIUS = 1;
    public static final int MAX_BRUSH_RADIUS = 50;

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

    public static final double RANDOM_THRESHOLD = 0.5;
    public static final float GRAVITY = 0.4f;
    public static final int MAX_FALL_SPEED = 6;
    public static final int WATER_FALL_SPEED = 2;
    public static final int WATER_DISPERSION_RATE = 16;
    public static final int MOUSE_BUTTON_RIGHT = 3;

    public static final double SMOKE_DISSIPATION_CHANCE = 0.05;
    public static final double FIRE_EVAPORATION_CHANCE = 0.2;
    public static final double FIRE_DISSIPATION_CHANCE = 0.05;
    public static final double WOOD_IGNITION_CHANCE = 0.02;
    public static final double WOOD_BURN_CHANCE = 0.05;
}

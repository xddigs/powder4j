package org.p4j.render;

import org.p4j.core.K;
import org.p4j.core.World;
import org.p4j.data.ElementID;

public class HeatMap {
    public enum Mode { NONE, HOT; }

    private Mode mode = Mode.NONE;
    private final World world;

    public HeatMap(World world) {
        this.world = world;
    }

    public int getColorForPixel(byte elementId, float temp) {
        if (mode == Mode.NONE) {
            return -1;
        }

        if (elementId == ElementID.EMPTY.getId()) {
            return 0xFF000000;
        }

        if (mode == Mode.HOT) {
            return getHotColor(temp);
        } else {
            return 0xFF000000;
        }
    }

    private int getHotColor(float temp) {
        float factor = Math.clamp((temp - K.DEFAULT_AMBIENT_TEMP) /
                (K.MAX_HOT_TEMP - K.DEFAULT_AMBIENT_TEMP), 0.0f, 1.0f);

        int r, g, b;

        if (factor < 0.25f) {
            float t = factor / 0.25f;
            r = (int) (30 + t * (220 - 30));
            g = (int) (30 + t * (20 - 30));
            b = (int) (35 + t * (20 - 35));
        } else if (factor < 0.65f) {
            float t = (factor - 0.25f) / 0.40f;
            r = (int) (220 + t * (255 - 220));
            g = (int) (20 + t * (180 - 20));
            b = 20;
        } else {
            float t = (factor - 0.65f) / 0.35f;
            r = 255;
            g = (int) (180 + t * (255 - 180));
            b = (int) (20 + t * (235 - 20));
        }

        return (0xFF << 24) | (r << 16) | (g << 8) | b;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public void toggleMode() {
       if (mode == Mode.NONE) mode = Mode.HOT;
       else mode = Mode.NONE;
    }
}
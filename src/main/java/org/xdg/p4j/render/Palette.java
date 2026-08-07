package org.xdg.p4j.render;

import java.util.HashMap;
import java.util.Map;

public class Palette {
    private final Map<Byte, Integer> colorMap = new HashMap<>();
    private final int defaultColor = 0xFF000000;

    public Palette() {
        setColor((byte) 0, 0xFF0B0E14);
        setColor((byte) 1, 0xFF808080);
        setColor((byte) 2, 0xFFE5C07B);
        setColor((byte) 3, 0xFF4FA6ED);
        setColor((byte) 4, 0xFFE06C75);
    }

    public void setColor(byte elementId, int argb) {
        colorMap.put(elementId, argb);
    }

    public int getColor(byte elementId) {
        return colorMap.getOrDefault(elementId, defaultColor);
    }
}
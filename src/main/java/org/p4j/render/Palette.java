package org.p4j.render;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.p4j.core.K;
import org.p4j.data.ElementID;

import java.util.HashMap;
import java.util.Map;

/**
 * Maintains the mapping between element identifiers and their visual colors.
 * This class allows for centralized management of the simulation's
 * aesthetic palette.
 */
public class Palette {
    private static final Logger log = LoggerFactory.getLogger(Palette.class);
    private final Map<Short, Integer> colorMap = new HashMap<>();
    private final int defaultColor = K.DEFAULT_PALETTE_COLOR;

    public Palette() {
        log.debug("Initializing color palette.");
        for (ElementID el : ElementID.values()) {
            setColor(el.getId(), el.getColorArgb());
        }
    }

    public void setColor(short elementId, int argb) {
        colorMap.put(elementId, argb);
    }

    public int getColor(short elementId) {
        return colorMap.getOrDefault(elementId, defaultColor);
    }
}
package org.xdg.p4j.render;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xdg.p4j.core.Constants;
import org.xdg.p4j.data.ElementID;

import java.util.HashMap;
import java.util.Map;

/**
 * Maintains the mapping between element identifiers and their visual colors.
 * This class allows for centralized management of the simulation's
 * aesthetic palette.
 */
public class Palette {
    private static final Logger log = LoggerFactory.getLogger(Palette.class);
    private final Map<Byte, Integer> colorMap = new HashMap<>();
    private final int defaultColor = Constants.DEFAULT_PALETTE_COLOR;

    public Palette() {
        log.debug("Initializing color palette.");
        for (ElementID el : ElementID.values()) {
            setColor(el.getId(), el.getColorArgb());
        }
    }

    public void setColor(byte elementId, int argb) {
        colorMap.put(elementId, argb);
    }

    public int getColor(byte elementId) {
        return colorMap.getOrDefault(elementId, defaultColor);
    }
}
package org.xdg.p4j.input;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xdg.p4j.core.Constants;
import org.xdg.p4j.data.ElementID;

/**
 * Handles the configuration of the drawing tool used by the user.
 * The brush determines which element is being placed and the area of
 * effect during interaction.
 */
public class Brush {
    private static final Logger log = LoggerFactory.getLogger(Brush.class);
    private ElementID currentElement;
    private int radius;

    public Brush(ElementID defaultElement, int initialRadius) {
        this.currentElement = defaultElement;
        this.radius = initialRadius;
    }

    public ElementID getCurrentElement() {
        return currentElement;
    }

    public void setCurrentElement(ElementID currentElement) {
        log.debug("Brush element changed to: {}", currentElement);
        this.currentElement = currentElement;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        int oldRadius = this.radius;
        this.radius = Math.clamp(radius, Constants.MIN_BRUSH_RADIUS, Constants.MAX_BRUSH_RADIUS);
        if (oldRadius != this.radius) {
            log.debug("Brush radius changed to: {}", this.radius);
        }
    }

    public void changeRadius(int delta) {
        setRadius(this.radius + delta);
    }
}
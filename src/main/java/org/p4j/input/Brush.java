package org.p4j.input;

import org.p4j.core.K;
import org.p4j.core.World;
import org.p4j.data.BrushType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.p4j.data.BrushShape;
import org.p4j.data.ElementID;

/**
 * Handles the configuration of the drawing tool used by the user.
 * The brush determines which element is being placed and the area of
 * effect during interaction.
 */
public class Brush {
    private static final Logger log = LoggerFactory.getLogger(Brush.class);
    private ElementID element;
    private BrushType type;
    private BrushShape shape;
    private int radius;
    private long lastRadiusChangeTime;
    private long lastTemperatureChangeTime;
    private final World world;

    public Brush(ElementID defaultElement, int initialRadius, World world) {
        this.element = defaultElement;
        this.shape = BrushShape.CIRCLE;
        this.type = BrushType.BRUSH;
        this.radius = initialRadius;
        this.lastRadiusChangeTime = 0;
        this.lastTemperatureChangeTime = 0;
        this.world = world;
    }

    public ElementID getElement() {
        return element;
    }

    public void setElement(ElementID currentElement) {
        log.debug("Brush element changed to: {}", currentElement);
        this.element = currentElement;
    }

    public BrushShape getShape() {
        return shape;
    }

    public void setShape(BrushShape shape) {
        log.debug("Brush shape changed to: {}", shape);
        this.shape = shape;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        int oldRadius = this.radius;
        this.radius = Math.clamp(radius, K.MIN_BRUSH_RADIUS, K.MAX_BRUSH_RADIUS);
        if (oldRadius != this.radius) {
            this.lastRadiusChangeTime = System.currentTimeMillis();
            log.debug("Brush radius changed to: {}", this.radius);
        }
    }

    public long getLastRadiusChangeTime() {
        return lastRadiusChangeTime;
    }

    public void changeRadius(int delta) {
        setRadius(this.radius + delta);
    }

    public void changeTemperature(float delta) {
        float calculatedTemp = world.getThermo().getAmbientTemp() + delta;
        float newTemp = Math.clamp(calculatedTemp, K.MIN_COLD_TEMP, K.MAX_HOT_TEMP);
        world.getThermo().setAmbientTemp(newTemp);
        this.lastTemperatureChangeTime = System.currentTimeMillis();
        log.debug("Changing temperature by: {}ºC, it's now {}", delta, newTemp);
    }

    public boolean contains(int dx, int dy) {
        int r = this.radius;
        return switch (shape) {
            case CIRCLE -> (dx * dx + dy * dy) <= (r * r);
            case SQUARE -> Math.abs(dx) <= r && Math.abs(dy) <= r;
            case TRIANGLE -> {
                if (dy < -r || dy > r) yield false;
                int maxWidthAtY = (int) ((r - dy) * 0.866f);
                yield Math.abs(dx) <= maxWidthAtY;
            }
        };
    }

    public BrushType getType() {
        return type;
    }

    public void setType(BrushType type) {
        this.type = type;
        log.debug("Brush type changed to: {}", type);
    }

    public float getTargetTemperature() {
        return world.getThermo().getAmbientTemp();
    }

    public long getLastTemperatureChangeTime() {
        return lastTemperatureChangeTime;
    }

}
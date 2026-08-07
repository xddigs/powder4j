package org.xdg.p4j.input;

import org.xdg.p4j.data.ElementID;

public class Brush {
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
        this.currentElement = currentElement;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = Math.clamp(radius, 1, 50);
    }

    public void changeRadius(int delta) {
        setRadius(this.radius + delta);
    }
}
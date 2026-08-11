package org.p4j.render;

import java.awt.*;

@FunctionalInterface
public interface Slicer<T> {
    void render(Graphics2D g2d, T item, boolean isSelected, int iconX, int iconY);
}
package org.p4j.core;

import org.p4j.input.Brush;
import org.p4j.input.KeyboardController;
import org.p4j.input.MouseController;

public interface Engine {
    void updatePixels(World world);
    void render(World world, KeyboardController keyController,
                MouseController mouseController, Brush brush);
    void dispose();
}

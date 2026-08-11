package org.p4j.gui;

import org.p4j.core.World;
import org.p4j.input.Brush;
import org.p4j.input.KeyboardController;
import org.p4j.input.MouseController;

public interface Engine {
    void init(World world);
    void updatePixels(World world);
    void render(World world, KeyboardController keyController,
                MouseController mouseController, Brush brush);
    boolean shouldClose();
    void cleanup();
}
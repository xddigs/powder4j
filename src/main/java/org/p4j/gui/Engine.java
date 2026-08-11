package org.p4j.gui;

import org.p4j.core.World;

public interface Engine {
    void init(World world);
    void render(World world);
    void cleanup();
    boolean shouldClose();
}